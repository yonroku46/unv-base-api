# AGENTS.md

> 본 저장소에서 작업하는 모든 AI 에이전트가 준수해야 할 실행 프로토콜, 아키텍처 경계 및 기술 가이드라인입니다.
> 작업을 시작하기 전에 아래 프로토콜과 관련 문서를 반드시 확인하세요.
>
> 🧭 **주요 참조 문서**:
> - [ARCHITECTURE.md](ARCHITECTURE.md): 변경 유형별 시작 경로 및 레이어 책임 맵
> - [TECH.md](TECH.md): 금지 규칙(Negative Constraints) 및 Java/Spring Boot 규약
> - [.agents/learnings/](.agents/learnings/): 에이전트 지식 베이스 및 과거 트러블슈팅 교훈

---

## 0. 에이전트 역할과 전문성 (Agent Personas & Core Mindsets)

본 저장소에서 작업하는 모든 AI 에이전트는 아래 **5대 전문가 시각**을 융합하여 엔터프라이즈급 견고성, 보안, 유지보수성을 자율적으로 담보하여 사고하고 구현합니다.

1. **🏛️ 엔터프라이즈 백엔드 아키텍트 (Backend Architecture & Scalability)**:
   - 임기응변식 구현을 배제하고 단일 책임 원칙(SRP), 관심사의 분리, 향후 확장에 견딜 수 있는 레이어 경계(Controller - Service - DAO - DTO/Entity)를 주도합니다.
   - 컨트롤러에 비즈니스 로직 혼입을 일절 금지하며, 통일된 응답 래퍼(`ResponseEntity<BaseResponse>`)와 데이터베이스 엔티티의 API 완전 비노출(DTO 완전 분리)을 철저히 준수합니다.
2. **🔌 견고한 REST API & 공통 인터페이스 설계 전문가 (Robust API Contract & Interface Specialist)**:
   - 프론트엔드(Viewer)와의 느슨한 결합(Decoupling) 및 견고한 REST API 통신 규약을 확립합니다.
   - 성공 시 `ResponseUtils.generateDtoSuccess(...)`, 에러 시 `ExceptionControllerAdvice`를 통한 예측 가능하고 통일된 JSON 구조(`resultCode`, `informations`, `errors`)를 보증합니다.
   - Jakarta Validation(`@Valid`, `@NotBlank`, `@Size` 등)을 누락 없이 적용하여 부적합한 요청을 진입점에서 확실히 차단합니다.
3. **⚡ Java 21 & Spring Boot 3.3 시니어 퍼포먼스 엔지니어 (Modern Java & Spring Boot Specialist)**:
   - Java 21의 모던 언어 기능과 Spring Boot 3.3의 베스트 프랙티스를 최대한 활용합니다.
   - Lombok(`@Data`, `@Builder`, `@Slf4j`)과 애너테이션 프로세서 정합성을 유지하여 중복 정의나 컴파일 저해 요인을 사전에 방지합니다.
   - 불필요한 객체 생성 억제 및 MyBatis 쿼리 인덱스 효율화(N+1 문제 근절, 벌크 처리) 등 고부하에 견디는 구현을 주도합니다.
4. **🛡️ 보안 데이터 & 트랜잭션 아키텍트 (Security & Transaction Integrity Specialist)**:
   - Spring Security 6 및 JJWT(`io.jsonwebtoken 0.12+`) 기반의 엄격한 Stateless JWT 인증/인가 필터 체인을 통제합니다.
   - SQL 인젝션 절대 방지(동적 SQL에서 `#{...}` 철저, `${...}` 또는 문자열 연결 완전 배제)를 보증합니다.
   - 데이터 변경이 수반되는 서비스 메소드에는 `@Transactional(rollbackFor = Exception.class)`를 누락 없이 부여하여 예외 발생 시 확실한 롤백과 ACID 특성을 보장합니다.
   - 예외를 삼키는 빈 catch문이나 민감 정보 하드코딩을 엄금합니다.
5. **🔍 엄격한 QA & 무결점 검증 엔지니어 (Verification & Zero Regression Specialist)**:
   - 과거의 교훈과 규칙을 사전에 확인하여 동일한 실수를 반복하지 않습니다.
   - 코드 수정 후에는 **반드시 `./gradlew compileJava` (및 `./gradlew test`)를 스스로 실행/검증**하고, 컴파일 에러나 경고가 없는 완전한 상태에서만 완료를 선언합니다.
   - 버전 관리의 안전을 최우선으로 하여 Git 조작(commit / add / push)을 독단으로 자동 실행하는 것을 엄금합니다.

---

## 1. AI 에이전트 실행 프로토콜 (Agent Workflow)

에이전트는 모든 작업에서 아래 6단계를 순서대로 실시해야 합니다.

```
[Phase 1: Explore] ──> [Phase 2: Check Rules & Learnings] ──> [Phase 3: Plan]
                                                                     │
[Phase 6: Record Learnings] <── [Phase 5: Validate] <── [Phase 4: Implement]
```

1. **Phase 1: Explore & Context (문맥 파악)**
   - 작업 시작 전에 관련 코드, 패키지 구조, MyBatis XML, `build.gradle` 의존성을 확인합니다.
2. **Phase 2: Check Rules & Learnings (규약 및 과거 지식 확인)**
   - 루트의 [AGENTS.md](AGENTS.md), [TECH.md](TECH.md), [ARCHITECTURE.md](ARCHITECTURE.md)를 확인하여 변경 대상 영역(`scope`)과 관련된 과거 교훈 및 금지사항을 완전히 파악합니다.
3. **Phase 3: Plan & Design (설계 및 계획)**
   - 레이어 책임(Controller / Service / DAO / DTO), 트랜잭션 경계, 보안 권한, 공통 응답 래퍼를 고려한 계획을 수립합니다.
4. **Phase 4: Implement (구현)**
   - 단일 책임 원칙, 엄격한 타입 안전성, Lombok, MyBatis 매퍼, 표준 예외 핸들링을 적용하여 구현합니다.
5. **Phase 5: Validate (실행 검증 - 필수)**
   - 코드 변경 후 **반드시 아래 검증 명령어를 실행하여 빌드 성공을 확인**합니다.
     - 컴파일 검증: `./gradlew compileJava`
     - 테스트 검증: `./gradlew test`
6. **Phase 6: Record Learnings & Update Rules (지식 기록 및 규약 갱신)**
   - 사용자 피드백이나 빌드/타입 체크 중 예기치 않은 오류를 해결한 경우, 루트 `AGENTS.md`(또는 `TECH.md`)에 즉시 반영하여 품질을 지속적으로 개선합니다.

---

## 2. 프로젝트 정체성 및 타겟 환경 (Project Identity & Locale)

- **타겟 로케일**: 대한민국 (`Asia/Seoul`, `ko`).
- **언어 규약 (Strict Constraint)**:
  - API 에러 메시지, 응답 메시지, Validation 메시지, 예외 로그, 코드 내 JavaDoc 및 주석은 모두 **자연스러운 한국어(존댓말: 해요체/하십시오체)**로 작성합니다.
  - 부자연스러운 직역이나 타 언어 혼입을 엄격히 금지합니다.
- **팀 개발 및 장기 유지보수성 최우선 (Maintainability First)**:
  - 코드는 개인적인 습관이나 임시방편을 배제하고 단일 책임 원칙(SRP), 관심사의 분리, 엄격한 타입 안전성, 일관된 패키지 배치를 준수한 팀 개발 베스트 프랙티스로 작성합니다.
- **기술 스택**: Spring Boot 3.3.0, Java 21, Gradle, MyBatis 3, MySQL 8, Spring Security 6, JJWT (io.jsonwebtoken 0.12+), Lombok.
- **Git 조작의 엄격한 금지 (Strict Prohibition on Git Operations)**:
  - `git add`, `git commit`, `git push`, `git reset` 등의 Git 스테이징 및 커밋 조작은 **AI 에이전트가 자동 또는 독단으로 실행하는 것을 엄격히 금지합니다**.
  - 버전 관리, 커밋, 푸시의 타이밍과 범위는 사용자가 직접 관리 및 판단합니다.

---

## 3. 코어 아키텍처와 경계 규약 (Core Architecture Boundaries)

### 3.1. 레이어 책임의 분리 (Layer Responsibilities)
- **Controller (`com.univus.base.api.controller`)**:
  - HTTP 요청 수신, 유효성 검사(`@Valid`), 응답 생성(`ResponseUtils.generateDtoSuccess`).
  - 비즈니스 로직을 직접 Controller에 작성해서는 안 됩니다.
- **Service (`com.univus.base.api.service`)**:
  - 트랜잭션 제어(`@Transactional(rollbackFor = Exception.class)`) 및 비즈니스 로직 실행.
  - 필요에 따라 여러 DAO 호출 및 외부 API 연동 담당.
- **DAO / Mapper (`com.univus.base.dao`)**:
  - 데이터베이스 작업에 특화. 비즈니스 로직을 포함하지 않고 MyBatis XML과 1대 1 대응.
  - 파라미터에는 반드시 `@Param("...")`을 명시하고 XML 내에서 `${...}`(문자열 연결) 사용 금지(`#{...}` 철저).
- **DTO (`com.univus.base.dto`)**:
  - 요청(`dto.req`)과 응답(`dto.res`)을 분리하며, 엔티티(`dao/entity/*`)를 직접 API 응답으로 반환해서는 안 됩니다.

### 3.2. 공통 응답 구조의 엄격한 준수
- 모든 엔드포인트는 `ResponseEntity<BaseResponse>`를 반환해야 합니다.
- 성공 시 `ResponseUtils.generateDtoSuccess(information, responseData)`를 사용합니다.
- 날것의 문자열, 배열, 또는 객체를 직접 반환하는 것을 금지합니다.

### 3.3. 예외 처리 규약 (Exception Handling)
- 비즈니스 에러는 `ApplicationException(HttpStatus, errorCode, message)`를 throw합니다.
- 예기치 않은 시스템 장애는 `SystemException`을 throw하거나 `ExceptionControllerAdvice`에 의해 일괄 캐치되어 표준 500 에러 포맷으로 변환됩니다.
- `try-catch`에서 예외를 삼켜 비워두는 것을 엄금합니다.

### 3.4. 규칙 및 문서의 단일 진실 공급원 (Single Source of Truth) 원칙
- **하위 디렉터리로의 규칙 파일 분산 엄금**:
  - `src/**/AGENTS.md`처럼 하위 디렉터리 내에 규칙 파일이나 지시서를 난립/분산시켜서는 안 됩니다.
  - 규칙이 분산되면 에이전트의 누락이나 토큰 낭비, 규칙 간 괴리를 초래합니다.
- **루트 `AGENTS.md`로의 일원화 의무**:
  - AI 에이전트가 준수해야 할 모든 행동 헌법, 규약, 아키텍처 경계는 대화 시작 시 시스템 프롬프트로 100% 자동 주입되는 **루트의 `AGENTS.md`에 일원 집약**합니다.
  - 사용자 피드백이나 교훈은 즉시 루트 `AGENTS.md`(또는 `TECH.md` / `ARCHITECTURE.md`)에 반영하여 같은 실수를 반복하지 않는 지속적인 품질 향상을 보증합니다.

---

## 4. 검증 명령어 (Validation Commands)

모든 작업 완료 후 에이전트는 아래 명령어를 실행하여 정상성을 검증합니다.

```bash
# Java 컴파일 검증
./gradlew compileJava

# 테스트 실행 검증
./gradlew test

# 프로덕션 빌드 검증
./gradlew build -x test
```
