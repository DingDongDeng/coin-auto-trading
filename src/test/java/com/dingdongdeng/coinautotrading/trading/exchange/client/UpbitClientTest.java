package com.dingdongdeng.coinautotrading.trading.exchange.client;

//import static org.junit.jupiter.api.Assertions.*;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.UpbitClient;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.MarketType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.State;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.MarketCodeRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderBookRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderChanceRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderInfoListRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderInfoRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitRequest.TickerRequest;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.CandleResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.MarketCodeResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.OrderBookResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.OrderResponse;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitResponse.TickerResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UpbitClientTest {

    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;
    @Autowired
    private UpbitClient upbitClient;
    @Value("${upbit.client.accessKey}")
    private String accessKey;
    @Value("${upbit.client.secretKey}")
    private String secretKey;

    private String userId = "123456";
    private String keyPairId;

    @BeforeEach
    public void setUp() {
        String keyPairId = UUID.randomUUID().toString();
        exchangeKeyRepository.save(
            ExchangeKey.builder()
                .pairId(keyPairId)
                .coinExchangeType(CoinExchangeType.UPBIT)
                .name("ACCESS_KEY")
                .value(accessKey)
                .userId(userId)
                .build()
        );

        exchangeKeyRepository.save(
            ExchangeKey.builder()
                .pairId(keyPairId)
                .coinExchangeType(CoinExchangeType.UPBIT)
                .name("SECRET_KEY")
                .value(secretKey)
                .userId(userId)
                .build()
        );

        this.keyPairId = keyPairId;

    }

    @Test
    public void 전체_계좌_조회_테스트() {
        List<AccountsResponse> response = upbitClient.getAccounts(keyPairId);
        log.info("result : {}", response);
        AccountsResponse element = response.stream().findFirst().orElse(null);
        log.info("result of first element : {}", element);
    }

    @Test
    public void 마켓_정보_조회_테스트() {
        MarketCodeRequest request = MarketCodeRequest.builder()
            .isDetail(true)
            .build();
        List<MarketCodeResponse> response = upbitClient.getMarketList(request, keyPairId);
        log.info("result : {}", response);
        MarketCodeResponse element = response.stream().findFirst().orElse(null);
        log.info("result of first element : {}", element);
    }

    @Test
    public void 주문_가능_정보_조회_테스트() {
        OrderChanceRequest request = OrderChanceRequest.builder()
            .market(MarketType.of(CoinType.ETHEREUM).getCode())
            .build();
        log.info("result : {}", upbitClient.getOrdersChance(request, keyPairId));
    }

    @Test
    public void 주문_리스트_조회_테스트() {
        // 리스트 조회
        OrderInfoListRequest orderInfoListRequest = OrderInfoListRequest.builder()
            .market("KRW-ETH")
            .identifierList(null)
            .state(null)
            .stateList(List.of(State.wait, State.watch))
            .page(1)
            .limit(100)
            .orderBy("desc")
            .build();
        List<OrderResponse> orderInfoListResponse = upbitClient.getOrderInfoList(orderInfoListRequest, keyPairId);
        log.info("result orderInfoListResponse: {}", orderInfoListResponse);
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
        OrderResponse orderResponse = upbitClient.order(orderRequest, keyPairId);
        log.info("result orderResponse: {}", orderResponse);

        // 조회
        OrderInfoRequest orderInfoRequest = OrderInfoRequest.builder()
            .uuid(orderResponse.getUuid())
            .build();
        OrderResponse orderInfoResponse = upbitClient.getOrderInfo(orderInfoRequest, keyPairId);
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
        List<OrderResponse> orderInfoListResponse = upbitClient.getOrderInfoList(orderInfoListRequest, keyPairId);
        log.info("result orderInfoListResponse: {}", orderInfoListResponse);

        // 취소
        OrderCancelRequest orderCancelRequest = OrderCancelRequest.builder()
            .uuid(orderResponse.getUuid())
            .build();
        log.info("result orderCancelResponse: {}", upbitClient.orderCancel(orderCancelRequest, keyPairId));
    }

    @Test
    public void 분봉_캔들_조회_테스트() {
        LocalDateTime to = LocalDateTime.of(2022, 01, 13, 21, 9, 0);

        CandleRequest request = CandleRequest.builder()
            .unit(1)
            .market("KRW-ETH")
            .toKst(to)
            .count(2)
            .build();

        List<CandleResponse> response = upbitClient.getMinuteCandle(request, keyPairId);
        log.info("result : {}", response);
    }

    @Test
    public void 일봉_캔들_조회_테스트() {
        LocalDateTime to = LocalDateTime.of(2022, 8, 9, 20, 9, 0);

        CandleRequest request = CandleRequest.builder()
            .market("KRW-ETH")
            .toKst(to)
            .count(2)
            .build();

        List<CandleResponse> response = upbitClient.getDayCandle(request, keyPairId);
        log.info("result : {}", response);
    }

    @Test
    public void 주문_호가_조회_테스트() {
        List<OrderBookResponse> response = upbitClient.getOrderBook(
            OrderBookRequest.builder()
                .marketList(List.of(MarketType.KRW_ETH.getCode()))
                .build(),
            keyPairId
        );
        log.info("result : {}", response);
    }

    @Test
    public void 현재가_조회_테스트() {
        List<TickerResponse> response = upbitClient.getTicker(
            TickerRequest.builder()
                .marketList(List.of(MarketType.KRW_ETH.getCode()))
                .build(),
            keyPairId
        );
        log.info("result : {}", response);
    }

}