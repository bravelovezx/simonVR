package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Annotation extends BaseEntity {
    private Integer annotationId;
    private Integer userId;
    private String module; // writing, reading, dialogue
    private Integer refId;
    private String positionJson;
    private String annotationContent;
}