# TECH.md

> 본 문서는 본 프로젝트의 코드 품질, 타입 안전성, 의존성 관리, 그리고 **AI 에이전트가 엄격히 준수해야 할 11대 금지 규칙(Negative Constraints)**을 규정합니다.

---

## 1. 11대 금지 규칙 (Negative Constraints - STRICT)

에이전트는 아래 11개 항목을 위반해서는 안 되며, 위반한 코드는 작업 완료로 간주되지 않습니다.

1. **날것의 SQL 문자열 연결 금지 (SQL 인젝션 방지)**:
   - MyBatis XML이나 Java 코드 내에서 문자열 연결(`+` 또는 `${...}`)을 통한 동적 SQL 조립을 원칙적으로 금지합니다. 반드시 파라미터 플레이스홀더(`#{...}`)를 사용하세요.
2. **예외 삼키기(빈 catch문) 금지**:
   - `catch (Exception e) {}`처럼 로그도 남기지 않고 예외를 무시하는 것을 엄금합니다. 반드시 `log.error` / `log.warn`을 기록하거나 적절한 `ApplicationException` / `SystemException`으로 다시 throw하세요.
3. **날것의 응답 직접 반환 금지 (BaseResponse 래핑 철저)**:
   - 컨트롤러에서 원시 데이터, 문자열, Map 등을 직접 반환하는 것을 금지합니다. 반드시 `ResponseUtils.generateDtoSuccess(...)`를 사용하여 통일된 `BaseResponse` 구조로 반환하세요.
4. **엔티티(Entity)의 API 노출 금지 (DTO 완전 분리)**:
   - 데이터베이스 테이블과 매핑되는 엔티티(`dao/entity/*`)를 컨트롤러 요청/응답에 직접 사용해서는 안 됩니다. 반드시 전용 DTO(`dto/req/*`, `dto/res/*`)를 정의하고 필드 복사(`ResponseUtils.copyObject` 등)를 수행하세요.
5. **표준 출력(`System.out.println`) 금지 (`@Slf4j` 통일)**:
   - 디버깅 목적을 포함하여 표준 출력 직접 사용을 금지합니다. 반드시 클래스에 `@Slf4j` 애너테이션을 부여하고 적절한 로그 레벨(`log.debug`, `log.info`, `log.warn`, `log.error`)로 출력하세요.
6. **보안 정보/시크릿 하드코딩 금지**:
   - JWT 시크릿, DB 비밀번호, API 키 등의 민감 정보를 Java 소스 코드 내에 직접 작성해서는 안 됩니다. 반드시 `application-dev.yml`, 환경 변수, 또는 시크릿 관리 프로퍼티를 참조하세요.
7. **무단 외부 라이브러리 추가 금지**:
   - 사용자의 명시적 허가 없이 `build.gradle`에 새로운 서드파티 의존성을 임의로 추가하는 것을 금지합니다.
8. **타임존 및 로케일 규약 위반 금지**:
   - 본 프로젝트의 표준 타임존은 한국 표준시(`Asia/Seoul`), 표준 로케일은 한국어(`ko`)입니다. 날짜/시간 처리 및 DB 연결 시 타임존을 혼재시키지 마세요.
9. **비한국어 에러 메시지 및 주석 노출 금지**:
   - API 클라이언트에 반환되는 에러 메시지, 유효성 검사 메시지, 코드 내 주석은 모두 **자연스럽고 정중한 한국어(해요체/하십시오체)**로 작성하세요.
10. **미검증 상태에서의 작업 완료 선언 금지**:
    - 코드 수정 후 `./gradlew compileJava`를 통한 컴파일 성공을 확인하지 않고 작업을 종료하는 것을 금지합니다.
11. **Git 조작(commit / add / push)의 자동 실행 엄금**:
    - `git add`, `git commit`, `git push`, `git reset` 등의 Git 조작을 AI 에이전트가 자동 또는 독단으로 실행하는 것을 엄격히 금지합니다. 커밋과 스테이징의 판단 및 실행은 사용자가 직접 수행합니다.

---

## 2. 코딩 규약 및 스타일

### 2.1. 패키지 구조와 디렉터리 계층의 정합성 (Lombok 애너테이션 처리 보장)
- Java 패키지 구조는 반드시 디렉터리를 점(.)이 아닌 계층으로 물리 분할하여 생성합니다 (예: `com/univus/base/config`).
- 동일한 완전 정규화 이름(FQCN)의 클래스 중복(duplicate class)이 발생하면 javac 컴파일러가 AST 파싱 단계에서 중단되어 Lombok 코드 생성(`log`, getter, setter, builder)이 무력화되므로 디렉터리 무결성을 엄격히 유지합니다.

### 2.2. Lombok의 적절한 활용
- Getter, Setter, equals, hashCode, toString은 `@Data`로 자동 생성합니다.
- 응답 DTO 및 엔티티 생성에는 `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`를 적극 활용합니다.
- 클래스 로깅에는 클래스 선언부에 `@Slf4j`를 부여합니다.

### 2.3. 유효성 검사(Validation) 규약
- 요청 DTO 항목에는 Jakarta Validation(`@NotNull`, `@NotBlank`, `@Size`, `@Pattern`, `@Min`, `@Max`)을 누락 없이 부여하고, `message` 속성에 사용자 친화적인 자연스러운 한국어를 설정하세요.

### 2.4. 트랜잭션 관리
- 변경 계열(INSERT, UPDATE, DELETE) 서비스 메소드에는 반드시 `@Transactional(rollbackFor = Exception.class)`를 부여하여 예외 발생 시 안전하게 롤백되도록 구현합니다.
