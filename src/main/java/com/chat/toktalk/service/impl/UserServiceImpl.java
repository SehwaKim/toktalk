package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.repository.UserRepository;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {

        return userRepository.findUsersByEmail(email);
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long invitedUserId) {
        return userRepository.getOne(invitedUserId);
    }
}
