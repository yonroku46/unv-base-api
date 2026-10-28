package com.univus.base.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Google 로그인 요청 DTO
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginReq {

    /**
     * OAuth 인가 코드 (Code Flow)
     */
    private String code;

    /**
     * Google ID Token / Credential (One Tap / Google Identity)
     */
    private String credential;

    /**
     * 개발 및 테스트용 폴백
     */
    private String email;
    private String name;
    private String picture;
}
