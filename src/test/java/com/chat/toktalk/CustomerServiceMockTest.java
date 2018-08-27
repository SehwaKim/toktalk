package com.chat.toktalk;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceMockTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 이메일로_유저정보조회하기() {
        
        User registerUser = new User();
        registerUser.setEmail("아기상어@gmail.com");
        registerUser.setPassword("1234");
        registerUser.setNickname("뚜루뚜루");

        userRepository.save(registerUser);

        User result = userRepository.findUserByEmail("아기상어@gmail.com");

        assertThat(result.getNickname()).isEqualTo("뚜루뚜루");
    }


}
