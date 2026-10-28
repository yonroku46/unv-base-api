package com.univus.base.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.univus.base.aspect.LocaleAspect;
import com.univus.base.common.constants.MessageIdConst;
import com.univus.base.common.utils.ResponseUtils;
import com.univus.base.dto.res.core.BaseResponse;
import com.univus.base.dto.res.core.Error;
import com.univus.base.dto.res.core.Information;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * 공통 예외 핸들러
 *
 * @author Univus
 */
@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    /**
     * 애플리케이션 비즈니스 예외 핸들링
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BaseResponse> handleApplicationException(ApplicationException exception) {
        log.warn("Application exception: errorCode={}, message={}", exception.getErrorCode(), exception.getMessage());
        return new ResponseEntity<>(
                ResponseUtils.generateDtoSuccessAbnormal(
                        new Information(exception.getErrorCode(), exception.getMessage()), null
                ),
                exception.getStatus() != null ? exception.getStatus() : HttpStatus.BAD_REQUEST
        );
    }

    /**
     * 유효성 검사 에러 핸들링
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationException(MethodArgumentNotValidException exception) {
        List<Error> errors = new ArrayList<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.add(new Error(
                    MessageIdConst.E_BAD_REQUEST,
                    fieldError.getDefaultMessage(),
                    fieldError.getField()
            ));
        }
        return new ResponseEntity<>(
                ResponseUtils.generateDtoFailed(errors),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * JSON 파싱 예외 핸들링
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<BaseResponse> handleJsonProcessingException(JsonProcessingException exception) {
        log.warn("JSON processing error: {}", exception.getMessage());
        return new ResponseEntity<>(
                ResponseUtils.generateDtoFailed(new Error(MessageIdConst.E_BAD_REQUEST, exception.getMessage())),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * JWT 만료 예외 핸들링
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<BaseResponse> handleExpiredJwtException(ExpiredJwtException exception) {
        String message = messageSource.getMessage(MessageIdConst.E_EXPIRED_TOKEN, null, LocaleAspect.LOCALE);
        return new ResponseEntity<>(
                ResponseUtils.generateDtoFailed(new Error(MessageIdConst.E_EXPIRED_TOKEN, message)),
                HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * 시스템 예기치 않은 예외 핸들링 (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGeneralException(Exception exception) {
        log.error("Unhandled system exception: ", exception);
        String message = messageSource.getMessage(MessageIdConst.E_SYSTEM, null, LocaleAspect.LOCALE);
        return new ResponseEntity<>(
                ResponseUtils.generateDtoFailed(new Error(MessageIdConst.E_SYSTEM, message)),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
