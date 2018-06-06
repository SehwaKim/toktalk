package com.chat.toktalk.amqp;

import com.chat.toktalk.config.RabbitConfig;
import com.chat.toktalk.dto.ChatMessage;
import com.chat.toktalk.websocket.SessionManager;
import org.hibernate.Session;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Component
public class MessageListener{

  @Autowired
  SessionManager sessionManager;
  @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
  public void recieveAndBroadCastingMessage(ChatMessage chatMessage){
    Set<WebSocketSession> sessions = sessionManager.getWebSocketSessions(chatMessage.getChannelId());
    if(sessions != null){
      sessions.stream().forEach(session->{
        try {
          session.sendMessage(new TextMessage(chatMessage.getMessage()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
  }
}
