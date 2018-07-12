package com.chat.toktalk.service;

import com.chat.toktalk.domain.User;

public interface UserService{
    void registerUser(User user);
    void deleteUser(String email);
    void updateUserData(User user);
    User getUserByEmail(String email);
    User getUserById(Long invitedUserId);
}
