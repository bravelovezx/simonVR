package com.example.simon.service;

import com.example.simon.entity.Message;

import java.util.List;

public interface MessageService {
    /**
     * 创建新消息
     * @param message 消息信息
     * @return 创建的消息
     */
    Message createMessage(Message message);
    
    /**
     * 根据ID获取消息
     * @param messageId 消息ID
     * @return 消息信息
     */
    Message getMessageById(Integer messageId);
    
    /**
     * 获取会话的所有消息
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<Message> getMessagesByConversationId(Integer conversationId);
    
    /**
     * 更新消息内容
     * @param message 消息信息
     * @return 更新后的消息
     */
    Message updateMessage(Message message);
    
    /**
     * 删除消息
     * @param messageId 消息ID
     * @return 是否删除成功
     */
    boolean deleteMessage(Integer messageId);
    
    /**
     * 删除会话的所有消息
     * @param conversationId 会话ID
     * @return 是否删除成功
     */
    boolean deleteMessagesByConversationId(Integer conversationId);
}