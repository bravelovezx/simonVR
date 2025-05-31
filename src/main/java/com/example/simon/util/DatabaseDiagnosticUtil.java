package com.example.simon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库诊断工具类
 * 用于检测和诊断数据库相关问题
 */
public class DatabaseDiagnosticUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDiagnosticUtil.class);
    
    /**
     * 检查积累记录字段长度
     */
    public static void validateAccumulationFieldLengths(String type, String content, String meaning, String positionJson) {
        logger.info("=== 数据库字段长度诊断 ===");
        
        // 检查type字段
        if (type != null) {
            logger.info("type字段 - 值: '{}', 长度: {}, 推荐最大长度: 20", type, type.length());
            if (type.length() > 20) {
                logger.warn("⚠️ type字段长度超出推荐值！当前: {}, 推荐最大: 20", type.length());
            }
            if (!"word".equals(type) && !"sentence".equals(type)) {
                logger.warn("⚠️ type字段值不在允许范围内！当前: '{}', 允许值: 'word', 'sentence'", type);
            }
        } else {
            logger.warn("⚠️ type字段为null");
        }
        
        // 检查content字段
        if (content != null) {
            logger.info("content字段 - 长度: {}, 类型: TEXT (65535字符)", content.length());
            if (content.length() > 65535) {
                logger.warn("⚠️ content字段长度超出TEXT类型限制！当前: {}, 最大: 65535", content.length());
            }
        } else {
            logger.warn("⚠️ content字段为null");
        }
        
        // 检查meaning字段
        if (meaning != null) {
            logger.info("meaning字段 - 长度: {}, 类型: TEXT (65535字符)", meaning.length());
            if (meaning.length() > 65535) {
                logger.warn("⚠️ meaning字段长度超出TEXT类型限制！当前: {}, 最大: 65535", meaning.length());
            }
        } else {
            logger.warn("⚠️ meaning字段为null");
        }
        
        // 检查positionJson字段
        if (positionJson != null) {
            logger.info("positionJson字段 - 长度: {}, 类型: JSON", positionJson.length());
            // 简单的JSON格式检查
            if (!positionJson.trim().startsWith("{") || !positionJson.trim().endsWith("}")) {
                logger.warn("⚠️ positionJson字段可能不是有效的JSON格式！");
            }
        } else {
            logger.warn("⚠️ positionJson字段为null");
        }
        
        logger.info("=== 字段长度诊断完成 ===");
    }
    
    /**
     * 分析数据库错误信息
     */
    public static String analyzeDatabaseError(Exception e) {
        if (e == null || e.getMessage() == null) {
            return "未知数据库错误";
        }
        
        String message = e.getMessage().toLowerCase();
        String originalMessage = e.getMessage();
        
        logger.error("=== 数据库错误分析 ===");
        logger.error("原始错误信息: {}", originalMessage);
        
        // 分析常见的数据库错误
        if (message.contains("data truncated")) {
            if (message.contains("'type'")) {
                logger.error("错误类型: 字段数据截断 - type字段");
                logger.error("可能原因: type字段值长度超出数据库定义长度，或值不在ENUM范围内");
                logger.error("建议解决方案: 1. 检查type值是否为'word'或'sentence' 2. 修改数据库type字段长度为VARCHAR(20)");
                return "type字段数据被截断，请检查值是否为'word'或'sentence'，并确保数据库字段长度足够";
            } else {
                logger.error("错误类型: 字段数据截断");
                return "数据长度超出字段限制，请检查输入数据长度";
            }
        } else if (message.contains("duplicate entry")) {
            logger.error("错误类型: 数据重复");
            return "数据重复，该记录可能已存在";
        } else if (message.contains("foreign key constraint")) {
            logger.error("错误类型: 外键约束违反");
            return "关联数据不存在，请检查用户ID是否有效";
        } else if (message.contains("column") && message.contains("cannot be null")) {
            logger.error("错误类型: 非空字段为空");
            return "必填字段不能为空，请检查所有必填项";
        } else if (message.contains("table") && message.contains("doesn't exist")) {
            logger.error("错误类型: 表不存在");
            return "数据库表不存在，请检查数据库配置";
        } else if (message.contains("unknown column")) {
            logger.error("错误类型: 字段不存在");
            return "数据库字段不存在，请检查表结构";
        }
        
        logger.error("错误类型: 其他数据库错误");
        return "数据库操作失败: " + originalMessage;
    }
    
    /**
     * 生成建议的数据库修复SQL
     */
    public static String generateFixSql() {
        return """
            -- 积累表字段修复建议
            -- 1. 修改type字段长度
            ALTER TABLE accumulations MODIFY COLUMN type VARCHAR(20) NOT NULL COMMENT '积累类型：word(单词) 或 sentence(句子)';
            
            -- 2. 或者使用ENUM类型（更严格）
            -- ALTER TABLE accumulations MODIFY COLUMN type ENUM('word', 'sentence') NOT NULL COMMENT '积累类型：word(单词) 或 sentence(句子)';
            
            -- 3. 查看当前表结构
            DESCRIBE accumulations;
            """;
    }
} 