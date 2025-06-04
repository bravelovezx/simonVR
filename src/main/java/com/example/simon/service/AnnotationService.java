package com.example.simon.service;

import com.example.simon.entity.Annotation;

import java.util.List;

public interface AnnotationService {

    /**
     * 创建注释记录
     */
    Annotation createAnnotation(Annotation annotation);

    /**
     * 根据ID和用户ID获取注释记录（验证用户权限）
     */
    Annotation getAnnotationByIdAndUserId(Integer annotationId, Integer userId);

    /**
     * 根据用户ID获取注释记录列表
     */
    List<Annotation> getAnnotationsByUserId(Integer userId);

    /**
     * 更新注释记录
     */
    Annotation updateAnnotation(Annotation annotation);

    /**
     * 根据ID和用户ID删除注释记录（验证用户权限）
     */
    void deleteAnnotationByIdAndUserId(Integer annotationId, Integer userId);

    /**
     * 根据用户ID删除所有注释记录
     */
    void deleteAllAnnotationsByUserId(Integer userId);
} 