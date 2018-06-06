package com.chat.toktalk.service;

import com.chat.toktalk.domain.ChannelUser;

import java.util.List;

public interface ChannelUserService {
    public void addChannelUser(ChannelUser channelUser);

    public ChannelUser getChannelUser(Long channelId, Long userId);

    List<ChannelUser> getChannelUsersByUserId(Long userId);
}
