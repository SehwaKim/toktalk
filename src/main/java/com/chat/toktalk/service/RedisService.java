package com.chat.toktalk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    @Autowired
    RedisTemplate redisTemplate;

    HashOperations<String, Object, Object> hashMap;

    public RedisService(){
//        hashMap = redisTemplate.opsForHash().keys();
    }

    public void saveRoom(){
        hashMap.put("romm..", "...", "...");
    }
}
