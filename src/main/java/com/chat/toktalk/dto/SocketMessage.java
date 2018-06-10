package com.chat.toktalk.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class SocketMessage implements Serializable {

    private String type;
    private Long channelId;
    private String nickname;
    private String text;
    private Boolean notification;
    private List<UnreadMessageInfo> unreadMessages;

    public SocketMessage() {
    }

    public SocketMessage(List<UnreadMessageInfo> unreadMessages){
        this.type = "unread";
        this.unreadMessages = unreadMessages;
    }

    public SocketMessage(Long channelId, String text, String nickname) {
        this.type = "message";
        this.channelId = channelId;
        this.text = text;
        this.nickname = nickname;
        this.notification = false;
    }

    // TODO 파일정보 보낼때도 type 구분하기
}