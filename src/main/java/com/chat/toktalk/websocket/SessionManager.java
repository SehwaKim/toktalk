package com.chat.toktalk.websocket;

import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.service.ChannelUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Component
public class SessionManager {
    @Autowired
    ChannelUserService channelUserService;

    private Map<Long, WebSocketSession> sessions = Collections.synchronizedMap(new HashMap<>()); // userId, WebSocketSession

    public void addWebSocketSession(Long userId, WebSocketSession session){
        sessions.put(userId, session);
    }

    public void removeWebSocketSession(Long userId){
        sessions.remove(userId);
    }

    public List<WebSocketSession> getWebSocketSessionsByChannelId(Long channelId){
        List<ChannelUser> channelUsers = channelUserService.getChannelUsersByChannelId(channelId);
        List<WebSocketSession> targetSessions = new ArrayList<>();

        for(ChannelUser channelUser : channelUsers){
            Long targetUserId = channelUser.getUser().getId();
            if(sessions.containsKey(targetUserId)){
                targetSessions.add(sessions.get(targetUserId));
            }
        }
        return targetSessions;
    }
}
