package com.univus.base.api.controller.auth;

import com.univus.base.api.controller.base.BaseController;
import com.univus.base.api.service.auth.AuthService;
import com.univus.base.aspect.attribute.LoginToken;
import com.univus.base.common.constants.MessageIdConst;
import com.univus.base.common.constants.SecurityConst;
import com.univus.base.common.utils.ResponseUtils;
import com.univus.base.dto.req.GoogleLoginReq;
import com.univus.base.dto.req.LoginReq;
import com.univus.base.dto.req.SignupReq;
import com.univus.base.dto.res.auth.LoginUserRes;
import com.univus.base.dto.res.core.BaseResponse;
import com.univus.base.dto.res.core.Information;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 컨트롤러
 *
 * @author Univus
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    /**
     * 이메일/비밀번호 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@Valid @RequestBody LoginReq req) {
        LoginUserRes res = authService.login(req);
        String msg = getMessage(MessageIdConst.I_LOGIN_SUCCESS);
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_LOGIN_SUCCESS, msg), res)
        );
    }

    /**
     * 신규 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signup(@Valid @RequestBody SignupReq req) {
        LoginUserRes res = authService.signup(req);
        String msg = getMessage(MessageIdConst.I_INSERT_SUCCESS, "사용자");
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_INSERT_SUCCESS, msg), res)
        );
    }

    /**
     * Google 인증 로그인
     */
    @PostMapping("/google")
    public ResponseEntity<BaseResponse> googleLogin(@RequestBody GoogleLoginReq req) {
        LoginUserRes res = authService.googleLogin(req);
        String msg = getMessage(MessageIdConst.I_LOGIN_SUCCESS);
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_LOGIN_SUCCESS, msg), res)
        );
    }

    /**
     * 현재 로그인 사용자 정보 조회
     */
    @GetMapping("/me")
    @LoginToken
    public ResponseEntity<BaseResponse> getMe() {
        String token = request.getHeader(SecurityConst.TOKEN_HEADER);
        LoginUserRes res = authService.getMe(token);
        String msg = getMessage(MessageIdConst.I_GETTING_SUCCESS, "사용자 정보");
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_GETTING_SUCCESS, msg), res)
        );
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh-token")
    @LoginToken
    public ResponseEntity<BaseResponse> refreshToken(@RequestBody(required = false) Map<String, String> body) {
        String refreshToken = null;
        if (body != null && body.containsKey("refreshToken")) {
            refreshToken = body.get("refreshToken");
        }
        if (refreshToken == null) {
            refreshToken = request.getHeader(SecurityConst.REFRESH_TOKEN_HEADER);
        }

        LoginUserRes res = authService.refreshToken(refreshToken);
        String msg = getMessage(MessageIdConst.I_TOKEN_REFRESHED);
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_TOKEN_REFRESHED, msg), res)
        );
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout() {
        String msg = getMessage(MessageIdConst.I_LOGOUT_SUCCESS);
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_LOGOUT_SUCCESS, msg), null)
        );
    }
}
