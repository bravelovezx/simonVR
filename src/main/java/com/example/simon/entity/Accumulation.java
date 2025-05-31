package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Accumulation extends BaseEntity {
    /**
     * 积累ID
     */
    private Integer accumulationId;

    /**
     * 所属用户ID
     */
    private Integer userId;

    /**
     * 积累类型：word(单词) 或 sentence(句子)
     */
    private String type;

    /**
     * 积累内容
     */
    private String content;

    /**
     * 当前语境下含义
     */
    private String meaning;

    /**
     * 位置信息，包含：
     * - module：所属模块（writing/reading/dialogue）
     * - refId：关联ID（writing_version_id/reading_id/turn_id）
     * - row：行号
     * - column：列号
     */
    private Position position;
}