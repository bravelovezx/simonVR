package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class DialogueSession extends BaseEntity {
    private Integer sessionId;
    private Integer userId;
    private String scene;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}