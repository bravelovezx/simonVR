package com.example.simon.controller;

import com.example.simon.entity.Annotation;
import com.example.simon.service.AnnotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Autowired
    private AnnotationService annotationService;

    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            throw new RuntimeException("Authentication为null，用户未认证");
        }
        
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        
        if (details == null) {
            throw new RuntimeException("Authentication details为null，无法获取用户ID");
        }
        
        Integer userId = (Integer) details.get("userId");
        
        if (userId == null) {
            throw new RuntimeException("从Authentication details中获取的userId为null");
        }
        
        return userId;
    }

    /**
     * 创建注释记录
     */
    @PostMapping
    @Operation(summary = "创建注释记录")
    public ResponseEntity<Map<String, Object>> createAnnotation(@RequestBody Annotation annotation) {
        // 自动设置当前用户ID
        Integer currentUserId = getCurrentUserId();
        annotation.setUserId(currentUserId);
        
        // 验证设置是否成功
        if (!currentUserId.equals(annotation.getUserId())) {
            throw new RuntimeException("用户ID设置失败");
        }
        
        Map<String, Object> response = new HashMap<>();
        try {
            Annotation created = annotationService.createAnnotation(annotation);
            response.put("success", true);
            response.put("message", "注释记录创建成功");
            response.put("data", created);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建注释记录失败");
            response.put("errorDetail", e.getMessage());
            
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
        
        Map<String, Object> response = new HashMap<>();
        try {
            Annotation annotation = annotationService.getAnnotationByIdAndUserId(id, currentUserId);
            if (annotation != null) {
                response.put("success", true);
                response.put("data", annotation);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "注释记录不存在或无权限访问");
                
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取注释记录失败");
            
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
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Annotation> annotations = annotationService.getAnnotationsByUserId(currentUserId);
            response.put("success", true);
            response.put("data", annotations);
            response.put("total", annotations.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取注释记录列表失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 通过position中的module和refid查询对应批注，返回批注内容和批注位置
     */
    @GetMapping("/by-position")
    @Operation(summary = "通过模块和关联ID查询批注")
    public ResponseEntity<Map<String, Object>> getAnnotationsByPosition(
            @RequestParam String module, 
            @RequestParam Integer refId) {
        Integer currentUserId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Annotation> annotations = annotationService.getAnnotationsByUserIdAndModuleAndRefId(
                currentUserId, module, refId);
            response.put("success", true);
            response.put("data", annotations);
            response.put("total", annotations.size());
            response.put("module", module);
            response.put("refId", refId);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询批注失败");
            
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
        // 自动设置当前用户ID
        Integer currentUserId = getCurrentUserId();
        annotation.setUserId(currentUserId);
        annotation.setAnnotationId(id);
        
        Map<String, Object> response = new HashMap<>();
        try {
            Annotation updated = annotationService.updateAnnotation(annotation);
            response.put("success", true);
            response.put("message", "注释记录更新成功");
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新注释记录失败");
            
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
        
        Map<String, Object> response = new HashMap<>();
        try {
            annotationService.deleteAnnotationByIdAndUserId(id, currentUserId);
            response.put("success", true);
            response.put("message", "注释记录删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除注释记录失败");
            
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
        
        Map<String, Object> response = new HashMap<>();
        try {
            annotationService.deleteAllAnnotationsByUserId(currentUserId);
            response.put("success", true);
            response.put("message", "所有注释记录删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除所有注释记录失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 