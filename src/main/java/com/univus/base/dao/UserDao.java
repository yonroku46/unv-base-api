package com.univus.base.dao;

import com.univus.base.dao.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 사용자 DAO 매퍼
 *
 * @author Univus
 */
@Mapper
public interface UserDao {

    /**
     * 사용자 ID 및 이메일로 사용자 조회
     */
    User findUser(@Param("userId") String userId, @Param("mail") String mail);

    /**
     * 사용자 ID로 조회
     */
    User findById(@Param("userId") String userId);

    /**
     * 이메일로 조회
     */
    User findByMail(@Param("mail") String mail);

    /**
     * 사용자 등록
     */
    int insert(User user);

    /**
     * 사용자 수정
     */
    int update(User user);
}
