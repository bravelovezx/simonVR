package com.example.simon.controller;

import com.example.simon.entity.Reading;
import com.example.simon.service.ReadingService;
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
@RequestMapping("/api/readings")
@Tag(name = "阅读管理", description = "阅读记录相关的API")
@PreAuthorize("isAuthenticated()")
public class ReadingController {

    @Autowired
    private ReadingService readingService;

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
     * 创建阅读记录
     */
    @PostMapping
    @Operation(summary = "创建阅读记录")
    public ResponseEntity<Map<String, Object>> createReading(@RequestBody Reading reading) {
        // 自动设置当前用户ID
        Integer currentUserId = getCurrentUserId();
        reading.setUserId(currentUserId);
        
        // 验证设置是否成功
        if (!currentUserId.equals(reading.getUserId())) {
            throw new RuntimeException("用户ID设置失败");
        }
        
        Map<String, Object> response = new HashMap<>();
        try {
            Reading created = readingService.createReading(reading);
            response.put("success", true);
            response.put("message", "阅读记录创建成功");
            response.put("data", created);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建阅读记录失败");
            response.put("errorDetail", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取阅读记录（仅当前用户可访问）
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取阅读记录")
    public ResponseEntity<Map<String, Object>> getReadingById(@PathVariable Integer id) {
        Integer currentUserId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> readingWithAnnotations = readingService.getReadingWithAnnotationsByIdAndUserId(id, currentUserId);
            if (readingWithAnnotations != null && readingWithAnnotations.get("reading") != null) {
                response.put("success", true);
                response.put("data", readingWithAnnotations);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "阅读记录不存在或无权限访问");
                
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取阅读记录失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当前用户的阅读记录列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取当前用户的阅读记录列表")
    public ResponseEntity<Map<String, Object>> getMyReadings() {
        Integer currentUserId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Reading> readings = readingService.getReadingsByUserId(currentUserId);
            response.put("success", true);
            response.put("data", readings);
            response.put("total", readings.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取阅读记录列表失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据来源类型获取当前用户的阅读记录列表
     */
    @GetMapping("/my/source/{sourceType}")
    @Operation(summary = "根据来源类型获取当前用户的阅读记录列表")
    public ResponseEntity<Map<String, Object>> getMyReadingsBySourceType(@PathVariable String sourceType) {
        Integer currentUserId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Reading> readings = readingService.getReadingsByUserIdAndSourceType(currentUserId, sourceType);
            response.put("success", true);
            response.put("data", readings);
            response.put("total", readings.size());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取阅读记录列表失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 搜索当前用户的阅读记录
     */
    @GetMapping("/my/search")
    @Operation(summary = "根据标题关键词搜索当前用户的阅读记录")
    public ResponseEntity<Map<String, Object>> searchMyReadings(@RequestParam String keyword) {
        Integer currentUserId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Reading> readings = readingService.searchReadingsByTitleKeyword(currentUserId, keyword);
            response.put("success", true);
            response.put("data", readings);
            response.put("total", readings.size());
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "搜索阅读记录失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当前用户的阅读记录统计
     */
    @GetMapping("/my/stats")
    @Operation(summary = "获取当前用户的阅读记录统计")
    public ResponseEntity<Map<String, Object>> getMyReadingStats() {
        Integer currentUserId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = readingService.getReadingStatsByUserId(currentUserId);
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取阅读记录统计失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新阅读记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新阅读记录")
    public ResponseEntity<Map<String, Object>> updateReading(
            @PathVariable Integer id, @RequestBody Reading reading) {
        // 自动设置当前用户ID
        Integer currentUserId = getCurrentUserId();
        reading.setUserId(currentUserId);
        reading.setReadingId(id);
        
        Map<String, Object> response = new HashMap<>();
        try {
            Reading updated = readingService.updateReading(reading);
            response.put("success", true);
            response.put("message", "阅读记录更新成功");
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新阅读记录失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除阅读记录（仅当前用户可删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除阅读记录")
    public ResponseEntity<Map<String, Object>> deleteReading(@PathVariable Integer id) {
        Integer currentUserId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        try {
            readingService.deleteReadingByIdAndUserId(id, currentUserId);
            response.put("success", true);
            response.put("message", "阅读记录删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除阅读记录失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除当前用户所有阅读记录
     */
    @DeleteMapping("/my/all")
    @Operation(summary = "删除当前用户所有阅读记录")
    public ResponseEntity<Map<String, Object>> deleteAllMyReadings() {
        Integer currentUserId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        try {
            readingService.deleteAllReadingsByUserId(currentUserId);
            response.put("success", true);
            response.put("message", "所有阅读记录删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除所有阅读记录失败");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 