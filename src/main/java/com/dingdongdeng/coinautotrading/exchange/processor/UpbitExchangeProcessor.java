package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.exchange.client.UpbitClient;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.MarketType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.State;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.CandleRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderInfoListRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.CandleResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.OrderCancelResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.OrderResponse;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessCandle;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrder;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancel;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessTradingInfo;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessTradingInfoParam;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitExchangeProcessor implements ExchangeProcessor {

    private final UpbitClient upbitClient;

    @Override
    public ProcessOrder order(ProcessOrderParam param) {
        log.info("upbit process : order param = {}", param);
        OrderResponse response = upbitClient.order(
            OrderRequest.builder()
                .market(MarketType.of(param.getCoinType()).getCode())
                .side(Side.of(param.getOrderType()))
                .volume(param.getVolume())
                .price(param.getPrice())
                .ordType(OrdType.of(param.getPriceType()))
                .build()
        );
        return makeProcessOrderResult(response);
    }

    @Override
    public ProcessOrderCancel orderCancel(ProcessOrderCancelParam param) {
        log.info("upbit process : order cancel param = {}", param);
        OrderCancelResponse response = upbitClient.orderCancel(
            OrderCancelRequest.builder()
                .uuid(param.getOrderId())
                .build()
        );
        return ProcessOrderCancel.builder()
            .orderId(response.getUuid())
            .orderType(response.getSide().getOrderType())
            .priceType(response.getOrdType().getPriceType())
            .price(response.getPrice())
            .state(response.getState())
            .market(response.getMarket())
            .createdAt(response.getCreatedAt())
            .volume(response.getVolume())
            .remainingVolume(response.getRemainingVolume())
            .reservedFee(response.getReservedFee())
            .remainingFee(response.getRemainingFee())
            .paidFee(response.getPaidFee())
            .locked(response.getLocked())
            .executedVolume(response.getExecutedVolume())
            .tradeCount(response.getTradeCount())
            .build();
    }

    @Override
    public ProcessTradingInfo getTradingInformation(ProcessTradingInfoParam param) {
        log.info("upbit process : get trading information param = {}", param);

        // 미체결 주문내역 조회
        List<ProcessOrder> undecidedOrderList = makeUndecidedOrderList(param);

        // 캔들 정보 조회
        ProcessCandle candleInfo = makeCandleInfo(param);

        // 계좌 정보 조회
        AccountsResponse accounts = upbitClient.getAccounts().stream().findFirst().orElseThrow(() -> new NoSuchElementException("계좌를 찾지 못함"));

        return ProcessTradingInfo.builder()
            .currency(accounts.getCurrency())
            .balance(accounts.getBalance())
            .locked(accounts.getLocked())

            .avgBuyPrice(accounts.getAvgBuyPrice())
            .avgBuyPriceModified(accounts.isAvgBuyPriceModified())
            .unitCurrency(accounts.getUnitCurrency())

            .undecidedOrderList(undecidedOrderList)
            .candle(candleInfo)

            .rsi(0.5)
            .build();
    }

    @Override
    public CoinExchangeType getExchangeType() {
        return CoinExchangeType.UPBIT;
    }

    private List<ProcessOrder> makeUndecidedOrderList(ProcessTradingInfoParam param) {
        return upbitClient.getOrderInfoList(
            OrderInfoListRequest.builder()
                .market(MarketType.of(param.getCoinType()).getCode())
                .stateList(List.of(State.wait, State.watch))
                .build())
            .stream()
            .map(this::makeProcessOrderResult)
            .collect(Collectors.toList());
    }

    private ProcessCandle makeCandleInfo(ProcessTradingInfoParam param) {
        int unit = param.getTradingTerm() == TradingTerm.SCALPING ? 15 : 60;
        List<CandleResponse> response = upbitClient.getCandle(
            CandleRequest.builder()
                .unit(unit)
                .market(MarketType.of(param.getCoinType()).getCode())
                .to(LocalDateTime.now())
                .count(200)
                .build()
        );
        return ProcessCandle.builder()
            .unit(unit)
            .coinType(param.getCoinType())
            .candleList(
                response.stream().map(
                    candel -> ProcessCandle.Candle.builder()
                        .candleDateTimeUtc(candel.getCandleDateTimeUtc())
                        .candleDateTimeKst(candel.getCandleDateTimeKst())
                        .openingPrice(candel.getOpeningPrice())
                        .highPrice(candel.getHighPrice())
                        .lowPrice(candel.getLowPrice())
                        .tradePrice(candel.getTradePrice())
                        .timestamp(candel.getTimestamp())
                        .candleAccTradePrice(candel.getCandleAccTradePrice())
                        .candleAccTradeVolume(candel.getCandleAccTradeVolume())
                        .build()
                ).collect(Collectors.toList())
            )
            .build();
    }

    private ProcessOrder makeProcessOrderResult(OrderResponse response) {
        return ProcessOrder.builder()
            .orderId(response.getUuid())
            .orderType(response.getSide().getOrderType())
            .priceType(response.getOrdType().getPriceType())
            .price(response.getPrice())
            .avgPrice(response.getAvgPrice())
            .orderState(response.getState().getOrderState())
            .market(response.getMarket())
            .createdAt(response.getCreatedAt())
            .volume(response.getVolume())
            .remainingVolume(response.getRemainingVolume())
            .reservedFee(response.getReservedFee())
            .remainingFee(response.getRemainingFee())
            .paidFee(response.getPaidFee())
            .locked(response.getLocked())
            .executedVolume(response.getExecutedVolume())
            .tradeCount(response.getTradeCount())
            .tradeList(response.getTradeList())
            .build();
    }
}
