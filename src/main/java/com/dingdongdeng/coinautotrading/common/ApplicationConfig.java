package com.dingdongdeng.coinautotrading.common;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableConfigurationProperties
@EnableAsync
@Configuration
public class ApplicationConfig {}
