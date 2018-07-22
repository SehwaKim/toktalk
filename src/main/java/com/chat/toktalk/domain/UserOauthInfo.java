package com.chat.toktalk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_oauth_info")
public class UserOauthInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String accessToken;
    String sub;
    String name;
    String picture;
    String email;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_id")
    User user;

    @Builder
    public UserOauthInfo(String sub, String name, String picture, String email) {
        this.sub = sub;
        this.name = name;
        this.picture = picture;
        this.email = email;
    }

    public void setUser(User user){
        this.user = user;
        if(!user.getOauthInfos().contains(this)){
            user.getOauthInfos().add(this);
        }
    }
}
