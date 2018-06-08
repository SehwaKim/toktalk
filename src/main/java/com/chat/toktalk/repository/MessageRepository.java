package com.chat.toktalk.repository;

import com.chat.toktalk.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where m.channelId = :channelId order by m.id asc")
    List<Message> findAllByChannelId(@Param(value = "channelId") Long channelId);
}
