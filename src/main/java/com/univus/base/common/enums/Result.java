package com.univus.base.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * レスポンス結果コード
 *
 * @author Univus
 */
@Getter
@AllArgsConstructor
public enum Result {
    SUCCESS(0),
    FAILED(1);

    private final int code;
}
