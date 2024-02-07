package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.entity.ExchangeCandle
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChart
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.repository.CachedExchangeCandleRepository
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.TradeState
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

@Service
class BackTestSpotCoinExchangeService(
    private val exchangeCandleRepository: CachedExchangeCandleRepository,
) : SpotCoinExchangeService {

    override fun order(param: SpotCoinExchangeOrderParam, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder {
        return SpotCoinExchangeOrder(
            orderId = UUID.randomUUID().toString(),
            orderType = param.orderType,
            priceType = param.priceType,
            price = param.price,
            volume = param.volume,
            tradeState = TradeState.DONE,
            exchangeType = EXCHANGE_TYPE,
            coinType = param.coinType,
            fee = param.volume * param.price * (0.05 / 100), // upbit 수수료 0.05% 적용
            orderDateTime = TimeContext.now(),
            cancelDateTime = null,
        )
    }

    override fun cancel(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder {
        throw WarnException.of(userMessage = "백테스트에서는 지원하지 않는 기능입니다. (모든 주문이 즉시 DONE 상태가 됩니다)")
    }

    override fun getOrder(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder {
        throw WarnException.of(userMessage = "백테스트에서는 지원하지 않는 기능입니다. (모든 주문이 즉시 DONE 상태가 됩니다)")
    }

    // from <= 조회범위 <= to
    override fun getChart(param: SpotCoinExchangeChartParam, keyParam: ExchangeKeyPair): ExchangeChart {
        val exchangeType = EXCHANGE_TYPE_FOR_BACKTEST

        // 캔들 조회
        val candles = exchangeCandleRepository.findAllExchangeCandle(
            exchangeType = exchangeType,
            coinType = param.coinType,
            unit = param.candleUnit,
            from = param.from,
            to = param.to,
        )

        // 가상 캔들 생성 (최소 단위 캔들을 사용하여, 현재 시간 기준으로 생성)
        // 예를 들어, 15분봉일때, 현재시간이 13:17이라면 13:15에 대한 가상캔들을 새로 만들어야한다.
        // DB에서 조회한 13:15캔들은 13:30까지의 상태가 반영되었기 때문이다.
        val virtualCandle = makeVirtualCandle(
            candleUnit = param.candleUnit,
            minUnitCandles = exchangeCandleRepository.findAllExchangeCandle(
                exchangeType = exchangeType,
                coinType = param.coinType,
                unit = CandleUnit.min(),
                from = candles.last().candleDateTimeKst,
                to = TimeContext.now(),
            )
        )

        // 최종 캔들
        val resultCandles = candles
            .subList(0, candles.size - 1) // 마지막 캔들을 가상 캔들로 대체
            .map {
                ExchangeChartCandle(
                    candleUnit = it.unit,
                    candleDateTimeUtc = it.candleDateTimeUtc,
                    candleDateTimeKst = it.candleDateTimeKst,
                    openingPrice = it.openingPrice,
                    highPrice = it.highPrice,
                    lowPrice = it.lowPrice,
                    closingPrice = it.closingPrice,
                    accTradePrice = it.accTradePrice,
                    accTradeVolume = it.accTradeVolume,
                )
            } + virtualCandle

        return ExchangeChart(
            from = param.from,
            to = param.to,
            currentPrice = virtualCandle.closingPrice,
            candles = resultCandles,
        )

    }

    override fun loadChart(param: SpotCoinExchangeChartParam, keyParam: ExchangeKeyPair) {
        throw WarnException.of(userMessage = "백테스트에서는 지원하지 않는 기능입니다. (거래소 차트 데이터 DB로 다운로드)")
    }

    override fun getKeyPair(keyPairId: String): ExchangeKeyPair {
        return ExchangeKeyPair(
            accessKey = "",
            secretKey = "",
        )
    }

    override fun registerKeyPair(accessKey: String, secretKey: String, userId: Long): String {
        throw WarnException.of("백테스트에서는 지원하지 않는 기능입니다. (key 등록)")
    }

    override fun support(exchangeType: ExchangeType): Boolean {
        return exchangeType == EXCHANGE_TYPE
    }

    private fun makeVirtualCandle(candleUnit: CandleUnit, minUnitCandles: List<ExchangeCandle>): ExchangeChartCandle {
        val candleDateTimeUtc = minUnitCandles.first().candleDateTimeUtc
        val candleDateTimeKst = minUnitCandles.first().candleDateTimeKst
        val openingPrice = minUnitCandles.first().openingPrice
        var highPrice = 0.0
        var lowPrice = 0.0
        var closingPrice = 0.0
        var accTradePrice = 0.0
        var accTradeVolume = 0.0

        minUnitCandles.forEach { candle ->
            highPrice = max(highPrice, candle.closingPrice)
            lowPrice = min(lowPrice, candle.closingPrice)
            closingPrice = candle.closingPrice
            accTradePrice += candle.accTradePrice
            accTradeVolume += candle.accTradeVolume
        }

        return ExchangeChartCandle(
            candleUnit = candleUnit,
            candleDateTimeUtc = candleDateTimeUtc,
            candleDateTimeKst = candleDateTimeKst,
            openingPrice = openingPrice,
            highPrice = highPrice,
            lowPrice = lowPrice,
            closingPrice = closingPrice,
            accTradePrice = accTradePrice,
            accTradeVolume = accTradeVolume,
        )
    }

    companion object {
        val EXCHANGE_TYPE = ExchangeType.BACKTEST
        val EXCHANGE_TYPE_FOR_BACKTEST = ExchangeType.UPBIT // 업비트 차트로 백테스트 진행
    }
}