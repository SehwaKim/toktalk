package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelType;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.ChannelService;
import com.chat.toktalk.service.ChannelUserService;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/channelUsers")
public class ChannelUserApiController {
    @Autowired
    private ChannelUserService channelUserService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<String> deleteChannelUser(@PathVariable Long userId, @RequestBody Map<String, Long> params, LoginUserInfo loginUserInfo) {
        if (loginUserInfo != null) {
            if (loginUserInfo.getId().equals(userId)) {
                channelUserService.removeChannelUser(userId, params.get("channelId"));
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/direct")
    public ResponseEntity<Channel> createDirectChannel(@RequestBody Map<String, String> params, LoginUserInfo loginUserInfo) {
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
}
