package com.chat.toktalk.security;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class TokTalkUserDetailsService implements UserDetailsService {
    @Autowired
    UserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(username);

        if(user == null){
            throw new UsernameNotFoundException(username + " not found");
        }

        List<GrantedAuthority> list = new ArrayList<>();
        user.getRoles().forEach(role -> list.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName())));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), list);
    }
}
