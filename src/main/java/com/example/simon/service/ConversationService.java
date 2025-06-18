package com.example.simon.service;

import com.example.simon.entity.Conversation;

import java.util.List;
import java.util.Map;

public interface ConversationService {
    /**
     * 创建新会话
     * @param conversation 会话信息
     * @return 创建的会话
     */
    Conversation createConversation(Conversation conversation);
    
    /**
     * 根据ID获取会话
     * @param conversationId 会话ID
     * @return 会话信息
     */
    Conversation getConversationById(Integer conversationId);
    
    /**
     * 获取用户的所有会话
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Conversation> getConversationsByUserId(Integer userId);
    
    /**
     * 搜索用户的会话
     * @param userId 用户ID
     * @param keyword 关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 会话列表
     */
    Map<String, Object> searchConversations(Integer userId, String keyword, int offset, int limit);
    
    /**
     * 更新会话信息
     * @param conversation 会话信息
     * @return 更新后的会话
     */
    Conversation updateConversation(Conversation conversation);
    
    /**
     * 归档会话
     * @param conversationId 会话ID
     * @return 更新后的会话
     */
    Conversation archiveConversation(Integer conversationId);
    
    /**
     * 删除会话
     * @param conversationId 会话ID
     * @return 是否删除成功
     */
    boolean deleteConversation(Integer conversationId);
}