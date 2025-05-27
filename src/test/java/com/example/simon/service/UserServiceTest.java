package com.example.simon.service;

import com.example.simon.entity.ProfileInfo;
import com.example.simon.entity.User;
import com.example.simon.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void testInsertAndRetrieveUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setProfileInfo(new ProfileInfo());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userService.register(user);

        // 假设有一个方法可以查询用户
        User retrievedUser = userService.getUserById(user.getUserId());
        assertNotNull(retrievedUser);
        assertNotNull(retrievedUser.getCreatedAt());
        assertNotNull(retrievedUser.getUpdatedAt());
    }
}
