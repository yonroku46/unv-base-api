package com.univus.base.dto.res.sample;

import com.univus.base.dto.res.core.ResponseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * サンプルレスポンスデータ
 *
 * @author Univus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SampleResponseData extends ResponseData {

    private String id;
    private String title;
    private String content;
    private Date createdAt;
}
