package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelType;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.repository.ChannelRepository;
import com.chat.toktalk.repository.ChannelUserRepository;
import com.chat.toktalk.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    ChannelUserRepository channelUserRepository;

    @Override
    public List<Channel> getChannelsByUser(Long userId, ChannelType type) {
        List<ChannelUser> channelUsers = channelUserRepository.findAllByUserId(userId);
        List<Channel> channels = new ArrayList<>(channelUsers.size());
        channelUsers.stream().filter(user -> user.getChannel().getType() == type)
                .forEach(user -> channels.add(user.getChannel()));

        return channels;
    }

    @Override
    public Channel getChannel(Long channelId) {
        return channelRepository.findById(channelId).get();
    }

    @Override
    public Channel addChannel(Channel channel) {
        Channel newChannel = channelRepository.save(channel);

        List<ChannelUser> usersOfNewChannel = channelUserRepository.findAllByChannelId(newChannel.getId());
        usersOfNewChannel.stream().forEach(channelUser -> {
            channelUser.setFirstReadId(1L);
            channelUserRepository.saveAndFlush(channelUser);
        });

        return newChannel;
    }

    @Override
    public Channel getDirectChannel(Long userId, Long partnerId) {
        return channelRepository.findDirectChannelByIds(userId, partnerId);
    }
}
