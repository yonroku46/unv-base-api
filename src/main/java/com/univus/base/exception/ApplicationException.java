package com.univus.base.exception;

import org.springframework.http.HttpStatus;

/**
 * アプリケーション例外クラス
 *
 * @author Univus
 */
public class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public ApplicationException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = null;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public String getErrorCode() {
        return this.errorCode;
    }
}
