package com.example.simon.service.impl;

import com.example.simon.entity.Conversation;
import com.example.simon.mapper.ConversationMapper;
import com.example.simon.mapper.MessageMapper;
import com.example.simon.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Conversation createConversation(Conversation conversation) {
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setStartedAt(LocalDateTime.now());
        if (conversation.getIsArchived() == null) {
            conversation.setIsArchived(false);
        }
        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public Conversation getConversationById(Integer conversationId) {
        return conversationMapper.selectById(conversationId);
    }

    @Override
    public List<Conversation> getConversationsByUserId(Integer userId) {
        return conversationMapper.selectByUserId(userId);
    }

    @Override
    public Map<String, Object> searchConversations(Integer userId, String keyword, int offset, int limit) {
        List<Conversation> conversations = conversationMapper.search(userId, keyword, offset, limit);
        int total = conversationMapper.countByUserId(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("conversations", conversations);
        result.put("total", total);
        result.put("offset", offset);
        result.put("limit", limit);
        
        return result;
    }

    @Override
    public Conversation updateConversation(Conversation conversation) {
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationMapper.update(conversation);
        return conversationMapper.selectById(conversation.getConversationId());
    }

    @Override
    public Conversation archiveConversation(Integer conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setIsArchived(true);
            conversation.setEndedAt(LocalDateTime.now());
            conversation.setUpdatedAt(LocalDateTime.now());
            conversationMapper.update(conversation);
        }
        return conversation;
    }

    @Override
    @Transactional
    public boolean deleteConversation(Integer conversationId) {
        // 先删除会话下的所有消息
        messageMapper.deleteByConversationId(conversationId);
        // 再删除会话
        conversationMapper.deleteById(conversationId);
        return true;
    }
}