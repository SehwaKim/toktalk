package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.PasswordResetToken;
import com.chat.toktalk.repository.PasswordResetTokenRepository;
import com.chat.toktalk.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

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
}
