package com.example.simon.mapper;

import com.example.simon.entity.Conversation;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface ConversationMapper {
    @Insert("INSERT INTO conversation (user_id, title, started_at, ended_at, is_archived, created_at, updated_at) " +
            "VALUES (#{userId}, #{title}, #{startedAt}, #{endedAt}, #{isArchived}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "conversationId")
    void insert(Conversation conversation);
    
    @Select("SELECT conversation_id, user_id, title, started_at, ended_at, is_archived, created_at, updated_at " +
            "FROM conversation WHERE conversation_id = #{conversationId}")
    @Results(id = "conversationResultMap", value = {
            @Result(property = "conversationId", column = "conversation_id", jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", jdbcType = JdbcType.INTEGER),
            @Result(property = "title", column = "title", jdbcType = JdbcType.VARCHAR),
            @Result(property = "startedAt", column = "started_at", jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "endedAt", column = "ended_at", jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "isArchived", column = "is_archived", jdbcType = JdbcType.BOOLEAN),
            @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "updatedAt", column = "updated_at", jdbcType = JdbcType.TIMESTAMP)
    })
    Conversation selectById(Integer conversationId);
    
    @Select("SELECT conversation_id, user_id, title, started_at, ended_at, is_archived, created_at, updated_at " +
            "FROM conversation WHERE user_id = #{userId} ORDER BY created_at DESC")
    @ResultMap("conversationResultMap")
    List<Conversation> selectByUserId(Integer userId);
    
    @Select("<script>" +
            "SELECT conversation_id, user_id, title, started_at, ended_at, is_archived, created_at, updated_at " +
            "FROM conversation WHERE user_id = #{userId} " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND title LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    @ResultMap("conversationResultMap")
    List<Conversation> search(Integer userId, String keyword, int offset, int limit);
    
    @Update("UPDATE conversation SET title = #{title}, " +
            "ended_at = #{endedAt}, is_archived = #{isArchived}, updated_at = #{updatedAt} " +
            "WHERE conversation_id = #{conversationId}")
    void update(Conversation conversation);
    
    @Delete("DELETE FROM conversation WHERE conversation_id = #{conversationId}")
    void deleteById(Integer conversationId);
    
    @Select("SELECT COUNT(*) FROM conversation WHERE user_id = #{userId}")
    int countByUserId(Integer userId);
}