package com.univus.base.common.constants;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * セキュリティ定数定義
 *
 * @author Univus
 */
@Component
@NoArgsConstructor
public class SecurityConst {

    @Value("${security.jwt.secret-key:unv-base-api-development-secret-key-must-be-at-least-256-bits-long!!}")
    private String jwtSecretKeyConfig;

    @PostConstruct
    public void init() {
        SECRET_KEY = jwtSecretKeyConfig;
    }

    /**
     * JWTシークレットキー
     */
    public static String SECRET_KEY;

    /**
     * 言語指定ヘッダー
     */
    public static final String LANGUAGE = "Accept-Language";

    /**
     * Authorizationリクエストヘッダー
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * アクセストークンヘッダー
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * リフレッシュトークンヘッダー
     */
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";

    /**
     * JWTアクセストークン有効期限 (60分)
     */
    public static final Long EXPIRATION_TIME = 1000 * 60 * 60L;

    /**
     * JWTリフレッシュトークン有効期限 (7日)
     */
    public static final Long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7L;
}
