package com.univus.base.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ログインリクエストDTO
 *
 * @author Univus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq {

    @NotBlank(message = "メールアドレスは必須です。")
    @Email(message = "有効なメールアドレス形式で入力してください。")
    private String email;

    @NotBlank(message = "パスワードは必須です。")
    private String password;

    /**
     * mailエイリアス対応
     */
    public String getMail() {
        return this.email;
    }

    public void setMail(String mail) {
        this.email = mail;
    }
}
