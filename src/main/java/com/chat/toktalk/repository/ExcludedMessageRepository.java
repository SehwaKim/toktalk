package com.chat.toktalk.repository;

import com.chat.toktalk.domain.ExcludedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcludedMessageRepository extends JpaRepository<ExcludedMessage, Long> {
    Long countExcludedMessageByChannelId(Long channelId);
}
