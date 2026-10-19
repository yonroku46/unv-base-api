---
id: 002-standard-response-and-exception-handling
title: BaseResponseラッパー規約とExceptionControllerAdviceによる標準エラーハンドリング
date: 2026-10-19
scope: [api, response, exception, controller]
status: active
---

### 背景・発生した問題（Problem / Context）
APIエンドポイントにおいて、コントローラーが個別にMapや生データを返却したり、例外発生時にSpring BootデフォルトのWhiteLabelエラーや生のスタックトレースがクライアントに露出してしまうと、フロントエンド（`unv-base-viewer`など）側のAxiosインターセプターによる一貫したエラーハンドリングが破綻する。

### 根本原因（Root Cause）
1. コントローラー側でレスポンス構造の統一基準が守られていない場合、クライアント側で成功判定（`resultCode == 0`）やエラーメッセージ抽出（`errors`, `informations`）のロジックが複雑化する。
2. `@RestControllerAdvice` による中央集権的な例外ハンドリングが漏れていると、未処理例外がHTTP 500のHTML形式で返却されてしまう。

### 解決策および今後のルール（Rule & Solution）
1. **全エンドポイントで `BaseResponse` を返却**: すべてのコントローラーメソッドは `ResponseEntity<BaseResponse>` を返却し、`ResponseUtils.generateDtoSuccess(...)` を使用すること。
2. **`ExceptionControllerAdvice` による統一捕捉**:
   - ビジネスエラー: `ApplicationException` をスロー
   - バリデーションエラー: `MethodArgumentNotValidException` を自動キャッチして `errors` リストに整形
   - システムエラー: 予期せぬ `Exception` を一括キャッチし、ログを記録した上で一律500の `BaseResponse`（エラーメッセージ付き）として返却すること。
