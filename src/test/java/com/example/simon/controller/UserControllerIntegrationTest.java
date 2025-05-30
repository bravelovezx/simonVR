package com.example.simon.controller;

import com.example.simon.entity.ProfileInfo;
import com.example.simon.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;
/*
        @Test
        void testRegisterUser() throws Exception {
                // 准备测试数据
                User user = new User();
                user.setUsername("testuser123");
                user.setEmail("test@example.com");
                user.setPassword("password123");

                ProfileInfo profileInfo = new ProfileInfo();
                profileInfo.setNickname("测试用户");
                profileInfo.setAvatar("default-avatar.jpg");
                profileInfo.setBio("这是一个测试用户");
                profileInfo.setLocation("北京");
                user.setProfileInfo(profileInfo);

                // 执行测试请求
                String requestBody = objectMapper.writeValueAsString(user);
                System.out.println("Request body: " + requestBody);

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andDo(MockMvcResultHandlers.print())
                                .andDo(result -> {
                                        System.out.println("Response status: " + result.getResponse().getStatus());
                                        System.out.println(
                                                        "Response body: " + result.getResponse().getContentAsString());
                                })
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.user.userId").exists())
                                .andExpect(jsonPath("$.user.username").value("testuser123"))
                                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                                .andExpect(jsonPath("$.user.password").doesNotExist())
                                .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
                                .andExpect(jsonPath("$.user.profileInfo.nickname").value("测试用户"));
        }

 */
        @Test
        void testRegisterUserWithInvalidData() throws Exception {
                // 测试用户名为空的情况
                User invalidUser = new User();
                invalidUser.setEmail("test@example.com");
                invalidUser.setPassword("password123");

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser)))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isBadRequest());

                // 测试邮箱格式不正确的情况
                invalidUser = new User();
                invalidUser.setUsername("testuser");
                invalidUser.setEmail("invalid-email");
                invalidUser.setPassword("password123");

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser)))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isBadRequest());

                // 测试密码为空的情况
                invalidUser = new User();
                invalidUser.setUsername("testuser");
                invalidUser.setEmail("test@example.com");

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser)))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isBadRequest());
        }
}