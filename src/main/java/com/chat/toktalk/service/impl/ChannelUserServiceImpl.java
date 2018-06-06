package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.repository.ChannelUserRepository;
import com.chat.toktalk.service.ChannelUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelUserServiceImpl implements ChannelUserService {
    @Autowired
    ChannelUserRepository channelUserRepository;

    @Override
    public void addChannelUser(ChannelUser channelUser) {
        channelUserRepository.save(channelUser);
    }

    @Override
    public ChannelUser getChannelUser(Long channelId, Long userId) {
        return channelUserRepository.findChannelUserByChannelIdAndUserId(channelId, userId);
    }

    @Override
    public List<ChannelUser> getChannelUsersByUserId(Long userId) {
        return channelUserRepository.findAllByUserId(userId);
    }

}
