package com.dingdongdeng.coinautotrading.exchange.client;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.MarketCodeRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderChanceRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.OrderResponse;
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
        MarketCodeRequest request = MarketCodeRequest.builder()
            .isDetail(true)
            .build();
        log.info("result : {}", upbitClient.getMarketList(request));
    }

    @Test
    public void 주문_가능_정보_조회_테스트() {
        OrderChanceRequest request = OrderChanceRequest.builder()
            .market("KRW-ETH")
            .build();
        log.info("result : {}", upbitClient.getOrdersChance(request));
    }

    @Test
    public void 지정가_주문_매수_테스트() {
        OrderRequest request = OrderRequest.builder()
            .market("KRW-ETH")
            .side(Side.bid)
            .volume(1.0)
            .price(5000.0)
            .ordType(OrdType.limit)
            .build();

        log.info("result : {}", upbitClient.order(request));
    }

    @Test
    public void 지정가_주문_매도_테스트() {
        OrderRequest request = OrderRequest.builder()
            .market("KRW-ETH")
            .side(Side.ask)
            .volume(1.0)
            .price(5000.0)
            .ordType(OrdType.limit)
            .build();

        log.info("result : {}", upbitClient.order(request));
    }

    @Test
    public void 주문_이후_취소_테스트() {
        OrderRequest orderRequest = OrderRequest.builder()
            .market("KRW-ETH")
            .side(Side.bid)
            .volume(1.0)
            .price(5000.0)
            .ordType(OrdType.limit)
            .build();

        OrderResponse orderResponse = upbitClient.order(orderRequest);
        log.info("result orderResponse: {}", orderResponse);

        OrderCancelRequest orderCancelRequest = OrderCancelRequest.builder()
            .uuid(orderResponse.getUuid())
            .build();
        log.info("result orderCancelResponse: {}", upbitClient.orderCancel(orderCancelRequest));
    }


}