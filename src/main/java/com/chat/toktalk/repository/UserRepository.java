package com.chat.toktalk.repository;

import com.chat.toktalk.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findUsersByEmail(String email);
}
