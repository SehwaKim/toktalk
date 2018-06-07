package com.chat.toktalk.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Component
public class SessionManager {

    private Map<Long, Set<WebSocketSession>> sessions = Collections.synchronizedMap(new HashMap<>());//방번호,세션들
    /*
    *  1. 웹소켓 연결 open 할 때
    *  2. 한 채널 입장 시
    * */
    public void addWebSocketSession(Long channelId,WebSocketSession session){
        Set<WebSocketSession> sessionContainer = getWebSocketSessionContainer(channelId);
        sessionContainer.add(session);
        sessions.put(channelId,sessionContainer);
    }

    /*
     *  1. 웹소켓 연결 close 할 때
     *  2. 한 채널 퇴장 시
     * */
    public void removeWebSocketSession(Long channelId,WebSocketSession webSocketSession){
        Set<WebSocketSession> sessionContainer = getWebSocketSessionContainer(channelId);
        sessionContainer.remove(webSocketSession);
    }

    public Set<WebSocketSession> getWebSocketSessions(Long channelId){
        return sessions.get(channelId);
    }

    private Set<WebSocketSession> getWebSocketSessionContainer(Long channelId){
        if(!sessions.containsKey(channelId)){
            return Collections.synchronizedSet(new HashSet<WebSocketSession>());
        }else{
            return sessions.get(channelId);
        }
    }
}
