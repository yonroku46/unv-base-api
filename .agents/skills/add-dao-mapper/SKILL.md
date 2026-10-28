---
name: add-dao-mapper
description: MyBatis를 사용한 새로운 DAO(Mapper) 인터페이스, Entity 및 XML 매퍼를 규약에 맞춰 안전하게 추가하는 스킬
---

# add-dao-mapper 스킬 가이드

본 스킬은 `unv-base-api`에서 데이터베이스 액세스를 수행하는 새로운 DAO, Entity 및 MyBatis XML 매퍼를 추가할 때의 표준 절차를 규정합니다.

## 1. 구현 절차 (Implementation Flow)

```
[1. Entity 클래스 생성] ──> [2. @Mapper 인터페이스 생성] ──> [3. XML 매퍼 생성] ──> [4. 검증]
```

### 단계 1: Entity 클래스 생성
- 위치: `com.univus.base.dao.entity.<Model>.java`
- 테이블 각 컬럼에 대응하는 필드를 정의하고 `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`를 부여합니다.
- 스네이크케이스(`user_id`)에 대해 Java 측은 카멜케이스(`userId`)를 사용합니다.

### 단계 2: DAO (@Mapper) 인터페이스 생성
- 위치: `com.univus.base.dao.<Model>Dao.java`
- 인터페이스에 `@Mapper`를 부여합니다.
- 복수 파라미터를 전달하는 메소드에는 각 인자에 `@Param("name")`을 명시합니다.

```java
package com.univus.base.dao;

import com.univus.base.dao.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ArticleDao {
    Article findById(@Param("articleId") String articleId);
    List<Article> findByAuthor(@Param("authorId") String authorId);
    int insert(Article article);
    int update(Article article);
    int delete(@Param("articleId") String articleId);
}
```

### 단계 3: MyBatis XML 매퍼 생성
- 위치: `src/main/resources/sql/<category>/<Model>Mapper.xml`
- `namespace`에는 생성한 DAO의 완전 정규화 클래스명(FQCN)을 지정합니다.
- SQL 인젝션 방지를 위해 파라미터 참조에는 반드시 `#{paramName}`을 사용합니다 (`${...}` 사용 금지).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.univus.base.dao.ArticleDao">

    <resultMap id="BaseResultMap" type="com.univus.base.dao.entity.Article">
        <id column="article_id" jdbcType="VARCHAR" property="articleId" />
        <result column="author_id" jdbcType="VARCHAR" property="authorId" />
        <result column="title" jdbcType="VARCHAR" property="title" />
        <result column="content" jdbcType="LONGVARCHAR" property="content" />
        <result column="created_at" jdbcType="TIMESTAMP" property="createdAt" />
        <result column="updated_at" jdbcType="TIMESTAMP" property="updatedAt" />
    </resultMap>

    <select id="findById" resultMap="BaseResultMap">
        SELECT *
        FROM `article`
        WHERE article_id = #{articleId}
        LIMIT 1
    </select>

    <insert id="insert" parameterType="com.univus.base.dao.entity.Article">
        INSERT INTO `article` (
            article_id, author_id, title, content, created_at, updated_at
        ) VALUES (
            #{articleId}, #{authorId}, #{title}, #{content}, NOW(), NOW()
        )
    </insert>

</mapper>
```

### 단계 4: 컴파일 및 매핑 검증
아래 명령어를 실행하여 XML 문법 에러나 컴파일 에러가 없음을 확인합니다.

```bash
./gradlew compileJava
```
