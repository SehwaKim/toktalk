package com.chat.toktalk.service;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.User;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.List;

public interface RedisService {
    // TODO 유저리스트 저장
    public void addUser(Long id,WebSocketSession session);

    // TODO 삭제

    // TODO 조회
    public List<User> getUsers(Long id);

    public User getUser(Principal principal);

    public List<Channel> getChannels();
}
