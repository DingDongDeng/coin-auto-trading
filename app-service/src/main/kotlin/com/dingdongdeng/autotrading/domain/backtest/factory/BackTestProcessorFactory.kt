package com.dingdongdeng.autotrading.domain.backtest.factory

import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.backtest.model.AvailBackTestRange
import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BackTestProcessorFactory(
    private val coinChartService: CoinChartService,
) {

    fun of(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        durationUnit: CandleUnit, // 백테스트 시간 간격
        autoTradeProcessor: CoinAutoTradeProcessor,
    ): CoinBackTestProcessor {
        // FIXME 장기간을 테스트 하기가 힘드네...
        //  백테스트 가능 구간을 교집합으로 알려주도록 수정
        //  아 메일로 문의해보니 점검이었나보네 (체결이 일어나야 캔들이 생성됨 <<< 잡 알트코인에서 1~2개씩 누락된 이유가 이거네)
        //  즉, 과거 캔들을 가상으로 생성하는거는 오히려 리얼한 백테스트가 아니야 없는채로 동작하는게 맞아!!!
        autoTradeProcessor.coinTypes.forEach { coinType ->
            val ranges = getAvailRanges(autoTradeProcessor.exchangeType.asReal, coinType, startDateTime, endDateTime)
            if (ranges.none { it.isRanged(coinType, startDateTime, endDateTime) }) {
                throw WarnException.of("백테스트 불가능한 구간입니다. availBackTestRanges=$ranges")
            }
        }
        return CoinBackTestProcessor(
            exchangeType = autoTradeProcessor.exchangeType,
            coinTypes = autoTradeProcessor.coinTypes,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            autoTradeProcessor = autoTradeProcessor,
        )
    }

    private fun getAvailRanges(
        exchangeType: ExchangeType,
        coinType: CoinType,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): List<AvailBackTestRange> {
        val minUnit = CandleUnit.min()
        val missingDateTimes = coinChartService.getMissingChart(
            exchangeType = exchangeType,
            coinType = coinType,
            candleUnit = minUnit,
            from = startDateTime,
            to = endDateTime,
        ).candles.map { it.candleDateTimeKst }

        val availBackTestRanges = AvailBackTestRange.fromMissingDateTimes(
            exchangeType = exchangeType,
            coinType = coinType,
            candleUnit = minUnit,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            missingDateTimes = missingDateTimes,
        )

        return AvailBackTestRange.merge(availBackTestRanges, minUnit.getSecondSize() * ALLOW_MISSING_CANDLE_COUNT)
    }

    companion object {
        private const val ALLOW_MISSING_CANDLE_COUNT = 10
    }
}