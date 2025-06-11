package com.example.simon.service.impl;

import com.example.simon.entity.Reading;
import com.example.simon.mapper.ReadingMapper;
import com.example.simon.service.ReadingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReadingServiceImpl implements ReadingService {

    private static final Logger logger = LoggerFactory.getLogger(ReadingServiceImpl.class);

    @Autowired
    private ReadingMapper readingMapper;

    @Override
    public Reading createReading(Reading reading) {
        logger.info("=== Service层: 开始创建阅读记录 ===");
        logger.info("接收到的阅读记录对象: {}", reading);
        logger.info("接收到的用户ID: {}", reading != null ? reading.getUserId() : "null");

        try {
            validateReading(reading);

            reading.setCreatedAt(LocalDateTime.now());
            reading.setUpdatedAt(LocalDateTime.now());

            logger.info("调用Mapper层创建阅读记录: {}", reading);
            readingMapper.insert(reading);
            logger.info("阅读记录创建成功，生成的ID: {}", reading.getReadingId());
            
            return reading;
        } catch (Exception e) {
            logger.error("创建阅读记录时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Reading getReadingByIdAndUserId(Integer readingId, Integer userId) {
        logger.info("查询阅读记录，ID: {}, 用户ID: {}", readingId, userId);
        try {
            if (readingId == null) {
                logger.error("查询阅读记录失败: 阅读记录ID不能为空");
                throw new IllegalArgumentException("阅读记录ID不能为空");
            }
            if (userId == null) {
                logger.error("查询阅读记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            Reading result = readingMapper.selectByIdAndUserId(readingId, userId);
            if (result != null) {
                logger.info("查询阅读记录成功: {}", result);
            } else {
                logger.warn("阅读记录不存在或无权限访问，ID: {}, 用户ID: {}", readingId, userId);
            }
            return result;
        } catch (Exception e) {
            logger.error("查询阅读记录失败，ID: {}, 用户ID: {}, 错误: {}", readingId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Reading> getReadingsByUserId(Integer userId) {
        logger.info("查询用户阅读记录列表，用户ID: {}", userId);
        try {
            if (userId == null) {
                logger.error("查询用户阅读记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            List<Reading> result = readingMapper.selectByUserId(userId);
            logger.info("查询用户阅读记录成功，用户ID: {}, 记录数: {}", userId, result.size());
            return result;
        } catch (Exception e) {
            logger.error("查询用户阅读记录失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Reading> getReadingsByUserIdAndSourceType(Integer userId, String sourceType) {
        logger.info("查询用户阅读记录列表，用户ID: {}, 来源类型: {}", userId, sourceType);
        try {
            if (userId == null) {
                logger.error("查询用户阅读记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }
            if (sourceType == null || sourceType.trim().isEmpty()) {
                logger.error("查询用户阅读记录失败: 来源类型不能为空");
                throw new IllegalArgumentException("来源类型不能为空");
            }

            // 验证来源类型
            if (!"recommended".equals(sourceType) && !"user_input".equals(sourceType)) {
                logger.error("查询用户阅读记录失败: 来源类型只能是recommended或user_input，当前类型: {}", sourceType);
                throw new IllegalArgumentException("来源类型只能是recommended或user_input");
            }

            List<Reading> result = readingMapper.selectByUserIdAndSourceType(userId, sourceType);
            logger.info("查询用户阅读记录成功，用户ID: {}, 来源类型: {}, 记录数: {}", userId, sourceType, result.size());
            return result;
        } catch (Exception e) {
            logger.error("查询用户阅读记录失败，用户ID: {}, 来源类型: {}, 错误: {}", userId, sourceType, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Reading> searchReadingsByTitleKeyword(Integer userId, String keyword) {
        logger.info("搜索用户阅读记录，用户ID: {}, 关键词: {}", userId, keyword);
        try {
            if (userId == null) {
                logger.error("搜索用户阅读记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }
            if (keyword == null || keyword.trim().isEmpty()) {
                logger.error("搜索用户阅读记录失败: 搜索关键词不能为空");
                throw new IllegalArgumentException("搜索关键词不能为空");
            }

            List<Reading> result = readingMapper.selectByUserIdAndTitleKeyword(userId, keyword.trim());
            logger.info("搜索用户阅读记录成功，用户ID: {}, 关键词: {}, 记录数: {}", userId, keyword, result.size());
            return result;
        } catch (Exception e) {
            logger.error("搜索用户阅读记录失败，用户ID: {}, 关键词: {}, 错误: {}", userId, keyword, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Reading updateReading(Reading reading) {
        logger.info("开始更新阅读记录: {}", reading);
        try {
            validateReading(reading);

            if (reading.getReadingId() == null) {
                logger.error("更新阅读记录失败: 阅读记录ID不能为空");
                throw new IllegalArgumentException("阅读记录ID不能为空");
            }

            // 验证记录是否存在且属于当前用户
            Reading existingReading = readingMapper.selectByIdAndUserId(reading.getReadingId(), reading.getUserId());
            if (existingReading == null) {
                logger.error("更新阅读记录失败: 阅读记录不存在或无权限访问，ID: {}, 用户ID: {}", 
                    reading.getReadingId(), reading.getUserId());
                throw new IllegalArgumentException("阅读记录不存在或无权限访问");
            }

            reading.setUpdatedAt(LocalDateTime.now());

            readingMapper.update(reading);
            logger.info("阅读记录更新成功，ID: {}", reading.getReadingId());
            return reading;
        } catch (Exception e) {
            logger.error("更新阅读记录失败，错误: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteReadingByIdAndUserId(Integer readingId, Integer userId) {
        logger.info("开始删除阅读记录，ID: {}, 用户ID: {}", readingId, userId);
        try {
            if (readingId == null) {
                logger.error("删除阅读记录失败: 阅读记录ID不能为空");
                throw new IllegalArgumentException("阅读记录ID不能为空");
            }
            if (userId == null) {
                logger.error("删除阅读记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            int result = readingMapper.deleteByIdAndUserId(readingId, userId);
            if (result > 0) {
                logger.info("阅读记录删除成功，ID: {}, 用户ID: {}", readingId, userId);
            } else {
                logger.error("删除阅读记录失败: 记录不存在或无权限访问，ID: {}, 用户ID: {}", readingId, userId);
                throw new IllegalArgumentException("阅读记录不存在或无权限访问");
            }
        } catch (Exception e) {
            logger.error("删除阅读记录失败，ID: {}, 用户ID: {}, 错误: {}", readingId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAllReadingsByUserId(Integer userId) {
        logger.info("开始删除用户所有阅读记录，用户ID: {}", userId);
        try {
            if (userId == null) {
                logger.error("删除用户阅读记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            int deletedRows = readingMapper.deleteAllByUserId(userId);
            logger.info("用户阅读记录删除成功，用户ID: {}, 删除记录数: {}", userId, deletedRows);
        } catch (Exception e) {
            logger.error("删除用户阅读记录失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map<String, Object> getReadingStatsByUserId(Integer userId) {
        logger.info("获取用户阅读记录统计，用户ID: {}", userId);
        try {
            if (userId == null) {
                logger.error("获取用户阅读记录统计失败: 用户ID不能为空");
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

            logger.info("用户阅读记录统计获取成功，用户ID: {}, 统计结果: {}", userId, stats);
            return stats;
        } catch (Exception e) {
            logger.error("获取用户阅读记录统计失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 验证阅读记录的必填字段
     */
    private void validateReading(Reading reading) {
        logger.debug("开始验证阅读记录字段: {}", reading);

        if (reading.getUserId() == null) {
            logger.error("字段验证失败: 用户ID不能为空");
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (reading.getSourceType() == null || reading.getSourceType().trim().isEmpty()) {
            logger.error("字段验证失败: 来源类型不能为空");
            throw new IllegalArgumentException("来源类型不能为空");
        }

        // 验证来源类型值
        String sourceType = reading.getSourceType().trim();
        if (!"recommended".equals(sourceType) && !"user_input".equals(sourceType)) {
            logger.error("字段验证失败: 来源类型只能是recommended或user_input，当前类型: {}", sourceType);
            throw new IllegalArgumentException("来源类型只能是recommended或user_input");
        }

        if (reading.getArticleTitle() == null || reading.getArticleTitle().trim().isEmpty()) {
            logger.error("字段验证失败: 文章标题不能为空");
            throw new IllegalArgumentException("文章标题不能为空");
        }
        if (reading.getArticleContent() == null || reading.getArticleContent().trim().isEmpty()) {
            logger.error("字段验证失败: 文章内容不能为空");
            throw new IllegalArgumentException("文章内容不能为空");
        }

        logger.debug("阅读记录字段验证通过");
    }
} 