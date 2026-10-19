package com.univus.base.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * ユーザーエンティティ
 *
 * @author Univus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * ユーザーID
     */
    private String userId;

    /**
     * メールアドレス
     */
    private String mail;

    /**
     * ユーザー名
     */
    private String userName;

    /**
     * 自己紹介
     */
    private String userIntro;

    /**
     * パスワード（暗号化）
     */
    private String userPw;

    /**
     * 電話番号
     */
    private String phoneNum;

    /**
     * プロフィール画像URL
     */
    private String profileImg;

    /**
     * ロールID (1: CLIENT, 2: PARTNER, 8: MANAGER, 9: ROOT)
     */
    private Integer roleId;

    /**
     * 作成日時
     */
    private Date createdAt;

    /**
     * 更新日時
     */
    private Date updatedAt;
}
