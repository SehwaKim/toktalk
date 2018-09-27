package com.chat.toktalk.repository;

import com.chat.toktalk.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUsersByEmail(String email);

    User findUserByEmail(String email);

    @Query("SELECT oauth.user FROM OauthInfo oauth WHERE oauth.email = :email")
    User findOauthUserByEmail(@Param("email") String email);

    @Query("select u from User u where u.nickname = :nickname")
    User findUserByNickname(@Param("nickname") String nickname);
}
