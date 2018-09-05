package com.chat.toktalk.repository.jpa;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.repository.ChannelUserRepository;
import com.chat.toktalk.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ChannelUserRepositoryTest {

    @Autowired
    ChannelUserRepository channelUserRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void testSaveChannelUser() {
        User user = userRepository.save(new User());
    }
}
