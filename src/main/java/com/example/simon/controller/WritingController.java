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
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
    public ResponseEntity<?> createWriting(@Valid @RequestBody Writing writing) {
        try {
            Integer userId = getCurrentUserId();
            writing.setUserId(userId);
            Writing result = writingService.createWriting(writing);
            return ResponseEntity.ok(result);
        } catch (ValidationException e) {
            // 参数校验错误 - 400 Bad Request
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            // 认证错误 - 401 Unauthorized
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            // 鉴权错误 - 403 Forbidden
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (NoSuchElementException e) {
            // 资源不存在 - 404 Not Found
            Map<String, Object> error = new HashMap<>();
            error.put("message", "请求的资源不存在");
            error.put("errorType", "资源不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 业务异常 - 200 OK + 业务码
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "业务异常");
            error.put("errorCode", 10001); // 示例业务错误码
            return ResponseEntity.ok(error);
        } catch (Exception e) {
            // 系统异常 - 500 Internal Server Error
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            // 记录日志，但不返回详细错误给前端
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    @Operation(summary = "搜索作文列表")
    public ResponseEntity<?> searchWritings(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Writing> writings = writingService.searchWritings(getCurrentUserId(), keyword, offset, limit);
            return ResponseEntity.ok(writings);
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{writingId}/versions")
    @Operation(summary = "获取作文所有版本")
    public ResponseEntity<?> getVersions(@PathVariable Integer writingId) {
        try {
            List<WritingVersion> versions = writingService.getVersionsByWritingId(writingId, getCurrentUserId());
            return ResponseEntity.ok(versions);
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (NoSuchElementException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "请求的作文不存在");
            error.put("errorType", "资源不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{writingId}")
    @Operation(summary = "获取作文详情")
    public ResponseEntity<?> getWriting(@PathVariable Integer writingId) {
        try {
            Writing writing = writingService.getWritingById(writingId, getCurrentUserId());
            if (writing == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "请求的作文不存在");
                error.put("errorType", "资源不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(writing);
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{writingId}")
    @Operation(summary = "更新作文")
    public ResponseEntity<?> updateWriting(
            @PathVariable Integer writingId,
            @Valid @RequestBody Writing writing) {
        try {
            writing.setWritingId(writingId);
            writing.setUserId(getCurrentUserId());
            Writing updatedWriting = writingService.updateWriting(writing);
            return ResponseEntity.ok(updatedWriting);
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (NoSuchElementException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "请求的作文不存在");
            error.put("errorType", "资源不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "业务异常");
            error.put("errorCode", 10001);
            return ResponseEntity.ok(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{writingId}")
    @Operation(summary = "删除作文及其版本")
    public ResponseEntity<?> deleteWriting(@PathVariable Integer writingId) {
        try {
            Integer currentUserId = getCurrentUserId();

            // 1. 查询该作文是否属于当前用户
            Writing writing = writingService.getWritingById(writingId, currentUserId);
            if (writing == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "无权限删除该作文或作文不存在");
                error.put("errorType", "鉴权错误");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // 2. 删除该作文及其所有版本
            writingService.deleteWriting(writingId, currentUserId);
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/{writingId}/versions")
    @Operation(summary = "添加作文版本")
    public ResponseEntity<?> addVersion(
            @PathVariable Integer writingId,
            @Valid @RequestBody WritingVersion version) {
        try {
            Writing writing = writingService.getWritingById(writingId, getCurrentUserId());
            if (writing == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "请求的作文不存在");
                error.put("errorType", "资源不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            version.setWritingId(writingId);
            WritingVersion addedVersion = writingService.addVersion(version);
            return ResponseEntity.ok(addedVersion);
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "业务异常");
            error.put("errorCode", 10001);
            return ResponseEntity.ok(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/versions/{versionId}")
    @Operation(summary = "获取作文版本详情")
    public ResponseEntity<?> getVersion(@PathVariable Integer versionId) {
        try {
            Integer currentUserId = getCurrentUserId();
            
            // 获取版本详情
            WritingVersion version = writingService.getVersionById(versionId, currentUserId);
            if (version == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "请求的作文版本不存在");
                error.put("errorType", "资源不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            // 获取与该版本相关的所有批注
            List<Annotation> annotations = annotationService.getAnnotationsByUserIdAndModuleAndRefId(
                    currentUserId, "writing", versionId);
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("version", version);
            response.put("annotations", annotations);
            
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/versions/{versionId}")
    @Operation(summary = "删除作文版本")
    public ResponseEntity<?> deleteVersion(@PathVariable Integer versionId) {
        try {
            Integer currentUserId = getCurrentUserId();

            // 1. 先查数据库，确认这个版本是否属于当前用户的写作记录
            WritingVersion version = writingService.getVersionById(versionId, currentUserId);
            if (version == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "无权限删除该作文版本或版本不存在");
                error.put("errorType", "鉴权错误");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // 2. 验证通过后执行删除
            writingService.deleteVersion(versionId, currentUserId);
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/versions/{versionId}")
    @Operation(summary = "修改作文版本内容")
    public ResponseEntity<?> updateVersion(
            @PathVariable Integer versionId,
            @Valid @RequestBody WritingVersion version) {
        try {
            Integer currentUserId = getCurrentUserId();
            
            // 设置版本ID
            version.setVersionId(versionId);
            
            // 更新版本内容
            WritingVersion updatedVersion = writingService.updateVersion(version, currentUserId);
            return ResponseEntity.ok(updatedVersion);
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (NoSuchElementException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "资源不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "业务异常");
            error.put("errorCode", 10001);
            return ResponseEntity.ok(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/polish")
    @Operation(summary = "AI润色作文原文")
    public ResponseEntity<?> polishText(@RequestBody Map<String, String> payload) {
        try {
            String draft = payload.get("userDraft");
            if (draft == null || draft.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "内容不能为空");
                error.put("errorType", "参数校验错误");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            String polished = writingService.aiPolish(draft);
            return ResponseEntity.ok(polished);
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/with-versions")
    @Operation(summary = "获取用户所有作文及其对应的所有版本")
    public ResponseEntity<?> getWritingsWithVersions(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        try {
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
        } catch (ValidationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("errorType", "参数校验错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "用户未登录或Token过期");
            error.put("errorType", "认证错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AccessDeniedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "无权限执行此操作");
            error.put("errorType", "鉴权错误");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "系统发生异常，请稍后再试");
            error.put("errorType", "系统异常");
            System.err.println("系统异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}