package com.chat.toktalk.repository.jpa;

import com.chat.toktalk.domain.*;
import com.chat.toktalk.repository.ChannelRepository;
import com.chat.toktalk.repository.ChannelUserRepository;
import com.chat.toktalk.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ChannelRepositoryTest {

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChannelUserRepository channelUserRepository;

    @Test
    public void testNotNull() {
        assertThat(channelRepository).isNotNull();
    }

    private User createAndSaveTestUser() {
        return createAndSaveTestUser("first@gmail.com");
    }

    private User createAndSaveTestUser(String email) {
        Role role = new Role();
        role.setRoleState(RoleState.USER);

        User testUser = new User();
        testUser.setEmail(email);
        testUser.setUserStatus(UserStatus.NORMAL);
        testUser.addUserRole(role);

        return userRepository.save(testUser);
    }

    private ChannelUser createAndSaveTestChannelUser(User testUser) {
        ChannelUser channelUser = new ChannelUser();
        channelUser.setUser(testUser);
        return channelUserRepository.save(channelUser);
    }

    private Channel createChannel(ChannelType type) {
        Channel channel = new Channel();
        channel.setType(type);
        return channel;
    }

    @Test
    public void FindDirectChannelByIds() {
        User firstUser = createAndSaveTestUser();
        User secondUser = createAndSaveTestUser("second@gmail.com");

        ChannelUser firstChannelUser = createAndSaveTestChannelUser(firstUser);
        ChannelUser secondChannelUser = createAndSaveTestChannelUser(secondUser);

        Channel testChannel = createChannel(ChannelType.DIRECT);
        testChannel.addChannelUser(firstChannelUser);
        testChannel.addChannelUser(secondChannelUser);
        testChannel.setFirstUserId(firstUser.getId());
        testChannel.setSecondUserId(secondUser.getId());
        channelRepository.save(testChannel);

        assertThat(channelRepository.findDirectChannelByIds(firstUser.getId(), secondUser.getId())).isNotNull();
    }

    @Test
    public void testSaveChannel() {
        User firstUser = createAndSaveTestUser();
        ChannelUser firstChannelUser = createAndSaveTestChannelUser(firstUser);

        Channel testChannel = createChannel(ChannelType.PUBLIC);
        testChannel.addChannelUser(firstChannelUser);
        Channel saveChannel = channelRepository.save(testChannel);

        Channel findChannel = channelRepository.findById(saveChannel.getId()).get();

        assertThat(findChannel.getId()).isEqualTo(saveChannel.getId());
    }
}
