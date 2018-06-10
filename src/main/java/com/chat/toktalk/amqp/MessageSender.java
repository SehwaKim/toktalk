package com.chat.toktalk.amqp;

import com.chat.toktalk.config.RabbitConfig;
import com.chat.toktalk.dto.SocketMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    @Autowired
    private RabbitTemplate template;

    public void sendMessage(SocketMessage socketMessage){
        template.convertAndSend(RabbitConfig.EXCHANGE_NAME,"NO_ROUTING_KEY", socketMessage);
    }
}
