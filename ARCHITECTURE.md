# ARCHITECTURE.md

> 본 문서는 본 프로젝트의 패키지 구조, 레이어 경계, 그리고 **변경 유형별 시작 경로(Starting Paths)**를 정의하는 아키텍처 설계서입니다.
> 새로운 서비스를 구축하거나 기능을 확장할 때, 모든 AI 에이전트는 이 설계서를 바탕으로 적절한 변경 지점을 식별해야 합니다.

---

## 1. 변경 유형별 시작 경로 (Starting Paths by Change Type)

AI 에이전트는 작업 내용에 따라 아래 기점 파일 및 패키지를 확인하고 구현을 시작하세요.

| 변경 유형 | 기점 파일 / 패키지 | 작업 흐름 |
| :--- | :--- | :--- |
| **신규 API 엔드포인트 추가** | `src/main/java/com/univus/base/api/controller/` | 1. `dto/req/` 및 `dto/res/`에 요청/응답 DTO 정의<br>2. `api/service/`에 비즈니스 로직 및 트랜잭션 구현<br>3. `api/controller/`에 엔드포인트 생성 후 `ResponseUtils`로 래핑 반환 |
| **신규 DAO 및 데이터베이스 작업 추가** | `src/main/java/com/univus/base/dao/` | 1. `dao/entity/`에 테이블 대응 엔티티 생성<br>2. `dao/`에 `@Mapper` 인터페이스 정의<br>3. `src/main/resources/sql/`에 MyBatis XML 매퍼 생성 |
| **인증 및 권한 관리 변경** | `com/univus/base/config/SecurityConfig.java` | 1. `common/enums/Roles.java`에 역할 정의/추가<br>2. `SecurityConfig.java` URL 인가 규칙 갱신<br>3. 필요 시 `@CheckToken` 또는 `@LoginToken`을 컨트롤러에 부여 |
| **공통 메시지 및 다국어 문구 추가** | `src/main/resources/ValidationMessages_ko.properties` | 1. `ValidationMessages_ko.properties` 및 `ValidationMessages.properties`에 메시지 키 추가<br>2. `common/constants/MessageIdConst.java`에 상수 키 등록 |
| **예외 핸들링 추가 및 확장** | `com/univus/base/exception/ExceptionControllerAdvice.java` | 1. `exception/`에 신규 커스텀 예외 클래스 생성<br>2. `ExceptionControllerAdvice.java`에 `@ExceptionHandler` 메소드 추가 |
| **데이터베이스 및 환경 설정 변경** | `src/main/resources/application.yml` | 1. `application.yml` 및 `application-dev.yml` 접속 정보/파라미터 갱신 |

---

## 2. 레이어 아키텍처와 책임 경계

```
[HTTP Client / Viewer (unv-base-viewer)]
       │
       ▼
[Filter Layer]
   └─ JwtAuthFilter (JWT 토큰 검증, SecurityContextHolder 인증 정보 설정)
       │
       ▼
[Controller Layer (`com.univus.base.api.controller`)]
   ├─ BaseController (공통 세션 사용자 정보 조회, 다국어 메시지 조회)
   └─ *Controller (요청 유효성 검사, 응답 BaseResponse 래핑)
       │
       ▼
[Service Layer (`com.univus.base.api.service`)]
   └─ *Service (@Transactional 트랜잭션 제어, 비즈니스 도메인 로직)
       │
       ▼
[DAO / Mapper Layer (`com.univus.base.dao`)]
   ├─ *Dao (@Mapper 인터페이스)
   ├─ entity/* (데이터 바인딩 엔티티)
   └─ resources/sql/**/*.xml (MyBatis SQL 정의 파일)
       │
       ▼
[Database (MySQL 8+)]
```

### 2.1. 컨트롤러 계층의 원칙
- 컨트롤러 내에 비즈니스 로직이나 직접적인 SQL 호출을 작성해서는 안 됩니다.
- 모든 요청 DTO에는 Jakarta Validation(`@Valid`, `@NotBlank`, `@Size` 등)을 부여하여 검증합니다.
- 반환값은 반드시 `ResponseEntity<BaseResponse>`로 감싸야 하며, 엔티티나 원시 데이터를 직접 반환해서는 안 됩니다.

### 2.2. 서비스 계층의 원칙
- 데이터 변경(등록, 수정, 삭제)을 수반하는 메소드에는 반드시 `@Transactional(rollbackFor = Exception.class)`를 부여합니다.
- 비즈니스 규칙 위반 시 적절한 HTTP 상태 코드와 함께 `ApplicationException`을 throw합니다.

### 2.3. DAO 및 MyBatis 매퍼 계층의 원칙
- DAO 메소드와 XML 매퍼의 ID는 엄격히 1대 1로 일치시킵니다.
- 카멜케이스와 스네이크케이스의 자동 매핑(`mapUnderscoreToCamelCase: true`)이 활성화되어 있으므로 네이밍 규칙을 준수합니다.
- 동적 SQL의 파라미터 결합은 반드시 플레이스홀더(`#{...}`)를 사용하여 SQL 인젝션 취약점을 원천 배제합니다.

### 2.4. DTO 계층 및 데이터 변환 원칙
- 데이터베이스 테이블과 1대 1 매핑되는 엔티티(`dao/entity/*`)를 컨트롤러 입출력으로 노출하는 것을 엄금합니다.
- 반드시 요청 전용 DTO(`dto/req/*`) 및 응답 전용 DTO(`dto/res/*`)를 정의하여 타입 안전한 데이터 바인딩을 적용합니다.

### 2.5. 예외 핸들링 및 공통 응답 원칙
- 비즈니스 에러는 `ApplicationException`, 예기치 않은 예외는 `SystemException`으로 throw하며, `ExceptionControllerAdvice`에서 일괄 인터셉트합니다.
- 성공 및 실패 여부와 관계없이 모든 HTTP 응답은 통일된 `BaseResponse`(`resultCode`, `informations`, `errors`) 포맷으로 반환되어 클라이언트 측의 일관된 에러 핸들링을 보장합니다.
