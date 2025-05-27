package com.example.simon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WritingVersion extends BaseEntity {
    private Integer versionId;
    private Integer writingId;
    private Integer versionNumber;
    private String userDraft;
    private String correctedDraft;
}