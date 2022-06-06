package com.dingdongdeng.coinautotrading.trading.exchange.service.selector;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExchangeServiceSelector {

  private final List<ExchangeService> exchangeServiceList;

  public ExchangeService getTargetService(CoinExchangeType coinExchangeType) {
    return exchangeServiceList.stream()
        .filter(processor -> processor.getCoinExchangeType() == coinExchangeType)
        .findFirst()
        .orElseThrow();
  }
}
