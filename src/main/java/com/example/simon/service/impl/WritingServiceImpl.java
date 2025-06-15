package com.example.simon.service.impl;

import com.example.simon.entity.Writing;
import com.example.simon.entity.WritingVersion;
import com.example.simon.mapper.WritingMapper;
import com.example.simon.mapper.WritingVersionMapper;
import com.example.simon.service.WritingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WritingServiceImpl implements WritingService {

    @Autowired
    private WritingMapper writingMapper;

    @Autowired
    private WritingVersionMapper versionMapper;

    @Override
    @Transactional
    public Writing createWriting(Writing writing) {
        LocalDateTime now = LocalDateTime.now();
        writing.setCreatedAt(now);
        writing.setUpdatedAt(now);
        writingMapper.insert(writing);
        return writing;
    }

    @Override
    public Writing getWritingById(Integer writingId, Integer userId) {
        return writingMapper.selectById(writingId, userId);
    }

    @Override
    public List<Writing> searchWritings(Integer userId, String keyword, int offset, int limit) {
        // 模拟分页和模糊搜索（真实场景应配合 PageHelper 或 LIMIT 语句）
        List<Writing> all = writingMapper.selectByUserId(userId);
        return all.stream()
                .filter(w -> keyword == null || w.getWritingTopic().toLowerCase().contains(keyword.toLowerCase()))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Writing updateWriting(Writing writing) {
        writing.setUpdatedAt(LocalDateTime.now());
        writingMapper.update(writing);
        return writing;
    }

    @Override
    @Transactional
    public void deleteWriting(Integer writingId, Integer userId) {
        versionMapper.deleteByWritingId(writingId, userId);
        writingMapper.delete(writingId, userId);
    }

    @Override
    @Transactional
    public WritingVersion addVersion(WritingVersion version) {
        version.setCreatedAt(LocalDateTime.now());
        versionMapper.insert(version);
        return version;
    }

    @Override
    public List<WritingVersion> getVersionsByWritingId(Integer writingId, Integer userId) {
        return versionMapper.selectByWritingId(writingId, userId);
    }

    @Override
    public WritingVersion getVersionById(Integer versionId, Integer userId) {
        return versionMapper.selectById(versionId, userId);
    }

    @Override
    @Transactional
    public void deleteVersion(Integer versionId, Integer userId) {
        versionMapper.deleteById(versionId, userId);
    }

    @Override
    public String aiPolish(String userDraft) {
        // 模拟AI润色逻辑（真实环境下调用LLM或模型服务）
        return "【AI润色】" + userDraft.replaceAll("\\bi\\b", "I").replaceAll("  ", " ").trim();
    }
}
