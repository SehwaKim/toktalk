package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.*;
import com.chat.toktalk.dto.ChannelForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.ChannelService;
import com.chat.toktalk.service.ChannelUserService;
import com.chat.toktalk.service.RedisService;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/channels")
public class ChannelApiController {
    @Autowired
    ChannelService channelService;

    @Autowired
    UserService userService;

    @Autowired
    ChannelUserService channelUserService;

    @Autowired
    RedisService redisService;

    /* 새 채널 생성 */
    @PostMapping
    public Set<Channel> addChannel(@RequestBody ChannelForm channelForm, LoginUserInfo loginUserInfo){
        if(loginUserInfo != null){
            User user = userService.getUserByEmail(loginUserInfo.getUsername());

            ChannelUser channelUser = new ChannelUser();
            channelUser.setUser(user);
            channelUser.setIsOperator(true);

            Channel channel = new Channel();
            channel.addChanneUser(channelUser);
            channel.setName(channelForm.getName());
            channel.setType("public");

            channel = channelService.addChannel(channel);
            redisService.createMessageIdCounter(channel.getId());

            return channelService.getChannelsByUser(loginUserInfo.getId());
        }

        return null;
    }

    @GetMapping(value = "/{channelId}")
    public Boolean isJoiningNewChannel(LoginUserInfo loginUserInfo, @PathVariable(value = "channelId") Long channelId){
        Long userId = null;
        if(loginUserInfo != null) {
            userId = loginUserInfo.getId();
        }

        ChannelUser channelUser= channelUserService.getChannelUser(channelId, userId);

        if(channelUser != null){
            return false;
        }

        return true;
    }
}
