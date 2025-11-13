package event.oms.adapter.`in`.web.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import event.oms.adapter.`in`.web.order.request.OrderItemRequest
import event.oms.adapter.`in`.web.order.request.OrderRequest
import event.oms.adapter.`in`.web.order.request.ReceiverInfoRequest
import event.oms.adapter.out.persistence.order.repository.OrderJpaRepository
import event.oms.adapter.out.persistence.product.entity.ProductEntity
import event.oms.adapter.out.persistence.product.repository.ProductJpaRepository
import event.oms.application.port.`in`.order.OrderSummaryResult
import event.oms.common.extensions.getLogger
import event.oms.domain.model.order.OrderTraceStatus
import event.oms.security.WithMockCustomUser
import org.awaitility.kotlin.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class OrderConcurrencyIntegrationTest @Autowired constructor(
    private val mockMvc          : MockMvc,
    private val objectMapper     : ObjectMapper,
    private val productRepository: ProductJpaRepository,
) {
    private val log = getLogger()
    private val firstProductId  = 5312040002L
    private val secondProductId = 5312040003L
    private val thirdProductId  = 5312040004L

    /*
    @AfterEach
    fun tearDown() {
        // 테스트 후 데이터 정리
        productRepository.deleteById(productId)
        orderRepository.deleteAll()
        // Redis 키 정리 (패턴으로)
        val traceKeys = redisTemplate.keys("order:trace:*")
        if (traceKeys.isNotEmpty()) {
            redisTemplate.delete(traceKeys)
        }
    }
    */

    @Test
    @DisplayName("재고가 N개일 때 N명이 동시에 주문하면, 재고는 0개가 되어야 한다")
    @WithMockCustomUser(id = 1L, username = "armada") // 테스트용 인증 유저 주입
    fun `should handle concurrent order requests correctly when stock is limited`() {
        // given: 100명의 동시 사용자, 10개의 재고
        val threadCount = 1000

        // 1. 재고 10개짜리 상품 생성
        // productRepository.save(ProductEntity(productId, "동시성 테스트 상품", BigDecimal(1000), stockCount, 0L))

        // 2. 동시성 테스트 도구 준비
        val executor  = Executors.newFixedThreadPool(threadCount)
        val startGate = CountDownLatch(1) // 모든 스레드가 동시에 출발하도록 막는 '문'
        val endGate   = CountDownLatch(threadCount) // 모든 스레드가 끝날 때까지 대기하는 '문'

        // 3. 각 스레드의 주문 요청 결과(traceId)를 저장할 동기화된 맵
        val traceResults = ConcurrentHashMap<Int, String>()

        // when: N명의 사용자가 동시에 주문 (스레드 생성)
        for (i in 1..threadCount) {
            executor.submit {
                try {
                    startGate.await() // 대기

                    // 1. 주문 요청 DTO 생성
                    val request = OrderRequest(
                        items = listOf(
                            OrderItemRequest(firstProductId, 1),
                            OrderItemRequest(secondProductId, 1),
                            OrderItemRequest(thirdProductId, 1)
                        ), // 1개 구매
                        receiverInfo = ReceiverInfoRequest("User $i", "010-1234-5678", "주소 $i")
                    )

                    // 2. [ACT] 신규 주문 API (POST /api/v1/orders) 호출
                    val mvcResult = mockMvc.post("/api/v1/orders") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(request)
                    }.andExpect {
                        status { isAccepted() } // 202 Accepted
                        jsonPath("$.data") { exists() }
                    }.andReturn()

                    // 3. 결과(traceId) 저장
                    val responseBody = mvcResult.response.contentAsString
                    val traceId = JsonPath.read<String>(responseBody, "$.data")
                    log.info("traceId = {}", traceId)
                    traceResults[i] = traceId

                } catch (e: Exception) {
                    println("Error in thread $i: ${e.message}")
                } finally {
                    endGate.countDown() // 작업 완료 알림
                }
            }
        }

        // 동시 요청 시작
        startGate.countDown()

        // 모든 스레드가 끝날 때까지 30초간 대기
        val finishedInTime = endGate.await(30, TimeUnit.SECONDS)
        executor.shutdown()

        // then: 30초 내에 모든 요청이 traceId를 반환했는지 확인
        assertEquals(true, finishedInTime, "30초 내에 모든 스레드가 완료되어야 합니다.")
        assertEquals(threadCount, traceResults.size, "N개의 모든 요청이 traceId를 반환해야 합니다.")

        // --- Kafka 컨슈머가 모든 메시지를 처리할 때까지 대기 (Polling) ---
        val finalStatuses = ConcurrentHashMap<String, OrderTraceStatus>()

        // N개의 주문 상태가 모두 COMPLETED 또는 FAILED가 될 때까지 최대 30초간 폴링
        await.atMost(30, TimeUnit.SECONDS).until {
            traceResults.values.forEach { traceId ->
                if (!finalStatuses.containsKey(traceId)) {
                    val statusDto = getStatusFromPollApi(traceId) // 폴링 API 호출
                    if (statusDto.status == OrderTraceStatus.COMPLETED || statusDto.status == OrderTraceStatus.FAILED) {
                        finalStatuses[traceId] = statusDto.status
                    }
                }
            }
            finalStatuses.size == threadCount
        }

        // --- 최종 결과 검증 ---
        val successCount = finalStatuses.values.count { it == OrderTraceStatus.COMPLETED }
        val failedCount = finalStatuses.values.count { it == OrderTraceStatus.FAILED }

        log.info("--- 동시성 테스트 결과 ---")
        log.info("총 요청: $threadCount")
        log.info("성공 (COMPLETED): $successCount")
        log.info("실패 (FAILED): $failedCount")

        // 3. 최종 재고는 0이어야 함
        val firstProduct  = productRepository.findById(firstProductId).get()
        val secondProduct = productRepository.findById(secondProductId).get()
        val thirdProduct  = productRepository.findById(thirdProductId).get()
        assertEquals(0, firstProduct.stock, "최종 상품 재고는 0이어야 합니다.")
        assertEquals(0, secondProduct.stock, "최종 상품 재고는 0이어야 합니다.")
        assertEquals(0, thirdProduct.stock, "최종 상품 재고는 0이어야 합니다.")

    }

    /**
     * 폴링 API(/by-trace/{traceId})를 호출하여 현재 주문 상태를 가져오는 헬퍼 함수
     */
    @WithMockCustomUser(id = 1L, username = "testuser")
    private fun getStatusFromPollApi(traceId: String): OrderSummaryResult {
        val mvcResult = mockMvc.get("/api/v1/orders/by-trace/{traceId}", traceId)
            .andExpect { status { isOk() } }
            .andReturn()

        val responseBody = mvcResult.response.contentAsString
        // objectMapper를 사용해 응답 DTO(OrderSummaryResult)를 직접 파싱
        // (이 방식이 JsonPath보다 더 강력하고 타입 안전합니다)
        // OrderSummaryResult는 data class이므로 Jackson이 변환 가능해야 함.
        // (만약 OrderSummaryResult에 기본 생성자가 없다면, jackson-module-kotlin이 필요함)
        val status = JsonPath.read<String>(responseBody, "$.data.status")
        val orderId = JsonPath.read<Long?>(responseBody, "$.data.orderId")

        return OrderSummaryResult(OrderTraceStatus.valueOf(status), orderId, traceId)
    }

}