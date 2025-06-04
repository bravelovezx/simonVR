package com.example.simon.controller;

import com.example.simon.entity.Annotation;
import com.example.simon.service.AnnotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/annotations")
@Tag(name = "注释管理", description = "注释相关的API")
@PreAuthorize("isAuthenticated()")
public class AnnotationController {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationController.class);

    @Autowired
    private AnnotationService annotationService;

    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("=== getCurrentUserId() 方法调试 ===");
        logger.debug("Authentication对象: {}", authentication);
        logger.debug("Authentication类型: {}", authentication != null ? authentication.getClass().getName() : "null");
        logger.debug("Authentication主体: {}", authentication != null ? authentication.getPrincipal() : "null");
        
        if (authentication == null) {
            logger.error("Authentication为null，无法获取用户ID");
            throw new RuntimeException("Authentication为null，用户未认证");
        }
        
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        logger.debug("Authentication.getDetails(): {}", details);
        
        if (details == null) {
            logger.error("Authentication.getDetails()为null，无法获取用户ID");
            throw new RuntimeException("Authentication details为null，无法获取用户ID");
        }
        
        Integer userId = (Integer) details.get("userId");
        logger.debug("从details中获取的userId: {}", userId);
        
        if (userId == null) {
            logger.error("从Authentication details中获取的userId为null");
            throw new RuntimeException("从Authentication details中获取的userId为null");
        }
        
        logger.info("成功获取当前用户ID: {}", userId);
        return userId;
    }

    /**
     * 创建注释记录
     */
    @PostMapping
    @Operation(summary = "创建注释记录")
    public ResponseEntity<Map<String, Object>> createAnnotation(@RequestBody Annotation annotation) {
        logger.info("=== API调用: 创建注释记录 ===");
        
        // 记录请求体中原始的用户ID
        Integer originalUserId = annotation.getUserId();
        logger.info("请求体中的原始用户ID: {}", originalUserId);
        
        // 自动设置当前用户ID
        Integer currentUserId = getCurrentUserId();
        annotation.setUserId(currentUserId);
        
        logger.info("请求参数: {}", annotation);
        logger.info("自动设置用户ID: {}", currentUserId);
        logger.info("设置后annotation中的用户ID: {}", annotation.getUserId());
        
        // 验证设置是否成功
        if (!currentUserId.equals(annotation.getUserId())) {
            logger.error("用户ID设置失败! 期望: {}, 实际: {}", currentUserId, annotation.getUserId());
            throw new RuntimeException("用户ID设置失败");
        }
        
        Map<String, Object> response = new HashMap<>();
        try {
            Annotation created = annotationService.createAnnotation(annotation);
            response.put("success", true);
            response.put("message", "注释记录创建成功");
            response.put("data", created);
            
            logger.info("API响应成功: {}", response);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            logger.warn("API请求参数错误: {}", e.getMessage());
            logger.info("API响应错误: {}", response);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建注释记录失败");
            response.put("errorDetail", e.getMessage()); // 添加详细错误信息用于调试
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取注释记录（仅当前用户可访问）
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取注释记录")
    public ResponseEntity<Map<String, Object>> getAnnotationById(@PathVariable Integer id) {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 根据ID获取注释记录 ===");
        logger.info("请求参数: id={}, 当前用户ID: {}", id, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            Annotation annotation = annotationService.getAnnotationByIdAndUserId(id, currentUserId);
            if (annotation != null) {
                response.put("success", true);
                response.put("data", annotation);
                
                logger.info("API响应成功，找到记录: {}", annotation);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "注释记录不存在或无权限访问");
                
                logger.warn("API响应: 记录不存在或无权限访问，ID={}, 用户ID={}", id, currentUserId);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            logger.warn("API请求参数错误: {}", e.getMessage());
            logger.info("API响应错误: {}", response);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取注释记录失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当前用户的注释记录列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取当前用户的注释记录列表")
    public ResponseEntity<Map<String, Object>> getMyAnnotations() {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 获取当前用户的注释记录列表 ===");
        logger.info("当前用户ID: {}", currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Annotation> annotations = annotationService.getAnnotationsByUserId(currentUserId);
            response.put("success", true);
            response.put("data", annotations);
            response.put("total", annotations.size());
            
            logger.info("API响应成功，返回记录数: {}", annotations.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取注释记录列表失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新注释记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新注释记录")
    public ResponseEntity<Map<String, Object>> updateAnnotation(
            @PathVariable Integer id, @RequestBody Annotation annotation) {
        logger.info("=== API调用: 更新注释记录 ===");
        
        // 自动设置当前用户ID
        Integer currentUserId = getCurrentUserId();
        annotation.setUserId(currentUserId);
        annotation.setAnnotationId(id);
        
        logger.info("请求参数: id={}, annotation={}", id, annotation);
        logger.info("自动设置用户ID: {}", currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            Annotation updated = annotationService.updateAnnotation(annotation);
            response.put("success", true);
            response.put("message", "注释记录更新成功");
            response.put("data", updated);
            
            logger.info("API响应成功: {}", response);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            logger.warn("API请求参数错误: {}", e.getMessage());
            logger.info("API响应错误: {}", response);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新注释记录失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除注释记录（仅当前用户可删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除注释记录")
    public ResponseEntity<Map<String, Object>> deleteAnnotation(@PathVariable Integer id) {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 删除注释记录 ===");
        logger.info("请求参数: id={}, 当前用户ID: {}", id, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            annotationService.deleteAnnotationByIdAndUserId(id, currentUserId);
            response.put("success", true);
            response.put("message", "注释记录删除成功");
            
            logger.info("API响应成功: {}", response);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            logger.warn("API请求参数错误: {}", e.getMessage());
            logger.info("API响应错误: {}", response);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除注释记录失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除当前用户所有注释记录
     */
    @DeleteMapping("/my/all")
    @Operation(summary = "删除当前用户所有注释记录")
    public ResponseEntity<Map<String, Object>> deleteAllMyAnnotations() {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 删除当前用户所有注释记录 ===");
        logger.info("当前用户ID: {}", currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            annotationService.deleteAllAnnotationsByUserId(currentUserId);
            response.put("success", true);
            response.put("message", "所有注释记录删除成功");
            
            logger.info("API响应成功: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除所有注释记录失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 