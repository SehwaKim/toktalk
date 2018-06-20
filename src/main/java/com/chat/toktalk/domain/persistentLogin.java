package com.chat.toktalk.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "persistent_Login")
public class persistentLogin {
    @Id
    String series;
}
