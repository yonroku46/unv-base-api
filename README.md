# Web Service API Boilerplate (`unv-base-api`)

Spring Boot 3.3.0 (Java 21)을 기반으로 한 RESTful API 표준 보일러플레이트입니다.
MyBatis 3, Spring Security 6, JWT 인증 인프라, 통일된 응답 래퍼(`BaseResponse`), 다국어 메시지 인프라, 그리고 AI 에이전트용 행동 규범 및 지식 베이스를 표준 탑재하고 있습니다.

---

## 🚀 신규 서비스 시작 시 설정 (Quick Setup)

본 저장소를 복제하여 새로운 서비스를 시작할 때는 **아래 항목을 변경**하면 즉시 프로젝트 기반이 마련됩니다.

### 1. `settings.gradle`
```groovy
rootProject.name = 'your-new-service-api'
```

### 2. `build.gradle`
```groovy
group = 'com.yourcompany.service'
```

### 3. `src/main/resources/application-dev.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database_name?characterEncoding=UTF-8&serverTimezone=Asia/Seoul
    username: your_db_user
    password: your_db_password
```

---

## 🛠️ 기술 스택 (Tech Stack)

- **언어 및 런타임**: Java 21 (LTS)
- **프레임워크**: Spring Boot 3.3.0
- **빌드 도구**: Gradle 8.5 (Wrapper 포함)
- **데이터베이스 연동**: MyBatis 3.0.3, MySQL Connector/J
- **보안 및 인증**: Spring Security 6, JJWT 0.12.3 (HMAC-SHA256)
- **유효성 검사**: Hibernate Validator (Jakarta Bean Validation)
- **로깅**: SLF4J, Logback (롤링 파일 어펜더 구성 완료)
- **코드 생성**: Project Lombok 1.18.32
- **ID 생성**: KSUID (`com.github.ksuid:ksuid:1.1.0`), JNanoId

---

## 🧭 아키텍처 및 에이전트 규약

- [ARCHITECTURE.md](ARCHITECTURE.md): 변경 유형별 시작 경로(Starting Paths) 및 레이어 책임 맵
- [TECH.md](TECH.md): 금지 규칙(Negative Constraints) 및 Java/Spring Boot 규약
- [AGENTS.md](AGENTS.md): AI 에이전트의 6단계 작업 프로토콜
- [.agents/learnings/](.agents/learnings/): 과거 트러블 및 해결책을 축적하는 지식 베이스
- [.agents/skills/](.agents/skills/): 엔드포인트 및 DAO 추가 표준 가이드

---

## 💻 실행 및 검증 명령어 (Commands)

```bash
# 애플리케이션 실행 (http://localhost:8080/api)
./gradlew bootRun

# Java 컴파일 검증
./gradlew compileJava

# 테스트 클래스 컴파일 검증
./gradlew compileTestJava

# 단위 테스트 실행
./gradlew test

# 프로덕션 빌드 (JAR 생성)
./gradlew build -x test
```

---

## 🔌 표준 제공 엔드포인트

- **헬스 체크**: `GET /api/health-check` (200 OK & 서버 실행 상태 메시지 반환)
- **샘플 데이터 조회**: `GET /api/sample/{id}`
- **샘플 데이터 등록**: `POST /api/sample`
