# 주문 관리 시스템 아키텍처 상세 설명서

## 목차
1. [아키텍처 개요](#1-아키텍처-개요)
2. [헥사고날 아키텍처](#2-헥사고날-아키텍처)
3. [계층별 상세 설명](#3-계층별-상세-설명)
4. [데이터 흐름](#4-데이터-흐름)
5. [의존성 역전 원칙](#5-의존성-역전-원칙)
6. [패키지 구조 매핑](#6-패키지-구조-매핑)
7. [주요 패턴](#7-주요-패턴)

---

## 1. 아키텍처 개요

이 프로젝트는 **헥사고날 아키텍처(Hexagonal Architecture)** 또는 **포트-어댑터 아키텍처(Ports and Adapters Architecture)**를 따릅니다.

### 핵심 원칙
1. **역계층 의존성**: 외부 계층이 내부 계층에 의존 (역방향)
2. **포트 인터페이스**: 내부 계층은 포트로 외부와 통신
3. **어댑터 구현**: 외부 계층은 어댑터로 포트 구현
4. **도메인 보호**: 핵심 비즈니스 로직은 프레임워크와 무관

### 아키텍처 다이어그램

```
┌────────────────────────────────────────────────────────────────┐
│                     외부 계층 (ADAPTERS)                        │
│  ┌────────────────────────────────────────────────────────┐   │
│  │        Inbound Adapters (진입점)                       │   │
│  │  ┌─────────────────┐  ┌──────────────────┐           │   │
│  │  │ REST Controller │  │ GraphQL Resolver │  (미사용)  │   │
│  │  │   (HTTP API)    │  │                  │           │   │
│  │  └────────┬────────┘  └──────────────────┘           │   │
│  │           │                                           │   │
│  │           ▼                                           │   │
│  │  ┌─────────────────────────────────────┐            │   │
│  │  │  Request/Response DTO 변환          │            │   │
│  │  │  (OrderRequest → OrderCommand)      │            │   │
│  │  └────────────────────────────────────┘            │   │
│  │                                                       │   │
│  │  ┌─────────────────────────────────────────────┐   │   │
│  │  │        Outbound Adapters (출력)             │   │   │
│  │  │  ┌──────────────────┐  ┌────────────────┐  │   │   │
│  │  │  │ JPA Repository   │  │  Redis Cache   │  │   │   │
│  │  │  │ (데이터 접근)    │  │  (미사용)      │  │   │   │
│  │  │  └──────────────────┘  └────────────────┘  │   │   │
│  │  └─────────────────────────────────────────────┘   │   │
│  └────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────┘
             │                                    │
             │ Ports (인터페이스)                 │
             ▼                                    ▼
┌────────────────────────────────────────────────────────────────┐
│                  애플리케이션 계층 (APPLICATION)                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │         Inbound Ports (UseCase 인터페이스)             │   │
│  │  ┌─────────────────┐  ┌────────────────┐             │   │
│  │  │  OrderUseCase   │  │ GetOrderQuery  │ (인터페이스) │   │
│  │  │  (주문 생성)    │  │ (주문 조회)    │             │   │
│  │  └─────────────────┘  └────────────────┘             │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                │
│  ┌────────────────────────────────────────────────────────┐   │
│  │           Services (UseCase 구현체)                    │   │
│  │  ┌──────────────────────────────────────┐            │   │
│  │  │ OrderService                         │            │   │
│  │  │ GetOrderService                      │            │   │
│  │  │ GetOrderListService                  │            │   │
│  │  │ AddProductService                    │            │   │
│  │  │ UpdateProductService                 │            │   │
│  │  │ GetProductListService                │            │   │
│  │  └──────────────────────────────────────┘            │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                │
│  ┌────────────────────────────────────────────────────────┐   │
│  │         Outbound Ports (저장소 인터페이스)             │   │
│  │  ┌─────────────────┐  ┌────────────────┐             │   │
│  │  │ SaveOrderPort   │  │ LoadOrderPort  │ (인터페이스) │   │
│  │  │ SaveProductPort │  │ LoadProductPort│             │   │
│  │  └─────────────────┘  └────────────────┘             │   │
│  └────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────┘
             │                                    │
             │ Domain Models                      │
             ▼                                    ▼
┌────────────────────────────────────────────────────────────────┐
│                     도메인 계층 (DOMAIN)                        │
│  ┌────────────────────────────────────────────────────────┐   │
│  │         Domain Models (엔티티)                         │   │
│  │  ┌──────────────┐  ┌─────────────┐                   │   │
│  │  │ Order        │  │ Product     │                   │   │
│  │  │ OrderItem    │  │ OrderStatus │                   │   │
│  │  │ ReceiverInfo │  │             │                   │   │
│  │  └──────────────┘  └─────────────┘                   │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                │
│  ┌────────────────────────────────────────────────────────┐   │
│  │         Domain Services (비즈니스 로직)                │   │
│  │  ┌──────────────────────────────────────┐            │   │
│  │  │ OrderPriceCalculator                 │            │   │
│  │  │ (재고/가격 검증 및 계산)             │            │   │
│  │  └──────────────────────────────────────┘            │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                │
│  ┌────────────────────────────────────────────────────────┐   │
│  │         Value Objects                                 │   │
│  │  ┌──────────────────────────────────────┐            │   │
│  │  │ ReceiverInfo (불변 객체)             │            │   │
│  │  └──────────────────────────────────────┘            │   │
│  └────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────┘
```

---

## 2. 헥사고날 아키텍처

### 2.1 핵심 개념

#### 목표: 비즈니스 로직을 외부 의존성으로부터 보호

```
       외부 의존성
      (DB, API, 웹)
           │
           ▼
        ┌─────┐
        │어댑터│  ◄──── 외부 기술과의 통신
        └──┬──┘
           │
           ▼ Port Interface
        ┌──────────┐
        │비즈니스  │  ◄──── 비즈니스 로직
        │로직      │         (DB나 프레임워크 무관)
        └──────────┘
```

### 2.2 포트(Port)

포트는 **인터페이스**로, 내부 계층이 외부와 통신하는 방식을 정의합니다.

#### Inbound Port (진입 포트)
```kotlin
// UseCase 인터페이스: 외부에서 내부로 들어오는 요청
interface OrderUseCase {
    fun createOrder(command: OrderCommand): Order
}

interface GetOrderQuery {
    fun getOrder(orderId: Long): Order
}
```

#### Outbound Port (출력 포트)
```kotlin
// 저장소 인터페이스: 내부가 외부와 통신하는 방식
interface SaveOrderPort {
    fun save(order: Order): Order
}

interface LoadOrderPort {
    fun findOrderById(orderId: Long): Order?
    fun findAllByIds(ids: List<Long>): List<Order>
}
```

### 2.3 어댑터(Adapter)

어댑터는 포트 인터페이스를 **구현**하여 실제 기술을 적용합니다.

#### Inbound Adapter (진입 어댑터)
```kotlin
@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderUseCase: OrderUseCase,  // Inbound Port
    private val getOrderService: GetOrderQuery  // Inbound Port
) {
    @PostMapping
    fun createOrder(@RequestBody request: OrderRequest): ResponseEntity<OrderResponse> {
        // 1. Request DTO → Command 변환 (변환 로직)
        val command = request.toCommand()

        // 2. Inbound Port 호출
        val order = orderUseCase.createOrder(command)

        // 3. Order → Response DTO 변환
        return ResponseEntity.status(201).body(order.toResponse())
    }
}
```

#### Outbound Adapter (출력 어댑터)
```kotlin
@Component
class OrderPersistenceAdapter(
    private val orderJpaRepository: OrderJpaRepository,
    private val orderItemJpaRepository: OrderItemJpaRepository,
    private val orderMapper: OrderMapper
) : SaveOrderPort, LoadOrderPort {

    // SaveOrderPort 구현
    override fun save(order: Order): Order {
        val orderEntity = orderMapper.toPersistence(order)
        val savedEntity = orderJpaRepository.save(orderEntity)
        return orderMapper.toDomain(savedEntity)
    }

    // LoadOrderPort 구현
    override fun findOrderById(orderId: Long): Order? {
        return orderJpaRepository.findById(orderId)
            ?.let { orderMapper.toDomain(it) }
    }
}
```

---

## 3. 계층별 상세 설명

### 3.1 Domain Layer (도메인 계층)

**책임**: 핵심 비즈니스 로직과 엔티티

```
┌─────────────────────────────────────────┐
│        Domain Layer                     │
├─────────────────────────────────────────┤
│                                         │
│  Domain Models:                         │
│  ├── Order (주문 도메인)                │
│  ├── OrderItem (주문항목)               │
│  ├── Product (제품)                     │
│  ├── OrderStatus (상태 enum)            │
│  └── ReceiverInfo (수령인 VO)           │
│                                         │
│  Domain Services:                       │
│  └── OrderPriceCalculator (비즈니스 로직)│
│                                         │
│  특징:                                  │
│  ✓ 프레임워크 무관 (순수 Kotlin)        │
│  ✓ 비즈니스 규칙만 포함                 │
│  ✓ 외부 의존성 없음                     │
│  ✓ 테스트 용이                          │
│                                         │
└─────────────────────────────────────────┘
```

**예시**: Order 도메인 모델

```kotlin
// 순수 비즈니스 로직만 포함
class Order(
    val id: Long? = null,
    val memberId: Long,
    val orderItems: List<OrderItem>,
    var status: OrderStatus,
    val orderDate: LocalDateTime,
    val receiverInfo: ReceiverInfo
) {
    fun getTotalPrice(): BigDecimal {
        return orderItems.sumOf { it.price * it.quantity.toBigDecimal() }
    }

    fun canCancel(): Boolean {
        return status == OrderStatus.PENDING
    }

    fun cancel() {
        if (!canCancel()) throw IllegalStateException("취소할 수 없는 상태입니다")
        this.status = OrderStatus.CANCELLED
    }
}
```

### 3.2 Application Layer (애플리케이션 계층)

**책임**: UseCase 구현, 도메인 조직화, 트랜잭션 관리

```
┌─────────────────────────────────────────────────┐
│      Application Layer                          │
├─────────────────────────────────────────────────┤
│                                                 │
│  Inbound Ports (인터페이스):                    │
│  ├── OrderUseCase (주문 생성)                  │
│  ├── GetOrderQuery (주문 조회)                 │
│  ├── AddProductUseCase (제품 등록)             │
│  └── UpdateProductUseCase (제품 수정)          │
│                                                 │
│  Services (구현체):                             │
│  ├── OrderService                              │
│  ├── GetOrderService                           │
│  ├── AddProductService                         │
│  └── UpdateProductService                      │
│                                                 │
│  Outbound Ports (인터페이스):                   │
│  ├── SaveOrderPort (주문 저장)                 │
│  ├── LoadOrderPort (주문 조회)                 │
│  ├── SaveProductPort (제품 저장)               │
│  └── LoadProductPort (제품 조회)               │
│                                                 │
│  특징:                                          │
│  ✓ UseCase별 서비스                            │
│  ✓ 도메인 모델과 엔티티 변환                   │
│  ✓ 비즈니스 로직 조직화                        │
│  ✓ 포트를 통한 의존성 주입                     │
│                                                 │
└─────────────────────────────────────────────────┘
```

**예시**: Service 구현체

```kotlin
@Service
class OrderService(
    private val loadProductPort: LoadProductPort,
    private val saveOrderPort: SaveOrderPort,
    private val orderPriceCalculator: OrderPriceCalculator
) : OrderUseCase {

    override fun createOrder(command: OrderCommand): Order {
        // 1. Port를 통해 제품 조회
        val products = loadProductPort.findAllByIds(
            command.items.map { it.productId }
        )

        // 2. 도메인 서비스로 주문 항목 생성 (검증 포함)
        val orderItems = orderPriceCalculator.calculateOrderItems(
            command.items,
            products
        )

        // 3. 도메인 모델 생성
        val order = Order(
            memberId = command.memberId,
            orderItems = orderItems,
            status = OrderStatus.PENDING,
            orderDate = LocalDateTime.now(),
            receiverInfo = ReceiverInfo(
                name = command.receiverInfo.name,
                phone = command.receiverInfo.phone,
                address = command.receiverInfo.address
            )
        )

        // 4. Port를 통해 저장
        return saveOrderPort.save(order)
    }
}
```

### 3.3 Adapter Layer (어댑터 계층)

**책임**: 외부 기술(HTTP, DB, 캐시)과의 통합

```
┌───────────────────────────────────────────────┐
│         Adapter Layer                         │
├───────────────────────────────────────────────┤
│                                               │
│  Inbound Adapters:                            │
│  ├── OrderController (HTTP REST)              │
│  │   ├── Request/Response DTO                 │
│  │   └── Request → Command 변환               │
│  └── ProductController                        │
│                                               │
│  Outbound Adapters:                           │
│  ├── OrderPersistenceAdapter (DB)             │
│  │   ├── JPA Repository 호출                  │
│  │   ├── Entity ↔ Domain 변환                │
│  │   └── Port 구현                            │
│  └── ProductPersistenceAdapter                │
│                                               │
│  특징:                                        │
│  ✓ 프레임워크 의존 (Spring, JPA)             │
│  ✓ 외부 기술 처리 (HTTP, DB)                 │
│  ✓ DTO 변환                                  │
│  ✓ Port 구현으로 Application 계층과 분리     │
│                                               │
└───────────────────────────────────────────────┘
```

**예시**: Inbound Adapter (Controller)

```kotlin
@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderUseCase: OrderUseCase
) {
    @PostMapping
    fun createOrder(
        @RequestBody request: OrderRequest
    ): ResponseEntity<BaseResponse<OrderResponse>> {
        // Request DTO를 Domain Command로 변환
        val command = OrderCommand(
            memberId = request.memberId,
            items = request.items.map { itemRequest ->
                OrderItemCommand(
                    productId = itemRequest.productId,
                    quantity = itemRequest.quantity
                )
            },
            receiverInfo = ReceiverInfoCommand(
                name = request.receiverInfo.name,
                phone = request.receiverInfo.phone,
                address = request.receiverInfo.address
            )
        )

        // UseCase 호출 (Inbound Port)
        val order = orderUseCase.createOrder(command)

        // Domain을 Response DTO로 변환
        val response = OrderResponse.from(order)

        return ResponseEntity.status(201)
            .body(BaseResponse.created(response))
    }
}
```

**예시**: Outbound Adapter (Persistence)

```kotlin
@Component
class OrderPersistenceAdapter(
    private val orderJpaRepository: OrderJpaRepository,
    private val orderItemJpaRepository: OrderItemJpaRepository,
    private val orderMapper: OrderMapper
) : SaveOrderPort, LoadOrderPort {

    override fun save(order: Order): Order {
        // Domain Model → JPA Entity 변환
        val orderEntity = orderMapper.toPersistence(order)

        // JPA Repository를 통해 DB 저장
        val savedEntity = orderJpaRepository.save(orderEntity)

        // JPA Entity → Domain Model 변환
        return orderMapper.toDomain(savedEntity)
    }

    override fun findOrderById(orderId: Long): Order? {
        return orderJpaRepository.findById(orderId)
            .map { orderMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAllByIds(ids: List<Long>): List<Order> {
        return orderJpaRepository.findAllById(ids)
            .map { orderMapper.toDomain(it) }
    }
}
```

---

## 4. 데이터 흐름

### 4.1 주문 생성 흐름 (Request → Response)

```
1. HTTP Request 도착
   ↓
   POST /api/v1/orders
   {
     "memberId": 100,
     "items": [{"productId": 1001, "quantity": 2}],
     "receiverInfo": {...}
   }

2. Controller에서 DTO 수신 (Inbound Adapter)
   ↓
   OrderRequest.fromJson(json)

3. DTO → Domain Command 변환
   ↓
   OrderCommand(
     memberId = 100,
     items = [OrderItemCommand(1001, 2)],
     receiverInfo = ReceiverInfoCommand(...)
   )

4. UseCase 호출 (Inbound Port)
   ↓
   orderUseCase.createOrder(command)

5. Service에서 비즈니스 로직 실행
   ↓
   OrderService.createOrder(command):
     a) LoadProductPort.findAllByIds([1001])
        ↓
        OrderPersistenceAdapter (Outbound Adapter)
        ↓
        ProductJpaRepository.findAllById([1001])
        ↓
        DB Query 실행
        ↓
        List<ProductEntity>
        ↓
        ProductMapper.toDomain()
        ↓
        List<Product> 반환

     b) OrderPriceCalculator.calculateOrderItems()
        - 재고 확인
        - 가격 검증
        ↓
        List<OrderItem> 생성

     c) Order 도메인 모델 생성
        ↓
        Order(
          memberId = 100,
          orderItems = [OrderItem(..., 15000, 2)],
          status = PENDING,
          orderDate = LocalDateTime.now(),
          receiverInfo = ReceiverInfo(...)
        )

     d) SaveOrderPort.save(order)
        ↓
        OrderPersistenceAdapter (Outbound Adapter)
        ↓
        OrderMapper.toPersistence()
        ↓
        OrderEntity + OrderItemEntity 생성
        ↓
        OrderJpaRepository.save(orderEntity)
        ↓
        INSERT INTO orders, order_item
        ↓
        OrderEntity (DB 조회)
        ↓
        OrderMapper.toDomain()
        ↓
        Order 반환

6. Order 도메인 모델 → Response DTO 변환
   ↓
   OrderResponse.from(order)

7. HTTP Response 반환
   ↓
   201 Created
   {
     "code": 201,
     "message": "요청이 정상적으로 처리되었습니다.",
     "data": {
       "orderId": 1,
       "memberId": 100,
       "status": "PENDING",
       ...
     }
   }
```

### 4.2 의존성 흐름 (역방향)

```
HTTP Request
    ↓
┌─────────────────────────────────────┐
│ OrderController (Inbound Adapter)   │
│  • OrderUseCase 의존                │
└────────────┬────────────────────────┘
             ↓ (implements)
┌─────────────────────────────────────┐
│ OrderUseCase (Inbound Port)         │
│  • Interface (추상)                 │
└────────────┬────────────────────────┘
             ↓ (implements)
┌─────────────────────────────────────┐
│ OrderService (Service)              │
│  • LoadProductPort 의존             │
│  • SaveOrderPort 의존               │
│  • OrderPriceCalculator 의존        │
└────────────┬────────────────────────┘
             ├─────────────────┬───────────────────┐
             ↓                 ↓                   ↓
     ┌─────────────────┐  ┌──────────────┐  ┌──────────────┐
     │LoadProductPort  │  │SaveOrderPort │  │OrderPrice    │
     │(Outbound Port)  │  │(Outbound)    │  │Calculator    │
     │ • Interface     │  │ • Interface  │  │(Domain Svc)  │
     └────────┬────────┘  └──────┬───────┘  └──────────────┘
              ↓                  ↓
     ┌─────────────────────┬─────────────────────┐
     │OrderPersistenceAdapter (Outbound Adapter)│
     │ • OrderJpaRepository 호출                 │
     │ • Entity ↔ Domain 변환                   │
     └────────────┬────────────────────────────┘
                  ↓
     ┌──────────────────────────────────────┐
     │ OrderJpaRepository (JPA Interface)   │
     │ • Spring Data JPA                    │
     └────────────┬─────────────────────────┘
                  ↓
     ┌──────────────────────────────────────┐
     │ MySQL Database                       │
     │ • orders, order_item, product 테이블 │
     └──────────────────────────────────────┘
```

**핵심**: Controller는 OrderUseCase에 의존하지만,
OrderService가 OrderUseCase를 구현하므로
실제 의존성은 **역방향**입니다.

---

## 5. 의존성 역전 원칙

### 5.1 Traditional Layered Architecture (❌ 순방향 의존)

```
Presentation (Controller)
    ↓ depends on
Business Logic (Service)
    ↓ depends on
Data Access (Repository)
    ↓ depends on
Database
```

**문제점**:
- 상위 계층이 하위 계층에 강하게 의존
- 데이터베이스 변경 시 서비스 코드 수정 필요
- 테스트하기 어려움 (DB 의존성)

### 5.2 Hexagonal Architecture (✓ 역방향 의존)

```
Controller
    ↓ depends on
OrderUseCase (Interface)
    ↑ implemented by
Service
    ↓ depends on
SaveOrderPort (Interface)
    ↑ implemented by
PersistenceAdapter
    ↓ depends on
JpaRepository
    ↓
Database
```

**장점**:
- 내부 계층(Domain, Service)이 외부 의존성 없음
- Port 인터페이스로 느슨한 결합
- 쉬운 테스트 (Mock으로 대체 가능)

### 5.3 의존성 역전 예시

```kotlin
// ❌ 순방향 의존성 (나쁜 예)
class OrderService {
    private val orderRepository = OrderRepository()  // 직접 의존

    fun createOrder(command: OrderCommand): Order {
        // repository 직접 사용
        orderRepository.save(...)
    }
}

// ✓ 역방향 의존성 (좋은 예)
class OrderService(
    private val saveOrderPort: SaveOrderPort  // 인터페이스 의존
) : OrderUseCase {

    fun createOrder(command: OrderCommand): Order {
        // port를 통해 저장 (구현체는 외부)
        saveOrderPort.save(...)
    }
}

// Port 구현
class OrderPersistenceAdapter(
    private val orderJpaRepository: OrderJpaRepository
) : SaveOrderPort {
    override fun save(order: Order): Order {
        orderJpaRepository.save(...)
    }
}
```

---

## 6. 패키지 구조 매핑

### 6.1 패키지와 계층의 매핑

```
event/oms/
│
├── domain/                  ◄── DOMAIN LAYER
│   ├── model/              (비즈니스 엔티티)
│   │   ├── order/
│   │   │   ├── Order.kt
│   │   │   ├── OrderItem.kt
│   │   │   ├── OrderStatus.kt
│   │   │   └── ReceiverInfo.kt
│   │   └── product/
│   │       └── Product.kt
│   │
│   └── service/            (도메인 서비스)
│       └── order/
│           └── OrderPriceCalculator.kt
│
├── application/             ◄── APPLICATION LAYER
│   ├── port/               (포트 인터페이스)
│   │   ├── in/            (Inbound)
│   │   │   ├── order/
│   │   │   │   ├── OrderUseCase.kt
│   │   │   │   ├── GetOrderQuery.kt
│   │   │   │   ├── GetOrderListQuery.kt
│   │   │   │   ├── OrderCommand.kt
│   │   │   │   └── OrderItemCommand.kt
│   │   │   └── product/
│   │   │       ├── AddProductUseCase.kt
│   │   │       ├── UpdateProductUseCase.kt
│   │   │       ├── GetProductListQuery.kt
│   │   │       ├── AddProductCommand.kt
│   │   │       └── UpdateProductCommand.kt
│   │   │
│   │   └── out/           (Outbound)
│   │       ├── order/
│   │       │   ├── SaveOrderPort.kt
│   │       │   └── LoadOrderPort.kt
│   │       └── product/
│   │           ├── SaveProductPort.kt
│   │           └── LoadProductPort.kt
│   │
│   └── service/            (UseCase 구현)
│       ├── order/
│       │   ├── OrderService.kt
│       │   ├── GetOrderService.kt
│       │   └── GetOrderListService.kt
│       └── product/
│           ├── AddProductService.kt
│           ├── UpdateProductService.kt
│           └── GetProductListService.kt
│
├── adapter/                 ◄── ADAPTER LAYER
│   ├── in/                 (Inbound)
│   │   └── web/
│   │       ├── common/
│   │       │   ├── BaseResponse.kt
│   │       │   ├── ErrorResponse.kt
│   │       │   └── GlobalExceptionHandler.kt
│   │       ├── order/
│   │       │   ├── OrderController.kt
│   │       │   ├── OrderSpec.kt (API 계약)
│   │       │   ├── request/
│   │       │   │   ├── OrderRequest.kt
│   │       │   │   ├── OrderItemRequest.kt
│   │       │   │   └── ReceiverInfoRequest.kt
│   │       │   └── response/
│   │       │       ├── OrderResponse.kt
│   │       │       └── OrderItemResponse.kt
│   │       │
│   │       └── product/
│   │           ├── ProductController.kt
│   │           ├── ProductSpec.kt (API 계약)
│   │           ├── request/
│   │           │   ├── AddProductRequest.kt
│   │           │   └── UpdateProductRequest.kt
│   │           └── response/
│   │               └── ProductResponse.kt
│   │
│   └── out/                (Outbound)
│       └── persistence/
│           ├── order/
│           │   ├── OrderPersistenceAdapter.kt
│           │   ├── entity/
│           │   │   ├── OrderEntity.kt
│           │   │   └── OrderItemEntity.kt
│           │   ├── embeddable/
│           │   │   └── ReceiverInfoEmbeddable.kt
│           │   ├── mapper/
│           │   │   └── OrderMapper.kt
│           │   └── repository/
│           │       ├── OrderJpaRepository.kt
│           │       └── OrderItemJpaRepository.kt
│           │
│           └── product/
│               ├── ProductPersistenceAdapter.kt
│               ├── entity/
│               │   └── ProductEntity.kt
│               ├── mapper/
│               │   └── ProductMapper.kt
│               └── repository/
│                   └── ProductJpaRepository.kt
│
└── config/                  ◄── CONFIGURATION
    └── swagger/
        └── OpenApiConfig.kt
```

### 6.2 클래스 의존성 매트릭스

```
┌──────────────────┬────────────┬─────────────┬──────────┐
│ 클래스           │ Domain     │ Application │ Adapter  │
├──────────────────┼────────────┼─────────────┼──────────┤
│ Order (Domain)   │ ✓          │ -           │ -        │
│ Product (Domain) │ ✓          │ -           │ -        │
│                  │            │             │          │
│ OrderUseCase     │ -          │ ✓           │ -        │
│ GetOrderQuery    │ -          │ ✓           │ -        │
│                  │            │             │          │
│ OrderService     │ ✓          │ ✓           │ -        │
│ (implements      │ (uses)     │             │          │
│  OrderUseCase)   │            │             │          │
│                  │            │             │          │
│ SaveOrderPort    │ -          │ ✓           │ -        │
│ (Outbound Port)  │            │             │          │
│                  │            │             │          │
│ OrderController  │ -          │ ✓           │ ✓        │
│ (Inbound Adapter)│            │ (uses)      │ (uses    │
│                  │            │             │ Spring)  │
│                  │            │             │          │
│ OrderPersis...   │ ✓          │ ✓           │ ✓        │
│ Adapter          │ (uses)     │ (implements)│ (uses DB)│
│ (implements      │            │             │          │
│  SaveOrderPort)  │            │             │          │
│                  │            │             │          │
│ OrderEntity      │ -          │ -           │ ✓        │
│ (JPA Entity)     │            │             │ (uses JPA)
└──────────────────┴────────────┴─────────────┴──────────┘

✓ : 해당 클래스가 속한 계층
- : 해당 계층에 속하지 않음
```

---

## 7. 주요 패턴

### 7.1 Repository 패턴 (데이터 접근 추상화)

```kotlin
// Port Interface (Application Layer)
interface SaveOrderPort {
    fun save(order: Order): Order
}

// Adapter Implementation (Adapter Layer)
class OrderPersistenceAdapter(
    private val orderJpaRepository: OrderJpaRepository
) : SaveOrderPort {
    override fun save(order: Order): Order {
        val entity = orderMapper.toPersistence(order)
        return orderMapper.toDomain(orderJpaRepository.save(entity))
    }
}

// Usage (Application Layer)
class OrderService(
    private val saveOrderPort: SaveOrderPort
) : OrderUseCase {
    override fun createOrder(command: OrderCommand): Order {
        val order = Order(...)
        return saveOrderPort.save(order)  // Port 호출
    }
}
```

### 7.2 Mapper 패턴 (Domain ↔ Entity 변환)

```kotlin
// Mapper (Adapter Layer)
@Component
class OrderMapper {

    // Domain → Entity
    fun toPersistence(order: Order): OrderEntity {
        return OrderEntity(
            id = order.id,
            memberId = order.memberId,
            status = order.status,
            orderDate = order.orderDate,
            receiverInfo = ReceiverInfoEmbeddable(
                receiverName = order.receiverInfo.name,
                receiverPhone = order.receiverInfo.phone,
                receiverAddress = order.receiverInfo.address
            )
        )
    }

    // Entity → Domain
    fun toDomain(entity: OrderEntity): Order {
        return Order(
            id = entity.id,
            memberId = entity.memberId,
            orderItems = /* fetch order items */,
            status = entity.status,
            orderDate = entity.orderDate,
            receiverInfo = ReceiverInfo(
                name = entity.receiverInfo.receiverName,
                phone = entity.receiverInfo.receiverPhone,
                address = entity.receiverInfo.receiverAddress
            )
        )
    }
}
```

### 7.3 UseCase 패턴 (비즈니스 로직 캡슐화)

```kotlin
// Inbound Port (Application Layer)
interface OrderUseCase {
    fun createOrder(command: OrderCommand): Order
}

// UseCase Command (Data Transfer Object)
data class OrderCommand(
    val memberId: Long,
    val items: List<OrderItemCommand>,
    val receiverInfo: ReceiverInfoCommand
)

// Service Implementation (Application Layer)
@Service
class OrderService(
    private val loadProductPort: LoadProductPort,
    private val saveOrderPort: SaveOrderPort
) : OrderUseCase {
    override fun createOrder(command: OrderCommand): Order {
        // 비즈니스 로직 구현
        // ...
        return order
    }
}
```

### 7.4 Query 패턴 (조회 로직 분리)

```kotlin
// Inbound Port (쓰기)
interface OrderUseCase {
    fun createOrder(command: OrderCommand): Order
}

// Inbound Port (읽기)
interface GetOrderQuery {
    fun getOrder(orderId: Long): Order
}

// Service Implementation (쓰기)
@Service
class OrderService : OrderUseCase {
    override fun createOrder(command: OrderCommand): Order { ... }
}

// Service Implementation (읽기)
@Service
class GetOrderService(
    private val loadOrderPort: LoadOrderPort
) : GetOrderQuery {
    override fun getOrder(orderId: Long): Order {
        return loadOrderPort.findOrderById(orderId)
            ?: throw NoSuchElementException()
    }
}
```

### 7.5 Domain Service 패턴 (비즈니스 규칙)

```kotlin
// Domain Service (Domain Layer)
@Component
class OrderPriceCalculator {

    fun calculateOrderItems(
        itemRequests: List<OrderItemCommand>,
        products: List<Product>
    ): List<OrderItem> {
        return itemRequests.map { request ->
            val product = products.find { it.id == request.productId }
                ?: throw NoSuchElementException()

            // 재고 검증 (비즈니스 규칙)
            if (product.stock < request.quantity) {
                throw IllegalArgumentException(
                    "재고가 부족합니다: ${product.name}"
                )
            }

            // OrderItem 생성
            OrderItem(
                productId = request.productId,
                price = product.price,
                quantity = request.quantity
            )
        }
    }
}
```

---

## 결론

이 아키텍처의 핵심 이점:

1. **테스트 용이**: Mock으로 포트 대체 가능
2. **유지보수성**: 계층이 명확히 분리됨
3. **확장성**: 새로운 어댑터 추가 용이
4. **비즈니스 로직 보호**: Domain이 프레임워크 무관
5. **기술 교체**: 데이터베이스, 웹 프레임워크 변경 가능

