package com.example.simon.mapper;

import com.example.simon.entity.Message;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface MessageMapper {
    @Insert("INSERT INTO message (conversation_id, role, content, created_at) " +
            "VALUES (#{conversationId}, #{role}, #{content}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    void insert(Message message);
    
    @Select("SELECT message_id, conversation_id, role, content, created_at " +
            "FROM message WHERE message_id = #{messageId}")
    @Results(id = "messageResultMap", value = {
            @Result(property = "messageId", column = "message_id", jdbcType = JdbcType.INTEGER),
            @Result(property = "conversationId", column = "conversation_id", jdbcType = JdbcType.INTEGER),
            @Result(property = "role", column = "role", jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "content", jdbcType = JdbcType.VARCHAR),
            @Result(property = "createdAt", column = "created_at", jdbcType = JdbcType.TIMESTAMP)
    })
    Message selectById(Integer messageId);
    
    @Select("SELECT message_id, conversation_id, role, content, created_at " +
            "FROM message WHERE conversation_id = #{conversationId} ORDER BY created_at ASC")
    @ResultMap("messageResultMap")
    List<Message> selectByConversationId(Integer conversationId);
    
    @Update("UPDATE message SET content = #{content} WHERE message_id = #{messageId}")
    void update(Message message);
    
    @Delete("DELETE FROM message WHERE message_id = #{messageId}")
    void deleteById(Integer messageId);
    
    @Delete("DELETE FROM message WHERE conversation_id = #{conversationId}")
    void deleteByConversationId(Integer conversationId);
    
    @Select("SELECT COUNT(*) FROM message WHERE conversation_id = #{conversationId}")
    int countByConversationId(Integer conversationId);
}