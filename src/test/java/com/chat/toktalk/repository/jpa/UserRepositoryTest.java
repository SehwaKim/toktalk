package com.chat.toktalk.repository.jpa;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testNotNull() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void testGetOptionalUser() {
        User user = userRepository.findUserByNickname("아이스베어");
        Optional<User> optUser = Optional.ofNullable(user);
        assertThat(optUser.get()).isNotNull();
        assertThat(optUser.get().getNickname()).isEqualTo("아이스베어");
    }

    @Test(expected = NoSuchElementException.class)
    public void testThrowNoSuchElementException() {
        Optional<User> optUser = Optional.ofNullable(userRepository.findUserByNickname("a"));
        assertThat(optUser.isPresent()).isFalse();
        optUser.get();
    }

    @Test
    public void testNullUser() {
        User user = userRepository.findUserByNickname("");
        assertThat(Objects.nonNull(user)).isFalse();
    }
}
