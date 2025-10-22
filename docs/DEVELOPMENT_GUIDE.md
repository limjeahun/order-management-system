# OMS 개발 가이드

## 목차
1. [환경 설정](#1-환경-설정)
2. [프로젝트 실행](#2-프로젝트-실행)
3. [개발 워크플로우](#3-개발-워크플로우)
4. [코드 작성 규칙](#4-코드-작성-규칙)
5. [테스트 작성 가이드](#5-테스트-작성-가이드)
6. [커밋 규칙](#6-커밋-규칙)
7. [문제 해결](#7-문제-해결)
8. [주요 개선 과제](#8-주요-개선-과제)

---

## 1. 환경 설정

### 1.1 필수 요구사항

- **JDK 21**: Java Development Kit 21 이상
- **Gradle 8.x**: 빌드 도구
- **MySQL 8.0**: 데이터베이스
- **IntelliJ IDEA**: IDE (추천)
- **Git**: 버전 관리

### 1.2 JDK 설치 확인

```bash
# Java 버전 확인
java -version
# openjdk 21.0.x 이상 필요

# JAVA_HOME 설정 확인
echo $JAVA_HOME
# /Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home (macOS)
```

### 1.3 Gradle 확인

```bash
# Gradle 래퍼 확인
./gradlew --version
# 프로젝트 루트에서 자동으로 Gradle 8.x 다운로드
```

### 1.4 MySQL 설정

```bash
# MySQL 서비스 시작 (macOS)
brew services start mysql

# MySQL 접속
mysql -u armada -p armada

# 데이터베이스 생성
CREATE DATABASE write_qr;

# 스키마 초기화
mysql -u armada -p armada write_qr < sql/oms.sql
```

### 1.5 IntelliJ IDEA 설정

1. **프로젝트 열기**
   - File → Open → `order-management-system` 디렉토리 선택

2. **JDK 설정**
   - IntelliJ IDEA → Preferences → Project Structure → Project
   - SDK: openjdk-21 선택

3. **Kotlin 플러그인**
   - 기본으로 설치되어 있음
   - Preferences → Plugins → Kotlin 확인

4. **Gradle 설정**
   - Preferences → Build, Execution, Deployment → Build Tools → Gradle
   - "Gradle JVM": java 21로 설정

---

## 2. 프로젝트 실행

### 2.1 의존성 다운로드

```bash
# 프로젝트 루트에서
./gradlew clean build
# 또는
./gradlew build
```

### 2.2 애플리케이션 시작

#### IntelliJ IDEA에서

1. `src/main/kotlin/event/oms/OmsApplication.kt` 열기
2. `main()` 함수 왼쪽의 ▶️ 버튼 클릭
3. 또는 **Shift + F10** (Run 단축키)

#### 터미널에서

```bash
# 방법 1: Gradle 직접 실행
./gradlew bootRun

# 방법 2: JAR 빌드 후 실행
./gradlew clean build
java -jar build/libs/oms-0.0.1-SNAPSHOT.jar
```

### 2.3 애플리케이션 상태 확인

```bash
# 헬스 체크
curl http://localhost:8080/actuator/health

# API 목록 (Swagger)
open http://localhost:8080/swagger-ui.html
```

### 2.4 로그 확인

```bash
# 터미널 콘솔에서 로그 확인
# 또는 IntelliJ IDEA의 Run 탭에서 확인

# 로그 레벨 설정 (application.yml)
logging:
  level:
    event.oms: DEBUG
    org.springframework: INFO
```

---

## 3. 개발 워크플로우

### 3.1 기능 개발 프로세스

#### 1단계: 도메인 모델 작성

```kotlin
// 1. Domain Model 작성
// src/main/kotlin/event/oms/domain/model/xxx/NewEntity.kt

class NewEntity(
    val id: Long,
    var name: String,
    // 비즈니스 로직
    fun doSomething() { ... }
)
```

#### 2단계: Application 계층 작성

```kotlin
// 2-1. Inbound Port (UseCase 인터페이스)
// src/main/kotlin/event/oms/application/port/in/xxx/NewUseCase.kt
interface NewUseCase {
    fun create(command: NewCommand): NewEntity
}

// 2-2. Command 클래스
// src/main/kotlin/event/oms/application/port/in/xxx/NewCommand.kt
data class NewCommand(
    val field1: String,
    val field2: Int
)

// 2-3. Outbound Port (저장소 인터페이스)
// src/main/kotlin/event/oms/application/port/out/xxx/SaveNewPort.kt
interface SaveNewPort {
    fun save(entity: NewEntity): NewEntity
}

// 2-4. Service (UseCase 구현)
// src/main/kotlin/event/oms/application/service/xxx/NewService.kt
@Service
class NewService(
    private val saveNewPort: SaveNewPort
) : NewUseCase {
    override fun create(command: NewCommand): NewEntity {
        val entity = NewEntity(...)
        return saveNewPort.save(entity)
    }
}
```

#### 3단계: Adapter 계층 작성

```kotlin
// 3-1. Inbound Adapter (Controller)
// src/main/kotlin/event/oms/adapter/in/web/xxx/NewController.kt
@RestController
@RequestMapping("/api/v1/xxx")
class NewController(
    private val newUseCase: NewUseCase
) {
    @PostMapping
    fun create(@RequestBody request: NewRequest): ResponseEntity<BaseResponse<NewResponse>> {
        val command = NewCommand(...)
        val entity = newUseCase.create(command)
        return ResponseEntity.status(201).body(entity.toResponse())
    }
}

// 3-2. Request/Response DTO
// src/main/kotlin/event/oms/adapter/in/web/xxx/request/NewRequest.kt
data class NewRequest(
    val field1: String,
    val field2: Int
)

// 3-3. Outbound Adapter (Persistence)
// src/main/kotlin/event/oms/adapter/out/persistence/xxx/NewPersistenceAdapter.kt
@Component
class NewPersistenceAdapter(
    private val newJpaRepository: NewJpaRepository,
    private val newMapper: NewMapper
) : SaveNewPort {
    override fun save(entity: NewEntity): NewEntity {
        val newEntity = newMapper.toPersistence(entity)
        val saved = newJpaRepository.save(newEntity)
        return newMapper.toDomain(saved)
    }
}

// 3-4. JPA Entity
// src/main/kotlin/event/oms/adapter/out/persistence/xxx/entity/NewEntity.kt
@Entity
@Table(name = "new_table")
class NewEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val field1: String,
    val field2: Int
)

// 3-5. Mapper
// src/main/kotlin/event/oms/adapter/out/persistence/xxx/mapper/NewMapper.kt
@Component
class NewMapper {
    fun toDomain(entity: NewEntity): NewEntity {
        // 변환 로직
    }

    fun toPersistence(domain: NewEntity): NewEntity {
        // 변환 로직
    }
}

// 3-6. JPA Repository
// src/main/kotlin/event/oms/adapter/out/persistence/xxx/repository/NewJpaRepository.kt
interface NewJpaRepository : JpaRepository<NewEntity, Long>
```

### 3.2 기능 브랜치 생성

```bash
# 1. 새 브랜치 생성 (main에서)
git checkout main
git pull origin main
git checkout -b feature/기능-이름

# 예시
git checkout -b feature/주문-목록-조회
```

### 3.3 코드 작성 및 커밋

```bash
# 1. 파일 수정
# IDE에서 코드 작성

# 2. 변경사항 확인
git status
git diff

# 3. 파일 추가
git add .

# 4. 커밋
git commit -m "기능: 기능 설명"
# 예시
git commit -m "기능: 주문 목록 조회 API 추가"
```

### 3.4 테스트 작성

```bash
# 1. 테스트 파일 생성
# src/test/kotlin/event/oms/adapter/in/web/xxx/NewControllerTest.kt

# 2. 테스트 실행
./gradlew test

# 3. 특정 테스트만 실행
./gradlew test --tests "NewControllerTest"
```

### 3.5 Pull Request 생성

```bash
# 1. 원격 브랜치 푸시
git push origin feature/기능-이름

# 2. GitHub에서 PR 생성
# PR 제목: 기능: 기능 설명
# PR 본문: 상세 설명, 테스트 방법 등
```

---

## 4. 코드 작성 규칙

### 4.1 파일 이름 규칙

```
classes:        PascalCase (Order, OrderService, OrderEntity)
files:          PascalCase.kt (Order.kt, OrderService.kt)
packages:       lowercase (order, product, service)
constants:      UPPER_SNAKE_CASE (ORDER_TABLE_NAME)
variables:      camelCase (orderId, orderDate)
```

### 4.2 클래스 구조

```kotlin
// 1. 패키지와 임포트
package event.oms.domain.model.order

import java.math.BigDecimal
import java.time.LocalDateTime

// 2. 클래스 정의 (주석)
/**
 * 주문을 나타내는 도메인 모델
 * - 주문자 정보
 * - 주문 항목들
 * - 주문 상태
 */
class Order(
    // 3. 생성자 파라미터
    val id: Long? = null,
    val memberId: Long,
    val orderItems: List<OrderItem>,
    var status: OrderStatus,
    val orderDate: LocalDateTime,
    val receiverInfo: ReceiverInfo
) {
    // 4. 메서드들 (비즈니스 로직)
    fun getTotalPrice(): BigDecimal {
        return orderItems.sumOf { it.getTotalPrice() }
    }

    fun canCancel(): Boolean {
        return status == OrderStatus.PENDING
    }
}
```

### 4.3 Nullable vs Non-Nullable

```kotlin
// ❌ Nullable 피하기 (가능하면)
val orderId: Long? = null

// ✓ Non-nullable 사용
val orderId: Long = 1L

// ✓ Optional이 필요하면
val order: Order? = orderPort.findById(1)
order?.let { ... }
```

### 4.4 Exception 처리

```kotlin
// ❌ 일반적인 Exception 사용 피하기
throw Exception("오류 발생")

// ✓ 구체적인 Exception 사용
throw NoSuchElementException("주문을 찾을 수 없습니다: $orderId")
throw IllegalArgumentException("재고가 부족합니다")

// ✓ 커스텀 Exception (향후)
// throw OrderNotFoundException(orderId)
// throw InsufficientStockException(productName)
```

### 4.5 로깅

```kotlin
// ❌ System.out 사용 피하기
println("주문 생성 시작")

// ✓ Logger 사용
private val log = LoggerFactory.getLogger(javaClass)
log.info("주문 생성 시작 - memberId: $memberId")
log.error("주문 생성 실패", exception)
```

### 4.6 Data Class 사용

```kotlin
// Command (요청)
data class OrderCommand(
    val memberId: Long,
    val items: List<OrderItemCommand>,
    val receiverInfo: ReceiverInfoCommand
)

// Response (응답)
data class OrderResponse(
    val orderId: Long,
    val memberId: Long,
    val status: String,
    val orderDate: LocalDateTime
)

// Value Object (도메인)
data class ReceiverInfo(
    val name: String,
    val phone: String,
    val address: String
)
```

---

## 5. 테스트 작성 가이드

### 5.1 단위 테스트 (Unit Test)

```kotlin
// Controller 단위 테스트
@WebMvcTest(OrderController::class)
@Import(UseCaseTestConfiguration::class)
class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var orderUseCase: OrderUseCase

    @Test
    fun `createOrder - should return 201 when valid request`() {
        // given
        val request = OrderRequest(
            memberId = 100,
            items = listOf(OrderItemRequest(1001, 2)),
            receiverInfo = ReceiverInfoRequest("홍길동", "010-1234-5678", "서울시")
        )
        val order = Order(
            id = 1,
            memberId = 100,
            orderItems = listOf(OrderItem(1001, BigDecimal("15000"), 2)),
            status = OrderStatus.PENDING,
            orderDate = LocalDateTime.now(),
            receiverInfo = ReceiverInfo("홍길동", "010-1234-5678", "서울시")
        )
        given(orderUseCase.createOrder(any())).willReturn(order)

        // when
        val resultActions = mockMvc.perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value(201))
            .andExpect(jsonPath("$.data.orderId").value(1))
    }
}
```

### 5.2 통합 테스트 (Integration Test)

```kotlin
// 전체 계층 통합 테스트
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var orderJpaRepository: OrderJpaRepository

    @Autowired
    private lateinit var productJpaRepository: ProductJpaRepository

    @BeforeEach
    fun setUp() {
        orderJpaRepository.deleteAll()
        productJpaRepository.deleteAll()
    }

    @Test
    fun `createOrder - should save to database`() {
        // given
        val product = productJpaRepository.save(
            ProductEntity(1001, "테스트 상품", BigDecimal("15000"), 50)
        )
        val request = OrderRequest(
            memberId = 100,
            items = listOf(OrderItemRequest(1001, 2)),
            receiverInfo = ReceiverInfoRequest("홍길동", "010-1234-5678", "서울시")
        )

        // when
        mockMvc.perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated)

        // then
        val savedOrders = orderJpaRepository.findAll()
        assertEquals(1, savedOrders.size)
        assertEquals(100, savedOrders[0].memberId)
    }
}
```

### 5.3 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스만 실행
./gradlew test --tests OrderControllerTest

# 특정 테스트 메서드만 실행
./gradlew test --tests OrderControllerTest.createOrder*

# 테스트 리포트 보기
open build/reports/tests/test/index.html
```

### 5.4 테스트 작성 체크리스트

- [ ] 성공 케이스 작성
- [ ] 실패 케이스 작성 (404, 400 등)
- [ ] 엣지 케이스 작성 (비어있음, null 등)
- [ ] 비즈니스 규칙 테스트 (재고 부족 등)
- [ ] 데이터베이스 상태 검증
- [ ] 응답 형식 검증

---

## 6. 커밋 규칙

### 6.1 커밋 메시지 형식

```
<type>: <subject>

<body>

<footer>
```

### 6.2 Type 종류

- `기능`: 새로운 기능 추가
- `수정`: 버그 수정
- `개선`: 코드 개선/리팩토링
- `테스트`: 테스트 코드 추가
- `문서`: 문서 수정
- `설정`: 설정 파일 변경

### 6.3 커밋 메시지 예시

```bash
# 기능 추가
git commit -m "기능: 주문 목록 조회 API 구현"

# 버그 수정
git commit -m "수정: ProductControllerTest 테스트 코드 오류 수정"

# 코드 개선
git commit -m "개선: OrderService 리팩토링"

# 테스트 추가
git commit -m "테스트: OrderControllerIntegrationTest 추가"

# 문서 수정
git commit -m "문서: API 명세서 업데이트"
```

### 6.4 커밋 전 체크리스트

```bash
# 1. 변경사항 확인
git status
git diff

# 2. 테스트 실행
./gradlew test

# 3. 빌드 확인
./gradlew clean build

# 4. 파일 추가
git add .

# 5. 커밋
git commit -m "타입: 설명"

# 6. 푸시
git push origin 브랜치-이름
```

---

## 7. 문제 해결

### 7.1 빌드 실패

```bash
# 캐시 클리어 후 빌드
./gradlew clean build

# Gradle 데몬 종료
./gradlew --stop
./gradlew build
```

### 7.2 테스트 실패

```bash
# 테스트 로그 확인
./gradlew test --info

# 특정 테스트 디버그
./gradlew test --tests OrderControllerTest -d
```

### 7.3 데이터베이스 연결 실패

```bash
# MySQL 상태 확인
mysql -u armada -p armada -e "SELECT 1"

# 데이터베이스 재생성
mysql -u armada -p armada -e "DROP DATABASE write_qr; CREATE DATABASE write_qr;"
mysql -u armada -p armada write_qr < sql/oms.sql

# application.yml 설정 확인
```

### 7.4 포트 충돌 (8080 이미 사용)

```bash
# 8080 포트 사용하는 프로세스 확인 (macOS)
lsof -i :8080

# 프로세스 강제 종료
kill -9 <PID>

# 또는 다른 포트 사용
# application.yml에 추가
# server:
#   port: 8081
```

### 7.5 Kotlin 컴파일 에러

```bash
# IntelliJ IDEA 캐시 재빌드
File → Invalidate Caches → Invalidate and Restart

# 또는 터미널에서
./gradlew clean build --no-build-cache
```

---

## 8. 주요 개선 과제

### 8.1 우선순위 1: Order API 테스트 작성

**현재 상태**: 미작성

**필요한 작업**:
1. OrderControllerTest (단위 테스트) 작성
2. OrderControllerIntegrationTest (통합 테스트) 작성
3. 테스트 커버리지 80% 이상

**예상 시간**: 2-3시간

**관련 파일**:
- src/test/kotlin/event/oms/adapter/in/web/order/OrderControllerTest.kt
- src/test/kotlin/event/oms/adapter/in/web/order/OrderControllerIntegrationTest.kt

### 8.2 우선순위 2: 주문 목록 조회 API 완성

**현재 상태**: 개발 중

**필요한 작업**:
1. GetOrderListQuery 인터페이스 정의
2. GetOrderListService 구현
3. OrderPersistenceAdapter에서 findByMemberId() 구현
4. OrderController에 GET /api/v1/orders 엔드포인트 추가
5. 테스트 코드 작성

**예상 시간**: 1-2시간

**관련 파일**:
- src/main/kotlin/event/oms/application/port/in/order/GetOrderListQuery.kt
- src/main/kotlin/event/oms/application/service/order/GetOrderListService.kt
- src/main/kotlin/event/oms/adapter/in/web/order/OrderController.kt

### 8.3 우선순위 3: 입력 검증 강화

**현재 상태**: 기본 검증만 구현

**필요한 작업**:
1. @NotNull, @NotBlank, @Min 등 추가
2. custom validator 작성
3. Bean Validation 설정

**예상 시간**: 1시간

**예시**:
```kotlin
data class OrderItemRequest(
    @NotNull(message = "상품 ID는 필수입니다")
    val productId: Long,

    @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다")
    val quantity: Int
)
```

### 8.4 우선순위 4: 조회 성능 최적화

**현재 상태**: N+1 쿼리 가능성

**필요한 작업**:
1. JPA Fetch Join 적용
2. 쿼리 최적화
3. 인덱스 추가

**예상 시간**: 1-2시간

### 8.5 우선순위 5: 트랜잭션 관리

**현재 상태**: @Transactional 누락

**필요한 작업**:
1. Service 계층에 @Transactional 추가
2. 트랜잭션 격리 수준 설정
3. 롤백 규칙 정의

**예상 시간**: 30분

### 8.6 장기 과제: Spring Security 인증 추가

**필요한 작업**:
1. Spring Security 의존성 추가
2. JWT 토큰 인증 구현
3. 권한 검증 추가

**예상 시간**: 4-5시간

### 8.7 장기 과제: Redis 캐싱 활성화

**현재 상태**: 의존성만 추가, 미사용

**필요한 작업**:
1. Redis 서버 설정
2. @Cacheable, @CacheEvict 적용
3. 캐시 전략 수립

**예상 시간**: 3-4시간

---

## 추가 자료

### 코드 스타일 참고

- [Google Kotlin Style Guide](https://android.github.io/kotlin-guides/style.html)
- [Kotlin Official Style Guide](https://kotlinlang.org/docs/coding-conventions.html)

### 아키텍처 참고

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

### 테스트 참고

- [Spring Boot Testing Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

