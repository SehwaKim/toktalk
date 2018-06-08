package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate redisTemplate;

    List<User> userList = new ArrayList<>();
    List<Channel> channelList = new ArrayList<>();

//    @Override
//    public void addUser(Long id, WebSocketSession session) {
//        redisTemplate.opsForList().rightPush(id,session.getId());
//    }

    @Override
    public void addChannelUser(Long channelId, String userId) {
        redisTemplate.opsForList().rightPush(channelId,userId);
    }

    @Override
    public void addChannelForUser(String userId, Long channelId) {
        redisTemplate.opsForList().rightPush(userId,channelId);
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
        redisTemplate.opsForSet().add(userId, session.getId());
        Set<String> sessions = redisTemplate.opsForSet().members(userId);
        for(String id : sessions){
            System.out.println("id : " + id);
        }
    }

    @Override
    public void removeWebSocketSessionByUser(Long userId, WebSocketSession session) {
        Set<String> sessionIdSet = redisTemplate.opsForSet().members(userId);
        if(sessionIdSet.contains(session.getId())){
            redisTemplate.opsForSet().remove(userId, session.getId());
        }
        Set<String> sessions = redisTemplate.opsForSet().members(userId);
        if(sessions != null) {
            for (String id : sessions) {
                System.out.println("id : " + id);
            }
        }
    }

    @Override
    public void addActiveChannelInfo(String sessionId, Long channelId) {
        if(redisTemplate.hasKey(sessionId)){
            redisTemplate.opsForValue().getAndSet(sessionId, channelId);
        }else {
            redisTemplate.opsForValue().set(sessionId, channelId);
        }
        Long id = (Long) redisTemplate.opsForValue().get(sessionId);
        if(id != null){
            System.out.println("활성화된 채널의 아이디 " + id);
        }

    }
}
