package com.dingdongdeng.coinautotrading.common.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class RedisServiceTest {

  @Autowired private RedisService redisService;

  @Test
  public void Redis_조회_생성_삭제_테스트() {
    String key = "test";
    String value = "test string";
    redisService.set(key, value);
    String str = redisService.get(key);
    redisService.delete(key);
    String str2 = redisService.get(key);

    assertEquals(value, str);
    assertEquals(null, str2);
  }
}
