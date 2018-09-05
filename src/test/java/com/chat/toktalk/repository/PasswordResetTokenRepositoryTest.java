package com.chat.toktalk.repository;

import com.chat.toktalk.domain.PasswordResetToken;
import com.chat.toktalk.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordResetTokenRepositoryTest {

    @Autowired
    PasswordResetTokenRepository tokenRepository;

    @Test
    public void 토큰을_넘기면_토큰객체를_반환한다() {
        String uuid = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        User user  = new User();
        user.setNickname("뚜루뚜루");
        resetToken.setUser(user);
        resetToken.setToken(uuid);
        resetToken.setExpiryDate(30);
        tokenRepository.save(resetToken);

        PasswordResetToken resultToken =tokenRepository.findByToken(uuid);
        User resultUser = resetToken.getUser();

        assertEquals(resultToken.getToken(),uuid);
        assertEquals(resultUser.getNickname(),user.getNickname());
    }
}