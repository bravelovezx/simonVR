package com.example.simon.mapper;

import com.example.simon.entity.Reading;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface ReadingMapper {

    /**
     * 插入阅读记录
     */
    @Insert("INSERT INTO readings (user_id, source_type, article_title, article_content, created_at, updated_at) " +
            "VALUES (#{userId}, #{sourceType}, #{articleTitle}, #{articleContent}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "readingId")
    void insert(Reading reading);

    /**
     * 根据ID和用户ID查询阅读记录（权限验证）
     */
    @Select("SELECT reading_id, user_id, source_type, article_title, article_content, created_at, updated_at " +
            "FROM readings WHERE reading_id = #{readingId} AND user_id = #{userId}")
    @Results(id = "readingResultMap", value = {
            @Result(property = "readingId", column = "reading_id", jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", jdbcType = JdbcType.INTEGER),
            @Result(property = "sourceType", column = "source_type", jdbcType = JdbcType.VARCHAR),
            @Result(property = "articleTitle", column = "article_title", jdbcType = JdbcType.VARCHAR),
            @Result(property = "articleContent", column = "article_content", jdbcType = JdbcType.LONGVARCHAR),
            @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "updatedAt", column = "updated_at", jdbcType = JdbcType.TIMESTAMP)
    })
    Reading selectByIdAndUserId(Integer readingId, Integer userId);

    /**
     * 根据用户ID查询阅读记录列表
     */
    @Select("SELECT reading_id, user_id, source_type, article_title, article_content, created_at, updated_at " +
            "FROM readings WHERE user_id = #{userId} ORDER BY created_at DESC")
    @ResultMap("readingResultMap")
    List<Reading> selectByUserId(Integer userId);

    /**
     * 根据用户ID和来源类型查询阅读记录列表
     */
    @Select("SELECT reading_id, user_id, source_type, article_title, article_content, created_at, updated_at " +
            "FROM readings WHERE user_id = #{userId} AND source_type = #{sourceType} ORDER BY created_at DESC")
    @ResultMap("readingResultMap")
    List<Reading> selectByUserIdAndSourceType(Integer userId, String sourceType);

    /**
     * 根据用户ID和标题关键词查询阅读记录列表
     */
    @Select("SELECT reading_id, user_id, source_type, article_title, article_content, created_at, updated_at " +
            "FROM readings WHERE user_id = #{userId} AND article_title LIKE CONCAT('%', #{keyword}, '%') ORDER BY created_at DESC")
    @ResultMap("readingResultMap")
    List<Reading> selectByUserIdAndTitleKeyword(Integer userId, String keyword);

    /**
     * 更新阅读记录
     */
    @Update("UPDATE readings SET source_type = #{sourceType}, article_title = #{articleTitle}, " +
            "article_content = #{articleContent}, updated_at = #{updatedAt} WHERE reading_id = #{readingId}")
    void update(Reading reading);

    /**
     * 根据ID和用户ID删除阅读记录（权限验证）
     */
    @Delete("DELETE FROM readings WHERE reading_id = #{readingId} AND user_id = #{userId}")
    int deleteByIdAndUserId(Integer readingId, Integer userId);

    /**
     * 根据用户ID删除所有阅读记录
     */
    @Delete("DELETE FROM readings WHERE user_id = #{userId}")
    int deleteAllByUserId(Integer userId);

    /**
     * 统计用户阅读记录数量
     */
    @Select("SELECT COUNT(*) FROM readings WHERE user_id = #{userId}")
    int countByUserId(Integer userId);

    /**
     * 根据来源类型统计用户阅读记录数量
     */
    @Select("SELECT COUNT(*) FROM readings WHERE user_id = #{userId} AND source_type = #{sourceType}")
    int countByUserIdAndSourceType(Integer userId, String sourceType);
} 