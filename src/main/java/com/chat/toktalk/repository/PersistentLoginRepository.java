package com.chat.toktalk.repository;

import com.chat.toktalk.domain.PersistentLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistentTokenRepository extends JpaRepository<PersistentLogin,String> {

    int deleteByUsername(String username);

}
