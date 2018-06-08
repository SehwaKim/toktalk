package com.chat.toktalk.dto;

import lombok.ToString;
import org.springframework.web.socket.WebSocketMessage;

import java.io.Serializable;

@ToString
public class ChatMessage implements Serializable {

    private Long channelId;
    private String nickname;
    private String text;

    public ChatMessage() {
    }

    public ChatMessage(Long channelId, String text, String nickname) {
        this.channelId = channelId;
        this.text = text;
        this.nickname = nickname;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}