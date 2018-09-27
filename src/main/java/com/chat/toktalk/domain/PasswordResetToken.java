package com.chat.toktalk.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    Long tokenId;

    String token;

    @Column(name = "expiry_date")
    Date expiryDate;

    @OneToOne(targetEntity = User.class,cascade={CascadeType.ALL})
    @JoinColumn(name = "user_id")
    User user;


    public void setExpiryDate(int minutes){
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE,minutes);
        this.expiryDate = now.getTime();
    }

    public boolean isExpired(){
        return new Date().after(this.expiryDate);
    }


}
