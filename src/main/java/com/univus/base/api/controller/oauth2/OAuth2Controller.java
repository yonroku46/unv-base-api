package com.univus.base.api.controller.oauth2;

import com.univus.base.api.controller.base.BaseController;
import com.univus.base.api.service.auth.AuthService;
import com.univus.base.common.constants.MessageIdConst;
import com.univus.base.common.utils.ResponseUtils;
import com.univus.base.dto.req.GoogleLoginReq;
import com.univus.base.dto.res.auth.LoginUserRes;
import com.univus.base.dto.res.core.BaseResponse;
import com.univus.base.dto.res.core.Information;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2コールバック処理コントローラー
 *
 * @author Univus
 */
@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller extends BaseController {

    private final AuthService authService;

    /**
     * Google OAuth2コールバックエンドポイント
     */
    @GetMapping("/google/access-token")
    public ResponseEntity<BaseResponse> getGoogleAccessToken(@RequestParam("code") String code) {
        GoogleLoginReq req = new GoogleLoginReq();
        req.setCode(code);
        LoginUserRes res = authService.googleLogin(req);
        String msg = getMessage(MessageIdConst.I_LOGIN_SUCCESS);
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_LOGIN_SUCCESS, msg), res)
        );
    }
}
