package com.univus.base.dto.res.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * レスポンスインフォメーションクラス
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Information {

    /**
     * メッセージID
     */
    private String messageId;

    /**
     * メッセージ本文
     */
    private String message;
}
