package com.chat.toktalk.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "persistent_Login")
public class PersistentLogin {
    @Id
    private String series;
    private String username;
    private String token;
    @Column(name="last_used")
    private Date lastUsed;
}
