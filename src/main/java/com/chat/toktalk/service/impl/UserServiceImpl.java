package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.Role;
import com.chat.toktalk.domain.RoleState;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.repository.UserRepository;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User getUserByEmail(String email) {

        return userRepository.findUsersByEmail(email);
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void registerUser(User user) {
        //role설정
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setRoleState(RoleState.USER);
        user.addUserRole(role);
        userRepository.save(user);

        //TODO
        //회원상태 설정 필요
  }

    @Override
    public User getUserById(Long invitedUserId) {
        return userRepository.getOne(invitedUserId);

    }
}
