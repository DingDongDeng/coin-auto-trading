package com.dingdongdeng.coinautotrading.trading.exchange.spot.scheduler;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.Candle;
import com.dingdongdeng.coinautotrading.domain.service.CandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.common.ExchangeCandleService;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.SpotExchangeCandles;
import java.util.List;
import java.util.stream.Collectors;
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
    private final int LIMIT_DAYS = 2;
    private final ExchangeCandleService exchangeCandleService;
    private final CandleService candleService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (true) {
            return; //fixme 스케줄러가 아니라 백테스팅 세팅할때 로직 사용하도록하자
        }
//        log.info("run CandleStorJob");
//        TARGET_COINT_LIST.forEach(coinType -> {
//            Candle candle = candleService.findOneLastCandle(exchangeCandleService.getCoinExchangeType(), coinType, CANDLE_UNIT)
//                .orElse(Candle.builder().candleDateTimeKst(LocalDateTime.now().minusDays(LIMIT_DAYS)).build());
//            ExchangeCandles exchangeCandlesList = exchangeCandleService
//                .getCandleList(coinType, CANDLE_UNIT, candle.getCandleDateTimeKst(), LocalDateTime.now(), ""); //fixme keyPairId 로직 필요
//            candleService.saveAll(makeCandleList(exchangeCandlesList));
//        });

        //fixme CompletedFuture를 활용할수 있는 구조로 해보자
        // 중복 저장의 위험은 unique 컬럼을 설정해서 방지하는게 좋을거같아(거의 발생안할 이슈라고 판단됨)
    }

    private List<Candle> makeCandleList(SpotExchangeCandles spotExchangeCandles) {
        CoinType coinType = spotExchangeCandles.getCoinType();
        CandleUnit candleUnit = spotExchangeCandles.getCandleUnit();
        return spotExchangeCandles.getCandleList().stream()
            .map(candle -> Candle.builder()
                .coinType(coinType)
                .coinExchangeType(spotExchangeCandles.getCoinExchangeType())
                .candleUnit(candleUnit)
                .candleDateTimeUtc(candle.getCandleDateTimeUtc())
                .candleDateTimeKst(candle.getCandleDateTimeKst())
                .openingPrice(candle.getOpeningPrice())
                .highPrice(candle.getHighPrice())
                .lowPrice(candle.getLowPrice())
                .tradePrice(candle.getTradePrice())
                .candleAccTradePrice(candle.getCandleAccTradePrice())
                .candleAccTradeVolume(candle.getCandleAccTradeVolume())
                .build())
            .collect(Collectors.toList());
    }
}
