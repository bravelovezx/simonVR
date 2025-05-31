package com.example.simon.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 位置信息实体类
 * 用于记录积累记录在原文中的位置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    
    /**
     * 模块类型：writing(写作)、reading(阅读)、dialogue(对话)
     */
    private String module;
    
    /**
     * 关联ID：
     * - 当module为writing时，对应writing_version_id
     * - 当module为reading时，对应reading_id  
     * - 当module为dialogue时，对应turn_id
     */
    private Integer refId;
    
    /**
     * 行号（从1开始）
     */
    private Integer row;
    
    /**
     * 列号（从1开始）
     */
    private Integer column;
    
    /**
     * 验证模块类型是否有效
     */
    public boolean isValidModule() {
        return "writing".equals(module) || "reading".equals(module) || "dialogue".equals(module);
    }
    
    /**
     * 验证位置信息是否完整
     */
    public boolean isValid() {
        return module != null && !module.trim().isEmpty() && 
               refId != null && refId > 0 &&
               row != null && row > 0 &&
               column != null && column >= 0 &&
               isValidModule();
    }
    
    @Override
    public String toString() {
        return String.format("Position{module='%s', refId=%d, row=%d, column=%d}", 
                           module, refId, row, column);
    }
} 