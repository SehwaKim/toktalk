package com.chat.toktalk.service;

import com.chat.toktalk.domain.ChannelUser;

public interface ChannelUserService {
    public void addChannelUser(ChannelUser channelUser);

    public ChannelUser getChannelUser(Long channelId, Long userId);

}
