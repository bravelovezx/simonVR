package com.example.simon.service;

import com.example.simon.entity.Accumulation;

import java.util.List;

public interface AccumulationService {

    /**
     * 创建积累记录
     */
    Accumulation createAccumulation(Accumulation accumulation);

    /**
     * 根据ID和用户ID获取积累记录（验证用户权限）
     */
    Accumulation getAccumulationByIdAndUserId(Integer accumulationId, Integer userId);

    /**
     * 根据用户ID获取积累记录列表
     */
    List<Accumulation> getAccumulationsByUserId(Integer userId);

    /**
     * 根据用户ID和类型获取积累记录列表
     */
    List<Accumulation> getAccumulationsByUserIdAndType(Integer userId, String type);

    /**
     * 更新积累记录
     */
    Accumulation updateAccumulation(Accumulation accumulation);

    /**
     * 根据ID和用户ID删除积累记录（验证用户权限）
     */
    void deleteAccumulationByIdAndUserId(Integer accumulationId, Integer userId);

    /**
     * 根据用户ID删除所有积累记录
     */
    void deleteAllAccumulationsByUserId(Integer userId);
} 