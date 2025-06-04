package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Annotation extends BaseEntity {
    private Integer annotationId;
    private Integer userId;
    private Position position;
    private String original;
    private String annotationContent;
}