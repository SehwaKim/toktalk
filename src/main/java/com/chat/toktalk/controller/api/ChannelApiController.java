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

@RestController
@RequestMapping(value = "/api/channels", produces = "application/json; charset=utf8")
public class ChannelApiController {
    @Autowired
    ChannelService channelService;

    @Autowired
    UserService userService;

    @Autowired
    ChannelUserService channelUserService;

    @Autowired
    RedisService redisService;

    @GetMapping
    public ResponseEntity<List<Channel>> channels(LoginUserInfo loginUserInfo) {
        if (loginUserInfo != null) {
            List<Channel> channels = channelService.getChannelsByUser(loginUserInfo.getId());
            return new ResponseEntity<>(channels, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Channel> addChannel(@RequestBody ChannelForm channelForm, LoginUserInfo loginUserInfo){
        if(loginUserInfo != null){
            User user = userService.findUserByEmail(loginUserInfo.getUsername());

            ChannelUser channelCreator = new ChannelUser();
            channelCreator.setUser(user);
            channelCreator.setIsOperator(true);

            Channel channel = new Channel();
            channel.addChannelUser(channelCreator);
            channel.setName(channelForm.getName());
            if ("direct".equals(channelForm.getType())) {
                channel.setType(ChannelType.DIRECT);
            } else {
                channel.setType(ChannelType.PUBLIC);
            }

            channel = channelService.addChannel(channel);
            redisService.createMessageIdCounter(channel.getId());

            return new ResponseEntity<>(channel, HttpStatus.OK);
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
