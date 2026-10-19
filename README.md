# Web Service API Boilerplate (`unv-base-api`)

Spring Boot 3.3.0 (Java 21) をベースとした、日本国内向けWebサービスのためのRESTful API標準ボイラープレートです。
MyBatis 3、Spring Security 6、JWT認証基盤、統一レスポンスラッパー（`BaseResponse`）、多言語メッセージ基盤、およびAIエージェント向けの行動規範・ナレッジ体系を標準搭載しています。

---

## 🚀 新規サービス立ち上げ時の設定（Quick Setup）

本リポジトリを複製して新しいサービスを立ち上げる際は、**以下の箇所を変更**するだけで即座にプロジェクト基盤が整います。

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
    url: jdbc:mysql://localhost:3306/your_database_name?characterEncoding=UTF-8&serverTimezone=Asia/Tokyo
    username: your_db_user
    password: your_db_password
```

---

## 🛠️ 技術スタック（Tech Stack）

- **言語・ランタイム**: Java 21 (LTS)
- **フレームワーク**: Spring Boot 3.3.0
- **ビルドツール**: Gradle 8.5 (Wrapper搭載)
- **データベース連携**: MyBatis 3.0.3, MySQL Connector/J
- **セキュリティ・認証**: Spring Security 6, JJWT 0.12.3 (HMAC-SHA256)
- **バリデーション**: Hibernate Validator (Jakarta Bean Validation)
- **ロギング**: SLF4J, Logback (ローリングファイルアペンダー構成済み)
- **コード生成**: Project Lombok 1.18.32
- **ID生成**: KSUID (`com.github.ksuid:ksuid:1.1.0`), JNanoId

---

## 🧭 アーキテクチャおよびエージェント規約

- [ARCHITECTURE.md](ARCHITECTURE.md): 変更種別ごとの着手パス（Starting Paths）とレイヤー責務マップ
- [TECH.md](TECH.md): 10大禁止ルール（Negative Constraints）およびJava/Spring Boot規約
- [AGENTS.md](AGENTS.md): AIエージェントの6段階作業プロトコル
- [.agents/learnings/](.agents/learnings/): 過去のトラブルと解決策を蓄積するナレッジベース
- [.agents/skills/](.agents/skills/): エンドポイントおよびDAO追加の標準スキルガイド

---

## 💻 実行および検証コマンド（Commands）

```bash
# アプリケーション起動 (http://localhost:8080/api)
./gradlew bootRun

# Javaコンパイル検証
./gradlew compileJava

# テストクラスのコンパイル検証
./gradlew compileTestJava

# ユニットテスト実行
./gradlew test

# プロダクション向けビルド (JAR生成)
./gradlew build -x test
```

---

## 🔌 標準提供エンドポイント

- **ヘルスチェック**: `GET /api/health-check` (200 OK & サーバー起動状態メッセージ返却)
- **サンプルデータ取得**: `GET /api/sample/{id}`
- **サンプルデータ登録**: `POST /api/sample`
