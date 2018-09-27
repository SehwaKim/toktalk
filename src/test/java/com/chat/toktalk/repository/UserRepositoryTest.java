package com.chat.toktalk.repository;

import com.chat.toktalk.domain.OauthInfo;
import com.chat.toktalk.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Before
    public void setup(){
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Test
    public void 이메일로_유저정보조회하기() {

        User registerUser = new User();
        registerUser.setEmail("아기상어@gmail.com");
        registerUser.setPassword("1234");
        registerUser.setNickname("뚜루뚜루");
        userRepository.save(registerUser);

        User result = userRepository.findUserByEmail("아기상어@gmail.com");

        assertEquals(result.getNickname(),"뚜루뚜루");
    }

    @Test
    public void 이메일로_구글로그인정보조회하기(){
        
        User registerUser = new User();
        registerUser.setEmail("아기상어@gmail.com");
        registerUser.setPassword(passwordEncoder.encode("1234"));
        registerUser.setNickname("뚜루뚜루");

        OauthInfo oauthInfo = new OauthInfo();
        oauthInfo.setEmail("아기상어@gmail.com");

        registerUser.addUserOauthInfo(oauthInfo);
        userRepository.save(registerUser);

        User result = userRepository.findOauthUserByEmail("아기상어@gmail.com");

        assertEquals(registerUser.getOauthInfos().size(),1);
        assertEquals(result.getNickname(),"뚜루뚜루");

    }


}
