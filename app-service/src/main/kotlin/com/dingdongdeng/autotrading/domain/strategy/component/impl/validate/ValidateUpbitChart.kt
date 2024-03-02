package com.dingdongdeng.autotrading.domain.strategy.component.impl.validate

import com.dingdongdeng.autotrading.domain.strategy.component.SpotCoinStrategy
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyMakeTaskParam
import com.dingdongdeng.autotrading.domain.strategy.model.SpotCoinStrategyTask
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ValidateUpbitChart : SpotCoinStrategy {

    override fun makeTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask> {
        log.info("validate upbit chart ... ${TimeContext.now()}")
        params.all { it.exchangeType == ExchangeType.UPBIT }
        validateBitCoin(params.first { it.coinType == CoinType.BITCOIN })
        return emptyList()
    }

    override fun support(param: CoinStrategyType): Boolean {
        return param == CoinStrategyType.UPBIT_CHART_VALIDATE
    }

    private fun validateBitCoin(param: SpotCoinStrategyMakeTaskParam) {
        val now = TimeContext.now()
        val unit1M = CandleUnit.UNIT_1M
        val chart1M = param.getChart(unit1M)
        when (CandleDateTimeUtils.makeUnitDateTime(now, unit1M)) {
            LocalDateTime.of(2024, 2, 29, 22, 30) -> {
                val candle = chart1M.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 87416000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit1M closingPrice=${candle.closingPrice}")
                }
                if (round(candle.indicators.rsi, 4.0) != 0.4634) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit1M, rsi=${candle.indicators.rsi}")
                }
                if (round(indicators.ma.sma120) != 87817942.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit1M, sma120=${indicators.ma.sma120}")
                }
                if (round(indicators.bollingerBands.upper) != 88058560.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit1M, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (round(indicators.bollingerBands.lower) != 87038340.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit1M, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (floor(indicators.macd.signal) != -78959.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit1M, macd.signal=${indicators.macd.signal}")
                }
            }

            LocalDateTime.of(2024, 2, 28, 22, 27) -> {
                val candle = chart1M.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 84185000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit1M closingPrice=${candle.closingPrice}")
                }
                if (round(candle.indicators.rsi, 4.0) != 0.7163) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit1M, rsi=${candle.indicators.rsi}")
                }
                if (indicators.ma.sma120 != 82498675.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit1M, sma120=${indicators.ma.sma120}")
                }
                if (round(indicators.bollingerBands.upper) != 84320311.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit1M, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (round(indicators.bollingerBands.lower) != 82441989.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit1M, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (round(indicators.macd.signal) != 268222.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit1M, macd.signal=${indicators.macd.signal}")
                }
            }

            LocalDateTime.of(2024, 2, 27, 19, 39) -> {
                val candle = chart1M.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 77652000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit1M closingPrice=${candle.closingPrice}")
                }
                if (round(candle.indicators.rsi, 4.0) != 0.3272) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit1M, rsi=${candle.indicators.rsi}")
                }
                if (indicators.ma.sma120 != 77573200.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit1M, sma120=${indicators.ma.sma120}")
                }
                if (round(indicators.bollingerBands.upper) != 78058005.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit1M, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (round(indicators.bollingerBands.lower) != 77648595.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit1M, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (round(indicators.macd.signal) != 3594.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit1M, macd.signal=${indicators.macd.signal}")
                }
            }
        }

        val chart15M = param.getChart(CandleUnit.UNIT_15M)

    }

    private fun round(value: Double, digit: Double = 0.0): Double {
        val multiplier = Math.pow(10.0, digit)
        return kotlin.math.round(value * multiplier) / multiplier
    }

    private fun ceil(value: Double, digit: Double = 0.0): Double {
        val multiplier = Math.pow(10.0, digit)
        return kotlin.math.ceil(value * multiplier) / multiplier
    }

    private fun floor(value: Double, digit: Double = 0.0): Double {
        val multiplier = Math.pow(10.0, digit)
        return kotlin.math.floor(value * multiplier) / multiplier
    }

}