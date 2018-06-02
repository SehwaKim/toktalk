package com.chat.toktalk.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원, Admin
    @Column(name = "role_name")
    private String roleName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    // 헬퍼
    void setUser(User user){
        this.user = user;
        if(!user.getRoles().contains(this)){
            user.getRoles().add(this);
        }
    }
}
