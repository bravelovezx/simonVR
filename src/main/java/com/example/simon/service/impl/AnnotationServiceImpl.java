package com.example.simon.service.impl;

import com.example.simon.entity.Annotation;
import com.example.simon.mapper.AnnotationMapper;
import com.example.simon.service.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnotationServiceImpl implements AnnotationService {

    @Autowired
    private AnnotationMapper annotationMapper;

    @Override
    public Annotation createAnnotation(Annotation annotation) {
        try {
            validateAnnotation(annotation);

            annotation.setCreatedAt(LocalDateTime.now());
            annotation.setUpdatedAt(LocalDateTime.now());

            int result = annotationMapper.insert(annotation);

            if (result > 0) {
                return annotation;
            } else {
                throw new RuntimeException("注释记录创建失败");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Annotation getAnnotationByIdAndUserId(Integer annotationId, Integer userId) {
        try {
            if (annotationId == null) {
                throw new IllegalArgumentException("注释ID不能为空");
            }
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            Annotation result = annotationMapper.selectByIdAndUserId(annotationId, userId);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Annotation> getAnnotationsByUserId(Integer userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            List<Annotation> result = annotationMapper.selectByUserId(userId);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Annotation updateAnnotation(Annotation annotation) {
        try {
            validateAnnotation(annotation);

            if (annotation.getAnnotationId() == null) {
                throw new IllegalArgumentException("注释ID不能为空");
            }

            // 验证记录是否存在且属于当前用户
            int count = annotationMapper.countByIdAndUserId(annotation.getAnnotationId(), annotation.getUserId());
            if (count == 0) {
                throw new IllegalArgumentException("注释记录不存在或无权限访问");
            }

            annotation.setUpdatedAt(LocalDateTime.now());

            int result = annotationMapper.update(annotation);
            if (result > 0) {
                return annotation;
            } else {
                throw new RuntimeException("注释记录更新失败");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void deleteAnnotationByIdAndUserId(Integer annotationId, Integer userId) {
        try {
            if (annotationId == null) {
                throw new IllegalArgumentException("注释ID不能为空");
            }
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            int result = annotationMapper.deleteByIdAndUserId(annotationId, userId);
            if (result <= 0) {
                throw new IllegalArgumentException("注释记录不存在或无权限访问");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void deleteAllAnnotationsByUserId(Integer userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            annotationMapper.deleteAllByUserId(userId);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 验证注释记录的必填字段
     */
    private void validateAnnotation(Annotation annotation) {
        if (annotation.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (annotation.getPosition() == null) {
            throw new IllegalArgumentException("位置信息不能为空");
        }
        if (!annotation.getPosition().isValid()) {
            throw new IllegalArgumentException("位置信息无效");
        }
        if (annotation.getAnnotationContent() == null || annotation.getAnnotationContent().trim().isEmpty()) {
            throw new IllegalArgumentException("注释内容不能为空");
        }
    }
} 