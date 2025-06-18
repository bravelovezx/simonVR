package com.example.simon.service.impl;

import com.example.simon.entity.Message;
import com.example.simon.mapper.MessageMapper;
import com.example.simon.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Message createMessage(Message message) {
        if (message.getCreatedAt() == null) {
            message.setCreatedAt(LocalDateTime.now());
        }
        messageMapper.insert(message);
        return message;
    }

    @Override
    public Message getMessageById(Integer messageId) {
        return messageMapper.selectById(messageId);
    }

    @Override
    public List<Message> getMessagesByConversationId(Integer conversationId) {
        return messageMapper.selectByConversationId(conversationId);
    }

    @Override
    public Message updateMessage(Message message) {
        messageMapper.update(message);
        return messageMapper.selectById(message.getMessageId());
    }

    @Override
    public boolean deleteMessage(Integer messageId) {
        messageMapper.deleteById(messageId);
        return true;
    }

    @Override
    public boolean deleteMessagesByConversationId(Integer conversationId) {
        messageMapper.deleteByConversationId(conversationId);
        return true;
    }
}