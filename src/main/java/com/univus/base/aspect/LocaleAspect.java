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
 * リクエストロケール設定アスペクト
 *
 * @author Univus
 */
@Aspect
@Component
public class LocaleAspect {

    @Autowired
    protected HttpServletRequest request;

    public static Locale LOCALE = Locale.JAPAN;

    @Value("${app.language.default:ja}")
    private String systemLanguageDefault;

    @Before("execution(* com.univus.base.api.controller..*.*(..))")
    public void getLocale() {
        String language = request.getHeader(SecurityConst.LANGUAGE);
        if (language != null) {
            LOCALE = language.toLowerCase().contains("ja") ? Locale.JAPAN : Locale.ENGLISH;
        } else {
            LOCALE = "ja".equalsIgnoreCase(systemLanguageDefault) ? Locale.JAPAN : Locale.ENGLISH;
        }
    }
}
