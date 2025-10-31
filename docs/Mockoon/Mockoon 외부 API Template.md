## Mockoon: API 모의(Mocking) 서버 구축 방법
### 1. 설치 및 환경(Environment) 생성 :
Mockoon 다운로드: 공식 홈페이지(https://mockoon.com/)에서 자신의 운영체제에 맞는 애플리케이션을 다운로드하여 설치.
### 2. 환경 생성: :
- Mockoon을 실행하면 'Demo API'라는 예제 환경이 나타납니다. 
- 새로운 프로젝트를 위해 왼쪽 상단의 New environment 버튼을 클릭하여 새로운 환경을 생성 합니다.


## 프로젝트에 적용된 Mockoon Template
### 1. 결제 요청 (Payment Request)
- Method: POST
- Endpoint: /v1/payments
```
@PostMapping("/v1/payments")
{
  // 1. TossPaymentRequestResponse DTO 기반 응답
  // paymentKey는 고유하게 생성
  "paymentKey": "tk_{{faker 'string.uuid'}}",
  
  // 2. 요청 DTO(TossPaymentRequest)에서 받은 orderId를 그대로 반환
  "orderId": "{{body 'orderId'}}",
  
  // 3. 결제 요청 상태 (임의 지정)
  "status": "READY",
  
  // 4. 리디렉션될 가상 결제 페이지 URL
  "checkout": {
    "url": "https://mock-payment-page.com/pay?orderId={{body 'orderId'}}"
  }
}
```

### 2. 결제 승인 (Payment Confirm)
- Method: POST
- Endpoint: /v1/payments/confirm
```
@PostMapping("/v1/payments/confirm")
{
  // 1. TossPaymentApprovalResponse DTO 기반 응답
  // 요청 DTO(TossPaymentApprovalRequest)에서 받은 paymentKey, orderId, amount를 사용
  
  "paymentKey": "{{body 'paymentKey'}}",
  "orderId": "{{body 'orderId'}}",
  
  // 2. ConfirmPaymentService에서 확인하는 성공 상태 "DONE"
  "status": "DONE", 
  
  // 3. 요청받은 금액을 그대로 totalAmount로 설정
  "totalAmount": {{body 'amount'}},
  
  // 4. 승인 시각 (ISO 8601 형식)
  "approvedAt": "{{now format='iso'}}"
}
```