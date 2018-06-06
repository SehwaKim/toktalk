package com.chat.toktalk.dto;

import org.springframework.web.socket.WebSocketMessage;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private Long channelId;
    private String message;

    public ChatMessage() {
    }

    public ChatMessage(Long channelId, String message) {
        this.channelId = channelId;
        this.message = message;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public WebSocketMessage<?> getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
