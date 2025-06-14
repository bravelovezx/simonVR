package com.example.simon.service.impl;

import com.example.simon.entity.Accumulation;
import com.example.simon.mapper.AccumulationMapper;
import com.example.simon.service.AccumulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccumulationServiceImpl implements AccumulationService {

    private static final Logger logger = LoggerFactory.getLogger(AccumulationServiceImpl.class);

    @Autowired
    private AccumulationMapper accumulationMapper;

    @Override
    public Accumulation createAccumulation(Accumulation accumulation) {
        logger.info("=== Service层: 开始创建积累记录 ===");
        logger.info("接收到的积累记录对象: {}", accumulation);
        logger.info("接收到的用户ID: {}", accumulation != null ? accumulation.getUserId() : "null");
        
        try {
            if (accumulation == null) {
                logger.error("创建积累记录失败: 积累记录不能为空");
                throw new IllegalArgumentException("积累记录不能为空");
            }
            
            // 记录传入时的用户ID
            Integer originalUserId = accumulation.getUserId();
            logger.info("Service层接收到的原始用户ID: {}", originalUserId);
            
            // 设置创建时间
            LocalDateTime now = LocalDateTime.now();
            accumulation.setCreatedAt(now);
            accumulation.setUpdatedAt(now);  // 创建时也设置更新时间
            logger.info("设置创建时间: {}", now);
            logger.info("设置更新时间: {}", now);
            
            // 验证必填字段（在验证前再次确认用户ID）
            logger.info("验证前的用户ID: {}", accumulation.getUserId());
            validateAccumulation(accumulation);
            logger.info("验证后的用户ID: {}", accumulation.getUserId());
            
            // 插入数据库前最后一次确认用户ID
            logger.info("数据库插入前的用户ID: {}", accumulation.getUserId());
            accumulationMapper.insert(accumulation);
            
            // 插入后检查生成的ID和用户ID
            logger.info("数据库插入后 - 积累记录ID: {}, 用户ID: {}", 
                accumulation.getAccumulationId(), accumulation.getUserId());
            
            // 验证插入后的用户ID是否被修改
            if (originalUserId != null && !originalUserId.equals(accumulation.getUserId())) {
                logger.error("❌ 用户ID在插入过程中被修改！原始: {}, 插入后: {}", 
                    originalUserId, accumulation.getUserId());
            } else {
                logger.info("✅ 用户ID在插入过程中保持一致: {}", accumulation.getUserId());
            }
            
            logger.info("积累记录创建成功，ID: {}", accumulation.getAccumulationId());
            return accumulation;
        } catch (Exception e) {
            logger.error("创建积累记录失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Accumulation getAccumulationByIdAndUserId(Integer accumulationId, Integer userId) {
        logger.info("查询积累记录（带用户验证），ID: {}, 用户ID: {}", accumulationId, userId);
        try {
            if (accumulationId == null) {
                logger.error("查询积累记录失败: 积累记录ID不能为空");
                throw new IllegalArgumentException("积累记录ID不能为空");
            }
            if (userId == null) {
                logger.error("查询积累记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }
            
            Accumulation result = accumulationMapper.selectByIdAndUserId(accumulationId, userId);
            if (result != null) {
                logger.info("查询积累记录成功，ID: {}, 用户ID: {}", accumulationId, userId);
            } else {
                logger.warn("积累记录不存在或无权限访问，ID: {}, 用户ID: {}", accumulationId, userId);
            }
            return result;
        } catch (Exception e) {
            logger.error("查询积累记录失败，ID: {}, 用户ID: {}, 错误: {}", accumulationId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Accumulation> getAccumulationsByUserId(Integer userId) {
        logger.info("查询用户积累记录列表，用户ID: {}", userId);
        try {
            if (userId == null) {
                logger.error("查询用户积累记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }
            
            List<Accumulation> result = accumulationMapper.selectByUserId(userId);
            logger.info("查询用户积累记录成功，用户ID: {}, 记录数: {}", userId, result.size());
            return result;
        } catch (Exception e) {
            logger.error("查询用户积累记录失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Accumulation> getAccumulationsByUserIdAndType(Integer userId, String type) {
        logger.info("查询用户积累记录列表，用户ID: {}, 类型: {}", userId, type);
        try {
            if (userId == null) {
                logger.error("查询用户积累记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }
            if (type == null || type.trim().isEmpty()) {
                logger.error("查询用户积累记录失败: 积累类型不能为空");
                throw new IllegalArgumentException("积累类型不能为空");
            }
            
            // 验证类型值
            if (!"word".equals(type) && !"sentence".equals(type)) {
                logger.error("查询用户积累记录失败: 积累类型只能是word或sentence，当前类型: {}", type);
                throw new IllegalArgumentException("积累类型只能是word或sentence");
            }
            
            List<Accumulation> result = accumulationMapper.selectByUserIdAndType(userId, type);
            logger.info("查询用户积累记录成功，用户ID: {}, 类型: {}, 记录数: {}", userId, type, result.size());
            return result;
        } catch (Exception e) {
            logger.error("查询用户积累记录失败，用户ID: {}, 类型: {}, 错误: {}", userId, type, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Accumulation updateAccumulation(Accumulation accumulation) {
        logger.info("开始更新积累记录: {}", accumulation);
        try {
            if (accumulation == null || accumulation.getAccumulationId() == null) {
                logger.error("更新积累记录失败: 积累记录或ID不能为空");
                throw new IllegalArgumentException("积累记录或ID不能为空");
            }
            
            // 设置更新时间
            LocalDateTime now = LocalDateTime.now();
            accumulation.setUpdatedAt(now);
            logger.info("设置更新时间: {}", now);
            
            // 验证必填字段
            validateAccumulation(accumulation);
            
            accumulationMapper.update(accumulation);
            // 更新后重新查询返回最新数据
            Accumulation updated = accumulationMapper.selectByIdAndUserId(accumulation.getAccumulationId(), accumulation.getUserId());
            logger.info("积累记录更新成功，ID: {}", accumulation.getAccumulationId());
            return updated;
        } catch (Exception e) {
            logger.error("更新积累记录失败，ID: {}, 错误: {}", 
                accumulation != null ? accumulation.getAccumulationId() : "null", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAccumulationByIdAndUserId(Integer accumulationId, Integer userId) {
        logger.info("删除积累记录（带用户验证），ID: {}, 用户ID: {}", accumulationId, userId);
        try {
            if (accumulationId == null) {
                logger.error("删除积累记录失败: 积累记录ID不能为空");
                throw new IllegalArgumentException("积累记录ID不能为空");
            }
            if (userId == null) {
                logger.error("删除积累记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }
            
            int deletedRows = accumulationMapper.deleteByIdAndUserId(accumulationId, userId);
            if (deletedRows > 0) {
                logger.info("删除积累记录成功，ID: {}, 用户ID: {}", accumulationId, userId);
            } else {
                logger.warn("积累记录不存在或无权限删除，ID: {}, 用户ID: {}", accumulationId, userId);
                throw new IllegalArgumentException("积累记录不存在或无权限删除");
            }
        } catch (Exception e) {
            logger.error("删除积累记录失败，ID: {}, 用户ID: {}, 错误: {}", accumulationId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAllAccumulationsByUserId(Integer userId) {
        logger.info("开始删除用户所有积累记录，用户ID: {}", userId);
        try {
            if (userId == null) {
                logger.error("删除用户积累记录失败: 用户ID不能为空");
                throw new IllegalArgumentException("用户ID不能为空");
            }
            
            int deletedRows = accumulationMapper.deleteAllByUserId(userId);
            logger.info("用户积累记录删除成功，用户ID: {}, 删除记录数: {}", userId, deletedRows);
        } catch (Exception e) {
            logger.error("删除用户积累记录失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 验证积累记录的必填字段
     */
    private void validateAccumulation(Accumulation accumulation) {
        logger.debug("开始验证积累记录字段: {}", accumulation);
        
        if (accumulation.getUserId() == null) {
            logger.error("字段验证失败: 用户ID不能为空");
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (accumulation.getType() == null || accumulation.getType().trim().isEmpty()) {
            logger.error("字段验证失败: 积累类型不能为空");
            throw new IllegalArgumentException("积累类型不能为空");
        }
        
        // 验证类型值和长度
        String type = accumulation.getType().trim();
        if (!"word".equals(type) && !"sentence".equals(type)) {
            logger.error("字段验证失败: 积累类型只能是word或sentence，当前类型: '{}', 长度: {}", 
                type, type.length());
            throw new IllegalArgumentException("积累类型只能是word或sentence");
        }
        
        // 检查type字段长度（数据库字段可能有长度限制）
        if (type.length() > 20) {
            logger.error("字段验证失败: 积累类型长度超出限制，当前长度: {}, 最大允许: 20", type.length());
            throw new IllegalArgumentException("积累类型长度不能超过20个字符");
        }
        
        if (accumulation.getContent() == null || accumulation.getContent().trim().isEmpty()) {
            logger.error("字段验证失败: 积累内容不能为空");
            throw new IllegalArgumentException("积累内容不能为空");
        }
        if (accumulation.getMeaning() == null || accumulation.getMeaning().trim().isEmpty()) {
            logger.error("字段验证失败: 当前语境下含义不能为空");
            throw new IllegalArgumentException("当前语境下含义不能为空");
        }
        
        // 验证位置信息
        if (accumulation.getPosition() == null) {
            logger.error("字段验证失败: 位置信息不能为空");
            throw new IllegalArgumentException("位置信息不能为空");
        }
        
        // 详细验证Position对象
        validatePosition(accumulation.getPosition());
        
        // 记录字段长度信息，便于调试
        logger.debug("字段长度信息 - type: {}, content: {}, meaning: {}", 
            type.length(), 
            accumulation.getContent().length(),
            accumulation.getMeaning().length());
        
        logger.debug("积累记录字段验证通过");
    }
    
    /**
     * 验证Position对象
     */
    private void validatePosition(com.example.simon.entity.Position position) {
        logger.debug("开始验证Position字段: {}", position);
        
        if (position.getModule() == null || position.getModule().trim().isEmpty()) {
            logger.error("Position验证失败: 模块类型不能为空");
            throw new IllegalArgumentException("模块类型不能为空");
        }
        
        String module = position.getModule().trim();
        if (!"writing".equals(module) && !"reading".equals(module) && !"dialogue".equals(module)) {
            logger.error("Position验证失败: 模块类型只能是writing、reading或dialogue，当前类型: '{}'", module);
            throw new IllegalArgumentException("模块类型只能是writing、reading或dialogue");
        }
        
        if (position.getRefId() == null || position.getRefId() <= 0) {
            logger.error("Position验证失败: 关联ID必须大于0，当前值: {}", position.getRefId());
            throw new IllegalArgumentException("关联ID必须大于0");
        }
        
        if (position.getStartPos() == null || position.getStartPos() < 0) {
            logger.error("Position验证失败: 起始位置不能小于0，当前值: {}", position.getStartPos());
            throw new IllegalArgumentException("起始位置不能小于0");
        }
        
        if (position.getEndPos() == null || position.getEndPos() < 0) {
            logger.error("Position验证失败: 终止位置不能小于0，当前值: {}", position.getEndPos());
            throw new IllegalArgumentException("终止位置不能小于0");
        }
        
        if (position.getEndPos() < position.getStartPos()) {
            logger.error("Position验证失败: 终止位置不能小于起始位置，起始位置: {}，终止位置: {}", 
                        position.getStartPos(), position.getEndPos());
            throw new IllegalArgumentException("终止位置不能小于起始位置");
        }
        
        // 使用Position内置的验证方法
        if (!position.isValid()) {
            logger.error("Position验证失败: 位置信息整体验证不通过 - {}", position);
            throw new IllegalArgumentException("位置信息不完整或无效");
        }
        
        logger.debug("Position字段验证通过");
    }
} 