package com.univus.base.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GoogleログインリクエストDTO
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginReq {

    /**
     * OAuth認可コード (Code Flow)
     */
    private String code;

    /**
     * Google ID Token / Credential (One Tap / Google Identity)
     */
    private String credential;

    /**
     * 開発用・テスト用フォールバック
     */
    private String email;
    private String name;
    private String picture;
}
