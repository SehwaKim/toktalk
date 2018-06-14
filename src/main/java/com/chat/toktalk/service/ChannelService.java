package com.chat.toktalk.service;

import com.chat.toktalk.domain.Channel;

import java.util.List;
import java.util.Set;

public interface ChannelService {
    public Set<Channel> getChannelsByUser(Long userId);

    public Channel getChannel(Long channelId);

    public Channel addChannel(Channel channel);
}
