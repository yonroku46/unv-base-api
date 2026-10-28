package com.univus.base.api.service.auth;

import com.univus.base.common.constants.MessageIdConst;
import com.univus.base.common.constants.SecurityConst;
import com.univus.base.common.enums.Roles;
import com.univus.base.common.id.KsuidGenerator;
import com.univus.base.common.utils.JwtUtils;
import com.univus.base.dao.UserDao;
import com.univus.base.dao.entity.User;
import com.univus.base.dto.req.GoogleLoginReq;
import com.univus.base.dto.req.LoginReq;
import com.univus.base.dto.req.SignupReq;
import com.univus.base.dto.res.auth.LoginUserRes;
import com.univus.base.exception.ApplicationException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인증/인가 비즈니스 로직 서비스
 *
 * @author Univus
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    // DB 미연결 또는 개발 환경 폴백용 인메모리 저장소
    private static final Map<String, User> MEMORY_USER_STORE = new ConcurrentHashMap<>();

    /**
     * 이메일 및 비밀번호 로그인
     */
    public LoginUserRes login(LoginReq req) {
        String email = req.getEmail().trim().toLowerCase();
        log.info("Attempting login for email: {}", email);

        User user = findUserByMail(email);
        if (user == null) {
            throw new ApplicationException(
                    HttpStatus.UNAUTHORIZED,
                    MessageIdConst.E_LOGIN_USER_FAILED,
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        if (user.getUserPw() == null || !passwordEncoder.matches(req.getPassword(), user.getUserPw())) {
            throw new ApplicationException(
                    HttpStatus.UNAUTHORIZED,
                    MessageIdConst.E_LOGIN_USER_FAILED,
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        return createLoginUserRes(user);
    }

    /**
     * 회원가입
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginUserRes signup(SignupReq req) {
        String email = req.getEmail().trim().toLowerCase();
        log.info("Attempting signup for email: {}", email);

        User existingUser = findUserByMail(email);
        if (existingUser != null) {
            throw new ApplicationException(
                    HttpStatus.CONFLICT,
                    MessageIdConst.E_USER_ALREADY_REGISTER,
                    "이미 등록된 이메일입니다."
            );
        }

        String userId = KsuidGenerator.createId();
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        User newUser = User.builder()
                .userId(userId)
                .mail(email)
                .userName(req.getUserName().trim())
                .userPw(encodedPassword)
                .roleId(Roles.CLIENT.getValue())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        saveUser(newUser);

        log.info("Successfully registered user: userId={}, email={}", userId, email);
        return createLoginUserRes(newUser);
    }

    /**
     * Google 인증 로그인 (OAuth / Credential)
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginUserRes googleLogin(GoogleLoginReq req) {
        String email = null;
        String name = "Google 사용자";
        String picture = null;

        // 1. Google ID Token (Credential) 파싱
        if (req.getCredential() != null && !req.getCredential().isBlank()) {
            try {
                String[] parts = req.getCredential().split("\\.");
                if (parts.length >= 2) {
                    String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                    com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(payloadJson).getAsJsonObject();
                    if (json.has("email")) email = json.get("email").getAsString();
                    if (json.has("name")) name = json.get("name").getAsString();
                    if (json.has("picture")) picture = json.get("picture").getAsString();
                }
            } catch (Exception e) {
                log.warn("Failed to parse Google credential token: {}", e.getMessage());
            }
        }

        // 2. 직접 지정 또는 코드 경유 폴백
        if (email == null && req.getEmail() != null) {
            email = req.getEmail().trim().toLowerCase();
        }
        if (req.getName() != null) {
            name = req.getName();
        }
        if (req.getPicture() != null) {
            picture = req.getPicture();
        }

        // 3. 개발용 기본 폴백 (code/token만 지정 시)
        if (email == null) {
            email = "google.user@" + (req.getCode() != null ? "oauth.dev" : "example.com");
        }

        email = email.trim().toLowerCase();
        log.info("Google login processing for email: {}", email);

        User user = findUserByMail(email);
        if (user == null) {
            String userId = KsuidGenerator.createId();
            user = User.builder()
                    .userId(userId)
                    .mail(email)
                    .userName(name)
                    .profileImg(picture)
                    .roleId(Roles.CLIENT.getValue())
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();
            saveUser(user);
            log.info("Created new user via Google login: userId={}, email={}", userId, email);
        }

        return createLoginUserRes(user);
    }

    /**
     * 토큰 기반 현재 로그인 사용자 조회
     */
    public LoginUserRes getMe(String token) {
        if (token == null || token.isBlank()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, MessageIdConst.E_NO_ACCESS_TOKEN, "토큰이 존재하지 않습니다.");
        }
        String cleanToken = token.replace(SecurityConst.TOKEN_PREFIX, "").trim();
        Claims claims = JwtUtils.parseJWT(cleanToken);

        String userId = claims.get("userId", String.class);
        String email = claims.get("email", String.class);
        if (email == null) email = claims.get("mail", String.class);

        User user = findUserByMail(email);
        if (user == null) {
            Integer roleId = claims.get("roleId", Integer.class);
            return LoginUserRes.builder()
                    .userId(userId)
                    .mail(email)
                    .userName(claims.getSubject())
                    .roleId(roleId != null ? roleId : Roles.CLIENT.getValue())
                    .build();
        }

        return LoginUserRes.builder()
                .userId(user.getUserId())
                .mail(user.getMail())
                .userName(user.getUserName())
                .profileImg(user.getProfileImg())
                .roleId(user.getRoleId())
                .build();
    }

    /**
     * 토큰 갱신
     */
    public LoginUserRes refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, MessageIdConst.E_NO_REFRESH_TOKEN, "리프레시 토큰이 존재하지 않습니다.");
        }
        String cleanToken = refreshToken.replace(SecurityConst.TOKEN_PREFIX, "").trim();
        Claims claims = JwtUtils.parseJWT(cleanToken);

        String userId = claims.get("userId", String.class);
        String email = claims.get("email", String.class);
        if (email == null) email = claims.get("mail", String.class);
        Integer roleId = claims.get("roleId", Integer.class);

        String newAccessToken = JwtUtils.createJWT(SecurityConst.EXPIRATION_TIME, userId, email, roleId);
        String newRefreshToken = JwtUtils.createJWT(SecurityConst.REFRESH_EXPIRATION_TIME, userId, email, roleId);

        return LoginUserRes.builder()
                .userId(userId)
                .mail(email)
                .userName(claims.getSubject())
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .roleId(roleId)
                .build();
    }

    private User findUserByMail(String email) {
        if (email == null) return null;
        try {
            if (userDao != null) {
                User dbUser = userDao.findByMail(email);
                if (dbUser != null) return dbUser;
            }
        } catch (Exception e) {
            log.warn("Database lookup failed, checking in-memory store: {}", e.getMessage());
        }
        return MEMORY_USER_STORE.get(email);
    }

    private void saveUser(User user) {
        boolean savedToDb = false;
        try {
            if (userDao != null) {
                userDao.insert(user);
                savedToDb = true;
            }
        } catch (Exception e) {
            log.warn("Database save failed, falling back to in-memory store: {}", e.getMessage());
        }
        if (!savedToDb) {
            MEMORY_USER_STORE.put(user.getMail(), user);
        }
    }

    private LoginUserRes createLoginUserRes(User user) {
        Integer roleId = user.getRoleId() != null ? user.getRoleId() : Roles.CLIENT.getValue();
        String accessToken = JwtUtils.createJWT(
                SecurityConst.EXPIRATION_TIME,
                user.getUserId(),
                user.getMail(),
                roleId
        );
        String refreshToken = JwtUtils.createJWT(
                SecurityConst.REFRESH_EXPIRATION_TIME,
                user.getUserId(),
                user.getMail(),
                roleId
        );

        return LoginUserRes.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .mail(user.getMail())
                .profileImg(user.getProfileImg())
                .token(accessToken)
                .refreshToken(refreshToken)
                .roleId(roleId)
                .build();
    }
}
