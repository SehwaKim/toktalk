package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    RedisTemplate redisTemplate;

    RedisSerializer<String> redisSerializer = new StringRedisSerializer();

    List<User> userList = new ArrayList<>();

    List<Channel> channelList = new ArrayList<>();

    public RedisServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setKeySerializer(redisSerializer);
        this.redisTemplate.setValueSerializer(redisSerializer);
    }

    @Override
    public void addChannelUser(Long channelId, String userId) {
        redisTemplate.opsForList().rightPush(channelId.toString(), userId);
    }

    @Override
    public void addChannelForUser(String userId, Long channelId) {
        redisTemplate.opsForList().rightPush(userId.toString(), channelId.toString());
    }

    @Override
    public void addUserAtSocket(String socketId, String userId) {
        redisTemplate.opsForList().rightPush(socketId.toString(), userId);
    }

    @Override
    public List<User> getUsers(Long id) {
        // 해당 채널 키로 레디스의 모든 유저 가져오기. 인데스 0부터 -1은 있는거 다 불러오는거
        userList = redisTemplate.opsForList().range(id,0,-1);
        return userList;
    }

    @Override
    public User getUser(Principal principal) {
        User user = new User();
        for (int i=0;i<redisTemplate.opsForList().size("roonNo");i++) {
            user = (User)redisTemplate.opsForList().leftPop("roonNo");
            if(!principal.getName().equals(user.getEmail())) {
                continue;
            }
            break;
        }
        return user;
    }

    @Override
    public List<Channel> getChannels(String userId) {
        channelList = redisTemplate.opsForList().range(userId,0,-1);
        return channelList;
    }

    @Override
    public Boolean removeUser(String userId) {
        return redisTemplate.delete(userId);
    }

    @Override
    public void addWebSocketSessionByUser(Long userId, WebSocketSession session) {
        String key = "user:"+userId.toString();
        redisTemplate.opsForSet().add(key, session.getId());
        Set<String> sessionIdSet = redisTemplate.opsForSet().members(key);
        logger.info("userId " + userId +"의 웹소켓세션");
        for(String id : sessionIdSet){
            logger.info("WebSocketSession id: " + id);
        }
    }

    @Override
    public void removeWebSocketSessionByUser(Long userId, WebSocketSession session) {
        String key = "user:"+userId.toString();
        Set<String> sessionIdSet = redisTemplate.opsForSet().members(key);
        if (sessionIdSet.contains(session.getId())) {
            redisTemplate.opsForSet().remove(key, session.getId());
        }
        Set<String> sessions = redisTemplate.opsForSet().members(key);
        logger.info("user " + userId +"의 웹소켓세션");
        if(sessions.size() > 0) {
            for (String id : sessions) {
                logger.info("WebSocketSession id: " + id);
            }
        }else {
            logger.info("없음");
        }
    }

    @Override
    public void addActiveChannelInfo(String sessionId, Long channelId) {
        String key = "websocketsession:"+sessionId;
        logger.info(key);
        if(redisTemplate.hasKey(key)){
            redisTemplate.opsForValue().getAndSet(key, channelId.toString());
        }else {
            redisTemplate.opsForValue().set(key, channelId.toString());
        }
        String id = (String) redisTemplate.opsForValue().get(key);
        if(id != null){
            logger.info("currently active channel id: " + id);
        }
    }

    @Override
    public void removeActiveChannelInfo(WebSocketSession session) {
        String key = "websocketsession:" + session.getId();
        redisTemplate.delete(key);
    }

    @Override
    public Long getActiveChannelInfo(WebSocketSession session) {
        String key = "websocketsession:" + session.getId();
        String channelId = (String) redisTemplate.opsForValue().get(key);
        if (channelId != null){
            return Long.parseLong(channelId);
        }
        return null;
    }

    @Override
    public Boolean isChannelInSight(Long userId, Long channelId) {
        Boolean isChannelInSight = false;
        String key = "user:"+userId.toString();
        Set<String> sessionIdSet = redisTemplate.opsForSet().members(key);

        for(String sessionId : sessionIdSet){
            String sKey = "websocketsession:"+sessionId;
            String activeChannelId = (String) redisTemplate.opsForValue().get(sKey);
            if(channelId.toString().equals(activeChannelId)){
                isChannelInSight = true;
                break;
            }
        }

        return isChannelInSight;
    }

    /*@Override
    public void createMessageIdCounter(Long channelId) {
        String key = "channel:"+channelId.toString();
        redisTemplate.opsForValue().set(key, "0");
    }

    @Override
    public void increaseMessageIdByChannel(Long channelId) {
        String key = "channel:"+channelId.toString();
        redisTemplate.opsForValue().increment(key, 1);
    }

    @Override
    public Long getLastMessageIdByChannel(Long channelId) {
        String key = "channel:"+channelId.toString();
        String lastMessageId = (String) redisTemplate.opsForValue().get(key);
        if (lastMessageId != null) {
            return Long.parseLong(lastMessageId);
        }

        return null;
    }*/
}
