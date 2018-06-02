package com.chat.toktalk.service;

import com.chat.toktalk.domain.Channel;

public interface ChannelService {
    public Channel getChannel(Long channelId);

    public void addChannel(Channel channel);
}
