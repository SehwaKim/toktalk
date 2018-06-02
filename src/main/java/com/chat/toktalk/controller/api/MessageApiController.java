package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageApiController {
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public MessageApiController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public String sendMessage(Message message){
        rabbitTemplate.convertAndSend(message);

        return "";
    }

}
