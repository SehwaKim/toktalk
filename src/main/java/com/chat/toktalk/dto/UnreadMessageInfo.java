package com.chat.toktalk.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnreadMessageInfo implements Serializable {
    private Long channelId;
    private Long unreadCnt;

    public UnreadMessageInfo(Long channelId, Long unreadCnt) {
        this.channelId = channelId;
        this.unreadCnt = unreadCnt;
    }
}
