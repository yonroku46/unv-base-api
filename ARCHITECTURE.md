# ARCHITECTURE.md

> 本文書は、本プロジェクトのパッケージ構造、レイヤー境界、および**変更種別ごとの着手パス（Starting Paths）**を定義するアーキテクチャ設計図です。
> 新しいサービスを立ち上げたり機能を拡張する際、すべてのAIエージェントはこの設計図に基づいて適切な変更ポイントを特定しなければなりません。

---

## 1. 変更種別ごとの着手パス（Starting Paths by Change Type）

AIエージェントは、作業内容に応じて以下の起点ファイル・パッケージを確認して実装を開始してください。

| 変更種別 | 起点ファイル / パッケージ | 作業フロー |
| :--- | :--- | :--- |
| **新規APIエンドポイントの追加** | `src/main/java/com/univus/base/api/controller/` | 1. `dto/req/`および`dto/res/`にリクエスト・レスポンスDTOを定義<br>2. `api/service/`にビジネスロジック・トランザクションを実装<br>3. `api/controller/`にエンドポイントを作成し`ResponseUtils`でラップ返却 |
| **新規DAO・データベース操作の追加** | `src/main/java/com/univus/base/dao/` | 1. `dao/entity/`にテーブル対応エンティティを作成<br>2. `dao/`に`@Mapper`インターフェースを定義<br>3. `src/main/resources/sql/`にMyBatis XMLマッパーを作成 |
| **認証・権限管理の変更** | `com/univus/base/config/SecurityConfig.java` | 1. `common/enums/Roles.java`にロールを定義・追加<br>2. `SecurityConfig.java`のURL認可ルールを更新<br>3. 必要に応じて`@CheckToken`や`@LoginToken`をコントローラーに付与 |
| **共通メッセージ・多言語文言の追加** | `src/main/resources/ValidationMessages_ja.properties` | 1. `ValidationMessages_ja.properties`および`ValidationMessages.properties`にメッセージキーを追加<br>2. `common/constants/MessageIdConst.java`に定数キーを登録 |
| **例外ハンドリングの追加・拡張** | `com/univus/base/exception/ExceptionControllerAdvice.java` | 1. `exception/`に新規カスタム例外クラスを作成<br>2. `ExceptionControllerAdvice.java`に`@ExceptionHandler`メソッドを追加 |
| **データベース・環境設定の変更** | `src/main/resources/application.yml` | 1. `application.yml`および`application-dev.yml`の接続情報・パラメータを更新 |

---

## 2. レイヤーアーキテクチャと責務の境界

```
[HTTP Client / Viewer (unv-base-viewer)]
       │
       ▼
[Filter Layer]
   └─ JwtAuthFilter (JWTトークン検証、SecurityContextHolder認証情報設定)
       │
       ▼
[Controller Layer (`com.univus.base.api.controller`)]
   ├─ BaseController (共通セッションユーザー情報取得、多言語メッセージ取得)
   └─ *Controller (リクエストバリデーション、レスポンスBaseResponse包装)
       │
       ▼
[Service Layer (`com.univus.base.api.service`)]
   └─ *Service (@Transactionalトランザクション制御、ビジネスドメインロジック)
       │
       ▼
[DAO / Mapper Layer (`com.univus.base.dao`)]
   ├─ *Dao (@Mapperインターフェース)
   ├─ entity/* (データバインドエンティティ)
   └─ resources/sql/**/*.xml (MyBatis SQL定義ファイル)
       │
       ▼
[Database (MySQL 8+)]
```

### 2.1. コントローラー層の原則
- コントローラー内にはビジネスロジックや直接のSQL呼び出しを記述してはなりません。
- すべてのリクエストDTOには、Jakarta Validation（`@Valid`, `@NotBlank`, `@Size` 等）を付与して検証します。
- 戻り値は必ず `ResponseEntity<BaseResponse>` とし、直接エンティティや生データを返却してはなりません。

### 2.2. サービス層の原則
- データの変更（登録・更新・削除）を伴うメソッドには必ず `@Transactional(rollbackFor = Exception.class)` を付与します。
- ビジネスルール違反時は、適切なHTTPステータスコードを添えて `ApplicationException` をスローします。

### 2.3. DAOおよびMyBatisマッパー層の原則
- DAOメソッドとXMLマッパーのIDは厳格に1対1で一致させます。
- キャメルケースとスネークケースの自動マッピング（`mapUnderscoreToCamelCase: true`）が有効化されているため、マッピング命名規則を遵守してください。
- 動的SQLにおけるパラメータ結合は必ずプレースホルダー（`#{...}`）を用い、`${...}` や文字列連結によるSQLインジェクション脆弱性を完全に排除します。

### 2.4. DTO層およびデータ変換の原則
- データベーステーブルと1対1で対応するエンティティ（`dao/entity/*`）をコントローラーの入出力として露出することを厳禁とします。
- 必ずリクエスト専用DTO（`dto/req/*`）およびレスポンス専用DTO（`dto/res/*`）を定義し、型安全なデータバインディングとバリデーションを徹底します。

### 2.5. 例外ハンドリングおよび共通レスポンス原則
- ビジネスエラーは `ApplicationException`、予期せぬ例外は `SystemException` としてスローし、`ExceptionControllerAdvice` により一括インターセプトします。
- 成功・失敗を問わず、すべてのHTTPレスポンスは統一された `BaseResponse`（`resultCode`, `informations`, `errors`）フォーマットで返却され、クライアント側の一貫したエラーハンドリングを保証します。
