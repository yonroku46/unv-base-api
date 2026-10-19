# AGENTS.md

> 本リポジトリで作業するすべてのAIエージェントが遵守すべき実行プロトコル、アーキテクチャ境界、および技術ガイドラインです。
> 作業を開始する前に、以下のプロトコルと関連ドキュメントを必ず確認してください。
>
> 🧭 **主要参照ドキュメント**:
> - [ARCHITECTURE.md](ARCHITECTURE.md): 変更種別ごとの着手パスおよびレイヤー責務マップ
> - [TECH.md](TECH.md): 10大禁止事項（Negative Constraints）およびJava/Spring Boot規約
> - [.agents/learnings/](.agents/learnings/): エージェントナレッジベースおよび過去の知見・教訓

---

## 1. AIエージェント実行プロトコル（Agent Workflow）

エージェントはすべての作業において、以下の6段階を順番に実施しなければなりません。

```
[Phase 1: Explore] ──> [Phase 2: Check Learnings] ──> [Phase 3: Plan]
                                                           │
[Phase 6: Record Learnings] <── [Phase 5: Validate] <── [Phase 4: Implement]
```

1. **Phase 1: Explore & Context（文脈把握）**
   - 作業開始前に、関連コード、パッケージ構造、`build.gradle`の依存関係を確認します。
2. **Phase 2: Check Learnings（知見の確認）**
   - [`.agents/learnings/`](.agents/learnings/)ディレクトリを確認し、変更対象の領域（`scope`）に関連する過去の教訓や注意事項を読み込みます。
3. **Phase 3: Plan & Design（設計・計画）**
   - レイヤー責務（Controller / Service / DAO / DTO）、トランザクション境界、セキュリティ権限、共通レスポンスラップを考慮した計画を策定します。
4. **Phase 4: Implement（実装）**
   - 単一責任原則、厳格な型安全、Lombok、MyBatisマッパー、標準例外ハンドリングを用いて実装します。
5. **Phase 5: Validate（実行検証 - 必須）**
   - コード変更後、**必ず以下の検証コマンドを実行し、ビルド成功を確認**します。
     - コンパイル検証: `./gradlew compileJava`
     - テスト検証: `./gradlew test`
6. **Phase 6: Record Learnings（知見の記録）**
   - ユーザーからのフィードバックや、ビルド・型チェックでの予期せぬエラーを解決した場合は、[`.agents/learnings/entries/<id>.md`](.agents/learnings/entries/)に新たな教訓を記録し、今後の作業品質を持続的に改善します。

---

## 2. プロジェクトのアイデンティティとターゲット環境（Project Identity & Locale）

- **ターゲットロケール**: 日本（`Asia/Tokyo`, `ja`）。
- **言語規約（Strict Constraint）**:
  - APIエラーメッセージ、レスポンスメッセージ、Validationメッセージ、例外ログ、コード内JavaDocおよびコメントは、すべて**自然な日本語（丁寧語/敬体: です・ます）**で記述します。
  - 不自然な直訳や他言語の混入を厳禁とします。
- **技術スタック**: Spring Boot 3.3.0, Java 21, Gradle, MyBatis 3, MySQL 8, Spring Security 6, JJWT (io.jsonwebtoken 0.12+), Lombok。

---

## 3. コアアーキテクチャと境界規約（Core Architecture Boundaries）

### 3.1. レイヤー責務の分離（Layer Responsibilities）
- **Controller (`com.univus.base.api.controller`)**:
  - HTTPリクエストの受け付け、バリデーション（`@Valid`）、レスポンスの生成（`ResponseUtils.generateDtoSuccess`）。
  - ビジネスロジックを直接Controllerに記述してはなりません。
- **Service (`com.univus.base.api.service`)**:
  - トランザクション制御（`@Transactional`）およびビジネスロジックの実行。
  - 必要に応じて複数DAOの呼び出しや外部API連携を担当。
- **DAO / Mapper (`com.univus.base.dao`)**:
  - データベース操作に特化。ビジネスロジックを含めず、MyBatis XMLと1対1で対応。
- **DTO (`com.univus.base.dto`)**:
  - リクエスト（`dto.req`）とレスポンス（`dto.res`）を分離し、エンティティを直接APIレスポンスとして返却してはなりません。

### 3.2. 共通レスポンス構造の厳格な遵守
- すべてのエンドポイントは `ResponseEntity<BaseResponse>` を返却しなければなりません。
- 成功時は `ResponseUtils.generateDtoSuccess(information, responseData)` を使用します。
- 生の文字列、配列、または直接オブジェクトを返却することを禁止します。

### 3.3. 例外処理規約（Exception Handling）
- ビジネスエラーは `ApplicationException(HttpStatus, errorCode, message)` をスローします。
- 予期せぬシステム障害は `SystemException` をスローするか、`ExceptionControllerAdvice` により一括キャッチされて標準500エラーフォーマットに変換されます。
- `try-catch` で例外を握りつぶして空にすることを厳禁とします。

---

## 4. 検証コマンド（Validation Commands）

すべての作業完了後、エージェントは以下のコマンドを実行して正常性を検証します。

```bash
# Javaコンパイル検証
./gradlew compileJava

# テスト実行検証
./gradlew test

# プロダクションビルド検証
./gradlew build -x test
```
