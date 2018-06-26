package com.chat.toktalk.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserForm {
    @NotBlank
    String email;
    @NotBlank
    String name;
    @NotBlank
    String password;
}
