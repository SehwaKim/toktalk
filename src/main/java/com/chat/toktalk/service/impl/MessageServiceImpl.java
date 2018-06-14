package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.ExcludedMessage;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.repository.ExcludedMessageRepository;
import com.chat.toktalk.repository.MessageRepository;
import com.chat.toktalk.service.MessageService;
import com.chat.toktalk.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ExcludedMessageRepository excludedMessageRepository;

    @Autowired
    RedisService redisService;

    @Override
    public void addMessage(Message message) {
        Message saved = messageRepository.save(message);
        redisService.increaseMessageIdByChannel(message.getChannelId());

        if("system".equals(message.getType())){
            ExcludedMessage excludedMessage = new ExcludedMessage();
            excludedMessage.setType("system");
            excludedMessage.setMessageId(saved.getId());
            excludedMessage.setChannelId(saved.getChannelId());
            excludedMessageRepository.save(excludedMessage);
        }
    }

    @Override
    public List<Message> getMessagesByChannelId(Long channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Long countUnreadMessageByChannelUser(ChannelUser channelUser) {
//        채널의 마지막 글번호 - 유저가 마지막으로 읽은 글번호 - (삭제되거나 시스템메시지 수)
        // 근데 이 채널을 다른데서 보고있으면 무시되어야 함
        Long channelId = channelUser.getChannel().getId();
        Long userId = channelUser.getUser().getId();

        if(!redisService.isChannelInSight(userId, channelId)){
            Long lastMessageId = redisService.getLastMessageIdByChannel(channelId);
            if(lastMessageId == null){
                lastMessageId = 0L;
            }
            Long lastReadId = channelUser.getLastReadId();
            if(lastReadId == null){
                lastReadId = 0L;
            }
            Long excludedCnt = excludedMessageRepository.countExcludedMessageByChannelId(channelId);
            if(excludedCnt == null){
                excludedCnt = 0L;
            }
            Long result = lastMessageId - lastReadId - excludedCnt;
            if(result > 0){
                return result;
            }
        }

        return null;
    }
}
