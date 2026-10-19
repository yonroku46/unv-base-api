---
name: add-api-endpoint
description: 新しいAPIエンドポイントを追加する際、Controller、Service、DTO、バリデーション、およびBaseResponse規約に沿って安全に実装するスキル
---

# add-api-endpoint スキルガイド

本スキルは、`unv-base-api` において新しいAPIエンドポイントを追加する際の標準手順とベストプラクティスを定めます。

## 1. 実装手順（Implementation Flow）

```
[1. DTOの定義] ──> [2. Serviceの実装] ──> [3. Controllerの実装] ──> [4. 検証]
```

### ステップ 1: リクエスト・レスポンスDTOの作成
- リクエストDTO: `com.univus.base.dto.req.<Feature>Request.java`
  - Jakarta Validationアノテーション（`@NotNull`, `@NotBlank`, `@Size` など）を付与し、日本語のバリデーションメッセージを設定します。
- レスポンスDTO: `com.univus.base.dto.res.<feature>.<Feature>ResponseData.java`
  - 必ず `ResponseData` を継承します。
  - `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(callSuper = true)` を付与します。

```java
// 例: com.univus.base.dto.res.user.UserDetailResponseData.java
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

### ステップ 2: サービス層の実装
- 配置場所: `com.univus.base.api.service.<feature>.<Feature>Service.java`
- クラスに `@Service` および `@Slf4j` を付与します。
- データ変更メソッドには `@Transactional(rollbackFor = Exception.class)` を付与します。
- 不正な操作やリソース不存在時は、`ApplicationException` をスローします。

### ステップ 3: コントローラー層の実装
- 配置場所: `com.univus.base.api.controller.<feature>.<Feature>Controller.java`
- クラスに `@RestController` および `@RequestMapping("/<path>")` を付与し、`BaseController` を継承します。
- メソッド引数に `@Valid @RequestBody` を使用します。
- 戻り値は必ず `ResponseEntity<BaseResponse>` とし、`ResponseUtils.generateDtoSuccess(...)` を呼び出して返却します。

```java
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getUser(@PathVariable("id") String id) {
        UserDetailResponseData data = userService.getUserDetail(id);
        String msg = getMessage(MessageIdConst.I_GETTING_SUCCESS, "ユーザー情報");
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_GETTING_SUCCESS, msg), data)
        );
    }
}
```

### ステップ 4: コンパイルおよび動作確認
以下のコマンドを実行してエラーがないことを確認します。

```bash
./gradlew compileJava
```
