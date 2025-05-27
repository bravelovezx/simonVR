package com.example.simon.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProfileInfoTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testProfileInfoSerialization() throws Exception {
        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.setNickname("测试用户");
        profileInfo.setAvatar("avatar.jpg");
        profileInfo.setBio("这是一个测试简介");
        profileInfo.setLocation("北京");

        String json = objectMapper.writeValueAsString(profileInfo);

        assertTrue(json.contains("测试用户"));
        assertTrue(json.contains("avatar.jpg"));
        assertTrue(json.contains("这是一个测试简介"));
        assertTrue(json.contains("北京"));
    }

    @Test
    void testProfileInfoDeserialization() throws Exception {
        String json = """
                {
                    "nickname": "测试用户",
                    "avatar": "avatar.jpg",
                    "bio": "这是一个测试简介",
                    "location": "北京"
                }
                """;

        ProfileInfo profileInfo = objectMapper.readValue(json, ProfileInfo.class);

        assertEquals("测试用户", profileInfo.getNickname());
        assertEquals("avatar.jpg", profileInfo.getAvatar());
        assertEquals("这是一个测试简介", profileInfo.getBio());
        assertEquals("北京", profileInfo.getLocation());
    }
}