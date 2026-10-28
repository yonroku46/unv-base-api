package com.univus.base.exception;

/**
 * 시스템 내부 예외 클래스
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
