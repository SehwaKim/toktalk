package com.chat.toktalk.service;

import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.Message;

import java.util.List;

public interface MessageService {
    public Message addMessage(Message message);

    public List<Message> getMessagesByChannelId(Long channelId);
}
