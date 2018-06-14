package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.repository.ChannelRepository;
import com.chat.toktalk.repository.ChannelUserRepository;
import com.chat.toktalk.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    ChannelUserRepository channelUserRepository;

    @Override
    public Set<Channel> getChannelsByUser(Long userId) {
        List<ChannelUser> channelUsers = channelUserRepository.findAllByUserId(userId);
        Set<Channel> channels = channelRepository.findAllPublicChannels();
        for(ChannelUser channelUser : channelUsers){
            channels.add(channelUser.getChannel());
        }

//        return new ArrayList<>(channels);
        return channels;
    }

    @Override
    public Channel getChannel(Long channelId) {
        return channelRepository.getOne(channelId);
    }

    @Override
    public Channel addChannel(Channel channel) {
        Channel saved = channelRepository.save(channel);

        List<ChannelUser> channelUsers = channelUserRepository.findAllByChannelId(saved.getId());
        for(ChannelUser channelUser : channelUsers){
            channelUser.setFirstReadId(1L);
            channelUserRepository.saveAndFlush(channelUser);
        }

        return saved;
    }

}
