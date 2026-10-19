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
 * 認証コントローラー
 *
 * @author Univus
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    /**
     * メール・パスワードログイン
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
     * 新規会員登録 (サインアップ)
     */
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signup(@Valid @RequestBody SignupReq req) {
        LoginUserRes res = authService.signup(req);
        String msg = getMessage(MessageIdConst.I_INSERT_SUCCESS, "ユーザー");
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_INSERT_SUCCESS, msg), res)
        );
    }

    /**
     * Google認証ログイン
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
     * 現在ログイン中ユーザー情報取得
     */
    @GetMapping("/me")
    @LoginToken
    public ResponseEntity<BaseResponse> getMe() {
        String token = request.getHeader(SecurityConst.TOKEN_HEADER);
        LoginUserRes res = authService.getMe(token);
        String msg = getMessage(MessageIdConst.I_GETTING_SUCCESS, "ユーザー情報");
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_GETTING_SUCCESS, msg), res)
        );
    }

    /**
     * トークンリフレッシュ
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
     * ログアウト
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout() {
        String msg = getMessage(MessageIdConst.I_LOGOUT_SUCCESS);
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_LOGOUT_SUCCESS, msg), null)
        );
    }
}
