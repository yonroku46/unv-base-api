package com.univus.base.dto.res.core;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공통 API 응답 래퍼
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
public class BaseResponse<T> {

    /**
     * 처리 결과 코드 (0: SUCCESS, 1: FAILED)
     */
    private int resultCode;

    /**
     * 에러 발생 여부 플래그
     */
    private boolean hasErrors;

    /**
     * 안내 메시지 목록
     */
    private List<Information> informations;

    /**
     * 에러 목록
     */
    private List<Error> errors;

    /**
     * 응답 데이터 페이로드
     */
    private ResponseData responseData;
}
