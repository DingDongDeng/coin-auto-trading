package com.dingdongdeng.coinautotrading.exchange.client;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.State;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.MarketCodeRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderChanceRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderInfoListRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderInfoRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.CandleResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.MarketCodeResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.OrderResponse;
import java.time.LocalDateTime;
import java.util.List;
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
        List<AccountsResponse> response = upbitClient.getAccounts();
        log.info("result : {}", response);
        AccountsResponse element = response.stream().findFirst().orElse(null);
        log.info("result of first element : {}", element);
    }

    @Test
    public void 마켓_정보_조회_테스트() {
        MarketCodeRequest request = MarketCodeRequest.builder()
            .isDetail(true)
            .build();
        List<MarketCodeResponse> response = upbitClient.getMarketList(request);
        log.info("result : {}", response);
        MarketCodeResponse element = response.stream().findFirst().orElse(null);
        log.info("result of first element : {}", element);
    }

    @Test
    public void 주문_가능_정보_조회_테스트() {
        OrderChanceRequest request = OrderChanceRequest.builder()
            .market("KRW-ETH")
            .build();
        log.info("result : {}", upbitClient.getOrdersChance(request));
    }

    @Test
    public void 주문과_조회와_취소_테스트() {

        // 주문
        OrderRequest orderRequest = OrderRequest.builder()
            .market("KRW-ETH")
            .side(Side.bid)
            .volume(1.0)
            .price(5000.0)
            .ordType(OrdType.limit)
            .build();
        OrderResponse orderResponse = upbitClient.order(orderRequest);
        log.info("result orderResponse: {}", orderResponse);

        // 조회
        OrderInfoRequest orderInfoRequest = OrderInfoRequest.builder()
            .uuid(orderResponse.getUuid())
            .build();
        OrderResponse orderInfoResponse = upbitClient.getOrderInfo(orderInfoRequest);
        log.info("result orderInfoResponse: {}", orderInfoResponse);

        // 리스트 조회
        OrderInfoListRequest orderInfoListRequest = OrderInfoListRequest.builder()
            .market("KRW-ETH")
            .uuidList(List.of(orderInfoResponse.getUuid()))
            .identifierList(null)
            .state(null)
            .stateList(List.of(State.wait, State.watch))
            .page(1)
            .limit(100)
            .orderBy("desc")
            .build();
        List<OrderResponse> orderInfoListResponse = upbitClient.getOrderInfoList(orderInfoListRequest);
        log.info("result orderInfoListResponse: {}", orderInfoListResponse);

        // 취소
        OrderCancelRequest orderCancelRequest = OrderCancelRequest.builder()
            .uuid(orderResponse.getUuid())
            .build();
        log.info("result orderCancelResponse: {}", upbitClient.orderCancel(orderCancelRequest));
    }

    @Test
    public void 캔들_조회_테스트() {
        CandleRequest request = CandleRequest.builder()
            .unit(15)
            .market("KRW-ETH")
            .to(LocalDateTime.now())
            .count(200)
            .build();

        List<CandleResponse> response = upbitClient.getCandle(request);
        log.info("result : {}", response);
    }

    //@Test
    //public void 지정가_주문_매수_테스트() {
    //    OrderRequest request = OrderRequest.builder()
    //        .market("KRW-ETH")
    //        .side(Side.bid)
    //        .volume(1.0)
    //        .price(5000.0)
    //        .ordType(OrdType.limit)
    //        .build();
    //
    //    log.info("result : {}", upbitClient.order(request));
    //}
    //
    //@Test
    //public void 지정가_주문_매도_테스트() {
    //    OrderRequest request = OrderRequest.builder()
    //        .market("KRW-ETH")
    //        .side(Side.ask)
    //        .volume(1.0)
    //        .price(5000.0)
    //        .ordType(OrdType.limit)
    //        .build();
    //
    //    log.info("result : {}", upbitClient.order(request));
    //}
}