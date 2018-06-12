package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.ChannelForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.ChannelService;
import com.chat.toktalk.service.RedisService;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelApiController {
    @Autowired
    ChannelService channelService;

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    /* 새 채널 생성 */
    @PostMapping
    public List<Channel> addChannel(@RequestBody ChannelForm channelForm, LoginUserInfo loginUserInfo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof LoginUserInfo){
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
}
