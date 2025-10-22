# OMS (Order Management System) API 명세서

## 목차
1. [API 개요](#1-api-개요)
2. [공통 응답 형식](#2-공통-응답-형식)
3. [제품 API](#3-제품-api)
4. [주문 API](#4-주문-api)
5. [에러 처리](#5-에러-처리)
6. [Swagger 문서](#6-swagger-문서)

---

## 1. API 개요

### 기본 정보
- **Base URL**: `http://localhost:8080`
- **API Version**: v1
- **Content-Type**: `application/json`
- **Character Encoding**: UTF-8

### 인증
현재 버전에서는 인증이 없습니다. (향후 Spring Security 추가 예정)

### 일반 규칙
- 모든 API는 JSON 형식으로 요청/응답합니다
- 모든 응답은 `BaseResponse` 형식을 따릅니다
- 타임스탬프는 ISO 8601 형식입니다 (예: `2025-10-22T14:30:00`)

---

## 2. 공통 응답 형식

### 2.1 성공 응답 (BaseResponse)

```json
{
  "code": 200,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": {
    "orderId": 1,
    "memberId": 100,
    "status": "PENDING"
  }
}
```

**필드 설명**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `code` | Integer | HTTP 상태 코드와 동일 (200, 201, 400, 404, 500) |
| `message` | String | 결과 메시지 |
| `data` | Object | 실제 응답 데이터 (nullable) |

### 2.2 에러 응답

```json
{
  "code": 400,
  "message": "잘못된 요청입니다.",
  "data": null
}
```

또는 상세 에러:

```json
{
  "code": 400,
  "message": "유효성 검증 실패",
  "data": {
    "errors": [
      {
        "field": "name",
        "message": "제품명은 필수입니다"
      }
    ]
  }
}
```

---

## 3. 제품 API

### 3.1 제품 등록 (Create Product)

**Endpoint**: `POST /api/v1/products`

**설명**: 새로운 제품을 등록합니다.

**Request**:

```json
POST /api/v1/products
Content-Type: application/json

{
  "id": 1001,
  "name": "테스트 상품",
  "price": 15000.00,
  "stock": 50
}
```

**Request 필드**:

| 필드 | 타입 | 필수 | 범위 | 설명 |
|------|------|------|------|------|
| `id` | Long | O | > 0 | 상품 ID (클라이언트에서 지정) |
| `name` | String | O | 1-255자 | 상품명 |
| `price` | BigDecimal | O | > 0 | 판매 가격 |
| `stock` | Integer | O | >= 0 | 초기 재고 |

**Response** (201 Created):

```json
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

**Response 필드**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `productId` | Long | 상품 ID |
| `name` | String | 상품명 |
| `price` | BigDecimal | 판매 가격 |
| `stock` | Integer | 남은 재고 |

**에러 케이스**:

```
❌ 400 Bad Request
- 필수 필드 누락
- price <= 0
- stock < 0
- name이 비어있음

❌ 409 Conflict
- 중복된 상품 ID
```

**예시**:

```bash
# 요청
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1001,
    "name": "Marlboro Medium",
    "price": 5000.00,
    "stock": 300
  }'

# 응답
{
  "code": 201,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": {
    "productId": 1001,
    "name": "Marlboro Medium",
    "price": 5000.00,
    "stock": 300
  }
}
```

---

### 3.2 제품 수정 (Update Product)

**Endpoint**: `PUT /api/v1/products/{productId}`

**설명**: 기존 제품의 정보를 수정합니다.

**Request**:

```json
PUT /api/v1/products/1001
Content-Type: application/json

{
  "name": "수정된 상품명",
  "price": 20000.00,
  "stock": 100
}
```

**Path 파라미터**:

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `productId` | Long | O | 수정할 상품 ID |

**Request 필드**:

| 필드 | 타입 | 필수 | 범위 | 설명 |
|------|------|------|------|------|
| `name` | String | O | 1-255자 | 변경할 상품명 |
| `price` | BigDecimal | O | > 0 | 변경할 가격 |
| `stock` | Integer | O | >= 0 | 변경할 재고 |

**Response** (200 OK):

```json
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

**에러 케이스**:

```
❌ 404 Not Found
- 해당 상품이 없음

❌ 400 Bad Request
- price <= 0
- stock < 0
- name이 비어있음
```

**예시**:

```bash
# 요청
curl -X PUT http://localhost:8080/api/v1/products/1001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Marlboro",
    "price": 6000.00,
    "stock": 500
  }'

# 응답
{
  "code": 200,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": {
    "productId": 1001,
    "name": "Premium Marlboro",
    "price": 6000.00,
    "stock": 500
  }
}
```

---

### 3.3 제품 목록 조회 (Get All Products)

**Endpoint**: `GET /api/v1/products`

**설명**: 모든 제품 목록을 조회합니다.

**Request**:

```
GET /api/v1/products
```

**Query 파라미터**: 없음

**Response** (200 OK):

```json
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
    },
    {
      "productId": 1003,
      "name": "상품3",
      "price": 35000.00,
      "stock": 20
    }
  ]
}
```

**응답 필드** (배열의 각 요소):

| 필드 | 타입 | 설명 |
|------|------|------|
| `productId` | Long | 상품 ID |
| `name` | String | 상품명 |
| `price` | BigDecimal | 판매 가격 |
| `stock` | Integer | 남은 재고 |

**에러 케이스**:

```
✓ 200 OK (항상 성공, 데이터가 없으면 빈 배열)
```

**예시**:

```bash
# 요청
curl -X GET http://localhost:8080/api/v1/products

# 응답
{
  "code": 200,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": [
    {
      "productId": 1001,
      "name": "Marlboro Medium",
      "price": 5000.00,
      "stock": 300
    }
  ]
}
```

---

## 4. 주문 API

### 4.1 주문 생성 (Create Order)

**Endpoint**: `POST /api/v1/orders`

**설명**: 새로운 주문을 생성합니다.

**Request**:

```json
POST /api/v1/orders
Content-Type: application/json

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
```

**Request 필드**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `memberId` | Long | O | 주문자 ID |
| `items` | Array | O | 주문 항목 배열 (최소 1개) |
| `items[].productId` | Long | O | 상품 ID |
| `items[].quantity` | Integer | O | 주문 수량 (>= 1) |
| `receiverInfo` | Object | O | 수령인 정보 |
| `receiverInfo.name` | String | O | 수령인명 |
| `receiverInfo.phone` | String | O | 수령인 전화번호 |
| `receiverInfo.address` | String | O | 수령인 주소 |

**Response** (201 Created):

```json
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

**Response 필드**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `orderId` | Long | 주문 ID (DB 자동 생성) |
| `memberId` | Long | 주문자 ID |
| `status` | String | 주문 상태 (PENDING, PAID, SHIPPED, CANCELLED) |
| `orderDate` | DateTime | 주문 일시 |
| `items` | Array | 주문 항목 배열 |
| `items[].productId` | Long | 상품 ID |
| `items[].price` | BigDecimal | 주문 시점의 상품 가격 |
| `items[].quantity` | Integer | 주문 수량 |
| `receiverInfo` | Object | 수령인 정보 |
| `receiverInfo.name` | String | 수령인명 |
| `receiverInfo.phone` | String | 수령인 전화번호 |
| `receiverInfo.address` | String | 수령인 주소 |

**에러 케이스**:

```
❌ 400 Bad Request
- 필수 필드 누락
- items가 비어있음
- quantity <= 0

❌ 404 Not Found
- 상품을 찾을 수 없음

❌ 409 Conflict
- 재고가 부족함 (IllegalArgumentException)
```

**예시**:

```bash
# 요청
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "memberId": 100,
    "items": [
      {"productId": 1001, "quantity": 2}
    ],
    "receiverInfo": {
      "name": "홍길동",
      "phone": "010-1234-5678",
      "address": "서울시 강남구"
    }
  }'

# 응답
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

---

### 4.2 주문 상세 조회 (Get Order Details)

**Endpoint**: `GET /api/v1/orders/{orderId}`

**설명**: 특정 주문의 상세 정보를 조회합니다.

**Request**:

```
GET /api/v1/orders/1
```

**Path 파라미터**:

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `orderId` | Long | O | 조회할 주문 ID |

**Response** (200 OK):

```json
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
```

**에러 케이스**:

```
❌ 404 Not Found
- 해당 주문이 없음
```

**예시**:

```bash
# 요청
curl -X GET http://localhost:8080/api/v1/orders/1

# 응답 (성공)
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

# 응답 (실패)
{
  "code": 404,
  "message": "요청하신 자원을 찾을 수 없습니다."
}
```

---

### 4.3 주문 목록 조회 (Get Orders) - 개발 중

**Endpoint**: `GET /api/v1/orders?memberId={memberId}`

**설명**: 특정 회원의 주문 목록을 조회합니다. (현재 개발 중)

**Request**:

```
GET /api/v1/orders?memberId=100
```

**Query 파라미터**:

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `memberId` | Long | O | 회원 ID |

**예상 Response** (200 OK):

```json
{
  "code": 200,
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": [
    {
      "orderId": 1,
      "memberId": 100,
      "status": "PENDING",
      "orderDate": "2025-10-22T14:30:00"
    },
    {
      "orderId": 2,
      "memberId": 100,
      "status": "PAID",
      "orderDate": "2025-10-21T10:15:00"
    }
  ]
}
```

**상태**: 🔄 개발 중

---

## 5. 에러 처리

### 5.1 HTTP 상태 코드

| 코드 | 의미 | 예시 |
|------|------|------|
| `200` | OK | 조회 성공 |
| `201` | Created | 생성 성공 |
| `400` | Bad Request | 유효성 검증 실패 |
| `404` | Not Found | 요청한 자원이 없음 |
| `409` | Conflict | 재고 부족 등 비즈니스 로직 위반 |
| `500` | Internal Server Error | 서버 내부 오류 |

### 5.2 에러 응답 형식

#### 일반 에러

```json
{
  "code": 404,
  "message": "요청하신 자원을 찾을 수 없습니다.",
  "data": null
}
```

#### 검증 에러 (예상)

```json
{
  "code": 400,
  "message": "유효성 검증에 실패했습니다.",
  "data": {
    "errors": [
      {
        "field": "items",
        "message": "최소 1개 이상의 상품을 추가해야 합니다"
      },
      {
        "field": "receiverInfo.phone",
        "message": "전화번호는 필수입니다"
      }
    ]
  }
}
```

#### 재고 부족 에러

```json
{
  "code": 409,
  "message": "재고가 부족합니다: Marlboro Medium",
  "data": null
}
```

### 5.3 일반적인 에러 코드

| 상황 | HTTP 코드 | 메시지 |
|------|----------|--------|
| 제품 등록 시 ID 중복 | 409 | 이미 존재하는 상품입니다. |
| 제품 수정 시 없는 ID | 404 | 요청하신 자원을 찾을 수 없습니다. |
| 주문 생성 시 없는 상품 | 404 | 제품 정보를 찾을 수 없습니다 |
| 주문 생성 시 재고 부족 | 409 | 재고가 부족합니다: {상품명} |
| 주문 조회 시 없는 주문 | 404 | 요청하신 자원을 찾을 수 없습니다. |

---

## 6. Swagger 문서

### 6.1 Swagger UI 접근

- **URL**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **그룹별 API**:
  - `orders` 그룹: `/v3/api-docs/orders`
  - `products` 그룹: `/v3/api-docs/products`

### 6.2 Swagger에서 API 테스트

1. `http://localhost:8080/swagger-ui.html` 접속
2. 원하는 API 엔드포인트 펼치기
3. "Try it out" 버튼 클릭
4. Request 파라미터/바디 입력
5. "Execute" 버튼 클릭하여 테스트

### 6.3 OpenAPI 설정 파일

`src/main/kotlin/event/oms/config/swagger/OpenApiConfig.kt`에서 설정:

```kotlin
@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val info = Info()
            .title("OMS API Document")
            .version("v1.0.0")
            .description("주문 관리 시스템(OMS) API 명세서")
        return OpenAPI()
            .components(Components())
            .info(info)
    }

    @Bean
    fun ordersGroupOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("orders")
            .pathsToMatch("/api/v1/orders/**")
            .build()
    }

    @Bean
    fun productsGroupOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("products")
            .pathsToMatch("/api/v1/products/**")
            .build()
    }
}
```

---

## 7. 빠른 테스트 가이드

### 7.1 제품 등록 후 조회

```bash
# 1. 제품 등록
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1001,
    "name": "테스트 상품",
    "price": 10000,
    "stock": 50
  }'

# 2. 제품 목록 조회
curl -X GET http://localhost:8080/api/v1/products

# 3. 제품 수정
curl -X PUT http://localhost:8080/api/v1/products/1001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "수정된 상품",
    "price": 15000,
    "stock": 100
  }'
```

### 7.2 주문 생성 후 조회

```bash
# 1. 먼저 제품 생성 (위의 1번 실행)

# 2. 주문 생성
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "memberId": 100,
    "items": [
      {"productId": 1001, "quantity": 2}
    ],
    "receiverInfo": {
      "name": "홍길동",
      "phone": "010-1234-5678",
      "address": "서울시 강남구"
    }
  }'

# 3. 주문 상세 조회 (응답의 orderId 사용)
curl -X GET http://localhost:8080/api/v1/orders/1
```

---

## 변경 이력

### v1.0.0 (현재)
- ✓ Product API 완성 (등록, 수정, 조회)
- ✓ Order API 부분 완성 (생성, 상세 조회)
- 🔄 Order 목록 조회 개발 중
- ❌ 결제, 배송 등 미구현

