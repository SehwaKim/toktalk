package com.chat.toktalk.repository;

import com.chat.toktalk.domain.ChannelUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {
    ChannelUser findChannelUserByChannelIdAndUserId(Long channelId, Long userId);
}
