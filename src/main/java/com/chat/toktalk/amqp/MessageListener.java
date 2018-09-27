package com.chat.toktalk.amqp;

import com.chat.toktalk.config.RabbitConfig;
import com.chat.toktalk.dto.SendType;
import com.chat.toktalk.dto.SocketMessage;
import com.chat.toktalk.service.RedisService;
import com.chat.toktalk.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;

@Component
public class MessageListener{
    @Autowired
    SessionManager sessionManager;

    @Autowired
    RedisService redisService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveAndBroadcastMessage(SocketMessage socketMessage){

        if (SendType.CHANNEL_MARK == socketMessage.getType() || SendType.CHANNEL_JOINED == socketMessage.getType()) {
            toIndividual(socketMessage);
            return;
        }

        Long channelId = socketMessage.getChannelId();
        Map<Long, Set<WebSocketSession>> sessions = sessionManager.getWebSocketSessionsByChannelId(channelId);

        if(sessions != null){
            if (SendType.TYPING == socketMessage.getType()) {
                sessions.remove(socketMessage.getUserId());
            }

            for(Long userId : sessions.keySet()){
                if (SendType.CHAT == socketMessage.getType() || SendType.UPLOAD_FILE == socketMessage.getType()) {
                    Boolean isChannelInSight = redisService.isChannelInSight(userId, channelId);

                    if (isChannelInSight) {
                        socketMessage.setNotification(false);
                    }else {
                        socketMessage.setNotification(true);
                    }
                }

                sessions.get(userId).stream().forEach(session -> {
                    try {
                        String jsonStr = new ObjectMapper().writeValueAsString(socketMessage);
                        session.sendMessage(new TextMessage(jsonStr));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void toIndividual(SocketMessage socketMessage) {
        Set<WebSocketSession> sessions = sessionManager.getWebSocketSessionsByUser(socketMessage.getUserId());
        if(sessions != null) {
            sessions.forEach(session -> {
                try {
                    String jsonStr = new ObjectMapper().writeValueAsString(socketMessage);
                    session.sendMessage(new TextMessage(jsonStr));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
