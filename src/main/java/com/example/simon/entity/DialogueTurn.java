package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DialogueTurn extends BaseEntity {
    private Integer turnId;
    private Integer sessionId;
    private Integer turnNumber;
    private String speaker; // user, ai
    private String rawText;
    private String correctedText;
}