package com.dingdongdeng.coinautotrading.trading.exchange.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "upbit.client")
@Configuration
public class UpbitClientResourceProperties {

  private String accessKey;
  private String secretKey;

  private String baseUrl;
  private int readTimeout;
  private int connectionTimeout;
}
