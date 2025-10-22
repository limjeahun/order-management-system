# 주문 관리 시스템(OMS: Order Management System) - 프로젝트 분석 보고서

## 목차
1. [프로젝트 개요](#1-프로젝트-개요)
2. [프로젝트 구조](#2-프로젝트-구조)
3. [기술 스택](#3-기술-스택)
4. [아키텍처](#4-아키텍처)
5. [핵심 비즈니스 로직](#5-핵심-비즈니스-로직)
6. [데이터베이스](#6-데이터베이스)
7. [API 명세](#7-api-명세)
8. [테스트](#8-테스트)
9. [설정](#9-설정)
10. [개발 현황](#10-개발-현황)
11. [평가 및 개선사항](#11-평가-및-개선사항)

---

## 1. 프로젝트 개요

### 프로젝트 정보
- **프로젝트명**: OMS (Order Management System)
- **설명**: 주문 및 제품 관리 시스템
- **언어**: Kotlin 100%
- **빌드 도구**: Gradle 8.x (Kotlin DSL)
- **Spring Boot**: 3.5.6
- **Java**: 21
- **데이터베이스**: MySQL 8.0
- **상태**: 개발 중 (약 80% 완료)

### 프로젝트 목표
- 제품 관리 기능: 등록, 수정, 조회
- 주문 생성 및 관리 기능
- RESTful API 제공
- 재고 및 가격 관리

---

## 2. 프로젝트 구조

### 전체 디렉토리 레이아웃

```
order-management-system/
├── .git/                          # Git 버전 관리
├── .gradle/                       # Gradle 캐시
├── .idea/                         # IntelliJ IDEA 설정
├── build/                         # 빌드 산출물
├── docs/                          # 프로젝트 문서
├── gradle/                        # Gradle 래퍼
├── sql/                          # 데이터베이스 스크립트
│   └── oms.sql
├── src/
│   ├── main/
│   │   ├── kotlin/event/oms/
│   │   │   ├── OmsApplication.kt          # Spring Boot 진입점
│   │   │   ├── adapter/                   # Adapter 계층
│   │   │   ├── application/               # Application 계층
│   │   │   ├── domain/                    # Domain 계층
│   │   │   └── config/                    # 설정
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── kotlin/event/oms/
│           ├── OmsApplicationTests.kt
│           └── adapter/in/web/product/
├── build.gradle.kts               # Gradle 설정
├── settings.gradle.kts
└── gradlew / gradlew.bat
```

### 핵심 패키지 구조

```
event/oms/
├── adapter/                       # Adapter 계층
│   ├── in/web/                   # Inbound Adapters (Controllers)
│   │   ├── common/               # 공통 (BaseResponse, ExceptionHandler)
│   │   ├── order/                # Order API
│   │   └── product/              # Product API
│   └── out/persistence/          # Outbound Adapters (DB)
│       ├── order/                # Order 저장소
│       └── product/              # Product 저장소
├── application/                   # Application 계층
│   ├── port/
│   │   ├── in/                   # Inbound Ports (UseCase 인터페이스)
│   │   └── out/                  # Outbound Ports (저장소 인터페이스)
│   └── service/                  # UseCase 구현체
├── domain/                        # Domain 계층
│   ├── model/                    # Domain Model (Order, Product)
│   └── service/                  # Domain Service (OrderPriceCalculator)
└── config/                        # Spring 설정
```

---

## 3. 기술 스택

### 주요 의존성

| 분류 | 라이브러리 | 버전 | 용도 |
|-----|---------|------|------|
| **Web** | spring-boot-starter-web | 3.5.6 | REST API 구현 |
| **ORM** | spring-boot-starter-data-jpa | 3.5.6 | JPA/Hibernate ORM |
| **Database** | mysql-connector-j | 최신 | MySQL 드라이버 |
| **Validation** | spring-boot-starter-validation | 3.5.6 | Bean Validation (@Valid) |
| **API Docs** | springdoc-openapi-starter-webmvc-ui | 2.8.13 | Swagger/OpenAPI |
| **Kotlin** | kotlin-stdlib | 1.9.25 | Kotlin 표준 라이브러리 |
| **Test** | spring-boot-starter-test | 3.5.6 | JUnit 5, Mockito |
| **Redis** | spring-boot-starter-data-redis | 3.5.6 | 캐싱 (미사용) |

### 빌드 플러그인

```kotlin
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}
```

**플러그인 역할**:
- `kotlin("plugin.spring")`: Spring 클래스 전개(all-open)
- `kotlin("plugin.jpa")`: JPA Entity 자동 open, 기본 생성자 생성

---

## 4. 아키텍처

### 4.1 헥사고날 아키텍처 (Hexagonal Architecture)

이 프로젝트는 **헥사고날 아키텍처(포트-어댑터 패턴)**를 따릅니다.

```
┌─────────────────────────────────────────────────┐
│         Adapter 계층 (외부 계층)                  │
│  ┌──────────────────────────────────────────┐  │
│  │ Inbound Adapters                         │  │
│  │ - OrderController, ProductController     │  │
│  └──────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────┐  │
│  │ Outbound Adapters                        │  │
│  │ - OrderPersistenceAdapter                │  │
│  │ - ProductPersistenceAdapter              │  │
│  │ - JPA Repositories                       │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
         ↓ Port (의존성 역전)
┌─────────────────────────────────────────────────┐
│      Application 계층 (비즈니스 로직)             │
│  ┌──────────────────────────────────────────┐  │
│  │ Inbound Ports                            │  │
│  │ - OrderUseCase, GetOrderQuery            │  │
│  │ - AddProductUseCase, GetProductListQuery │  │
│  └──────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────┐  │
│  │ Services (UseCase 구현)                  │  │
│  │ - OrderService, GetOrderService          │  │
│  │ - AddProductService, UpdateProductService│ │
│  └──────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────┐  │
│  │ Outbound Ports                           │  │
│  │ - SaveOrderPort, LoadOrderPort           │  │
│  │ - SaveProductPort, LoadProductPort       │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
         ↓ Domain Model
┌─────────────────────────────────────────────────┐
│      Domain 계층 (핵심 비즈니스)                 │
│  ┌──────────────────────────────────────────┐  │
│  │ Domain Models                            │  │
│  │ - Order, OrderItem, Product              │  │
│  │ - OrderStatus, ReceiverInfo              │  │
│  └──────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────┐  │
│  │ Domain Services                          │  │
│  │ - OrderPriceCalculator                   │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

### 4.2 의존성 흐름

```
HTTP Request
    ↓
Controller (Adapter)
    ↓ (요청 변환)
UseCase/Query Interface (Inbound Port)
    ↓
Service (구현체)
    ↓ (포트 호출)
Outbound Port Interface
    ↓
Persistence Adapter
    ↓
JPA Repository
    ↓
Database
```

---

## 5. 핵심 비즈니스 로직

### 5.1 제품 관리

#### 제품 등록 (AddProductService)

```
POST /api/v1/products
  ↓ AddProductRequest
  ↓ AddProductCommand 변환
  ↓ AddProductUseCase.addProduct()
    1. Product 도메인 객체 생성
    2. 유효성 검증 (price > 0, stock >= 0)
    3. SaveProductPort를 통해 저장
  ↓ ProductResponse 반환
  ↓ 201 Created
```

**검증 규칙**:
- 가격(price) > 0
- 재고(stock) >= 0

#### 제품 수정 (UpdateProductService)

```
PUT /api/v1/products/{productId}
  ↓ UpdateProductRequest
  ↓ UpdateProductCommand 변환
  ↓ UpdateProductUseCase.updateProduct()
    1. LoadProductPort로 기존 제품 조회
    2. Product.updateDetails() 호출
    3. 유효성 검증 수행
    4. SaveProductPort로 저장
  ↓ ProductResponse 반환
  ↓ 200 OK
```

#### 제품 목록 조회 (GetProductListService)

```
GET /api/v1/products
  ↓ GetProductListQuery.getProductList()
    1. LoadProductPort.findAll() 호출
  ↓ List<Product> 반환
  ↓ List<ProductResponse> 변환
  ↓ 200 OK
```

### 5.2 주문 관리

#### 주문 생성 (OrderService)

```
POST /api/v1/orders
  ↓ OrderRequest
  ↓ OrderCommand 변환
  ↓ OrderUseCase.createOrder()
    1. 주문 항목 목록에서 productId 추출
    2. LoadProductPort.findAllByIds()로 모든 제품 조회
    3. OrderPriceCalculator.calculateOrderItems()
       - 각 상품의 재고 확인
       - 현재 가격 적용
    4. Order 도메인 객체 생성
       - status = PENDING
       - orderDate = LocalDateTime.now()
    5. SaveOrderPort.save() 호출
  ↓ OrderResponse 반환
  ↓ 201 Created
```

**OrderPriceCalculator 로직**:
```kotlin
fun calculateOrderItems(
    itemRequests: List<OrderItemCommand>,
    products: List<Product>
): List<OrderItem> {
    return itemRequests.map { request ->
        val product = products.find { it.id == request.productId }
            ?: throw NoSuchElementException("제품 정보를 찾을 수 없습니다")

        // 재고 검증
        if (product.stock < request.quantity) {
            throw IllegalArgumentException(
                "재고가 부족합니다: ${product.name}"
            )
        }

        // OrderItem 생성 (현재 상품 가격 적용)
        OrderItem(
            productId = request.productId,
            price = product.price,
            quantity = request.quantity
        )
    }
}
```

#### 주문 상세 조회 (GetOrderService)

```
GET /api/v1/orders/{orderId}
  ↓ GetOrderQuery.getOrder(orderId)
    1. LoadOrderPort.findOrderById(orderId) 호출
    2. 없으면 NoSuchElementException 발생
  ↓ Order 반환
  ↓ OrderResponse 변환
  ↓ 200 OK or 404 Not Found
```

#### 주문 목록 조회 (진행 중)

```
GET /api/v1/orders?memberId={memberId}
  ↓ GetOrderListQuery.getOrderList(memberId)
    1. LoadOrderPort.findByMemberId(memberId) 호출
  ↓ List<Order> 반환
  ↓ List<OrderResponse> 변환
  ↓ 200 OK
```

### 5.3 도메인 모델

#### Order (주문)

```kotlin
class Order(
    val id: Long? = null,
    val memberId: Long,                 // 주문자 ID
    val orderItems: List<OrderItem>,    // 주문 항목
    var status: OrderStatus,            // PENDING, PAID, SHIPPED, CANCELLED
    val orderDate: LocalDateTime,       // 주문 일시
    val receiverInfo: ReceiverInfo      // 수령인 정보
)

enum class OrderStatus {
    PENDING,    // 결제 대기
    PAID,       // 결제 완료
    SHIPPED,    // 배송됨
    CANCELLED   // 주문 취소
}

data class ReceiverInfo(
    val name: String,
    val phone: String,
    val address: String
)
```

#### OrderItem (주문 항목)

```kotlin
class OrderItem(
    val id: Long? = null,
    val orderId: Long? = null,
    val productId: Long,
    val price: BigDecimal,              // 주문 시점의 상품 가격
    val quantity: Int                   // 주문 수량
)
```

#### Product (제품)

```kotlin
class Product(
    val id: Long,
    var name: String,
    var price: BigDecimal,
    var stock: Int                      // 남은 재고
) {
    fun updateDetails(
        changeName: String,
        changePrice: BigDecimal,
        changeStock: Int
    ) {
        if (changePrice <= BigDecimal.ZERO)
            throw IllegalArgumentException("상품 가격은 0보다 커야 합니다.")
        if (changeStock < 0)
            throw IllegalArgumentException("재고를 입력해 주세요.")

        this.name = changeName
        this.price = changePrice
        this.stock = changeStock
    }
}
```

---

## 6. 데이터베이스

### 6.1 데이터베이스 스키마

#### orders 테이블

```sql
CREATE TABLE orders (
    id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT NOT NULL,                    -- 주문자 ID
    status           VARCHAR(255) NOT NULL,             -- PENDING, PAID, SHIPPED, CANCELLED
    order_date       DATETIME(6) NOT NULL,              -- 주문 일시
    receiver_name    VARCHAR(255) NOT NULL,            -- 수령인 이름
    receiver_phone   VARCHAR(255) NOT NULL,            -- 수령인 전화
    receiver_address VARCHAR(255) NOT NULL             -- 수령인 주소
) ENGINE=InnoDB;
```

#### order_item 테이블

```sql
CREATE TABLE order_item (
    id         BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL,                         -- orders.id 참조
    product_id BIGINT NOT NULL,                         -- product.id 참조
    price      DECIMAL(19, 2) NOT NULL,                -- 주문 시점의 상품 가격
    quantity   INT NOT NULL                            -- 주문 수량
) ENGINE=InnoDB;
```

#### product 테이블

```sql
CREATE TABLE product (
    id    BIGINT NOT NULL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,                       -- 제품명
    price DECIMAL(19, 2) NOT NULL,                     -- 현재 판매 가격
    stock BIGINT NOT NULL                              -- 남은 재고
) ENGINE=InnoDB;
```

### 6.2 JPA 엔티티 매핑

#### OrderEntity

```kotlin
@Entity
@Table(name = "orders")
class OrderEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val memberId: Long,
    @Enumerated(EnumType.STRING)
    var status: OrderStatus,
    val orderDate: LocalDateTime,
    @Embedded
    val receiverInfo: ReceiverInfoEmbeddable
)
```

#### ReceiverInfoEmbeddable

```kotlin
@Embeddable
class ReceiverInfoEmbeddable(
    val receiverName: String,
    val receiverPhone: String,
    val receiverAddress: String
)
```

#### OrderItemEntity

```kotlin
@Entity
@Table(name = "order_item")
class OrderItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val orderId: Long,
    val productId: Long,
    val price: BigDecimal,
    val quantity: Int
)
```

#### ProductEntity

```kotlin
@Entity
@Table(name = "product")
class ProductEntity(
    @Id
    val id: Long,
    var name: String,
    var price: BigDecimal,
    val stock: Int
)
```

---

## 7. API 명세

### 7.1 제품 API

#### 제품 등록

```http
POST /api/v1/products
Content-Type: application/json

Request:
{
  "id": 1001,
  "name": "테스트 상품",
  "price": 15000.00,
  "stock": 50
}

Response (201 Created):
{
  "code": 201,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": {
    "productId": 1001,
    "name": "테스트 상품",
    "price": 15000.00,
    "stock": 50
  }
}
```

#### 제품 수정

```http
PUT /api/v1/products/{productId}
Content-Type: application/json

Request:
{
  "name": "수정된 상품명",
  "price": 20000.00,
  "stock": 100
}

Response (200 OK):
{
  "code": 200,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": {
    "productId": 1001,
    "name": "수정된 상품명",
    "price": 20000.00,
    "stock": 100
  }
}
```

#### 제품 목록 조회

```http
GET /api/v1/products

Response (200 OK):
{
  "code": 200,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": [
    {
      "productId": 1001,
      "name": "상품1",
      "price": 15000.00,
      "stock": 50
    },
    {
      "productId": 1002,
      "name": "상품2",
      "price": 25000.00,
      "stock": 30
    }
  ]
}
```

### 7.2 주문 API

#### 주문 생성

```http
POST /api/v1/orders
Content-Type: application/json

Request:
{
  "memberId": 100,
  "items": [
    {
      "productId": 1001,
      "quantity": 2
    },
    {
      "productId": 1002,
      "quantity": 1
    }
  ],
  "receiverInfo": {
    "name": "홍길동",
    "phone": "010-1234-5678",
    "address": "서울시 강남구"
  }
}

Response (201 Created):
{
  "code": 201,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": {
    "orderId": 1,
    "memberId": 100,
    "status": "PENDING",
    "orderDate": "2025-10-22T14:30:00",
    "items": [
      {
        "productId": 1001,
        "price": 15000.00,
        "quantity": 2
      },
      {
        "productId": 1002,
        "price": 25000.00,
        "quantity": 1
      }
    ],
    "receiverInfo": {
      "name": "홍길동",
      "phone": "010-1234-5678",
      "address": "서울시 강남구"
    }
  }
}
```

#### 주문 상세 조회

```http
GET /api/v1/orders/{orderId}

Response (200 OK):
{
  "code": 200,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": {
    "orderId": 1,
    "memberId": 100,
    "status": "PENDING",
    "orderDate": "2025-10-22T14:30:00",
    "items": [
      {
        "productId": 1001,
        "price": 15000.00,
        "quantity": 2
      }
    ],
    "receiverInfo": {
      "name": "홍길동",
      "phone": "010-1234-5678",
      "address": "서울시 강남구"
    }
  }
}

Response (404 Not Found):
{
  "code": 404,
  "message": "요청하신 자원을 찾을 수 없습니다."
}
```

### 7.3 Swagger API 문서

- **URL**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **분류**:
  - Group 1: `orders` - 주문 API
  - Group 2: `products` - 제품 API

---

## 8. 테스트

### 8.1 테스트 전략

프로젝트는 다층 테스트 전략을 사용합니다:

1. **단위 테스트 (Unit Test)**: Mock을 사용한 컨트롤러 테스트
2. **통합 테스트 (Integration Test)**: 실제 DB와 함께 전체 계층 테스트

### 8.2 ProductControllerTest (단위 테스트)

```kotlin
@WebMvcTest(ProductController::class)
@Import(UseCaseTestConfiguration::class)
class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var addProductUseCase: AddProductUseCase

    // 테스트 케이스 1: 상품 등록 성공 (201 Created)
    @Test
    fun `addProduct - should return 201 Created`() {
        // given
        val productId = 1001L
        val request = AddProductRequest(
            id = productId,
            name = "테스트 상품",
            price = BigDecimal("15000"),
            stock = 50
        )
        val product = Product(productId, "테스트 상품", BigDecimal("15000"), 50)
        given(addProductUseCase.addProduct(any())).willReturn(product)

        // when
        val resultActions = mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value(201))
            .andExpect(jsonPath("$.data.productId").value(productId))
    }

    // 테스트 케이스 2: 유효성 검증 실패 (400 Bad Request)
    @Test
    fun `addProduct - should return 400 when invalid request`() {
        val invalidRequest = AddProductRequest(
            id = 456L,
            name = "",  // 필수 필드 비어있음
            price = BigDecimal("50.00"),
            stock = 5
        )

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
        .andExpect(status().isBadRequest)
    }
}
```

### 8.3 ProductControllerIntegrationTest (통합 테스트)

```kotlin
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var productJpaRepository: ProductJpaRepository

    @BeforeEach
    fun setUp() {
        productJpaRepository.deleteAll()
    }

    @Test
    fun `addProduct - should save to database`() {
        // given
        val productId = 5312040002L
        val request = AddProductRequest(
            id = productId,
            name = "Marlboro Medium",
            price = BigDecimal("5000"),
            stock = 300
        )

        // when
        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated)

        // then
        val savedProduct = productJpaRepository.findById(productId)
        assertTrue(savedProduct.isPresent)
        savedProduct.ifPresent { entity ->
            assertEquals(request.name, entity.name)
            assertEquals(request.price, entity.price)
            assertEquals(request.stock, entity.stock)
        }
    }
}
```

### 8.4 테스트 설정

```kotlin
@TestConfiguration
class UseCaseTestConfiguration {
    @Bean
    fun addProductUseCase(): AddProductUseCase {
        return Mockito.mock(AddProductUseCase::class.java)
    }

    @Bean
    fun updateProductUseCase(): UpdateProductUseCase {
        return Mockito.mock(UpdateProductUseCase::class.java)
    }

    @Bean
    fun getProductListQuery(): GetProductListQuery {
        return Mockito.mock(GetProductListQuery::class.java)
    }
}
```

### 8.5 테스트 커버리지

| 모듈 | 상태 |
|-----|------|
| Product API (Unit) | ✓ 완성 |
| Product API (Integration) | ✓ 완성 |
| Order API (Unit) | ❌ 미작성 |
| Order API (Integration) | ❌ 미작성 |

---

## 9. 설정

### 9.1 application.yml

```yaml
spring:
  application:
    name: oms

  # 데이터베이스 설정
  datasource:
    url: jdbc:mysql://localhost:3306/write_qr?
         useSSL=false&serverTimezone=Asia/Seoul&
         characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: armada
    password: armada
    driver-class-name: com.mysql.cj.jdbc.Driver

  # JPA & Hibernate 설정
  jpa:
    hibernate:
      ddl-auto: none                    # 운영 환경
    show-sql: true                      # SQL 콘솔 출력
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true                # SQL 포맷팅
        use_sql_comments: true          # SQL 주석

# 로깅 설정
logging:
  level:
    event.oms: DEBUG
    org.springframework: INFO
    org.hibernate.orm.jdbc.bind: TRACE
```

### 9.2 build.gradle.kts

```kotlin
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "event"
version = "0.0.1-SNAPSHOT"
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Database
    runtimeOnly("com.mysql:mysql-connector-j")

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

---

## 10. 개발 현황

### 10.1 최근 커밋 히스토리

| 커밋 | 메시지 | 상태 |
|-----|--------|------|
| d0dc5eb | 주문 목록 조회 개발 중 | 🔄 진행 중 |
| 6b17dda | ProductControllerTest 테스트 코드 오류 수정 | ✓ 완성 |
| 3ac6c47 | 제품 Controller 테스트 코드 작성 | ✓ 완성 |
| efe7f7d | 제품 목록 조회 추가 | ✓ 완성 |
| 5e203f7 | Swagger 적용 | ✓ 완성 |

### 10.2 기능 완성도

```
전체:           ████████░░ (약 80%)
Product:        ██████████ (100%)
Order:          ████████░░ (약 80%)
Test:           ████░░░░░░ (약 40%)
Documentation:  ████████░░ (약 80%)
```

### 10.3 완료된 기능 (✓)

1. Spring Boot 3.5.6 + Kotlin 프로젝트 초기 설정
2. 헥사고날 아키텍처 구조 설계
3. Product 도메인 모델 및 API 전체 구현
4. Order 도메인 모델 및 기본 API 구현
5. Product API 테스트 코드 (Unit + Integration)
6. 공통 예외 처리 (GlobalExceptionHandler)
7. Swagger/OpenAPI 문서

### 10.4 진행 중인 기능 (🔄)

1. 주문 목록 조회 API 개발 중

### 10.5 미구현 기능 (❌)

1. Order API 테스트 코드
2. 결제 처리 기능
3. 배송 상태 업데이트
4. 주문 취소 기능
5. Redis 캐싱
6. Spring Security 인증/인가

---

## 11. 평가 및 개선사항

### 11.1 강점 분석

✓ **헥사고날 아키텍처**: Port/Adapter 패턴을 올바르게 적용
- 계층 간 의존성이 역전되어 있음
- 외부 의존성이 내부로 주입됨

✓ **명확한 계층 분리**: Adapter/Application/Domain이 뚜렷함
- 각 계층의 책임이 명확함
- 테스트 용이성이 높음

✓ **도메인 로직 집중**: OrderPriceCalculator로 재고/가격 검증 분리
- 도메인 서비스가 비즈니스 로직을 담당
- 재사용성 높음

✓ **포괄적인 검증**: 여러 레이어에서 검증
- 입력 검증 (@Valid)
- 도메인 레벨 검증
- 비즈니스 로직 검증

✓ **문서화**: Swagger/OpenAPI로 API 자동 문서화
- 개발자 경험 향상
- 클라이언트 개발 용이

✓ **테스트 전략**: Unit Test와 Integration Test 모두 작성
- Mock을 활용한 빠른 단위 테스트
- 실제 DB 환경 통합 테스트

### 11.2 개선 사항

#### 1. 테스트 커버리지 증대 필요

**현재 상태**:
- Product API 테스트: 완성
- Order API 테스트: 미작성

**권장사항**:
```kotlin
// OrderControllerTest 작성 필요
@WebMvcTest(OrderController::class)
class OrderControllerTest {
    @Test
    fun `createOrder - should return 201`() { ... }

    @Test
    fun `createOrder - should fail when insufficient stock`() { ... }

    @Test
    fun `getOrder - should return 200`() { ... }
}
```

#### 2. 입력 검증 강화

**현재 상태**: 일부 필드에 @NotNull/@NotBlank 누락

**권장사항**:
```kotlin
// OrderItemRequest 개선
data class OrderItemRequest(
    @NotNull(message = "상품 ID는 필수입니다")
    val productId: Long,

    @Min(value = 1, message = "주문 수량은 최소 1개 이상이어야 합니다")
    val quantity: Int
)
```

#### 3. 조회 성능 최적화

**현재 상태**: N+1 쿼리 문제 가능성

**권장사항**:
```kotlin
// OrderItemEntity 조회 최적화
@Entity
@Table(name = "order_item")
class OrderItemEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val order: OrderEntity,  // 관계 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: ProductEntity  // 제품 정보 미리 로드
)
```

#### 4. 트랜잭션 관리

**현재 상태**: 명시적 @Transactional 없음

**권장사항**:
```kotlin
@Service
@Transactional  // 모든 비즈니스 로직에 적용
class OrderService(
    private val saveOrderPort: SaveOrderPort,
    private val loadProductPort: LoadProductPort
) : OrderUseCase {
    override fun createOrder(command: OrderCommand): Order {
        // 트랜잭션 내에서 실행
    }
}
```

#### 5. 에러 처리 상세화

**현재 상태**: 기본적인 예외 처리

**권장사항**:
```kotlin
// 커스텀 예외 클래스
sealed class OrderException(message: String) : RuntimeException(message)

class InsufficientStockException(productName: String) :
    OrderException("재고가 부족합니다: $productName")

class ProductNotFoundException(productId: Long) :
    OrderException("상품을 찾을 수 없습니다: $productId")
```

#### 6. 도메인 서비스 확장

**현재 상태**: OrderPriceCalculator만 존재

**권장사항**:
```kotlin
// OrderStatusService: 주문 상태 전이 로직
interface OrderStatusService {
    fun canTransitionTo(currentStatus: OrderStatus, nextStatus: OrderStatus): Boolean
}

// OrderDiscountService: 할인 정책
interface OrderDiscountService {
    fun calculateDiscount(order: Order): BigDecimal
}
```

#### 7. 페이지네이션 구현

**현재 상태**: 전체 목록 조회만 가능

**권장사항**:
```kotlin
// Pageable 지원
GET /api/v1/orders?memberId={memberId}&page=0&size=10&sort=orderDate,desc

interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findByMemberId(memberId: Long, pageable: Pageable): Page<OrderEntity>
}
```

#### 8. 로깅 추가

**현재 상태**: 로깅 거의 없음

**권장사항**:
```kotlin
@Service
class OrderService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun createOrder(command: OrderCommand): Order {
        log.info("주문 생성 시작 - memberId: ${command.memberId}, itemCount: ${command.items.size}")
        try {
            // ...
            log.info("주문 생성 완료 - orderId: ${order.id}")
        } catch (e: Exception) {
            log.error("주문 생성 실패", e)
            throw e
        }
    }
}
```

#### 9. Redis 캐싱 활성화

**현재 상태**: 의존성 추가만 됨

**권장사항**:
```kotlin
@Service
@CacheConfig(cacheNames = ["products"])
class GetProductListService {
    @Cacheable(key = "'all'")
    override fun getProductList(): List<Product> {
        return loadProductPort.findAll()
    }
}
```

#### 10. 마이그레이션 도구 도입

**현재 상태**: Flyway/Liquibase 미적용

**권장사항**:
```
src/main/resources/db/migration/
├── V1__Create_tables.sql
├── V2__Add_indexes.sql
└── V3__Add_constraints.sql
```

### 11.3 기술 스택 평가

| 항목 | 평가 | 의견 |
|-----|------|------|
| Spring Boot 3.5.6 | ✓ 최신 | 최신 안정화 버전 사용 |
| Kotlin | ✓ 효율적 | 안전한 언어, Null 안정성 |
| Gradle Kotlin DSL | ✓ 현대적 | 타입 안전한 빌드 설정 |
| MySQL 8.0 | ✓ 안정적 | 프로덕션 레벨 DB |
| JPA/Hibernate | ✓ 추천 | ORM으로 데이터 접근 추상화 |
| Swagger 2.8.13 | ✓ 최신 | OpenAPI 3.0 지원 |

---

## 요약

### 프로젝트 상태
- **진행도**: 약 80% 완료
- **품질**: 높음 (헥사고날 아키텍처 올바르게 적용)
- **테스트**: 부분적 (Product 완성, Order 미작성)
- **문서화**: 양호 (Swagger 적용)

### 핵심 강점
1. 명확한 아키텍처와 계층 분리
2. 포트-어댑터 패턴 올바른 적용
3. 포괄적인 검증과 예외 처리
4. 좋은 테스트 기반 구축

### 우선 개선 과제
1. Order API 테스트 코드 작성
2. 입력 검증 강화
3. 트랜잭션 관리 명확화
4. 조회 성능 최적화

