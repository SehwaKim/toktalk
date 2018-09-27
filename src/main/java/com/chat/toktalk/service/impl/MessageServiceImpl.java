package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.ExcludedMessage;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.repository.ChannelUserRepository;
import com.chat.toktalk.repository.ExcludedMessageRepository;
import com.chat.toktalk.repository.MessageRepository;
import com.chat.toktalk.service.MessageService;
import com.chat.toktalk.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ExcludedMessageRepository excludedMessageRepository;

    @Autowired
    ChannelUserRepository channelUserRepository;

    @Autowired
    RedisService redisService;

    @Override
    public Message addMessage(Message message) {
        Message saved = messageRepository.save(message);
//        redisService.increaseMessageIdByChannel(message.getChannelId());

        if("system".equals(message.getType())){
            ExcludedMessage excludedMessage = new ExcludedMessage();
            excludedMessage.setType("system");
            excludedMessage.setMessageId(saved.getId());
            excludedMessage.setChannelId(saved.getChannelId());
            excludedMessageRepository.save(excludedMessage);
        }

        return saved;
    }

    @Override
    public List<Message> getMessagesByChannelUser(Long channelId, Long firstReadId) {
        return messageRepository.findAllByChannelUser(channelId, firstReadId);
    }

    @Override
    public Long countUnreadMessageByChannelUser(ChannelUser channelUser) {
        // 채널의 마지막 글번호 - 유저가 마지막으로 읽은 글번호 - (삭제되거나 시스템메시지 수)
        // *글번호가 아니라 글 갯수로 가져와서 계산하도록 바꿈
        Long channelId = channelUser.getChannel().getId();
        Long userId = channelUser.getUser().getId();
        Long unread = 0L;

        if(!redisService.isChannelInSight(userId, channelId)){
            Long totMessageCnt = Optional.ofNullable(getTotalMessageCntByChannel(channelId)).orElse(0L);
            Long lastReadCnt = Optional.ofNullable(channelUser.getLastReadCnt()).orElse(0L);//lastreadid이름 바꿔야함
            Long excludedCnt = Optional.ofNullable(excludedMessageRepository.countExcludedMessageByChannelId(channelId))
                    .orElse(0L);

            System.out.println("===================================================================================");
            System.out.println("totMessageCnt: " + totMessageCnt);
            System.out.println("lastReadCnt: " + lastReadCnt);
            System.out.println("excludedCnt: " + excludedCnt);
            unread = totMessageCnt - lastReadCnt - excludedCnt;
        }
        return unread;
    }

    @Override
    public Long getTotalMessageCntByChannel(Long channelId) {
        return messageRepository.countMessageByChannel(channelId);
    }
}
