package com.univus.base.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * サンプルリクエストDTO
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleRequest {

    @NotBlank(message = "タイトルは必須項目です。")
    @Size(max = 100, message = "タイトルは100文字以内で入力してください。")
    private String title;

    @Size(max = 500, message = "内容は500文字以内で入力してください。")
    private String content;
}
