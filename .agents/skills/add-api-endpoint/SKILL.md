---
name: add-api-endpoint
description: 새로운 API 엔드포인트를 추가할 때 Controller, Service, DTO, 유효성 검사 및 BaseResponse 규약에 맞춰 안전하게 구현하는 스킬
---

# add-api-endpoint 스킬 가이드

본 스킬은 `unv-base-api`에서 새로운 API 엔드포인트를 추가할 때의 표준 절차와 모범 사례를 규정합니다.

## 1. 구현 절차 (Implementation Flow)

```
[1. DTO 정의] ──> [2. Service 구현] ──> [3. Controller 구현] ──> [4. 검증]
```

### 단계 1: 요청 및 응답 DTO 작성
- 요청 DTO: `com.univus.base.dto.req.<Feature>Request.java`
  - Jakarta Validation 애너테이션(`@NotNull`, `@NotBlank`, `@Size` 등)을 부여하고 한국어 유효성 검사 메시지를 설정합니다.
- 응답 DTO: `com.univus.base.dto.res.<feature>.<Feature>ResponseData.java`
  - 반드시 `ResponseData`를 상속합니다.
  - `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(callSuper = true)`를 부여합니다.

```java
// 예: com.univus.base.dto.res.user.UserDetailResponseData.java
package com.univus.base.dto.res.user;

import com.univus.base.dto.res.core.ResponseData;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDetailResponseData extends ResponseData {
    private String userId;
    private String userName;
    private String mail;
}
```

### 단계 2: 서비스 계층 구현
- 위치: `com.univus.base.api.service.<feature>.<Feature>Service.java`
- 클래스에 `@Service` 및 `@Slf4j`를 부여합니다.
- 데이터 변경 메소드에는 `@Transactional(rollbackFor = Exception.class)`를 부여합니다.
- 유효하지 않은 조작이나 리소스 부재 시 `ApplicationException`을 throw합니다.

### 단계 3: 컨트롤러 계층 구현
- 위치: `com.univus.base.api.controller.<feature>.<Feature>Controller.java`
- 클래스에 `@RestController` 및 `@RequestMapping("/<path>")`를 부여하고 `BaseController`를 상속합니다.
- 메소드 인자에 `@Valid @RequestBody`를 사용합니다.
- 반환값은 반드시 `ResponseEntity<BaseResponse>`로 하며 `ResponseUtils.generateDtoSuccess(...)`를 호출하여 반환합니다.

```java
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getUser(@PathVariable("id") String id) {
        UserDetailResponseData data = userService.getUserDetail(id);
        String msg = getMessage(MessageIdConst.I_GETTING_SUCCESS, "사용자 정보");
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_GETTING_SUCCESS, msg), data)
        );
    }
}
```

### 단계 4: 컴파일 및 검증
아래 명령어를 실행하여 에러가 없음을 확인합니다.

```bash
./gradlew compileJava
```
