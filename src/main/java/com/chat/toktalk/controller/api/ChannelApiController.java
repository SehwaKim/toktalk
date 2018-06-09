package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.ChannelForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.*;
import com.chat.toktalk.config.CustomWebSocketHandler;
import com.chat.toktalk.websocket.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelApiController {
    @Autowired
    ChannelService channelService;

    @Autowired
    ChannelUserService channelUserService;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    RedisService redisService;

    @Autowired
    SessionManager sessionManager;

    /* 새 채널 생성 */
    @PostMapping
    public List<Channel> addChannel(@RequestBody ChannelForm channelForm){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof LoginUserInfo){
            LoginUserInfo loginUserInfo = (LoginUserInfo) authentication.getPrincipal();
            User user = userService.getUserByEmail(loginUserInfo.getUsername());

            ChannelUser channelUser = new ChannelUser();
            channelUser.setUser(user);
            channelUser.setIsOperator(true);

            Channel channel = new Channel();
            channel.addChanneUser(channelUser);
            channel.setName(channelForm.getName());

            channel = channelService.addChannel(channel);
            redisService.createMessageIdCounter(channel.getId());

            return channelService.getChannels(loginUserInfo.getId());
        }

        return null;
    }

    /* 채널 입장 */
    @GetMapping(path = "/{channelId}")
    public ResponseEntity<List<Message>> enterChannel(Principal principal, @PathVariable(value = "channelId") Long channelId){
        User user = null;

        if(principal != null){
            user = userService.getUserByEmail(principal.getName());
        }

        /* 마지막으로 보고 있던 채널의 lastReadId 를 업데이트 */
        WebSocketSession webSocketSession = sessionManager.getWebSocketSession(user.getId());
        Long beforeId = redisService.getActiveChannelInfo(webSocketSession);
        if(beforeId != null){
            ChannelUser alreadyUser = channelUserService.getChannelUser(channelId, user.getId());
            alreadyUser.setLastReadId(redisService.getLastMessageIdByChannel(channelId));
            channelUserService.updateChannelUser(alreadyUser);
        }

        /* active channel 등록 */
        String sessionId = webSocketSession.getId();
        redisService.addActiveChannelInfo(sessionId, channelId);

        ChannelUser alreadyUser = channelUserService.getChannelUser(channelId, user.getId());

        if(alreadyUser != null){ // 이미 합류한 유저
            // 1. 마지막으로 읽은 메세지 id 업데이트
            // 2. 메세지 리스트 리턴
            List<Message> messages = messageService.getMessagesByChannelId(channelId);
            if(messages != null && messages.size() > 0){
                alreadyUser.setLastReadId(messages.get(messages.size()-1).getId());
                channelUserService.updateChannelUser(alreadyUser);

                return new ResponseEntity<>(messages, HttpStatus.OK);
            }

        }else{ // 새로 합류한 유저
            ChannelUser channelUser = new ChannelUser();
            channelUser.setUser(user);
            channelUser.setChannel(channelService.getChannel(channelId));
            channelUserService.addChannelUser(channelUser);
            // 시스템 메세지: "nickname 님이 입장하셨습니다."
        }

        return null;
    }
}
