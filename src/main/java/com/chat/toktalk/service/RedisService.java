package com.chat.toktalk.service;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.User;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.List;

public interface RedisService {
    // 유저리스트 저장
//    public void addUser(Long id,WebSocketSession session);
    public void addChannelUser(Long channelId,String userId);

    // 유저가 참여한 방정보 저장
    public void addChannelForUser(String userId, Long channelId);

    public void addUserAtSocket(String socketId, String userId);

    // TODO 삭제

    // TODO 조회
    public List<User> getUsers(Long id);

    public User getUser(Principal principal);

    // 해당 유저가 참여한 방목록
    public List<Channel> getChannels(String id);

    Boolean removeUser(String userId);

    void addWebSocketSessionByUser(Long userId, WebSocketSession session);
    void removeWebSocketSessionByUser(Long userId, WebSocketSession session);

    void addActiveChannelInfo(String sessionId, Long channelId);
    void removeActiveChannelInfo(WebSocketSession session);
    Long getActiveChannelInfo(WebSocketSession session);

    Boolean isChannelInSight(Long userId, Long channelId);

//    void createMessageIdCounter(Long channelId);
//    void increaseMessageIdByChannel(Long channelId);
//    Long getLastMessageIdByChannel(Long channelId);
}
