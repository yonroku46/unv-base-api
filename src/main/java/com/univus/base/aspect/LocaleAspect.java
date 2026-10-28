package com.univus.base.aspect;

import com.univus.base.common.constants.SecurityConst;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 요청 로케일 설정 Aspect
 *
 * @author Univus
 */
@Aspect
@Component
public class LocaleAspect {

    @Autowired
    protected HttpServletRequest request;

    public static Locale LOCALE = Locale.KOREA;

    @Value("${app.language.default:ko}")
    private String systemLanguageDefault;

    @Before("execution(* com.univus.base.api.controller..*.*(..))")
    public void getLocale() {
        String language = request.getHeader(SecurityConst.LANGUAGE);
        if (language != null) {
            LOCALE = language.toLowerCase().contains("ko") ? Locale.KOREA : Locale.ENGLISH;
        } else {
            LOCALE = "ko".equalsIgnoreCase(systemLanguageDefault) ? Locale.KOREA : Locale.ENGLISH;
        }
    }
}
