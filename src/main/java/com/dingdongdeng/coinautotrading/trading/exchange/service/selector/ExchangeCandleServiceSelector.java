package com.dingdongdeng.coinautotrading.trading.exchange.service.selector;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExchangeCandleServiceSelector {

  private final List<ExchangeCandleService> candleServiceList;

  public ExchangeCandleService getTargetService(CoinExchangeType coinExchangeType) {
    return candleServiceList.stream()
        .filter(processor -> processor.getCoinExchangeType() == coinExchangeType)
        .findFirst()
        .orElseThrow();
  }
}
