package com.chat.toktalk.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "user")
@Entity
public class User implements Serializable {
    public User() {
        this.regdate = LocalDateTime.now();
    }

    @Builder
    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String nickname;
    // private UploadFile uploadFile; TODO 1:1 인데 어떻게 하지
    private LocalDateTime regdate;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Column(name = "user_status")
    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus; //탈퇴 or 활성

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChannelUser> channelUsers = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Role> roles = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OauthInfo> oauthInfos = new ArrayList<>();

    public void addChanneUser(ChannelUser channelUser){
        channelUsers.add(channelUser);
        if(channelUser.getUser() != this){
            channelUser.setUser(this);
        }
    }

    public void addUserRole(Role role){
        this.roles.add(role);
        if(role.getUser()!=this){
            role.setUser(this);
        }
    }

    public void addUserOauthInfo(OauthInfo oauthInfo){
        oauthInfos.add(oauthInfo);
        if(oauthInfo.getUser() != this){
            oauthInfo.setUser(this);
        }
    }


}
