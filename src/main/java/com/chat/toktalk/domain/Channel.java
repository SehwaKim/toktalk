package com.chat.toktalk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "channel")
@JsonIgnoreProperties(value={"hibernateLazyInitializer", "handler"})
@Getter
@Setter
public class Channel implements Serializable {
    public Channel() {
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private String data;

    @Enumerated(value = EnumType.STRING)
    private ChannelType type;

    @Column(name = "first_user_id")
    private Long firstUserId;

    @Column(name = "second_user_id")
    private Long secondUserId;

    @Column(name = "first_user_name")
    private String firstUserName;

    @Column(name = "second_user_name")
    private String secondUserName;

    @JsonBackReference
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChannelUser> channelUsers = new ArrayList<>();

    @Column(name = "self_conversation")
    private Boolean selfConversation;

    private Boolean freeze;
    private LocalDateTime regdate;

    public void addChannelUser(ChannelUser channelUser) {
        channelUsers.add(channelUser);
        if(channelUser.getChannel() != this){
            channelUser.setChannel(this);
        }
    }
}
