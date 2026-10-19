package com.univus.base.dto.res.core;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 共通APIレスポンスラッパー
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
public class BaseResponse<T> {

    /**
     * 処理結果コード (0: SUCCESS, 1: FAILED)
     */
    private int resultCode;

    /**
     * エラー発生フラグ
     */
    private boolean hasErrors;

    /**
     * メッセージリスト
     */
    private List<Information> informations;

    /**
     * エラーリスト
     */
    private List<Error> errors;

    /**
     * レスポンスデータペイロード
     */
    private ResponseData responseData;
}
