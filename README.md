# Order API

사조참치 팀의 주문 도메인 백엔드 서비스입니다.  
주문/결제/쿠폰/포인트/재고 기능을 하나의 Spring Boot 애플리케이션에서 제공하며, RabbitMQ 기반 비동기 처리와 스케줄링을 함께 사용합니다.

## 1. 프로젝트 개요

### 핵심 기능
- **주문**: 주문 생성, 주문 상세 조회, 주문 상태 변경(취소/반품/배송 처리)
- **결제**: 결제 승인(Confirm), 결제 내역 조회
- **쿠폰**: 쿠폰 정책 관리, 사용자 쿠폰 발급/조회, 주문/도서 쿠폰 조회
- **포인트**: 포인트 정책 관리, 적립/사용 내역 조회, 회원 등급 조회
- **재고**: 재고 생성/수정, 재고 증감, 배치 재고 등록

### 모듈 구조
- `orders`: 주문/배송/패키징 관련 도메인
- `payment`: 결제 승인 및 결제 정보 관리
- `coupon`: 쿠폰 정책/발급/조회
- `point`: 포인트 정책, 적립 이력, 회원 등급
- `stock`: 도서 재고 관리
- `common`: 공통 예외 처리, 설정, 공용 도메인

## 2. 기술 스택

- **Java 21**
- **Spring Boot 3.4.6**
- Spring Data JPA, Validation, Web, Actuator
- Spring Cloud (Config Client, Eureka Client, OpenFeign)
- RabbitMQ (Spring AMQP)
- Querydsl
- MySQL, H2
- Test: JUnit5, Spring Boot Test, Testcontainers (MySQL/RabbitMQ)

## 3. 실행 전 준비

기본 프로필이 `prod`라서 실행 시 Config Server 연동을 시도합니다.  
로컬 단독 실행이 목적이라면 `local` 프로필로 실행하는 것을 권장합니다.

### 필수 환경
- JDK 21
- Maven Wrapper 사용 가능 환경 (`./mvnw`)
- (선택) MySQL, RabbitMQ
- (prod 프로필 사용 시) Spring Cloud Config Server

## 4. 로컬 실행 방법

### 4-1. 의존성/컴파일 확인
```bash
./mvnw clean compile
```

### 4-2. 애플리케이션 실행 (local 프로필)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 4-3. JAR 실행
```bash
./mvnw clean package
java -jar target/order-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

## 5. 테스트 실행

전체 테스트:
```bash
./mvnw test
```

특정 테스트 클래스 실행 예시:
```bash
./mvnw -Dtest=StockServiceTest test
```

> 일부 테스트는 Testcontainers 환경(Docker) 또는 메시지 브로커/DB 설정이 필요할 수 있습니다.

## 6. API 확인 방법

### Swagger UI
springdoc 의존성이 포함되어 있어 실행 후 아래 경로에서 API 문서를 확인할 수 있습니다.

- `http://localhost:8080/swagger-ui/index.html`

### HTTP 샘플 요청
`http/` 디렉터리에 도메인별 요청 예시 파일이 있습니다.

- `http/orders.http`
- `http/payment.http`
- `http/coupon.http`
- `http/point.http`
- `http/stock.http`

IntelliJ HTTP Client 또는 VS Code REST Client로 바로 실행해볼 수 있습니다.

## 7. 운영 관련 참고

- 로그 설정: `src/main/resources/logback-spring.xml`
  - `local`: 콘솔 + 파일 로그
  - `prod`: 파일 로그
  - `real`: 에러 레벨 파일 로그
- 프로파일 설정:
  - 기본: `prod`
  - `prod`에서는 `configserver:http://localhost:10379`를 import

## 8. 디렉터리 빠른 보기

```text
src/main/java/shop/sajotuna/order
├─ common
├─ coupon
├─ orders
├─ payment
├─ point
└─ stock
```

## 9. 기여/협업 팁

- 기능 단위로 패키지가 분리되어 있어, 먼저 해당 도메인의 `controller -> service -> repository/domain` 순으로 흐름을 보면 빠르게 파악할 수 있습니다.
- API 스펙 확인은 Swagger + `http/*.http` 파일을 병행하면 커뮤니케이션 비용을 크게 줄일 수 있습니다.
- 비동기 처리(RabbitMQ) 로직은 재시도/예외 처리 전략까지 함께 확인하는 것을 추천합니다.
