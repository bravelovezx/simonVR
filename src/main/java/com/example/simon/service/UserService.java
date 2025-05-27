package com.example.simon.service;

import com.example.simon.entity.User;

public interface UserService {
    User register(User user);

    User login(String username, String password);

    User getUserById(Integer userId);

    User updateUser(User user);

    void deleteUser(Integer userId);
}
