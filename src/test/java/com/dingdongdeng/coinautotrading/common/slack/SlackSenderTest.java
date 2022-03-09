package com.dingdongdeng.coinautotrading.common.slack;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class SlackSenderTest {

    @Autowired
    private SlackSender slackSender;

    @Test
    public void 슬랙_발송_테스트() {
        slackSender.send(new RuntimeException("슬랙 발송 테스트"));
    }
}