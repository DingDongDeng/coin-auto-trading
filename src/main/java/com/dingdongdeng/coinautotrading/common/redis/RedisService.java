package com.dingdongdeng.coinautotrading.common.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final int DEFAULT_EXPIRE_DAYS = 3;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, DEFAULT_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    public String get(String key) {
        return (String) (redisTemplate.opsForValue().get(key));
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

}
