package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.ChannelForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.*;
import com.chat.toktalk.config.CustomWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    CustomWebSocketHandler customWebSocketHandler;

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

            channelService.addChannel(channel);
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

        String sessionId = customWebSocketHandler.getWebSocketSession().getId();
        redisService.addActiveChannelInfo(sessionId, channelId);


        // 이 user 가 보고있는 채널이 이 채널이라는걸 Redis 에 공유시켜야 한다
        // key - userId
        // value - hash 로 channelId 들을 저장
        // 이미 value 로 channelId 존재하면 다른 기기에서 보고 있다는 거니까 skip
        // 음 그런데 .. 브라우저도 폰도 5번 채널을 보고있었는데 redis 에는 값이 하나만 올라가있다면
        // 내가 브라우저에서 4번 채널로 돌렸을 때 5를 삭제하고 4를 추가하면
        // 폰은 여전히 5번을 보고있어도 레디스상에서 정보가 삭제되버리니까
        // 웹소켓세션별로 저장을 해야겠네
        // *****공유되어야할 것은 이 user 가 보고 있는 채널들임!!!
        // 중복 저장 yes? no?


        ChannelUser alreadyUser = channelUserService.getChannelUser(channelId, user.getId());

        if(alreadyUser != null){ // 이미 합류한 유저
            // 마지막으로 읽은 메세지 id 갱신한 뒤 메세지 리스트 리턴
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
