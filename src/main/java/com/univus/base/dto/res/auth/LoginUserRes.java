package com.univus.base.dto.res.auth;

import com.univus.base.dto.res.core.ResponseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * ログイン・認証成功レスポンス
 *
 * @author Univus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginUserRes extends ResponseData {

    private String userId;
    private String userName;
    private String mail;
    private String profileImg;
    private String token;
    private String refreshToken;
    private Integer roleId;
}
