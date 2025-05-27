package com.example.simon.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BaseEntity {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}