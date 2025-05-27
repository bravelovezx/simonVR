package com.example.simon.entity;

import lombok.Data;

@Data
public class ProfileInfo {
    private String nickname;
    private String avatar;
    private String bio;
    private String location;
    // 可以根据需求添加更多字段
}