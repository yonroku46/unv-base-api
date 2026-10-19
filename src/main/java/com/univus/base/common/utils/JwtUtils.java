package com.univus.base.common.utils;

import com.univus.base.common.constants.SecurityConst;
import com.univus.base.common.id.KsuidGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

/**
 * JWT関連ユーティリティ
 *
 * @author Univus
 */
@Component
public class JwtUtils {

    /**
     * 通常ログイン用JWTアクセストークン生成
     */
    public static String createJWT(long ttlMillis, String userId, String email, Integer roleId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        if (roleId != null) {
            claims.put("roleId", roleId);
        }
        return buildJWT(ttlMillis, userId, claims);
    }

    /**
     * JWT共通生成
     */
    public static String buildJWT(long ttlMillis, String subject, Map<String, Object> claims) {
        byte[] secretKeyAsBytes = SecurityConst.SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        long nowMillis = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        JwtBuilder builder = Jwts.builder()
                .claims(claims)
                .id(KsuidGenerator.createId())
                .issuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .subject(subject)
                .signWith(Keys.hmacShaKeyFor(secretKeyAsBytes));

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            LocalDateTime exp = LocalDateTime.ofInstant(Instant.ofEpochMilli(expMillis), ZoneId.systemDefault());
            builder.expiration(Date.from(exp.atZone(ZoneId.systemDefault()).toInstant()));
        }
        return builder.compact();
    }

    /**
     * JWTトークン検証およびClaims復元
     */
    public static Claims parseJWT(String token) {
        byte[] secretKeyAsBytes = SecurityConst.SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyAsBytes);
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
