package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import com.dingdongdeng.coinautotrading.trading.strategy.repository.TradingResultRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StrategyAssistant {

    private final String keyPairId;
    private final ExchangeService exchangeService;
    private final TradingResultRepository tradingResultRepository;

    public void storeTradingResult(TradingResult tradingResult) {
        tradingResult.setId(getKey(tradingResult.getIdentifyCode(), tradingResult.getTag()));
        tradingResultRepository.save(tradingResult);
    }

    public TradingResultPack syncedTradingResultPack(String identifyCode) {
        return TradingResultPack.builder()
            .buyTradingResult(updateTradingResult(findTradingResult(identifyCode, TradingTag.BUY)))
            .profitTradingResult(updateTradingResult(findTradingResult(identifyCode, TradingTag.PROFIT)))
            .lossTradingResult(updateTradingResult(findTradingResult(identifyCode, TradingTag.LOSS)))
            .build();
    }

    public void reset(String identifyCode) {
        TradingResult buyTradingResult = findTradingResult(identifyCode, TradingTag.BUY);
        TradingResult profitTradingResult = findTradingResult(identifyCode, TradingTag.PROFIT);
        TradingResult lossTradingResult = findTradingResult(identifyCode, TradingTag.LOSS);

        if (buyTradingResult.isExist()) {
            tradingResultRepository.delete(buyTradingResult);
        }
        if (profitTradingResult.isExist()) {
            tradingResultRepository.delete(profitTradingResult);
        }
        if (lossTradingResult.isExist()) {
            tradingResultRepository.delete(lossTradingResult);
        }
    }

    public void reset(TradingResult tradingResult) {
        String identifyCode = tradingResult.getIdentifyCode();
        TradingTag tag = tradingResult.getTag();
        TradingResult storedTradingResult = findTradingResult(identifyCode, tag);
        tradingResultRepository.delete(storedTradingResult);
    }

    private TradingResult findTradingResult(String identifyCode, TradingTag tag) {
        return tradingResultRepository.findById(getKey(identifyCode, tag)).orElse(new TradingResult());
    }

    private String getKey(String identifyCode, TradingTag tag) {
        return identifyCode + ":" + tag.name(); // RSI:BUY, RSI:PROFIT, RSI:LOSS
    }

    private TradingResult updateTradingResult(TradingResult tradingResult) {
        if (!tradingResult.isExist()) {
            return new TradingResult();
        }
        ExchangeOrder exchangeOrder = exchangeService.getOrderInfo(
            ExchangeOrderInfoParam.builder().orderId(tradingResult.getOrderId()).build(),
            keyPairId
        );
        return TradingResult.builder()
            .id(tradingResult.getId())
            .identifyCode(tradingResult.getIdentifyCode())
            .coinType(tradingResult.getCoinType())
            .orderType(tradingResult.getOrderType())
            .orderState(exchangeOrder.getOrderState())
            .volume(tradingResult.getVolume())
            .price(tradingResult.getPrice())
            .priceType(tradingResult.getPriceType())
            .orderId(exchangeOrder.getOrderId())
            .tag(tradingResult.getTag())
            .createdAt(exchangeOrder.getCreatedAt())
            .build();
    }
}
