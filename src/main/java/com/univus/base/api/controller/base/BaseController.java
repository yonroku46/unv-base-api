package com.univus.base.api.controller.base;

import com.univus.base.aspect.LocaleAspect;
import com.univus.base.common.constants.SecurityConst;
import com.univus.base.common.utils.JwtUtils;
import com.univus.base.dao.UserDao;
import com.univus.base.dao.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * 共通参照用基底コントローラー
 *
 * @author Univus
 */
public class BaseController {

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired(required = false)
    protected UserDao userDao;

    /**
     * 認証済みユーザーの取得
     */
    public User loadUser() {
        String authorization = request.getHeader(SecurityConst.TOKEN_HEADER);
        if (authorization == null) {
            authorization = request.getHeader(SecurityConst.REFRESH_TOKEN_HEADER);
        }

        if (authorization != null && authorization.startsWith(SecurityConst.TOKEN_PREFIX)) {
            String token = authorization.replace(SecurityConst.TOKEN_PREFIX, "").trim();
            try {
                Claims claims = JwtUtils.parseJWT(token);
                String userId = claims.get("userId", String.class);
                String email = claims.get("email", String.class);
                if (email == null) {
                    email = claims.get("mail", String.class);
                }

                if (userDao != null && userId != null && email != null) {
                    User dbUser = userDao.findUser(userId, email);
                    if (dbUser != null) {
                        return dbUser;
                    }
                }

                if (userId != null) {
                    Integer roleId = claims.get("roleId", Integer.class);
                    return User.builder()
                            .userId(userId)
                            .mail(email)
                            .userName(claims.getSubject())
                            .roleId(roleId)
                            .build();
                }
            } catch (Exception ignored) {
                // トークン解析失敗時は空エンティティ返却
            }
        }
        return new User();
    }

    /**
     * ログイン中のユーザーID
     */
    public String getCurrentUserId() {
        return loadUser().getUserId();
    }

    /**
     * ログイン中のユーザーメール
     */
    public String getCurrentUserEmail() {
        return loadUser().getMail();
    }

    /**
     * ログイン中のユーザー名
     */
    public String getCurrentUserName() {
        return loadUser().getUserName();
    }

    /**
     * ログイン中のユーザー権限ID
     */
    public Integer getCurrentUserRoleId() {
        return loadUser().getRoleId();
    }

    /**
     * 多言語メッセージ取得ヘルパー
     */
    protected String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleAspect.LOCALE);
    }
}
