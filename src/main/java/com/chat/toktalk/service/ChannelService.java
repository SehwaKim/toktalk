package com.chat.toktalk.service;

import com.chat.toktalk.domain.Channel;

import java.util.List;

public interface ChannelService {
    public List<Channel> getChannels(Long userId);

    public Channel getChannel(Long channelId);

    public void addChannel(Channel channel);
}
