package com.chat.toktalk.repository;

import com.chat.toktalk.domain.OauthInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OauthRepository extends JpaRepository<OauthInfo,Long> {
    OauthInfo findOauthInfoByEmail(String email);

    @Modifying
    @Query("DELETE FROM OauthInfo oauth WHERE oauth.email = :email")
    int deleteOauthUserByEmail(@Param("email")String email);

}