package com.example.simon.service;

import com.example.simon.entity.Reading;

import java.util.List;
import java.util.Map;

public interface ReadingService {

    /**
     * 创建阅读记录
     */
    Reading createReading(Reading reading);

    /**
     * 根据ID和用户ID获取阅读记录（验证用户权限）
     */
    Reading getReadingByIdAndUserId(Integer readingId, Integer userId);

    /**
     * 根据ID获取阅读记录以及该记录对应的所有批注（仅当前用户可访问）
     */
    Map<String, Object> getReadingWithAnnotationsByIdAndUserId(Integer readingId, Integer userId);

    /**
     * 根据用户ID获取阅读记录列表
     */
    List<Reading> getReadingsByUserId(Integer userId);

    /**
     * 根据用户ID和来源类型获取阅读记录列表
     */
    List<Reading> getReadingsByUserIdAndSourceType(Integer userId, String sourceType);

    /**
     * 根据用户ID和标题关键词搜索阅读记录列表
     */
    List<Reading> searchReadingsByTitleKeyword(Integer userId, String keyword);

    /**
     * 更新阅读记录
     */
    Reading updateReading(Reading reading);

    /**
     * 根据ID和用户ID删除阅读记录（验证用户权限）
     */
    void deleteReadingByIdAndUserId(Integer readingId, Integer userId);

    /**
     * 根据用户ID删除所有阅读记录
     */
    void deleteAllReadingsByUserId(Integer userId);

    /**
     * 获取用户阅读记录统计信息
     */
    Map<String, Object> getReadingStatsByUserId(Integer userId);
} 