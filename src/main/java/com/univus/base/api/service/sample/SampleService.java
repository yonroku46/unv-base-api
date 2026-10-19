package com.univus.base.api.service.sample;

import com.univus.base.common.id.KsuidGenerator;
import com.univus.base.dto.req.SampleRequest;
import com.univus.base.dto.res.sample.SampleResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * サンプルサービスクラス
 *
 * @author Univus
 */
@Slf4j
@Service
public class SampleService {

    /**
     * サンプルデータ作成
     */
    public SampleResponseData createSample(SampleRequest request) {
        log.info("Creating sample with title: {}", request.getTitle());
        return SampleResponseData.builder()
                .id(KsuidGenerator.createId())
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(new Date())
                .build();
    }

    /**
     * サンプルデータ取得
     */
    public SampleResponseData getSample(String id) {
        log.info("Getting sample by id: {}", id);
        return SampleResponseData.builder()
                .id(id)
                .title("サンプルデータ")
                .content("これはボイラープレートのサンプルデータです。")
                .createdAt(new Date())
                .build();
    }
}
