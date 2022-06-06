package com.dingdongdeng.coinautotrading.common.redis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Slf4j
@Configuration
public class EmbeddedRedisConfig {

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private int port;

  private RedisServer redisServer;

  public EmbeddedRedisConfig(
      @Value("${spring.redis.port}") int port, @Value("${spring.redis.host}") String host) {
    this.host = host;
    this.port = port;
    this.redisServer = new RedisServer(port);
  }

  @PostConstruct
  public void postConstruct() {
    try {
      redisServer.stop();
      redisServer.start();
    } catch (Exception ige) {
      log.error(ige.getMessage());
    }
  }

  @PreDestroy
  public void preDestroy() {
    redisServer.stop();
  }
}
