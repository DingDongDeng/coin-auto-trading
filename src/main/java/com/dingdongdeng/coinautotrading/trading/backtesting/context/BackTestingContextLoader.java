package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import com.dingdongdeng.coinautotrading.trading.exchange.service.ExchangeCandleService;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackTestingContextLoader {

    private final ExchangeCandleService exchangeCandleService;

    private final LocalDateTime start;
    private final LocalDateTime end;

    private BackTestingContext currentContext;

    public BackTestingContextLoader(ExchangeCandleService candleService, LocalDateTime start, LocalDateTime end) {
        this.exchangeCandleService = candleService;
        this.start = start;
        this.end = end;
    }

    public boolean hasNext() {
        BackTestingContext context = getNextContext();
        if (Objects.nonNull(context)) {
            this.setCurrentContext(context);
            return true;
        }
        this.setCurrentContext(null);
        return false;
    }

    public BackTestingContext getCurrentContext() {
        return this.currentContext;
    }

    private BackTestingContext getNextContext() {
        //fixme
        return null;
    }

    private void setCurrentContext(BackTestingContext context) {
        this.currentContext = context;
    }

}
