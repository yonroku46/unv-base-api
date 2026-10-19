package com.univus.base.api.controller.base;

import com.univus.base.common.constants.MessageIdConst;
import com.univus.base.common.utils.ResponseUtils;
import com.univus.base.dto.res.core.BaseResponse;
import com.univus.base.dto.res.core.Information;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * サーバーヘルスチェックコントローラー
 *
 * @author Univus
 */
@RestController
@RequestMapping("/health-check")
public class HealthCheckController extends BaseController {

    @GetMapping
    public ResponseEntity<BaseResponse> healthCheck() {
        String msg = getMessage(MessageIdConst.I_SERVER_RUNNING);
        BaseResponse response = ResponseUtils.generateDtoSuccess(
                new Information(MessageIdConst.I_SERVER_RUNNING, msg), null
        );
        return ResponseEntity.ok(response);
    }
}
