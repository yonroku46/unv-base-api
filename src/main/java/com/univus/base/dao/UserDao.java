package com.univus.base.dao;

import com.univus.base.dao.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ユーザーDAOマッパー
 *
 * @author Univus
 */
@Mapper
public interface UserDao {

    /**
     * ユーザーIDとメールアドレスでユーザーを検索
     */
    User findUser(@Param("userId") String userId, @Param("mail") String mail);

    /**
     * ユーザーIDで検索
     */
    User findById(@Param("userId") String userId);

    /**
     * メールアドレスで検索
     */
    User findByMail(@Param("mail") String mail);

    /**
     * ユーザー登録
     */
    int insert(User user);

    /**
     * ユーザー更新
     */
    int update(User user);
}
