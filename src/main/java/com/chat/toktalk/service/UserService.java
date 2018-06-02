package com.chat.toktalk.service;

import com.chat.toktalk.domain.User;

public interface UserService {
    public void addUser(User user);

    public User getUserByEmail(String email);
}
