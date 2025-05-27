package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Reading extends BaseEntity {
    private Integer readingId;
    private Integer userId;
    private String sourceType; // recommended, user_input
    private String articleTitle;
    private String articleContent;
}