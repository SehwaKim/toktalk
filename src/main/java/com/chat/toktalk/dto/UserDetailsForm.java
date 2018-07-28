package com.chat.toktalk.dto;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class UserDetails implements Serializable {
    @NotEmpty
    String nickname;
    String email;
}
