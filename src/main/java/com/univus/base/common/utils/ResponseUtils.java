package com.univus.base.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.univus.base.common.enums.Result;
import com.univus.base.dto.res.core.BaseResponse;
import com.univus.base.dto.res.core.Error;
import com.univus.base.dto.res.core.Information;
import com.univus.base.dto.res.core.ResponseData;
import com.univus.base.exception.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * レスポンス返却共通ユーティリティ
 *
 * @author Univus
 */
@Component
public class ResponseUtils {

    public ResponseUtils() {
    }

    /**
     * クライアントエラーが発生したかチェックし、200以外ならスロー
     */
    public static void isClientError(ApplicationException exception) {
        if (exception.getStatus() == null) {
            return;
        }
        if (!HttpStatus.OK.equals(exception.getStatus())) {
            throw exception;
        }
    }

    /**
     * 正常レスポンス生成（単一メッセージ）
     */
    public static BaseResponse generateDtoSuccess(Information information, ResponseData responseData) {
        return generateDto(Result.SUCCESS, false, information, null, responseData);
    }

    /**
     * 正常レスポンス生成（メッセージなし）
     */
    public static BaseResponse generateDtoSuccess(ResponseData responseData) {
        return generateDto(Result.SUCCESS, false, (Information) null, null, responseData);
    }

    /**
     * 正常レスポンス生成（複数メッセージ）
     */
    public static BaseResponse generateDtoSuccess(List<Information> informations, ResponseData responseData) {
        return generateDto(Result.SUCCESS, false, informations, null, responseData);
    }

    /**
     * 異常発生レスポンス生成 (resultCode=0, hasErrors=true)
     */
    public static BaseResponse generateDtoSuccessAbnormal(Information information, ResponseData responseData) {
        return generateDto(Result.SUCCESS, true, information, null, responseData);
    }

    /**
     * エラーレスポンス生成 (resultCode=1, hasErrors=true)
     */
    public static BaseResponse generateDtoFailed(Error error) {
        return generateDto(Result.FAILED, true, (Information) null, error, null);
    }

    public static BaseResponse generateDtoFailed(List<Error> errors) {
        return generateDto(Result.FAILED, true, (List<Information>) null, errors, null);
    }

    public static BaseResponse generateDtoFailed(Information information, Error error) {
        return generateDto(Result.FAILED, true, information, error, null);
    }

    public static BaseResponse generateDtoFailed(List<Information> informations, List<Error> errors) {
        return generateDto(Result.FAILED, true, informations, errors, null);
    }

    public static BaseResponse generateDtoFailed(Information information, Error error, ResponseData responseData) {
        return generateDto(Result.FAILED, true, information, error, responseData);
    }

    public static BaseResponse generateDtoFailed(List<Information> informations, List<Error> errors, ResponseData responseData) {
        return generateDto(Result.FAILED, true, informations, errors, responseData);
    }

    public static BaseResponse generateDto(Result result, boolean hasErrors, Information information, Error error, ResponseData responseData) {
        List<Information> informations = null;
        if (information != null) {
            informations = new ArrayList<>();
            informations.add(information);
        }

        List<Error> errors = null;
        if (error != null) {
            errors = new ArrayList<>();
            errors.add(error);
        }

        return generateDto(result, hasErrors, informations, errors, responseData);
    }

    public static BaseResponse generateDto(Result result, boolean hasErrors, List<Information> informations, List<Error> errors, ResponseData responseData) {
        BaseResponse dto = new BaseResponse();
        dto.setResultCode(result.getCode());
        dto.setHasErrors(hasErrors);
        dto.setInformations(informations);
        dto.setErrors(errors);
        dto.setResponseData(responseData);
        return dto;
    }

    /**
     * オブジェクト間フィールドコピー
     */
    public static <A, B> B copyObject(A sourceObject, B targetObject) {
        if (sourceObject == null || targetObject == null) {
            return null;
        }
        try {
            for (Field field : sourceObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Field targetField = targetObject.getClass().getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    if (targetField.getType().isAssignableFrom(field.getType())) {
                        targetField.set(targetObject, field.get(sourceObject));
                    }
                } catch (NoSuchFieldException ignored) {
                }
            }
        } catch (IllegalAccessException e) {
            // Log or ignore
        }
        return targetObject;
    }
}
