package com.univus.base.dto.res.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 응답 안내 정보 클래스
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Information {

    /**
     * 메시지 ID
     */
    private String messageId;

    /**
     * 메시지 본문
     */
    private String message;
}
