package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Accumulation extends BaseEntity {
    private Integer accumulationId;
    private Integer userId;
    private String content;
    private String meaning;
    private String sourceModule; // writing, reading, dialogue
    private Integer sourceRefId;
    private String positionJson;
}