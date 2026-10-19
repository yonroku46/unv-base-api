package com.univus.base.exception;

/**
 * システム内部例外クラス
 *
 * @author Univus
 */
public class SystemException extends RuntimeException {

    private final String errorCode;
    private final String errorDetail;

    public SystemException(String errorCode, String message, String errorDetail) {
        super(message);
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
    }

    public SystemException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorDetail = null;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }
}
