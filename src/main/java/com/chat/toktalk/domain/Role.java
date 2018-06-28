package com.chat.toktalk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
public class UserRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원, Admin
    @Column(name = "role_name")
    private String roleName;

    @JsonBackReference
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
