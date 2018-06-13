package com.chat.toktalk.domain;

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
    private User user;

    @ManyToOne(targetEntity = Channel.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "last_read_id") // 마지막으로 읽은 메세지 id
    private Long lastReadId;

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
