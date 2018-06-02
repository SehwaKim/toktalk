package com.chat.toktalk.repository;

import com.chat.toktalk.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChannelId(Long channelId);
}
