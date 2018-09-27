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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OauthRepositoryTest {
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Autowired
    OauthRepository oauthRepository;

    @Before
    public void setup(){
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Test
    public void Email을_넘기면_OauthInfo를_반환함() {

        User registerUser = new User();
        registerUser.setEmail("아기상어@gmail.com");
        registerUser.setPassword("1234");
        registerUser.setNickname("뚜루뚜루");

        OauthInfo oauthInfo = new OauthInfo();
        oauthInfo.setEmail("아기상어@gmail.com");
        oauthInfo.setName("뚜루뚜루");
        registerUser.addUserOauthInfo(oauthInfo);
        userRepository.save(registerUser);

        OauthInfo result = oauthRepository.findOauthInfoByEmail("아기상어@gmail.com");

        assertEquals(result.getName(),oauthInfo.getName());
    }

    @Test
    @Transactional
    public void deleteOauthUserByEmail() {
        User registerUser = new User();
        registerUser.setEmail("아기상어@gmail.com");
        registerUser.setPassword("1234");
        registerUser.setNickname("뚜루뚜루");

        OauthInfo oauthInfo = new OauthInfo();
        oauthInfo.setEmail("아기상어@gmail.com");
        oauthInfo.setName("뚜루뚜루");
        registerUser.addUserOauthInfo(oauthInfo);
        userRepository.save(registerUser);

        oauthRepository.deleteOauthUserByEmail("아기상어@gmail.com");
        OauthInfo result = oauthRepository.findOauthInfoByEmail("아기상어@gmail.com");

        assertNull(result);
    }
}