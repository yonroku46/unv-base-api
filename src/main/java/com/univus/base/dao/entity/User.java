package com.univus.base.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 사용자 엔티티
 *
 * @author Univus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 사용자 ID
     */
    private String userId;

    /**
     * 이메일
     */
    private String mail;

    /**
     * 사용자 이름
     */
    private String userName;

    /**
     * 자기소개
     */
    private String userIntro;

    /**
     * 비밀번호 (암호화)
     */
    private String userPw;

    /**
     * 전화번호
     */
    private String phoneNum;

    /**
     * 프로필 이미지 URL
     */
    private String profileImg;

    /**
     * 권한 역할 ID (1: CLIENT, 2: PARTNER, 8: MANAGER, 9: ROOT)
     */
    private Integer roleId;

    /**
     * 생성일시
     */
    private Date createdAt;

    /**
     * 수정일시
     */
    private Date updatedAt;
}
