package com.dingdongdeng.autotrading.domain.backtest.factory

import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BackTestProcessorFactory {

    fun of(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        durationUnit: CandleUnit, // 백테스트 시간 간격
        autoTradeProcessor: CoinAutoTradeProcessor,
    ): CoinBackTestProcessor {
        return CoinBackTestProcessor(
            exchangeType = autoTradeProcessor.exchangeType,
            coinTypes = autoTradeProcessor.coinTypes,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            autoTradeProcessor = autoTradeProcessor,
        )
    }
}