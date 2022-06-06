package com.dingdongdeng.coinautotrading.common.slack.config;

import com.dingdongdeng.coinautotrading.common.client.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Configuration
public class WebclientConfig extends WebClientConfig {

  @Bean
  public WebClient slackWebClient() {
    return makeWebClient(null, 5000, 5000);
  }
}
