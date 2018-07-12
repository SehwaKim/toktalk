package com.chat.toktalk.repository;

import com.chat.toktalk.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {

    PasswordResetToken findByToken(String token);

}
