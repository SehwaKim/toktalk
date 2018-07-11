package com.chat.toktalk.service;

import com.chat.toktalk.domain.User;

public interface UserService{
    void registerUser(User user);
    void addUser(User user);
    void deleteUser(String email);
    User getUserByEmail(String email);


    User getUserById(Long invitedUserId);
}
