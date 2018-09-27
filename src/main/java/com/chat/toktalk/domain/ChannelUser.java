package com.chat.toktalk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "channel_user")
@Getter
@Setter
public class ChannelUser implements Serializable {
    public ChannelUser() {
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(targetEntity = Channel.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    @JsonBackReference
    private Channel channel;

    @Column(name = "first_read_id") // 처음으로 읽은 메세지 id
    private Long firstReadId;

    @Column(name = "last_read_cnt") // 마지막으로 읽은 메세지 갯수
    private Long lastReadCnt;

    @Column(name = "is_operator")
    private Boolean isOperator;
    private LocalDateTime regdate;

    public void setUser(User user){
        this.user = user;
        if(!user.getChannelUsers().contains(this)){
            user.getChannelUsers().add(this);
        }
    }
    public void setChannel(Channel channel){
        this.channel = channel;
        if(!channel.getChannelUsers().contains(this)){
            channel.getChannelUsers().add(this);
        }
    }
}
