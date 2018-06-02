package com.chat.toktalk.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
public class Message implements Serializable {
    public Message() {
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String type;
    private String data; // 메타데이터
    @Column(name = "user_id")
    private Long userId;
    private String nickname;
    @Column(name = "channel_id")
    private Long channelId;
    // private UploadFile uploadFile; TODO 1:1
    private LocalDateTime regdate;
}
