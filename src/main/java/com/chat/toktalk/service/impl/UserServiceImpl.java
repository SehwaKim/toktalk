package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.*;
import com.chat.toktalk.dto.UserDetailsForm;
import com.chat.toktalk.repository.OauthRepository;
import com.chat.toktalk.repository.UserRepository;
import com.chat.toktalk.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OauthRepository oauthRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, OauthRepository oauthRepository) {
        this.userRepository = userRepository;
        this.oauthRepository = oauthRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUsersByEmail(email);
    }

    @Override
    public User registerUser(User user,UserStatus userStatus) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setRoleState(RoleState.USER);
        user.addUserRole(role);
        user.setRegdate(LocalDateTime.now());
        user.setUserStatus(userStatus);
        userRepository.save(user);
        return user;

    }

    @Override
    public User updateNickName(User user,UserDetailsForm detailsForm){
        user.setNickname(detailsForm.getNickname());
        return user;
    }

    @Override
    public void updatePassword(User user){

        if("oauth".equals(user.getUserStatus().toString())){
            user.setUserStatus(UserStatus.NORMAL);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

    }

    @Override
    public void updateUserData(User user){
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String email) {
        //TODO
        //데이터 유지 필요 없다고 판단되면 데이터 삭제로 변경.
        //데이터는 일정기간 유지시키는게 맞다고 필요하다면
        //차라리 백업으로 구현해버리는게 어떨까..
        //백업된 데이터는 3개월정도 보관 ---> 배치프로그램이 일괄삭제시킴.

        //삭제되어야 될 데이터 (Oauth2 인증정보, 친구 목록, 상대방 쪽 친구목록 , 채팅방 세션 DB)
        User user = userRepository.findUserByEmail(email);
        user.setUserStatus(UserStatus.DELETE);

    }

    @Override
    public void disConnectSocial(User user) {
        OauthInfo oauthInfo = oauthRepository.findOauthInfoByEmail(user.getEmail());
        user.getOauthInfos().remove(oauthInfo);
        oauthRepository.deleteOauthUserByEmail(user.getEmail());
    }


    @Override
    public User findOauthUserByEmail(String email){
        return userRepository.findOauthUserByEmail(email);

    }
}
