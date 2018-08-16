package com.chat.toktalk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsForm implements Serializable {
    @NotEmpty
    String nickname;
    @NotEmpty
    String email;

}
