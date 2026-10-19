package com.univus.base.api.controller.sample;

import com.univus.base.api.controller.base.BaseController;
import com.univus.base.api.service.sample.SampleService;
import com.univus.base.common.constants.MessageIdConst;
import com.univus.base.common.utils.ResponseUtils;
import com.univus.base.dto.req.SampleRequest;
import com.univus.base.dto.res.core.BaseResponse;
import com.univus.base.dto.res.core.Information;
import com.univus.base.dto.res.sample.SampleResponseData;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * サンプルコントローラー
 *
 * @author Univus
 */
@RestController
@RequestMapping("/sample")
public class SampleController extends BaseController {

    @Autowired
    private SampleService sampleService;

    /**
     * サンプルデータ取得エンドポイント
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getSample(@PathVariable("id") String id) {
        SampleResponseData data = sampleService.getSample(id);
        String msg = getMessage(MessageIdConst.I_GETTING_SUCCESS, "サンプル");
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_GETTING_SUCCESS, msg), data)
        );
    }

    /**
     * サンプルデータ作成エンドポイント
     */
    @PostMapping
    public ResponseEntity<BaseResponse> createSample(@Valid @RequestBody SampleRequest request) {
        SampleResponseData data = sampleService.createSample(request);
        String msg = getMessage(MessageIdConst.I_INSERT_SUCCESS, "サンプル");
        return ResponseEntity.ok(
                ResponseUtils.generateDtoSuccess(new Information(MessageIdConst.I_INSERT_SUCCESS, msg), data)
        );
    }
}
