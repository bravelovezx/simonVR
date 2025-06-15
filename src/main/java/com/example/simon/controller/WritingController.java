package com.example.simon.controller;

import com.example.simon.entity.User;
import com.example.simon.entity.Writing;
import com.example.simon.entity.WritingVersion;
import com.example.simon.entity.Annotation;
import com.example.simon.service.UserService;
import com.example.simon.service.WritingService;
import com.example.simon.service.AnnotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/writings")
@Tag(name = "作文管理", description = "作文与版本管理相关API")
public class WritingController {

    @Autowired
    private WritingService writingService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnotationService annotationService;

    // ✅ 通用方法：获取当前登录用户的 userId
    private Integer getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("未找到当前登录用户");
        }
        return currentUser.getUserId();
    }

    @PostMapping
    @Operation(summary = "创建作文")
    public ResponseEntity<?> createWriting(@RequestBody Writing writing) {
        try {
            Integer userId = getCurrentUserId();
            writing.setUserId(userId);
            Writing result = writingService.createWriting(writing);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }




    @GetMapping
    @Operation(summary = "搜索作文列表")
    public ResponseEntity<List<Writing>> searchWritings(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(writingService.searchWritings(getCurrentUserId(), keyword, offset, limit));
    }

    @GetMapping("/{writingId}/versions")
    @Operation(summary = "获取作文所有版本")
    public ResponseEntity<List<WritingVersion>> getVersions(@PathVariable Integer writingId) {
        return ResponseEntity.ok(writingService.getVersionsByWritingId(writingId, getCurrentUserId()));
    }


    @GetMapping("/{writingId}")
    @Operation(summary = "获取作文详情")
    public ResponseEntity<Writing> getWriting(@PathVariable Integer writingId) {
        return ResponseEntity.ok(writingService.getWritingById(writingId, getCurrentUserId()));
    }

    @PutMapping("/{writingId}")
    @Operation(summary = "更新作文")
    public ResponseEntity<Writing> updateWriting(
            @PathVariable Integer writingId,
            @RequestBody Writing writing) {
        writing.setWritingId(writingId);
        writing.setUserId(getCurrentUserId());
        return ResponseEntity.ok(writingService.updateWriting(writing));
    }

    @DeleteMapping("/{writingId}")
    @Operation(summary = "删除作文及其版本")
    public ResponseEntity<?> deleteWriting(@PathVariable Integer writingId) {
        try {
            Integer currentUserId = getCurrentUserId();

            // 1. 查询该作文是否属于当前用户
            Writing writing = writingService.getWritingById(writingId, currentUserId);
            if (writing == null) {
                return ResponseEntity.status(403).body("无权限删除该作文");
            }

            // 2. 删除该作文及其所有版本
            writingService.deleteWriting(writingId, currentUserId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @PostMapping("/{writingId}/versions")
    @Operation(summary = "添加作文版本")
    public ResponseEntity<WritingVersion> addVersion(
            @PathVariable Integer writingId,
            @RequestBody WritingVersion version) {
        Writing writing = writingService.getWritingById(writingId, getCurrentUserId());
        if (writing == null) return ResponseEntity.notFound().build();
        version.setWritingId(writingId);
        return ResponseEntity.ok(writingService.addVersion(version));
    }



    @GetMapping("/versions/{versionId}")
    @Operation(summary = "获取作文版本详情")
    public ResponseEntity<Map<String, Object>> getVersion(@PathVariable Integer versionId) {
        Integer currentUserId = getCurrentUserId();
        
        // 获取版本详情
        WritingVersion version = writingService.getVersionById(versionId, currentUserId);
        
        // 获取与该版本相关的所有批注
        List<Annotation> annotations = annotationService.getAnnotationsByUserIdAndModuleAndRefId(
                currentUserId, "writing", versionId);
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("version", version);
        response.put("annotations", annotations);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/versions/{versionId}")
    @Operation(summary = "删除作文版本")
    public ResponseEntity<?> deleteVersion(@PathVariable Integer versionId) {
        try {
            Integer currentUserId = getCurrentUserId();

            // 1. 先查数据库，确认这个版本是否属于当前用户的写作记录
            WritingVersion version = writingService.getVersionById(versionId, currentUserId);
            if (version == null) {
                return ResponseEntity.status(403).body("无权限删除该作文版本");
            }

            // 2. 验证通过后执行删除
            writingService.deleteVersion(versionId, currentUserId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/polish")
    @Operation(summary = "AI润色作文原文")
    public ResponseEntity<String> polishText(@RequestBody Map<String, String> payload) {
        String draft = payload.get("userDraft");
        if (draft == null || draft.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("内容不能为空");
        }
        String polished = writingService.aiPolish(draft);
        return ResponseEntity.ok(polished);
    }

    @GetMapping("/with-versions")
    @Operation(summary = "获取用户所有作文及其对应的所有版本")
    public ResponseEntity<List<Map<String, Object>>> getWritingsWithVersions(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        Integer currentUserId = getCurrentUserId();
        
        // 获取用户的所有作文
        List<Writing> writings = writingService.searchWritings(currentUserId, keyword, offset, limit);
        
        // 为每个作文获取其所有版本，并构建响应数据
        List<Map<String, Object>> result = writings.stream().map(writing -> {
            Map<String, Object> writingWithVersions = new HashMap<>();
            writingWithVersions.put("writing", writing);
            
            // 获取该作文的所有版本
            List<WritingVersion> versions = writingService.getVersionsByWritingId(writing.getWritingId(), currentUserId);
            writingWithVersions.put("versions", versions);
            
            return writingWithVersions;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
}
