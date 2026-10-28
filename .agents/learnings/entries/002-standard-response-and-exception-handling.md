---
id: 002-standard-response-and-exception-handling
title: BaseResponse 래퍼 규약과 ExceptionControllerAdvice를 통한 표준 에러 핸들링
date: 2026-10-19
scope: [api, response, exception, controller]
status: active
---

### 배경 및 발생 문제 (Problem / Context)
API 엔드포인트에서 컨트롤러가 개별적으로 Map이나 원시 데이터를 반환하거나, 예외 발생 시 Spring Boot 기본 WhiteLabel 에러나 스택 트레이스가 클라이언트에 노출되면 프론트엔드(`unv-base-viewer` 등) 측의 Axios 인터셉터 기반 일관된 에러 핸들링이 훼손됨.

### 근본 원인 (Root Cause)
1. 컨트롤러 측에서 응답 구조의 통일 기준을 지키지 않으면 클라이언트 측의 성공 판정(`resultCode == 0`) 및 에러 메시지 추출(`errors`, `informations`) 로직이 복잡해짐.
2. `@RestControllerAdvice`를 통한 중앙집권적 예외 핸들링이 누락되면 미처리 예외가 HTTP 500 HTML 형식으로 반환됨.

### 해결책 및 향후 규칙 (Rule & Solution)
1. **전체 엔드포인트에서 `BaseResponse` 반환**: 모든 컨트롤러 메소드는 `ResponseEntity<BaseResponse>`를 반환하고 `ResponseUtils.generateDtoSuccess(...)`를 사용한다.
2. **`ExceptionControllerAdvice`를 통한 통일 인터셉트**:
   - 비즈니스 에러: `ApplicationException`을 throw
   - 유효성 검사 에러: `MethodArgumentNotValidException`을 자동 캐치하여 `errors` 리스트로 변환
   - 시스템 에러: 예기치 않은 `Exception`을 일괄 캐치하여 로그를 기록하고 일률 500의 `BaseResponse`(에러 메시지 포함)로 반환한다.
