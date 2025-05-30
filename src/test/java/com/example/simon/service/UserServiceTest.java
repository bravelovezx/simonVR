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


}
