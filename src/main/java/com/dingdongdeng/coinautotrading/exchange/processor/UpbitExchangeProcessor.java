package com.dingdongdeng.coinautotrading.exchange.processor;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.exchange.client.UpbitClient;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.MarketType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderCancelRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderInfoRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitRequest.OrderRequest;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.AccountsResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.OrderCancelResponse;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitResponse.OrderResponse;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessAccountResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderCancelResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderInfoResult;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderParam;
import com.dingdongdeng.coinautotrading.exchange.processor.model.ProcessOrderResult;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitExchangeProcessor implements ExchangeProcessor {

    private final UpbitClient upbitClient;

    @Override
    public ProcessOrderResult order(ProcessOrderParam param) {
        log.info("upbit execute : order {}", param);
        OrderResponse response = upbitClient.order(
            OrderRequest.builder()
                .market(MarketType.of(param.getCoinType()).getCode())
                .side(Side.of(param.getOrderType()))
                .volume(param.getVolume())
                .price(param.getPrice())
                .ordType(OrdType.of(param.getPriceType()))
                .build()
        );
        return ProcessOrderResult.builder()
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
            .build();
    }

    @Override
    public ProcessOrderCancelResult orderCancel(ProcessOrderCancelParam param) {
        log.info("upbit execute : order cancel {}", param);
        OrderCancelResponse response = upbitClient.orderCancel(
            OrderCancelRequest.builder()
                .uuid(param.getOrderId())
                .build()
        );
        return ProcessOrderCancelResult.builder()
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
    public ProcessAccountResult getAccount(ProcessAccountParam param) {
        log.info("upbit execute : get account {}", param);
        AccountsResponse response = upbitClient.getAccounts().stream()
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("계좌를 찾지 못함"));

        return ProcessAccountResult.builder()
            .currency(response.getCurrency())
            .balance(response.getBalance())
            .locked(response.getLocked())
            .avgBuyPrice(response.getAvgBuyPrice())
            .avgBuyPriceModified(response.isAvgBuyPriceModified())
            .unitCurrency(response.getUnitCurrency())
            .build();
    }

    @Override
    public ProcessOrderInfoResult getOrderInfo(ProcessOrderInfoParam param) {
        log.info("upbit execute : get order info {}", param);
        OrderResponse response = upbitClient.getOrderInfo(
            OrderInfoRequest.builder()
                .uuid(param.getOrderId())
                .build()
        );
        return ProcessOrderInfoResult.builder()
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

    @Override
    public CoinExchangeType getExchangeType() {
        return CoinExchangeType.UPBIT;
    }
}
