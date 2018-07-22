package com.chat.toktalk.repository;

import com.chat.toktalk.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Query("select c from Channel c where c.type = 'public'")
    List<Channel> findAllPublicChannels();
}
