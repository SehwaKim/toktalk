package com.chat.toktalk.controller.api;

import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.ChannelUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/channelUsers")
public class ChannelUserApiController {

    @Autowired
    private ChannelUserService channelUserService;

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<String> deleteChannelUser(@PathVariable Long userId, @RequestBody Map<String, Long> params, LoginUserInfo loginUserInfo) {
        if (loginUserInfo != null) {
            if (loginUserInfo.getId().equals(userId)) {
                channelUserService.removeChannelUser(userId, params.get("channelId"));
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
