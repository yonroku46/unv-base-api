package com.univus.base.dto.res.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 응답 에러 클래스
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Error extends Information {

    /**
     * 메소드 파라미터
     */
    private String parameter;

    /**
     * 에러 상세 내용
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
