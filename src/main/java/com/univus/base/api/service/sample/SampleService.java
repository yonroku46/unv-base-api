package com.univus.base.api.service.sample;

import com.univus.base.common.id.KsuidGenerator;
import com.univus.base.dto.req.SampleRequest;
import com.univus.base.dto.res.sample.SampleResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 샘플 서비스 클래스
 *
 * @author Univus
 */
@Slf4j
@Service
public class SampleService {

    /**
     * 샘플 데이터 생성
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
     * 샘플 데이터 조회
     */
    public SampleResponseData getSample(String id) {
        log.info("Getting sample by id: {}", id);
        return SampleResponseData.builder()
                .id(id)
                .title("샘플 데이터")
                .content("보일러플레이트 샘플 데이터입니다.")
                .createdAt(new Date())
                .build();
    }
}
