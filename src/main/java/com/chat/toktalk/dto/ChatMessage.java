package com.chat.toktalk.dto;

import org.springframework.web.socket.WebSocketMessage;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private Long channelId;
    private String nickname;
    private String message;

    public ChatMessage() {
    }

    public ChatMessage(Long channelId, String message, String nickname) {
        this.channelId = channelId;
        this.message = message;
        this.nickname = nickname;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}