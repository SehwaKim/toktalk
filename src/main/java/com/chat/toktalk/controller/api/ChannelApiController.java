package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.service.ChannelService;
import com.chat.toktalk.service.ChannelUserService;
import com.chat.toktalk.service.MessageService;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.config.CustomWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    CustomWebSocketHandler customWebSocketHandler;

    @GetMapping(path = "/{channelId}")
    public ResponseEntity<List<Message>> userEnter(Principal principal, @PathVariable(value = "channelId") Long channelId){
        User user = null;

        if(principal != null){
            user = userService.getUserByEmail(principal.getName());
        }

        ChannelUser alreadyUser = channelUserService.getChannelUser(channelId, user.getId());

        if(alreadyUser != null){
            System.out.println("이미 들어온 유저");
            return new ResponseEntity<>(messageService.getMessagesByChannelId(channelId), HttpStatus.OK);

        }else{
            ChannelUser channelUser = new ChannelUser();
            channelUser.setUser(user);
            channelUser.setChannel(channelService.getChannel(channelId));
            channelUserService.addChannelUser(channelUser);
        }

        return null;
    }

    @PostMapping(path = "/{channelId}/messages")
    public void sendMessages(Principal principal, @PathVariable(value = "channelId") Long channelId, @RequestBody Message message) throws Exception{
        User user = null;
        System.out.println(message);
        if(principal != null){
            user = userService.getUserByEmail(principal.getName());
        }
        message.setUserId(user.getId());
        message.setNickname(user.getNickname());
        Message savedMessage = messageService.addMessage(message);
        customWebSocketHandler.broadcast(savedMessage);
    }
}
