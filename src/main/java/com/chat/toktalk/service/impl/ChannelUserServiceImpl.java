package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.repository.ChannelRepository;
import com.chat.toktalk.repository.ChannelUserRepository;
import com.chat.toktalk.repository.UserRepository;
import com.chat.toktalk.service.ChannelUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChannelUserServiceImpl implements ChannelUserService {
    @Autowired
    ChannelUserRepository channelUserRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void addChannelUser(ChannelUser channelUser, Long userId, Long channelId) {
        channelUser.setUser(userRepository.getOne(userId));
        channelUser.setChannel(channelRepository.getOne(channelId));
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

    @Override
    public void updateChannelUser(ChannelUser alreadyUser) {
        channelUserRepository.saveAndFlush(alreadyUser);
    }

    @Override
    public List<ChannelUser> getChannelUsersByChannelId(Long channelId) {
        return channelUserRepository.findAllByChannelId(channelId);
    }

    @Override
    public void removeChannelUser(Long userId, Long channelId) {
        ChannelUser channelUser = channelUserRepository.findChannelUserByChannelIdAndUserId(channelId, userId);
        channelUserRepository.delete(channelUser);
    }
}
