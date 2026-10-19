---
id: 001-spring-boot-3-and-java21-compatibility
title: Spring Boot 3.3.0およびJava 21環境におけるLombokアノテーション処理とディレクトリ構造の注意点
date: 2026-10-19
scope: [springboot, java21, gradle, lombok, build]
status: active
---

### 背景・発生した問題（Problem / Context）
プロジェクト構築時、`./gradlew compileJava` を実行した際に、Lombokで自動生成されるはずの `@Slf4j` の `log` 変数や、`@Data` / `@Builder` で生成されるゲッター・セッター・ビルダーメソッドが参照できず、大量のシンボル未解決エラー（38 errors）が発生した。

### 根本原因（Root Cause）
1. `src/main/java/` 直下に不適切なパッケージ名（ドットを含むディレクトリ `com.univus.base`）が一時的に生成され、`com/univus/base/` 側のクラスと完全修飾クラス名が重複（duplicate class）していた。
2. javacコンパイラは、AST構文解析および重複クラス検証の段階で致命的な重複エラーを検出すると、アノテーションプロセッサ（Lombok）の処理ラウンドを実行せずに即座に停止する。そのため、後続のすべてのLombokコード生成が動作しなかった。

### 解決策および今後のルール（Rule & Solution）
1. **ディレクトリパスの厳密な作成**: Javaのパッケージ構造は必ずディレクトリをドットではなく階層として分割して作成すること（例: `com/univus/base/config`）。
2. **クリーンビルドによる確認**: 重複クラスやファイル移動を行った後は、必ず `./gradlew clean` を実行して古いキャッシュとビルド成果物を完全に消去した上で再コンパイルを行うこと。
