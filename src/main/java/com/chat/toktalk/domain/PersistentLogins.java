package com.chat.toktalk.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "persistent_logins")
public class PersistentLogins {

    private String username;
    @Id
    private String series;
    private String token;
    @Column(name="last_used")
    private Date lastUsed;
}
