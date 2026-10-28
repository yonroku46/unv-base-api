---
id: 001-spring-boot-3-and-java21-compatibility
title: Spring Boot 3.3.0 및 Java 21 환경에서의 Lombok 애너테이션 처리와 디렉터리 구조 주의점
date: 2026-10-19
scope: [springboot, java21, gradle, lombok, build]
status: active
---

### 배경 및 발생 문제 (Problem / Context)
프로젝트 구축 시 `./gradlew compileJava` 실행 중 Lombok으로 자동 생성되어야 하는 `@Slf4j`의 `log` 변수 및 `@Data` / `@Builder`의 getter/setter/builder 메소드를 참조하지 못해 대량의 심볼 미해결 에러(38 errors)가 발생함.

### 근본 원인 (Root Cause)
1. `src/main/java/` 바로 아래에 점(.)이 포함된 비정상적인 디렉터리(`com.univus.base`)가 일시 생성되어 `com/univus/base/` 측 클래스와 완전 정규화 클래스명(FQCN)이 중복(duplicate class)됨.
2. javac 컴파일러는 AST 구문 분석 및 중복 클래스 검증 단계에서 치명적 중복 에러를 감지하면 애너테이션 프로세서(Lombok) 라운드를 실행하지 않고 즉시 중단되므로 후속 Lombok 코드 생성이 모두 동작하지 않음.

### 해결책 및 향후 규칙 (Rule & Solution)
1. **디렉터리 경로의 엄격한 분할**: Java 패키지 구조는 반드시 디렉터리를 점(.)이 아닌 계층으로 물리 분할하여 생성한다 (예: `com/univus/base/config`).
2. **클린 빌드를 통한 확인**: 중복 클래스 제거나 파일 이동 후에는 반드시 `./gradlew clean`을 실행하여 이전 캐시와 빌드 산출물을 완전히 제거한 후 재컴파일한다.
