package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.Role;
import com.chat.toktalk.domain.RoleState;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.UserStatus;
import com.chat.toktalk.repository.UserRepository;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @Override
    public User findUserByEmail(String email) {

        return userRepository.findUsersByEmail(email);
    }

    @Override
    public void registerUser(User user,UserStatus userStatus) {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setRoleState(RoleState.USER);
        user.addUserRole(role);
        user.setRegdate(LocalDateTime.now());
        user.setUserStatus(userStatus);
        userRepository.save(user);

    }

    @Override
    public void updateUserData(User user){


    }

    @Override
    public void deleteUser(String email) {
        //TODO
        //데이터 유지 필요 없다고 판단되면 데이터 삭제로 변경.
        //데이터는 일정기간 유지시키는게 맞다고 필요하다면
        //차라리 백업으로 구현해버리는게 어떨까..
        //백업된 데이터는 3개월정도 보관 ---> 배치프로그램이 일괄삭제시킴.
        //TODO
        //삭제되어야 될 데이터 (Oauth2 인증정보, 친구 목록, 상대방 쪽 친구목록 , 채팅방 세션 DB)
        User user = userRepository.findUserByEmail(email);
        user.setUserStatus(UserStatus.DELETE);

    }

    @Override
    public User getUserById(Long invitedUserId) {
        return userRepository.getOne(invitedUserId);

    }


    @Override
    public User findOauthUserByEmail(String email){
        return userRepository.findOauthUserByEmail(email);

    }
}
