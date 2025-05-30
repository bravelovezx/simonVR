package com.example.simon.controller;

import com.example.simon.entity.DialogueSession;
import com.example.simon.entity.DialogueTurn;
import com.example.simon.service.DialogueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dialogues")
@Tag(name = "对话管理", description = "对话会话和轮次相关的API")
@PreAuthorize("isAuthenticated()")
public class DialogueController {

    @Autowired
    private DialogueService dialogueService;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        return (Integer) details.get("userId");
    }

    // Session endpoints
    @PostMapping("/sessions")
    @Operation(summary = "创建对话会话")
    public ResponseEntity<DialogueSession> createSession(@RequestBody(required = false) DialogueSession session) {
        if (session == null) {
            session = new DialogueSession();
        }

        // 设置必要的字段
        Integer userId = getCurrentUserId();
        session.setUserId(userId);

        // 如果没有设置开始时间，则使用当前时间
        if (session.getStartedAt() == null) {
            session.setStartedAt(LocalDateTime.now());
        }

        // 如果没有设置场景，则使用默认场景
        if (session.getScene() == null || session.getScene().trim().isEmpty()) {
            session.setScene("普通对话");
        }

        return ResponseEntity.ok(dialogueService.createSession(session));
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "获取对话会话")
    public ResponseEntity<DialogueSession> getSession(@PathVariable Integer sessionId) {
        Integer userId = getCurrentUserId();
        return ResponseEntity.ok(dialogueService.getSessionById(sessionId, userId));
    }

    @GetMapping("/sessions")
    @Operation(summary = "获取用户的所有对话会话")
    public ResponseEntity<List<DialogueSession>> getUserSessions() {
        Integer userId = getCurrentUserId();
        return ResponseEntity.ok(dialogueService.getSessionsByUserId(userId));
    }

    @PutMapping("/sessions/{sessionId}")
    @Operation(summary = "更新对话会话")
    public ResponseEntity<DialogueSession> updateSession(
            @PathVariable Integer sessionId,
            @RequestBody DialogueSession session) {
        Integer userId = getCurrentUserId();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        return ResponseEntity.ok(dialogueService.updateSession(session));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "删除对话会话")
    public ResponseEntity<Void> deleteSession(@PathVariable Integer sessionId) {
        Integer userId = getCurrentUserId();
        dialogueService.deleteSession(sessionId, userId);
        return ResponseEntity.ok().build();
    }

    // Turn endpoints
    @PostMapping("/sessions/{sessionId}/turns")
    @Operation(summary = "添加对话轮次")
    public ResponseEntity<DialogueTurn> addTurn(
            @PathVariable Integer sessionId,
            @RequestBody DialogueTurn turn) {
        Integer userId = getCurrentUserId();
        // 验证会话所有权
        DialogueSession session = dialogueService.getSessionById(sessionId, userId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        // 验证 speaker 字段
        if (turn.getSpeaker() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        turn.setSessionId(sessionId);
        // 设置创建时间
        if (turn.getCreatedAt() == null) {
            turn.setCreatedAt(LocalDateTime.now());
        }
        return ResponseEntity.ok(dialogueService.addTurn(turn));
    }

    @GetMapping("/turns/{turnId}")
    @Operation(summary = "获取对话轮次")
    public ResponseEntity<DialogueTurn> getTurn(@PathVariable Integer turnId) {
        Integer userId = getCurrentUserId();
        return ResponseEntity.ok(dialogueService.getTurnById(turnId, userId));
    }

    @GetMapping("/sessions/{sessionId}/turns")
    @Operation(summary = "获取对话轮次列表")
    public ResponseEntity<List<DialogueTurn>> getSessionTurns(@PathVariable Integer sessionId) {
        Integer userId = getCurrentUserId();
        return ResponseEntity.ok(dialogueService.getTurnsBySessionId(sessionId, userId));
    }

    @PutMapping("/turns/{turnId}")
    @Operation(summary = "更新对话轮次")
    public ResponseEntity<DialogueTurn> updateTurn(
            @PathVariable Integer turnId,
            @RequestBody DialogueTurn turn) {
        Integer userId = getCurrentUserId();
        turn.setTurnId(turnId);
        return ResponseEntity.ok(dialogueService.updateTurn(turn, userId));
    }

    @DeleteMapping("/turns/{turnId}")
    @Operation(summary = "删除对话轮次")
    public ResponseEntity<Void> deleteTurn(@PathVariable Integer turnId) {
        Integer userId = getCurrentUserId();
        dialogueService.deleteTurn(turnId, userId);
        return ResponseEntity.ok().build();
    }
}