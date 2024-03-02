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
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.floor
import com.dingdongdeng.autotrading.infra.common.utils.round
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ValidateUpbitChart : SpotCoinStrategy {

    override fun makeTask(
        params: List<SpotCoinStrategyMakeTaskParam>,
        config: Map<String, Any>
    ): List<SpotCoinStrategyTask> {
        /**
         * 백테스팅 환경 테스트를 위해
         * 2024-02-27 ~ 2024-02-29 기간에 대한 임의 검증
         */
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

        validate1M(now, param)
        validate15M(now, param)
    }

    private fun validate1M(now: LocalDateTime, param: SpotCoinStrategyMakeTaskParam) {
        val unit = CandleUnit.UNIT_1M
        val chart = param.getChart(unit)
        when (now) {
            LocalDateTime.of(2024, 2, 29, 22, 30) -> {
                val candle = chart.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 87416000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit closingPrice=${candle.closingPrice}")
                }
                if (candle.indicators.rsi.round(4.0) != 0.4634) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit, rsi=${candle.indicators.rsi}")
                }
                if (indicators.ma.sma120.round() != 87817942.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit, sma120=${indicators.ma.sma120}")
                }
                if (indicators.bollingerBands.upper.round() != 88058560.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (indicators.bollingerBands.lower.round() != 87038340.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (indicators.macd.signal.floor() != -78959.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit, macd.signal=${indicators.macd.signal}")
                }
            }

            LocalDateTime.of(2024, 2, 28, 22, 27) -> {
                val candle = chart.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 84185000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit closingPrice=${candle.closingPrice}")
                }
                if (candle.indicators.rsi.round(4.0) != 0.7163) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit, rsi=${candle.indicators.rsi}")
                }
                if (indicators.ma.sma120 != 82498675.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit, sma120=${indicators.ma.sma120}")
                }
                if (indicators.bollingerBands.upper.round() != 84320311.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (indicators.bollingerBands.lower.round() != 82441989.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (indicators.macd.signal.round() != 268222.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit, macd.signal=${indicators.macd.signal}")
                }
            }

            LocalDateTime.of(2024, 2, 27, 19, 39) -> {
                val candle = chart.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 77652000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit closingPrice=${candle.closingPrice}")
                }
                if (candle.indicators.rsi.round(4.0) != 0.3272) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit, rsi=${candle.indicators.rsi}")
                }
                if (indicators.ma.sma120 != 77573200.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit, sma120=${indicators.ma.sma120}")
                }
                if (indicators.bollingerBands.upper.round() != 78058005.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (indicators.bollingerBands.lower.round() != 77648595.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (indicators.macd.signal.round() != 3594.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit, macd.signal=${indicators.macd.signal}")
                }
            }
        }
    }

    private fun validate15M(now: LocalDateTime, param: SpotCoinStrategyMakeTaskParam) {
        val unit = CandleUnit.UNIT_15M
        val chart = param.getChart(unit)
        when (now) {
            LocalDateTime.of(2024, 2, 29, 22, 30) -> {
                val candle = chart.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 87416000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit closingPrice=${candle.closingPrice}")
                }
                if (candle.indicators.rsi.round(4.0) != 0.4598) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit, rsi=${candle.indicators.rsi}")
                }
                if (indicators.ma.sma120.round() != 85713075.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit, sma120=${indicators.ma.sma120}")
                }
                if (indicators.bollingerBands.upper.round() != 88324473.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (indicators.bollingerBands.lower.round() != 87046627.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (indicators.macd.signal.floor() != 9809.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit, macd.signal=${indicators.macd.signal}")
                }
            }

            LocalDateTime.of(2024, 2, 28, 22, 27) -> {
                val candle = chart.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 84185000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit closingPrice=${candle.closingPrice}")
                }
                if (candle.indicators.rsi.round(4.0) != 0.794) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit, rsi=${candle.indicators.rsi}")
                }
                if (indicators.ma.sma120.round() != 79177117.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit, sma120=${indicators.ma.sma120}")
                }
                if (indicators.bollingerBands.upper.round() != 83539677.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (indicators.bollingerBands.lower.round() != 81318123.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (indicators.macd.signal.round() != 716757.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit, macd.signal=${indicators.macd.signal}")
                }
            }

            LocalDateTime.of(2024, 2, 27, 19, 39) -> {
                val candle = chart.candles.last()
                val indicators = candle.indicators
                if (candle.closingPrice != 77652000.0) {
                    throw CriticalException.of("종가 일치하지 않음, now=$now, unit=$unit closingPrice=${candle.closingPrice}")
                }
                if (candle.indicators.rsi.round(4.0) != 0.5875) {
                    throw CriticalException.of("RSI 일치하지 않음, now=$now, unit=$unit, rsi=${candle.indicators.rsi}")
                }
                if (indicators.ma.sma120.round() != 73893042.0) {
                    throw CriticalException.of("sma120 일치하지 않음, now=$now, unit=$unit, sma120=${indicators.ma.sma120}")
                }
                if (indicators.bollingerBands.upper.round() != 77992190.0) {
                    throw CriticalException.of("boll.upper 일치하지 않음, now=$now, unit=$unit, boll.upper=${indicators.bollingerBands.upper}")
                }
                if (indicators.bollingerBands.lower.round() != 76899610.0) {
                    throw CriticalException.of("boll.lower 일치하지 않음, now=$now, unit=$unit, boll.lower=${indicators.bollingerBands.lower}")
                }
                if (indicators.macd.signal.round() != 327003.0) {
                    throw CriticalException.of("macd.signal 일치하지 않음, now=$now, unit=$unit, macd.signal=${indicators.macd.signal}")
                }
            }
        }
    }
}