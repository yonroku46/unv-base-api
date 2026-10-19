package com.univus.base.filter;

import com.univus.base.aspect.LocaleAspect;
import com.univus.base.aspect.attribute.CheckToken;
import com.univus.base.aspect.attribute.LoginToken;
import com.univus.base.common.constants.SecurityConst;
import com.univus.base.common.enums.Roles;
import com.univus.base.common.utils.JwtUtils;
import com.univus.base.dao.UserDao;
import com.univus.base.dao.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * JWT認証処理フィルター
 *
 * @author Univus
 */
@Slf4j
@Component
public class JwtAuthFilter implements Filter {

    @Autowired
    private MessageSource messageSource;

    @Autowired(required = false)
    private UserDao userDao;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (!preHandle((HttpServletRequest) request, (HttpServletResponse) response)) {
                return;
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HandlerMethod handlerMethod;
        try {
            var executionChain = handlerMapping.getHandler(request);
            if (executionChain == null || !(executionChain.getHandler() instanceof HandlerMethod)) {
                return true;
            }
            handlerMethod = (HandlerMethod) executionChain.getHandler();
        } catch (Exception e) {
            return true;
        }

        Class<?> clazz = handlerMethod.getBeanType();
        Method method = handlerMethod.getMethod();

        // ログイン中の判定
        if (method.isAnnotationPresent(LoginToken.class)) {
            LoginToken loginToken = method.getAnnotation(LoginToken.class);
            if (loginToken.required()) {
                return true;
            }
        }

        boolean isNeedVerify = false;
        if (clazz.isAnnotationPresent(CheckToken.class)) {
            CheckToken checkControllerToken = clazz.getAnnotation(CheckToken.class);
            if (checkControllerToken.required()) {
                isNeedVerify = true;
            }
        }
        if (method.isAnnotationPresent(CheckToken.class)) {
            CheckToken checkMethodToken = method.getAnnotation(CheckToken.class);
            if (checkMethodToken.required()) {
                isNeedVerify = true;
            }
        }

        if (isNeedVerify) {
            return verifyToken(request, response);
        }
        return true;
    }

    private boolean verifyToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String authorization = httpServletRequest.getHeader(SecurityConst.TOKEN_HEADER);
        if (authorization == null || authorization.isBlank()) {
            String message = messageSource.getMessage("error.noAccessToken", null, LocaleAspect.LOCALE);
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
            return false;
        }

        String token = authorization.replace(SecurityConst.TOKEN_PREFIX, "").trim();
        if (token.isEmpty()) {
            String message = messageSource.getMessage("error.noAccessToken", null, LocaleAspect.LOCALE);
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
            return false;
        }

        try {
            Claims claims = JwtUtils.parseJWT(token);
            User user = getAuthentication(claims);
            if (user == null) {
                String message = messageSource.getMessage("error.userNotRegister", null, LocaleAspect.LOCALE);
                httpServletResponse.sendError(HttpServletResponse.SC_CONFLICT, message);
                return false;
            }

            Integer roleId = user.getRoleId();
            String roleName = roleId != null ? Roles.findByValue(roleId) : Roles.CLIENT.name();
            if (roleName == null) {
                String message = messageSource.getMessage("error.invalidRole", null, LocaleAspect.LOCALE);
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, message);
                return false;
            }

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, List.of(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (ExpiredJwtException exception) {
            String message = messageSource.getMessage("error.expiredJWT", null, LocaleAspect.LOCALE);
            log.warn("Expired JWT: {}", exception.getMessage());
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        } catch (UnsupportedJwtException exception) {
            String message = messageSource.getMessage("error.unsupportedJWT", null, LocaleAspect.LOCALE);
            log.warn("Unsupported JWT: {}", exception.getMessage());
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        } catch (MalformedJwtException exception) {
            String message = messageSource.getMessage("error.invalidJWT", null, LocaleAspect.LOCALE);
            log.warn("Malformed JWT: {}", exception.getMessage());
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        } catch (IllegalArgumentException exception) {
            String message = messageSource.getMessage("error.illegalJWT", null, LocaleAspect.LOCALE);
            log.warn("Illegal JWT: {}", exception.getMessage());
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        }
        return false;
    }

    private User getAuthentication(Claims claims) {
        String userId = claims.get("userId", String.class);
        String mail = claims.get("email", String.class);
        if (mail == null) {
            mail = claims.get("mail", String.class);
        }
        if (userDao != null && userId != null && mail != null) {
            User dbUser = userDao.findUser(userId, mail);
            if (dbUser != null) {
                return dbUser;
            }
        }
        // DB未接続またはユーザーが見つからない場合のフォールバック（JWTクレームをベースにUser構築）
        if (userId != null) {
            Integer roleId = claims.get("roleId", Integer.class);
            return User.builder()
                    .userId(userId)
                    .mail(mail)
                    .userName(claims.getSubject())
                    .roleId(roleId != null ? roleId : Roles.CLIENT.getValue())
                    .build();
        }
        return null;
    }
}
