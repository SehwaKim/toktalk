package com.chat.toktalk.websocket;

import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.service.ChannelUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Component
public class SessionManager {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ChannelUserService channelUserService;

    // userId, Set<WebSocketSession>
    // 한 명의 유저가 한 WAS 에서 여러 웹소켓세션을 가질 수 있기때문에 세션을 set 으로.
    // WebSocketSession 에 바로 접근할 수 있게 HashSet 으로 집어넣음.
    private Map<Long, Set<WebSocketSession>> sessions = Collections.synchronizedMap(new HashMap<>());

    public void addWebSocketSession(Long userId, WebSocketSession session){
        if(sessions.containsKey(userId)){
            sessions.get(userId).add(session);
        }else{
            Set<WebSocketSession> set = new HashSet<>();
            set.add(session);
            sessions.put(userId, set);
        }
    }

    public void removeWebSocketSession(Long userId, WebSocketSession session){
        if(sessions.get(userId) != null){
            Set<WebSocketSession> set = sessions.get(userId);
            set.remove(session);
            logger.info("세션제거>>>>" + set);
            if(sessions.get(userId).size() == 0){
                sessions.remove(userId);
            }
        }
    }

    public Set<WebSocketSession> getWebSocketSessionsByUser(Long userId){
        return sessions.get(userId);
    }

    /*
    *   MessageListener 에서 웹소켓세션에 접근할 때 호출되는 메소드
    */
    public Map<Long, Set<WebSocketSession>> getWebSocketSessionsByChannelId(Long channelId){
        List<ChannelUser> channelUsers = channelUserService.getChannelUsersByChannelId(channelId);
        // DB에서 채널에 속한 유저리스트를 갖고오고 있는데 이 정보를 그냥 Redis에 올려야 할지
        // (그렇게되면 ChannelUser 가 생성될 때마다 레디스에 올리고 나갈땐 내려야 한다)
        // ex) 레디스에 key - channelId / value - Set<userId> 이렇게 올라가게끔
        Map<Long, Set<WebSocketSession>> targetSessions = new HashMap<>();

        for(ChannelUser channelUser : channelUsers){
            Long userId = channelUser.getUser().getId();
            if(sessions.containsKey(userId)){
                targetSessions.put(userId, sessions.get(userId));
            }
        }

//        유저리스트를 Redis에서 가져온다면 위의 과정이 생략되고 아래와 같은 코드 실행
//        List<Long> userIdList = redisService.getUsersByChannelId(channelId)
//        for(Long userId : userIdList){
//            if(sessions.containsKey(userId)){
//                targetSessions.put(userId, sessions.get(userId));
//        }
        return targetSessions;
    }
}
