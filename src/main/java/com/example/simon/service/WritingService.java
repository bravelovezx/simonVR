package com.example.simon.service;

import com.example.simon.entity.Writing;
import com.example.simon.entity.WritingVersion;

import java.util.List;

public interface WritingService {

    // 创建作文主记录
    Writing createWriting(Writing writing);

    // 获取单篇作文（需验证用户）
    Writing getWritingById(Integer writingId, Integer userId);

    // 分页 + 模糊搜索用户作文（关键词搜索题目）
    List<Writing> searchWritings(Integer userId, String keyword, int offset, int limit);

    // 更新作文主记录
    Writing updateWriting(Writing writing);

    // 删除作文及其所有版本
    void deleteWriting(Integer writingId, Integer userId);

    // 添加作文版本
    WritingVersion addVersion(WritingVersion version);

    // 获取某作文的所有版本
    List<WritingVersion> getVersionsByWritingId(Integer writingId, Integer userId);

    // 获取单个版本（含用户验证）
    WritingVersion getVersionById(Integer versionId, Integer userId);
    
    // 更新作文版本内容
    WritingVersion updateVersion(WritingVersion version, Integer userId);

    // 删除单个版本
    void deleteVersion(Integer versionId, Integer userId);

    // AI润色用户原文（模拟）
    String aiPolish(String userDraft);
}
