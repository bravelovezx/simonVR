package com.example.simon.controller;

import com.example.simon.entity.Message;
import com.example.simon.exception.ResourceNotFoundException;
import com.example.simon.exception.BusinessException;
import com.example.simon.service.ConversationService;
import com.example.simon.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResponseEntity<?> createMessage(@Valid @RequestBody Message message) {
        // 检查会话是否存在
        if (conversationService.getConversationById(message.getConversationId()) == null) {
            throw new ResourceNotFoundException("会话", "ID", message.getConversationId());
        }
        
        Message createdMessage = messageService.createMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<?> getMessage(@PathVariable Integer messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            throw new ResourceNotFoundException("消息", "ID", messageId);
        }
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<?> updateMessage(
            @PathVariable Integer messageId,
            @Valid @RequestBody Message message) {
        Message existingMessage = messageService.getMessageById(messageId);
        if (existingMessage == null) {
            throw new ResourceNotFoundException("消息", "ID", messageId);
        }
        
        message.setMessageId(messageId);
        message.setConversationId(existingMessage.getConversationId()); // 保持会话ID不变
        message.setRole(existingMessage.getRole()); // 保持角色不变
        message.setCreatedAt(existingMessage.getCreatedAt()); // 保持创建时间不变
        
        Message updatedMessage = messageService.updateMessage(message);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            throw new ResourceNotFoundException("消息", "ID", messageId);
        }
        
        boolean deleted = messageService.deleteMessage(messageId);
        if (!deleted) {
            throw new BusinessException("删除消息失败", 10002);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createBatchMessages(@Valid @RequestBody Message[] messages) {
        for (Message message : messages) {
            // 检查会话是否存在
            if (conversationService.getConversationById(message.getConversationId()) == null) {
                throw new ResourceNotFoundException("会话", "ID", message.getConversationId());
            }
            
            messageService.createMessage(message);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}