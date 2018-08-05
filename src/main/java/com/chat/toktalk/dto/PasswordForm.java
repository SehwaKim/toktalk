package com.chat.toktalk.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
public class PasswordForm implements Serializable{
    @NotEmpty
    private String password;
    @NotEmpty
    private String confirmPassword;

    private String token;
}
