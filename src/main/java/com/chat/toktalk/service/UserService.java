package com.chat.toktalk.service;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.UserStatus;
import com.chat.toktalk.dto.UserDetailsForm;

import java.util.Optional;

public interface UserService{
    User registerUser(User user,UserStatus userStatus);
    void deleteUser(String email);
    User updateNickName(User user,UserDetailsForm detailsForm);
    User findUserByEmail(String email);
    User findOauthUserByEmail(String email);
    void updateUserData(User user);
    void disConnectSocial(User user);
    void updatePassword(User user);

    User findUserByNickname(String nickname);
}
