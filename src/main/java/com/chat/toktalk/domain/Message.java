package com.chat.toktalk.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message implements Serializable {
    public Message() {
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String type; // deleted or system - 삭제된메세지 혹은 시스템메세지 구분
    private String data;

    @Column(name = "user_id")
    private Long userId;
    private String nickname;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "channel_type")
    @Enumerated(value = EnumType.STRING)
    private ChannelType channelType;

    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private UploadFile uploadFile;

    @JsonFormat(pattern = "a h:mm")
    private LocalDateTime regdate;
}
