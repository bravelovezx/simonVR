package com.example.simon.controller;

import com.example.simon.entity.Conversation;
import com.example.simon.entity.Message;
import com.example.simon.exception.ResourceNotFoundException;
import com.example.simon.exception.BusinessException;
import com.example.simon.service.ConversationService;
import com.example.simon.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;
    
    /**
     * 获取当前登录用户的ID
     * @return 当前用户ID
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            if (details != null && details.containsKey("userId")) {
                return (Integer) details.get("userId");
            }
        }
        throw new BusinessException("无法获取当前用户信息，请重新登录", 10003);
    }

    @PostMapping
    public ResponseEntity<?> createConversation(@Valid @RequestBody Conversation conversation) {
        // 自动设置当前用户ID
        conversation.setUserId(getCurrentUserId());
        Conversation createdConversation = conversationService.createConversation(conversation);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConversation);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getConversation(@PathVariable Integer conversationId) {
        Conversation conversation = conversationService.getConversationById(conversationId);
        if (conversation == null) {
            throw new ResourceNotFoundException("会话", "ID", conversationId);
        }
        // 验证会话所属用户
        validateConversationOwner(conversation);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping
    public ResponseEntity<?> getUserConversations(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        // 自动获取当前用户ID
        Integer userId = getCurrentUserId();
        Map<String, Object> result = conversationService.searchConversations(userId, keyword, offset, limit);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{conversationId}")
    public ResponseEntity<?> updateConversation(
            @PathVariable Integer conversationId,
            @Valid @RequestBody Conversation conversation) {
        Conversation existingConversation = conversationService.getConversationById(conversationId);
        if (existingConversation == null) {
            throw new ResourceNotFoundException("会话", "ID", conversationId);
        }
        
        // 验证会话所属用户
        validateConversationOwner(existingConversation);
        
        conversation.setConversationId(conversationId);
        // 保持原始用户ID不变
        conversation.setUserId(existingConversation.getUserId());
        Conversation updatedConversation = conversationService.updateConversation(conversation);
        return ResponseEntity.ok(updatedConversation);
    }

    @PutMapping("/{conversationId}/archive")
    public ResponseEntity<?> archiveConversation(@PathVariable Integer conversationId) {
        Conversation conversation = conversationService.getConversationById(conversationId);
        if (conversation == null) {
            throw new ResourceNotFoundException("会话", "ID", conversationId);
        }
        
        // 验证会话所属用户
        validateConversationOwner(conversation);
        
        Conversation archivedConversation = conversationService.archiveConversation(conversationId);
        return ResponseEntity.ok(archivedConversation);
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<?> deleteConversation(@PathVariable Integer conversationId) {
        Conversation conversation = conversationService.getConversationById(conversationId);
        if (conversation == null) {
            throw new ResourceNotFoundException("会话", "ID", conversationId);
        }
        
        // 验证会话所属用户
        validateConversationOwner(conversation);
        
        boolean deleted = conversationService.deleteConversation(conversationId);
        if (!deleted) {
            throw new BusinessException("删除会话失败", 10001);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<?> getConversationMessages(@PathVariable Integer conversationId) {
        Conversation conversation = conversationService.getConversationById(conversationId);
        if (conversation == null) {
            throw new ResourceNotFoundException("会话", "ID", conversationId);
        }
        
        // 验证会话所属用户
        validateConversationOwner(conversation);
        
        List<Message> messages = messageService.getMessagesByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{conversationId}/with-messages")
    public ResponseEntity<?> getConversationWithMessages(@PathVariable Integer conversationId) {
        Conversation conversation = conversationService.getConversationById(conversationId);
        if (conversation == null) {
            throw new ResourceNotFoundException("会话", "ID", conversationId);
        }
        
        // 验证会话所属用户
        validateConversationOwner(conversation);
        
        List<Message> messages = messageService.getMessagesByConversationId(conversationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("conversation", conversation);
        result.put("messages", messages);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 验证会话是否属于当前用户
     * @param conversation 会话对象
     */
    private void validateConversationOwner(Conversation conversation) {
        Integer currentUserId = getCurrentUserId();
        if (!currentUserId.equals(conversation.getUserId())) {
            throw new BusinessException("无权访问此会话", 10004);
        }
    }
}