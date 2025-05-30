package com.example.simon.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileInfo {
    private String nickname;
    private String avatar;
    private String bio;
    private String location;
    // 可以根据需求添加更多字段
}