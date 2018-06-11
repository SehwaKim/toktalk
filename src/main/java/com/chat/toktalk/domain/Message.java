package com.chat.toktalk.domain;

import lombok.Data;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

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
    @Column(name = "message_id")
    private Long id;
    private String text;
    private String type; // deleted or system - 삭제된메세지 혹은 시스템메세지 구분
    private String data; // 메타데이터
    @Column(name = "user_id")
    private Long userId;
    private String nickname;
    @Column(name = "channel_id")
    private Long channelId;
    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private UploadFile uploadFile;
    private LocalDateTime regdate;
}
