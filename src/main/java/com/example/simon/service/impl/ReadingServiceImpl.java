package com.example.simon.service.impl;

import com.example.simon.entity.Reading;
import com.example.simon.mapper.ReadingMapper;
import com.example.simon.service.ReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReadingServiceImpl implements ReadingService {

    @Autowired
    private ReadingMapper readingMapper;

    @Override
    public Reading createReading(Reading reading) {
        try {
            validateReading(reading);

            reading.setCreatedAt(LocalDateTime.now());
            reading.setUpdatedAt(LocalDateTime.now());

            readingMapper.insert(reading);
            
            return reading;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Reading getReadingByIdAndUserId(Integer readingId, Integer userId) {
        try {
            if (readingId == null) {
                throw new IllegalArgumentException("阅读记录ID不能为空");
            }
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            Reading result = readingMapper.selectByIdAndUserId(readingId, userId);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Reading> getReadingsByUserId(Integer userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            List<Reading> result = readingMapper.selectByUserId(userId);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Reading> getReadingsByUserIdAndSourceType(Integer userId, String sourceType) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            if (sourceType == null || sourceType.trim().isEmpty()) {
                throw new IllegalArgumentException("来源类型不能为空");
            }

            // 验证来源类型
            if (!"recommended".equals(sourceType) && !"user_input".equals(sourceType)) {
                throw new IllegalArgumentException("来源类型只能是recommended或user_input");
            }

            List<Reading> result = readingMapper.selectByUserIdAndSourceType(userId, sourceType);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Reading> searchReadingsByTitleKeyword(Integer userId, String keyword) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new IllegalArgumentException("搜索关键词不能为空");
            }

            List<Reading> result = readingMapper.selectByUserIdAndTitleKeyword(userId, keyword.trim());
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Reading updateReading(Reading reading) {
        try {
            validateReading(reading);

            if (reading.getReadingId() == null) {
                throw new IllegalArgumentException("阅读记录ID不能为空");
            }

            // 验证记录是否存在且属于当前用户
            Reading existingReading = readingMapper.selectByIdAndUserId(reading.getReadingId(), reading.getUserId());
            if (existingReading == null) {
                throw new IllegalArgumentException("阅读记录不存在或无权限访问");
            }

            reading.setUpdatedAt(LocalDateTime.now());

            readingMapper.update(reading);
            return reading;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void deleteReadingByIdAndUserId(Integer readingId, Integer userId) {
        try {
            if (readingId == null) {
                throw new IllegalArgumentException("阅读记录ID不能为空");
            }
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            int result = readingMapper.deleteByIdAndUserId(readingId, userId);
            if (result <= 0) {
                throw new IllegalArgumentException("阅读记录不存在或无权限访问");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void deleteAllReadingsByUserId(Integer userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            readingMapper.deleteAllByUserId(userId);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Map<String, Object> getReadingStatsByUserId(Integer userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            Map<String, Object> stats = new HashMap<>();
            
            // 总数统计
            int totalCount = readingMapper.countByUserId(userId);
            stats.put("totalCount", totalCount);
            
            // 按来源类型统计
            int recommendedCount = readingMapper.countByUserIdAndSourceType(userId, "recommended");
            int userInputCount = readingMapper.countByUserIdAndSourceType(userId, "user_input");
            
            Map<String, Integer> sourceTypeStats = new HashMap<>();
            sourceTypeStats.put("recommended", recommendedCount);
            sourceTypeStats.put("user_input", userInputCount);
            stats.put("sourceTypeStats", sourceTypeStats);

            return stats;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 验证阅读记录的必填字段
     */
    private void validateReading(Reading reading) {
        if (reading.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (reading.getSourceType() == null || reading.getSourceType().trim().isEmpty()) {
            throw new IllegalArgumentException("来源类型不能为空");
        }

        // 验证来源类型值
        String sourceType = reading.getSourceType().trim();
        if (!"recommended".equals(sourceType) && !"user_input".equals(sourceType)) {
            throw new IllegalArgumentException("来源类型只能是recommended或user_input");
        }

        if (reading.getArticleTitle() == null || reading.getArticleTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }
        if (reading.getArticleContent() == null || reading.getArticleContent().trim().isEmpty()) {
            throw new IllegalArgumentException("文章内容不能为空");
        }
    }
} 