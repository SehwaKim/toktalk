package com.chat.toktalk.service;


import com.chat.toktalk.domain.PasswordResetToken;

public interface PasswordService {

    void savePasswordResetToken(PasswordResetToken passwordResetToken);

    PasswordResetToken findByToken(String token);

    void deletepasswordResetToken(PasswordResetToken passwordResetToken);

}
