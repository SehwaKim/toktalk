package com.chat.toktalk.dto;

import lombok.Data;

@Data
public class ChannelForm {
    private String name;
    private String type;
    private String purpose;
    private Long invite;
}
