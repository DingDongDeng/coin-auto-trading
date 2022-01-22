package com.dingdongdeng.coinautotrading.exchange.scheduler;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.Candle;
import com.dingdongdeng.coinautotrading.domain.service.CandleService;
import com.dingdongdeng.coinautotrading.exchange.service.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.exchange.service.model.ExchangeCandles;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@RequiredArgsConstructor
@Slf4j
public abstract class CandleStoreJob implements Job {

    private final List<CoinType> TARGET_COINT_LIST;
    private final CandleUnit CANDLE_UNIT;
    private final ExchangeCandleService exchangeCandleService;
    private final CandleService candleService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        delay(500);

        log.info("run CandleStorJob");
        TARGET_COINT_LIST.forEach(coinType -> {
            Candle candle = candleService.findLastCandle(coinType, CANDLE_UNIT);
            ExchangeCandles exchangeCandlesList = exchangeCandleService.getCandleList(coinType, CANDLE_UNIT, candle.getCandleDateTimeKst(), LocalDateTime.now());
            candleService.saveAll(null);
        });

        //fixme CompletedFuture를 활용할수 있는 구조로 해보자
        // transaction 단위는 coinType단위로 끝내서 너무 오랫동안 트랜잭션 잡고있지 않게하자
        // 중복 저장의 위험은 unique 컬럼을 설정해서 방지하는게 좋을거같아(거의 발생안할 이슈라고 판단됨)
    }

    private void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
