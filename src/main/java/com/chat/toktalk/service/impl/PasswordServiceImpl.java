package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.PasswordResetToken;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.repository.PasswordResetTokenRepository;
import com.chat.toktalk.repository.UserRepository;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.PasswordService;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordServiceImpl implements PasswordService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void savePasswordResetToken(PasswordResetToken passwordResetToken){
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deletepasswordResetToken(PasswordResetToken passwordResetToken){
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Override
    @Transactional
    public void savePassword(LoginUserInfo loginUserInfo, String password){
        User user = userRepository.findUserByEmail(loginUserInfo.getEmail());
        user.setPassword(passwordEncoder.encode(password));
    }
}
