package com.example.simon.service.impl;

import com.example.simon.entity.ProfileInfo;
import com.example.simon.entity.User;
import com.example.simon.mapper.UserMapper;
import com.example.simon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 设置默认的 ProfileInfo
        if (user.getProfileInfo() == null) {
            user.setProfileInfo(new ProfileInfo());
            user.getProfileInfo().setNickname(user.getUsername());
            user.getProfileInfo().setAvatar("default-avatar.jpg");
        }

        // 加密密码
        if (user.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        } else {
            throw new RuntimeException("密码不能为空");
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        try {
            userMapper.insert(user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("注册失败：" + e.getMessage());
        }
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (password == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userMapper.selectById(user.getUserId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 如果要更新用户名，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername())) {
            User userWithNewUsername = userMapper.findByUsername(user.getUsername());
            if (userWithNewUsername != null) {
                throw new RuntimeException("新用户名已存在");
            }
        }

        // 如果提供了新密码，则加密
        if (user.getPasswordHash() != null && !user.getPasswordHash().equals(existingUser.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.deleteById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }
}