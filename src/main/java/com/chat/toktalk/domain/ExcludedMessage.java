package com.chat.toktalk.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "excluded_message")
@Data
public class ExcludedMessage implements Serializable {
    public ExcludedMessage() { this.regdate = LocalDateTime.now(); }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id")
    private Long messageId;

    private String type; // 삭제메세지 or 시스템메세지

    @Column(name = "channel_id")
    private Long channelId;

    private LocalDateTime regdate;
}