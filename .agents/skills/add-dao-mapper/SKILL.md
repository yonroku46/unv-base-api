---
name: add-dao-mapper
description: MyBatisを使用した新しいDAO（Mapper）インターフェース、Entity、およびXMLマッパーを規約に沿って安全に追加するスキル
---

# add-dao-mapper スキルガイド

本スキルは、`unv-base-api` においてデータベースアクセスを行う新しいDAO、Entity、およびMyBatis XMLマッパーを追加する際の標準手順を定めます。

## 1. 実装手順（Implementation Flow）

```
[1. Entityクラス作成] ──> [2. @Mapperインターフェース作成] ──> [3. XMLマッパー作成] ──> [4. 検証]
```

### ステップ 1: Entityクラスの作成
- 配置場所: `com.univus.base.dao.entity.<Model>.java`
- テーブルの各カラムに対応するフィールドを定義し、`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` を付与します。
- スネークケース（`user_id`）に対してJava側はキャメルケース（`userId`）を使用します。

### ステップ 2: DAO（@Mapper）インターフェースの作成
- 配置場所: `com.univus.base.dao.<Model>Dao.java`
- インターフェースに `@Mapper` を付与します。
- 複数パラメータを渡すメソッドには、各引数に `@Param("name")` を明示します。

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

### ステップ 3: MyBatis XMLマッパーの作成
- 配置場所: `src/main/resources/sql/<category>/<Model>Mapper.xml`
- `namespace` には作成したDAOの完全修飾クラス名（FQCN）を指定します。
- SQLインジェクション防止のため、パラメータ参照には必ず `#{paramName}` を使用します（`${...}` の使用は禁止）。

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

### ステップ 4: コンパイルおよびマッピング検証
以下のコマンドを実行し、XMLの文法エラーやコンパイルエラーがないことを確認します。

```bash
./gradlew compileJava
```
