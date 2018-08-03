package com.chat.toktalk.service;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.UserStatus;

public interface UserService{
    void registerUser(User user,UserStatus userStatus);
    void deleteUser(String email);
    void updateUserData(User user);
    User findUserByEmail(String email);
    User getUserById(Long invitedUserId);
    User findOauthUserByEmail(String email);
}
