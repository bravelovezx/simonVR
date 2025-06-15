package com.example.simon.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WritingVersion {
    private Integer versionId;
    private Integer writingId;
    private Integer versionNumber;
    private String userDraft;
    private String correctedDraft;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Integer getWritingId() {
        return writingId;
    }

    public void setWritingId(Integer writingId) {
        this.writingId = writingId;
    }

    public String getUserDraft() {
        return userDraft;
    }

    public void setUserDraft(String userDraft) {
        this.userDraft = userDraft;
    }

    public String getCorrectedDraft() {
        return correctedDraft;
    }

    public void setCorrectedDraft(String correctedDraft) {
        this.correctedDraft = correctedDraft;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
