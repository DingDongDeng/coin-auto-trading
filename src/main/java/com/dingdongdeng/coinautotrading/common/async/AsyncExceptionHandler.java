package com.dingdongdeng.coinautotrading.common.async;

import com.dingdongdeng.coinautotrading.common.slack.SlackSender;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final SlackSender slackSender;

    @Override
    public void handleUncaughtException(Throwable e, Method method, Object... params) {
        slackSender.send(e);
        log.error(e.getMessage(), e);
    }
}