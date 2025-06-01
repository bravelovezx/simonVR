package com.example.simon.controller;

import com.example.simon.entity.Accumulation;
import com.example.simon.service.AccumulationService;
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
@RequestMapping("/api/accumulations")
@Tag(name = "积累管理", description = "积累相关的API")
@PreAuthorize("isAuthenticated()")
public class AccumulationController {

    private static final Logger logger = LoggerFactory.getLogger(AccumulationController.class);

    @Autowired
    private AccumulationService accumulationService;

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
     * 创建积累记录
     */
    @PostMapping
    @Operation(summary = "创建积累记录")
    public ResponseEntity<Map<String, Object>> createAccumulation(@RequestBody Accumulation accumulation) {
        logger.info("=== API调用: 创建积累记录 ===");
        
        // 记录请求体中原始的用户ID
        Integer originalUserId = accumulation.getUserId();
        logger.info("请求体中的原始用户ID: {}", originalUserId);
        
        // 自动设置当前用户ID
        Integer currentUserId = getCurrentUserId();
        accumulation.setUserId(currentUserId);
        
        logger.info("请求参数: {}", accumulation);
        logger.info("自动设置用户ID: {}", currentUserId);
        logger.info("设置后accumulation中的用户ID: {}", accumulation.getUserId());
        
        // 验证设置是否成功
        if (!currentUserId.equals(accumulation.getUserId())) {
            logger.error("用户ID设置失败! 期望: {}, 实际: {}", currentUserId, accumulation.getUserId());
            throw new RuntimeException("用户ID设置失败");
        }
        
        Map<String, Object> response = new HashMap<>();
        try {
            Accumulation created = accumulationService.createAccumulation(accumulation);
            response.put("success", true);
            response.put("message", "积累记录创建成功");
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
            response.put("message", "创建积累记录失败");
            response.put("errorDetail", e.getMessage()); // 添加详细错误信息用于调试
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取积累记录（仅当前用户可访问）
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取积累记录")
    public ResponseEntity<Map<String, Object>> getAccumulationById(@PathVariable Integer id) {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 根据ID获取积累记录 ===");
        logger.info("请求参数: id={}, 当前用户ID: {}", id, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            Accumulation accumulation = accumulationService.getAccumulationByIdAndUserId(id, currentUserId);
            if (accumulation != null) {
                response.put("success", true);
                response.put("data", accumulation);
                
                logger.info("API响应成功，找到记录: {}", accumulation);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "积累记录不存在或无权限访问");
                
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
            response.put("message", "获取积累记录失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当前用户的积累记录列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取当前用户的积累记录列表")
    public ResponseEntity<Map<String, Object>> getMyAccumulations() {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 获取当前用户的积累记录列表 ===");
        logger.info("当前用户ID: {}", currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Accumulation> accumulations = accumulationService.getAccumulationsByUserId(currentUserId);
            response.put("success", true);
            response.put("data", accumulations);
            response.put("total", accumulations.size());
            
            logger.info("API响应成功，返回记录数: {}", accumulations.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取积累记录列表失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据类型获取当前用户的积累记录列表
     */
    @GetMapping("/my/type/{type}")
    @Operation(summary = "根据类型获取当前用户的积累记录列表")
    public ResponseEntity<Map<String, Object>> getMyAccumulationsByType(@PathVariable String type) {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 根据类型获取当前用户的积累记录列表 ===");
        logger.info("当前用户ID: {}, 类型: {}", currentUserId, type);
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Accumulation> accumulations = accumulationService.getAccumulationsByUserIdAndType(currentUserId, type);
            response.put("success", true);
            response.put("data", accumulations);
            response.put("total", accumulations.size());
            
            logger.info("API响应成功，返回记录数: {}", accumulations.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            logger.warn("API请求参数错误: {}", e.getMessage());
            logger.info("API响应错误: {}", response);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取积累记录列表失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新积累记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新积累记录")
    public ResponseEntity<Map<String, Object>> updateAccumulation(
            @PathVariable Integer id, @RequestBody Accumulation accumulation) {
        logger.info("=== API调用: 更新积累记录 ===");
        
        // 自动设置当前用户ID
        Integer currentUserId = getCurrentUserId();
        accumulation.setUserId(currentUserId);
        accumulation.setAccumulationId(id);
        
        logger.info("请求参数: id={}, accumulation={}", id, accumulation);
        logger.info("自动设置用户ID: {}", currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            Accumulation updated = accumulationService.updateAccumulation(accumulation);
            response.put("success", true);
            response.put("message", "积累记录更新成功");
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
            response.put("message", "更新积累记录失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除积累记录（仅当前用户可删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除积累记录")
    public ResponseEntity<Map<String, Object>> deleteAccumulation(@PathVariable Integer id) {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 删除积累记录 ===");
        logger.info("请求参数: id={}, 当前用户ID: {}", id, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            accumulationService.deleteAccumulationByIdAndUserId(id, currentUserId);
            response.put("success", true);
            response.put("message", "积累记录删除成功");
            
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
            response.put("message", "删除积累记录失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除当前用户所有积累记录
     */
    @DeleteMapping("/my/all")
    @Operation(summary = "删除当前用户所有积累记录")
    public ResponseEntity<Map<String, Object>> deleteAllMyAccumulations() {
        Integer currentUserId = getCurrentUserId();
        logger.info("=== API调用: 删除当前用户所有积累记录 ===");
        logger.info("当前用户ID: {}", currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            accumulationService.deleteAllAccumulationsByUserId(currentUserId);
            response.put("success", true);
            response.put("message", "所有积累记录删除成功");
            
            logger.info("API响应成功: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除所有积累记录失败");
            
            logger.error("API内部错误: {}", e.getMessage(), e);
            logger.info("API响应错误: {}", response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 