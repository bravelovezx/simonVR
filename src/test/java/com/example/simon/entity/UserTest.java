package com.example.simon.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testUserBasicProperties() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        assertEquals(1, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedpassword", user.getPasswordHash());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testUserWithProfileInfo() {
        User user = new User();
        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.setNickname("测试用户");
        profileInfo.setAvatar("avatar.jpg");
        profileInfo.setBio("这是一个测试简介");
        profileInfo.setLocation("北京");

        user.setProfileInfo(profileInfo);

        assertNotNull(user.getProfileInfo());
        assertEquals("测试用户", user.getProfileInfo().getNickname());
        assertEquals("avatar.jpg", user.getProfileInfo().getAvatar());
    }

    @Test
    void testUserSerialization() throws Exception {
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");

        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.setNickname("测试用户");
        user.setProfileInfo(profileInfo);

        String json = objectMapper.writeValueAsString(user);

        assertTrue(json.contains("testuser"));
        assertTrue(json.contains("test@example.com"));
        assertFalse(json.contains("hashedpassword")); // 验证 @JsonIgnore 注解生效
        assertTrue(json.contains("测试用户"));
    }

    @Test
    void testUserDeserialization() throws Exception {
        String json = """
                {
                    "userId": 1,
                    "username": "testuser",
                    "email": "test@example.com",
                    "profileInfo": {
                        "nickname": "测试用户",
                        "avatar": "avatar.jpg",
                        "bio": "这是一个测试简介",
                        "location": "北京"
                    }
                }
                """;

        User user = objectMapper.readValue(json, User.class);

        assertEquals(1, user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertNotNull(user.getProfileInfo());
        assertEquals("测试用户", user.getProfileInfo().getNickname());
    }
}