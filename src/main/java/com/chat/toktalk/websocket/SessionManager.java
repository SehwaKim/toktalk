package com.chat.toktalk.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Component
public class SessionManager {

    private Map<Long, WebSocketSession> sessions = new HashMap<>();//유저아이디(key),세션들(value)
    /*
    *  1. 웹소켓 연결 open 할 때
    *  2. 한 채널 입장 시
    * */
    public void addWebSocketSession(Long userId,WebSocketSession session){
//        Set<WebSocketSession> sessionContainer = getWebSocketSessionContainer(channelId);
//        sessionContainer.add(session);
        sessions.put(userId,session);
    }

    /*
     *  1. 웹소켓 연결 close 할 때
     *  2. 한 채널 퇴장 시
     * */
    // userId(Key)로 세션 지우기
    public void removeWebSocketSession(Long userId){
         // 방어적 코드 세션값이 없을때 고려ㅣ..
        sessions.remove(userId);
    }

    // userId(Key)로 WebsocketSession 꺼내기
    public Map<Long,WebSocketSession> getWebSocketSessions(Long channelId){
        return sessions;
    }


}
