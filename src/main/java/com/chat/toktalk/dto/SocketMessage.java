package com.chat.toktalk.dto;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.domain.UploadFile;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@ToString
public class SocketMessage implements Serializable {
    private SendType type;
    private Long channelId;
    private Long userId;
    private String nickname;
    private String text;
    private Boolean notification;
    private List<Message> messages;
    private List<UnreadMessageInfo> unreadMessages;
    private UploadFile uploadFile;
    private Channel channel;
    private LocalDateTime date;
    private String regdate;

    public SocketMessage() {
    }

    public SocketMessage(SendType type) {
        this.type = type;
        date = LocalDateTime.now();
        regdate = date.format(DateTimeFormatter.ofPattern("a h:mm"));
    }

 /*
    public SocketMessage(Long channelId,String nickname, UploadFile uploadFile){
        this.type = "upload_file";
        this.channelId = channelId;
        this.nickname = nickname;
        this.uploadFile = uploadFile;
        this.notification = false;
    }
*/
}