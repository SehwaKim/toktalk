package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/users", produces = "application/json; charset=utf8")
public class UserApiController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<User> user(LoginUserInfo loginUserInfo) {
        if (loginUserInfo != null) {
            User user = userService.findUserByEmail(loginUserInfo.getEmail());
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
