package com.dingdongdeng.coinautotrading.upbit.client;

//import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UpbitClientTest {

    @Autowired
    private UpbitClient upbitClient;

    @Test
    public void 전체_계좌_조회_테스트() {
        log.info("result : {}", upbitClient.getAccounts());
    }

    @Test
    public void 마켓_정보_조회_테스트() {
        log.info("result : {}", upbitClient.getMarketList(true));
    }

    @Test
    public void 주문_가능_정보_조회_테스트() {
        log.info("result : {}", upbitClient.getOrdersChance("KRW-ETH"));
    }

}