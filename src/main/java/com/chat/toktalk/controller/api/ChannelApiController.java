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
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/channels")
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

    @PostMapping(value = "/direct")
    public ResponseEntity<Channel> addDirectChannel(@RequestBody Map<String, String> params, LoginUserInfo loginUserInfo) {
        Long userId = loginUserInfo.getId();
        Long partnerId = getUserByEmail(params.get("partnerEmail")).getId();

        Channel direct = channelService.getDirectChannel(userId, partnerId);

        if (Objects.nonNull(direct)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        ChannelUser firstChannelUser = createChannelUser(getUserByEmail(loginUserInfo.getEmail()));
        ChannelUser secondChannelUser = createChannelUser(getUserByEmail(params.get("partnerEmail")));

        Channel newDirectChannel = new Channel();
        newDirectChannel.setType(ChannelType.DIRECT);
        newDirectChannel.setFirstUserId(userId);
        newDirectChannel.setSecondUserId(partnerId);
        newDirectChannel.addChannelUser(firstChannelUser);
        newDirectChannel.addChannelUser(secondChannelUser);
        channelService.addChannel(newDirectChannel);

        return new ResponseEntity<>(newDirectChannel, HttpStatus.CREATED);
    }

    private ChannelUser createChannelUser(User user) {
        ChannelUser channelUser = new ChannelUser();
        channelUser.setUser(user);
        return channelUser;
    }

    private User getUserByEmail(String email) {
        return userService.findUserByEmail(email);
    }

    @GetMapping(value = "/{channelId}")
    public Boolean isJoiningNewChannel(LoginUserInfo loginUserInfo, @PathVariable(value = "channelId") Long channelId){
        Long userId = null;
        if(loginUserInfo != null) {
            userId = loginUserInfo.getId();
        }

        ChannelUser channelUser = channelUserService.getChannelUser(channelId, userId);

        if(channelUser != null){
            return false;
        }

        return true;
    }
}
