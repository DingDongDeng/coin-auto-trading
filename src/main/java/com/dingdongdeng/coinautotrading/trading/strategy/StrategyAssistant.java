package com.dingdongdeng.coinautotrading.trading.strategy;

import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.exchange.service.model.ExchangeOrderInfoParam;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResult;
import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingResultPack;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import com.dingdongdeng.coinautotrading.trading.strategy.repository.TradingResultRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StrategyAssistant {

    private final ExchangeService exchangeService;
    private final TradingResultRepository tradingResultRepository;

    public void storeTradingResult(TradingResult tradingResult) {
        tradingResult.setId(getKey(tradingResult.getStrategyCode(), tradingResult.getTag()));
        tradingResultRepository.save(tradingResult);
    }

    public TradingResultPack syncedTradingResultPack(StrategyCode code) {
        return TradingResultPack.builder()
            .buyTradingResult(updateTradingResult(findTradingResult(code, TradingTag.BUY)))
            .profitTradingResult(updateTradingResult(findTradingResult(code, TradingTag.PROFIT)))
            .lossTradingResult(updateTradingResult(findTradingResult(code, TradingTag.LOSS)))
            .build();
    }

    public void reset(StrategyCode code) {
        TradingResult buyTradingResult = findTradingResult(code, TradingTag.BUY);
        TradingResult profitTradingResult = findTradingResult(code, TradingTag.PROFIT);
        TradingResult lossTradingResult = findTradingResult(code, TradingTag.LOSS);

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
        StrategyCode code = tradingResult.getStrategyCode();
        TradingTag tag = tradingResult.getTag();
        TradingResult storedTradingResult = findTradingResult(code, tag);
        tradingResultRepository.delete(storedTradingResult);
    }

    private TradingResult findTradingResult(StrategyCode code, TradingTag tag) {
        return tradingResultRepository.findById(getKey(code, tag)).orElse(new TradingResult());
    }

    private String getKey(StrategyCode code, TradingTag tag) {
        return code.name() + ":" + tag.name(); // RSI:BUY, RSI:PROFIT, RSI:LOSS
    }

    private TradingResult updateTradingResult(TradingResult tradingResult) {
        if (!tradingResult.isExist()) {
            return new TradingResult();
        }
        ExchangeOrder exchangeOrder = exchangeService.getOrderInfo(
            ExchangeOrderInfoParam.builder().orderId(tradingResult.getOrderId()).build()
        );
        return TradingResult.builder()
            .id(tradingResult.getId())
            .strategyCode(tradingResult.getStrategyCode())
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
