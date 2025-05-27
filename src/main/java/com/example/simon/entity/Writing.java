package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Writing extends BaseEntity {
    private Integer writingId;
    private Integer userId;
    private String writingTopic;
    private String sourceType; // recommended, user_input
}