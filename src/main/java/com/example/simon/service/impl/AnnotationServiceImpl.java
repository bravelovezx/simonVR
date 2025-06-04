package com.example.simon.service.impl;

import com.example.simon.entity.Annotation;
import com.example.simon.mapper.AnnotationMapper;
import com.example.simon.service.AnnotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnotationServiceImpl implements AnnotationService {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationServiceImpl.class);

    @Autowired
    private AnnotationMapper annotationMapper;

    @Override
    public Annotation createAnnotation(Annotation annotation) {
        logger.info("=== Service层: 开始创建注释记录 ===");
        logger.info("接收到的注释记录对象: {}", annotation);
        logger.info("接收到的用户ID: {}", annotation != null ? annotation.getUserId() : "null");

        try {
            validateAnnotation(annotation);

            annotation.setCreatedAt(LocalDateTime.now());
            annotation.setUpdatedAt(LocalDateTime.now());

            logger.info("调用Mapper层创建注释记录: {}", annotation);
            int result = annotationMapper.insert(annotation);
            logger.info("Mapper层返回结果: {}", result);

            if (result > 0) {
                logger.info("注释记录创建成功，生成的ID: {}", annotation.getAnnotationId());
                return annotation;
            } else {
                logger.error("注释记录创建失败，Mapper返回0");
                throw new RuntimeException("注释记录创建失败");
            }
        } catch (Exception e) {
            logger.error("创建注释记录时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Annotation getAnnotationByIdAndUserId(Integer annotationId, Integer userId) {
        logger.info("查询注释记录，ID: {}, 用户ID: {}", annotationId, userId);
        try {
            if (annotationId == null) {
                logger.error("查询注释记录失败: 注释ID不能为空");
                throw new IllegalArgumentException("注释ID不能为空");
            }
            if (userId == null) {
                logger.error("查询注释记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            Annotation result = annotationMapper.selectByIdAndUserId(annotationId, userId);
            if (result != null) {
                logger.info("查询注释记录成功: {}", result);
            } else {
                logger.warn("注释记录不存在或无权限访问，ID: {}, 用户ID: {}", annotationId, userId);
            }
            return result;
        } catch (Exception e) {
            logger.error("查询注释记录失败，ID: {}, 用户ID: {}, 错误: {}", annotationId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Annotation> getAnnotationsByUserId(Integer userId) {
        logger.info("查询用户注释记录列表，用户ID: {}", userId);
        try {
            if (userId == null) {
                logger.error("查询用户注释记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            List<Annotation> result = annotationMapper.selectByUserId(userId);
            logger.info("查询用户注释记录成功，用户ID: {}, 记录数: {}", userId, result.size());
            return result;
        } catch (Exception e) {
            logger.error("查询用户注释记录失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Annotation updateAnnotation(Annotation annotation) {
        logger.info("开始更新注释记录: {}", annotation);
        try {
            validateAnnotation(annotation);

            if (annotation.getAnnotationId() == null) {
                logger.error("更新注释记录失败: 注释ID不能为空");
                throw new IllegalArgumentException("注释ID不能为空");
            }

            // 验证记录是否存在且属于当前用户
            int count = annotationMapper.countByIdAndUserId(annotation.getAnnotationId(), annotation.getUserId());
            if (count == 0) {
                logger.error("更新注释记录失败: 注释记录不存在或无权限访问，ID: {}, 用户ID: {}", 
                    annotation.getAnnotationId(), annotation.getUserId());
                throw new IllegalArgumentException("注释记录不存在或无权限访问");
            }

            annotation.setUpdatedAt(LocalDateTime.now());

            int result = annotationMapper.update(annotation);
            if (result > 0) {
                logger.info("注释记录更新成功，ID: {}", annotation.getAnnotationId());
                return annotation;
            } else {
                logger.error("注释记录更新失败，无记录被更新，ID: {}", annotation.getAnnotationId());
                throw new RuntimeException("注释记录更新失败");
            }
        } catch (Exception e) {
            logger.error("更新注释记录失败，错误: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAnnotationByIdAndUserId(Integer annotationId, Integer userId) {
        logger.info("开始删除注释记录，ID: {}, 用户ID: {}", annotationId, userId);
        try {
            if (annotationId == null) {
                logger.error("删除注释记录失败: 注释ID不能为空");
                throw new IllegalArgumentException("注释ID不能为空");
            }
            if (userId == null) {
                logger.error("删除注释记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            int result = annotationMapper.deleteByIdAndUserId(annotationId, userId);
            if (result > 0) {
                logger.info("注释记录删除成功，ID: {}, 用户ID: {}", annotationId, userId);
            } else {
                logger.error("删除注释记录失败: 记录不存在或无权限访问，ID: {}, 用户ID: {}", annotationId, userId);
                throw new IllegalArgumentException("注释记录不存在或无权限访问");
            }
        } catch (Exception e) {
            logger.error("删除注释记录失败，ID: {}, 用户ID: {}, 错误: {}", annotationId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAllAnnotationsByUserId(Integer userId) {
        logger.info("开始删除用户所有注释记录，用户ID: {}", userId);
        try {
            if (userId == null) {
                logger.error("删除用户注释记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }

            int deletedRows = annotationMapper.deleteAllByUserId(userId);
            logger.info("用户注释记录删除成功，用户ID: {}, 删除记录数: {}", userId, deletedRows);
        } catch (Exception e) {
            logger.error("删除用户注释记录失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 验证注释记录的必填字段
     */
    private void validateAnnotation(Annotation annotation) {
        logger.debug("开始验证注释记录字段: {}", annotation);

        if (annotation.getUserId() == null) {
            logger.error("字段验证失败: 用户ID不能为空");
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (annotation.getPosition() == null) {
            logger.error("字段验证失败: 位置信息不能为空");
            throw new IllegalArgumentException("位置信息不能为空");
        }
        if (!annotation.getPosition().isValid()) {
            logger.error("字段验证失败: 位置信息无效，Position: {}", annotation.getPosition());
            throw new IllegalArgumentException("位置信息无效");
        }
        if (annotation.getAnnotationContent() == null || annotation.getAnnotationContent().trim().isEmpty()) {
            logger.error("字段验证失败: 注释内容不能为空");
            throw new IllegalArgumentException("注释内容不能为空");
        }

        logger.debug("注释记录字段验证通过");
    }
} 