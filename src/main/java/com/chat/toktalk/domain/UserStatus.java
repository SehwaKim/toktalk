package com.chat.toktalk.domain;

public enum UserStatus {
    NORMAL("normal"),DELETE("delete"),OAUTH("oauth");
    private String attribute;

    UserStatus(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }


    @Override
    public String toString() {
        return attribute;
    }
}
