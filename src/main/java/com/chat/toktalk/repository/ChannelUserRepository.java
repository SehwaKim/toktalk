package com.chat.toktalk.repository;

import com.chat.toktalk.domain.ChannelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {
    ChannelUser findChannelUserByChannelIdAndUserId(Long channelId, Long userId);

    @Query("select c from ChannelUser c where c.user.id = :userId")
    List<ChannelUser> findAllByUserId(@Param("userId") Long userId);

    List<ChannelUser> findAllByChannelId(Long channelId);
}
