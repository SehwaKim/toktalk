package com.chat.toktalk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "channel")
@JsonIgnoreProperties(value={"hibernateLazyInitializer", "handler"})
@Data
public class Channel implements Serializable {
    public Channel() {
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    // private UploadFile uploadFile; TODO 1:1임
    private String data; // 메타데이터
    private String type;

    @JsonBackReference
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChannelUser> channelUsers = new ArrayList<>();

    private Boolean freeze;
    private LocalDateTime regdate;

    public void addChanneUser(ChannelUser channelUser){
        channelUsers.add(channelUser);
        if(channelUser.getChannel() != this){
            channelUser.setChannel(this);
        }
    }
}
