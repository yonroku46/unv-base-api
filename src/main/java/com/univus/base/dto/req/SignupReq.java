package com.univus.base.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会員登録リクエストDTO
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupReq {

    @NotBlank(message = "メールアドレスは必須です。")
    @Email(message = "有効なメールアドレス形式で入力してください。")
    private String email;

    @NotBlank(message = "パスワードは必須です。")
    @Size(min = 6, message = "パスワードは6文字以上で入力してください。")
    private String password;

    @NotBlank(message = "お名前は必須です。")
    @Size(max = 50, message = "お名前は50文字以内で入力してください。")
    private String userName;

    public String getMail() {
        return this.email;
    }

    public void setMail(String mail) {
        this.email = mail;
    }
}
