package com.chat.toktalk.repository;

import com.chat.toktalk.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Query("select c from Channel c where c.type = 'public'")
    List<Channel> findAllPublicChannels();

    @Query("select c from Channel c where (c.firstUserId = :firstUserId and c.secondUserId = :secondUserId)" +
            "or (c.firstUserId = :secondUserId and c.secondUserId = :firstUserId)")
    Channel findDirectChannelByIds(@Param("firstUserId") Long firstUserId, @Param("secondUserId") Long secondUserId);
}
