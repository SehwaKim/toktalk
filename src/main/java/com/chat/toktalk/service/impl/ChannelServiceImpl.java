package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.repository.ChannelRepository;
import com.chat.toktalk.repository.ChannelUserRepository;
import com.chat.toktalk.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    ChannelUserRepository channelUserRepository;

    @Override
    public List<Channel> getChannels(Long userId) {
        List<Channel> channels = new ArrayList<>();
        List<ChannelUser> channelUsers = channelUserRepository.findAllByUserId(userId);
        for(ChannelUser channelUser : channelUsers){
            channels.add(channelUser.getChannel());
        }
        return channels;
    }

    @Override
    public Channel getChannel(Long channelId) {
        return channelRepository.getOne(channelId);
    }

    @Override
    public void addChannel(Channel channel) {
        channelRepository.save(channel);
    }
}
