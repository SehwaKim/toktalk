package com.chat.toktalk.service;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.User;

import java.util.List;

public interface ChannelUserService {
    public void addChannelUser(ChannelUser channelUser, Long userId, Long channelId);

    public ChannelUser getChannelUser(Long channelId, Long userId);

    List<ChannelUser> getChannelUsersByUserId(Long userId);

    void updateChannelUser(ChannelUser alreadyUser);

    List<ChannelUser> getChannelUsersByChannelId(Long channelId);

    void removeChannelUser(Long userId, Long channelId);
}
