package com.univus.base.dto.res.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * レスポンスエラークラス
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Error extends Information {

    /**
     * メソッドパラメータ
     */
    private String parameter;

    /**
     * エラー詳細
     */
    private String errorDetail;

    public Error(String messageId, String message) {
        super(messageId, message);
    }

    public Error(String messageId, String message, String parameter) {
        super(messageId, message);
        this.parameter = parameter;
    }

    public Error(String messageId, String message, String parameter, String errorDetail) {
        super(messageId, message);
        this.parameter = parameter;
        this.errorDetail = errorDetail;
    }
}
