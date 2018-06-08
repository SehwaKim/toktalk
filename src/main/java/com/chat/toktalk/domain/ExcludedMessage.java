package com.chat.toktalk.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "excluded_message")
@Data
public class ExcludedMessage {
    public ExcludedMessage() { this.regdate = LocalDateTime.now(); }

    @Column(name = "message_id")
    private Long messageId;

    private String type; // 삭제메세지 or 시스템메세지

    @Column(name = "channel_id")
    private Long channelId;

    private LocalDateTime regdate;
}

// 체널의 마지막 글번호 - 마지막읽은 글 - (삭제되거나 시스템메시지 수)
// (삭제되거나 시스템메시지 수) = select count(e) from ExcludedMessage e where e.channelId = :channelId
//

// 1. online 상태 && 다른 채널 보고있는 상태 ----> 새로운 메세지가 올 때마다 뱃지 숫자 증가 (표시만 해주는 거)
// 2. 나갔다가 다시 입장했을 때 안읽은 메세지 표시 ----> (채널의 마지막글번호 - 마지막읽은글번호 - 삭제되거나 시스템메세지수) 를 유저가 속한 채널마다 구해서 뱃지의 숫자로 뿌려주기