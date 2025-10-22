# 주문 관리 시스템(OMS) 문서

이 디렉토리에는 OMS 프로젝트의 모든 문서가 포함되어 있습니다.

## 📚 문서 목록

### 1. [PROJECT_ANALYSIS.md](./PROJECT_ANALYSIS.md)
**프로젝트 전체 분석 보고서** (33KB)

프로젝트의 모든 정보를 포괄적으로 설명합니다.

**포함 내용**:
- 프로젝트 개요 및 목표
- 전체 디렉토리 구조
- 기술 스택 (Spring Boot 3.5.6, Kotlin, MySQL)
- 핵심 비즈니스 로직 상세 설명
- 데이터베이스 스키마 및 JPA 엔티티
- 테스트 구조 (Unit/Integration Test)
- 설정 파일 (application.yml, build.gradle.kts)
- 개발 현황 및 커밋 히스토리
- 강점 분석 및 개선 사항

**대상**: 프로젝트 전체를 이해하고 싶은 개발자

**읽는 시간**: 20-30분

---

### 2. [ARCHITECTURE.md](./ARCHITECTURE.md)
**아키텍처 상세 설명서** (41KB)

헥사고날 아키텍처의 구조와 설계 원칙을 깊이 있게 설명합니다.

**포함 내용**:
- 헥사고날 아키텍처 개념
- Domain/Application/Adapter 계층 상세 설명
- Port(인터페이스)와 Adapter 구현 방식
- 데이터 흐름 (요청 → 응답)
- 의존성 역전 원칙 (Dependency Inversion)
- 패키지 구조와 계층의 매핑
- 주요 디자인 패턴 (Repository, Mapper, UseCase, Query, Domain Service)

**대상**: 아키텍처를 이해하고 새로운 기능을 개발하는 개발자

**읽는 시간**: 30-40분

**다이어그램**: 아스키 다이어그램으로 시각화된 아키텍처 구조

---

### 3. [API_SPECIFICATION.md](./API_SPECIFICATION.md)
**API 명세서** (16KB)

모든 REST API의 요청/응답 형식을 상세히 문서화합니다.

**포함 내용**:
- API 기본 정보 (Base URL, Content-Type)
- 공통 응답 형식 (BaseResponse)
- 제품 API 3개
  - POST /api/v1/products (등록)
  - PUT /api/v1/products/{id} (수정)
  - GET /api/v1/products (목록 조회)
- 주문 API 3개
  - POST /api/v1/orders (생성)
  - GET /api/v1/orders/{id} (상세 조회)
  - GET /api/v1/orders?memberId={id} (목록 조회, 개발 중)
- 에러 처리 (HTTP 상태 코드, 에러 응답 형식)
- Swagger 문서 접근 방법
- cURL 예제로 빠른 테스트

**대상**: API를 사용하는 프론트엔드 개발자, QA

**읽는 시간**: 15-20분

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

### 4. [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)
**개발 가이드** (18KB)

프로젝트에서 개발하는 방법을 단계별로 설명합니다.

**포함 내용**:
- 환경 설정 (JDK 21, MySQL, IntelliJ IDEA)
- 프로젝트 실행 방법
- 개발 워크플로우
  - 도메인 모델 작성
  - Application 계층 작성 (UseCase, Service)
  - Adapter 계층 작성 (Controller, Entity, Repository)
  - 기능 브랜치 생성 및 커밋
- 코드 작성 규칙 (네이밍, 클래스 구조, Exception 처리)
- 테스트 작성 가이드 (단위 테스트, 통합 테스트)
- 커밋 규칙 및 메시지 형식
- 문제 해결 (빌드 실패, DB 연결 오류 등)
- 주요 개선 과제 및 우선순위

**대상**: OMS 프로젝트에서 새로운 기능을 개발하는 개발자

**읽는 시간**: 20-25분

**실행 시간**: 10-20분 (처음 실행 시)

---

## 🚀 빠른 시작

### 1단계: 환경 설정
```bash
# JDK 21 설치 확인
java -version

# MySQL 시작
brew services start mysql

# 데이터베이스 생성
mysql -u armada -p armada write_qr < sql/oms.sql
```

### 2단계: 프로젝트 실행
```bash
# 의존성 다운로드
./gradlew clean build

# 애플리케이션 시작
./gradlew bootRun
```

### 3단계: API 테스트
```bash
# Swagger UI 접속
open http://localhost:8080/swagger-ui.html

# 또는 cURL로 테스트
curl -X GET http://localhost:8080/api/v1/products
```

자세한 내용은 [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)를 참고하세요.

---

## 📖 문서별 독서 순서 추천

### 신입 개발자
1. [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) - 먼저 환경 설정
2. [PROJECT_ANALYSIS.md](./PROJECT_ANALYSIS.md) - 프로젝트 이해
3. [ARCHITECTURE.md](./ARCHITECTURE.md) - 아키텍처 이해
4. [API_SPECIFICATION.md](./API_SPECIFICATION.md) - API 사용법

### 기존 개발자
1. [ARCHITECTURE.md](./ARCHITECTURE.md) - 빠르게 구조 파악
2. [PROJECT_ANALYSIS.md](./PROJECT_ANALYSIS.md) - 세부사항 확인
3. [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) - 개발 시작

### API 사용자 (프론트엔드)
1. [API_SPECIFICATION.md](./API_SPECIFICATION.md) - API 명세 확인
2. [PROJECT_ANALYSIS.md](./PROJECT_ANALYSIS.md) - 5-7장 데이터베이스/API 부분

---

## 🔍 특정 정보 찾기

### API 엔드포인트를 찾고 싶다면
→ [API_SPECIFICATION.md](./API_SPECIFICATION.md) 3-4장

### 데이터베이스 스키마를 알고 싶다면
→ [PROJECT_ANALYSIS.md](./PROJECT_ANALYSIS.md) 6장

### 새로운 기능을 개발하려면
→ [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) 3장

### 아키텍처 패턴을 이해하려면
→ [ARCHITECTURE.md](./ARCHITECTURE.md) 4-7장

### 테스트를 작성하려면
→ [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) 5장

### 에러 해결이 필요하다면
→ [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) 7장

---

## 📊 프로젝트 현황

### 진행도
```
전체:           ████████░░ (약 80%)
Product 기능:    ██████████ (100% 완료)
Order 기능:      ████████░░ (약 80% 완료)
테스트 커버리지: ████░░░░░░ (약 40% 완료)
문서화:          ████████░░ (약 80% 완료)
```

### 완료된 기능 ✓
- Product API (등록, 수정, 조회)
- Order 도메인 모델
- Order 생성 및 상세 조회 API
- 공통 예외 처리
- Swagger 문서화
- 제품 테스트 코드

### 진행 중 🔄
- Order 목록 조회 API

### 미구현 ❌
- Order API 테스트 코드
- 결제 처리
- 배송 상태 관리
- Spring Security 인증
- Redis 캐싱

---

## 🛠 주요 기술 스택

| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 21 | 프로그래밍 언어 |
| Kotlin | 1.9.25 | 주 언어 |
| Spring Boot | 3.5.6 | 웹 프레임워크 |
| Gradle | 8.x | 빌드 도구 |
| MySQL | 8.0 | 데이터베이스 |
| JPA/Hibernate | 6.x | ORM |
| Swagger | 2.8.13 | API 문서화 |
| JUnit 5 | - | 테스트 프레임워크 |

---

## 📞 도움이 필요하신가요?

### 자주 묻는 질문 (FAQ)

**Q: 프로젝트를 어떻게 시작하나요?**
A: [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)의 "프로젝트 실행" 섹션을 참고하세요.

**Q: 새로운 기능을 어떻게 개발하나요?**
A: [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)의 "개발 워크플로우" 섹션을 참고하세요.

**Q: API 응답 형식이 어떻게 되나요?**
A: [API_SPECIFICATION.md](./API_SPECIFICATION.md)의 "공통 응답 형식" 섹션을 참고하세요.

**Q: 아키텍처를 이해하려면 어떻게 해야 하나요?**
A: [ARCHITECTURE.md](./ARCHITECTURE.md)의 "헥사고날 아키텍처" 섹션부터 읽기 시작하세요.

**Q: 에러가 발생했어요.**
A: [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)의 "문제 해결" 섹션을 확인하세요.

### 추가 리소스

- **GitHub 저장소**: 프로젝트 루트의 `.git` 참고
- **Swagger 문서**: `http://localhost:8080/swagger-ui.html` (애플리케이션 실행 시)
- **SQL 스크립트**: `sql/oms.sql` (데이터베이스 스키마)

---

## 📝 문서 유지보수

문서는 프로젝트와 함께 최신으로 유지되어야 합니다.

### 문서 업데이트 체크리스트
- [ ] 새로운 API 추가 → API_SPECIFICATION.md 수정
- [ ] 아키텍처 변경 → ARCHITECTURE.md 수정
- [ ] 기술 스택 업데이트 → PROJECT_ANALYSIS.md 수정
- [ ] 개발 프로세스 변경 → DEVELOPMENT_GUIDE.md 수정

**최종 업데이트**: 2025-10-22

---

## 라이선스

이 문서는 OMS 프로젝트의 일부입니다.

