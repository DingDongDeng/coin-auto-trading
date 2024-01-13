package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChart
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.repository.ExchangeCandleRepository
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.TradeState
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import org.springframework.stereotype.Service
import java.util.*

@Service
class BackTestSpotCoinExchangeService(
    private val exchangeCandleRepository: ExchangeCandleRepository,
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
        val exchangeType = ExchangeType.UPBIT // 업비트 차트를 사용

        val candles = exchangeCandleRepository.findAllExchangeCandle(
            exchangeType = exchangeType,
            coinType = param.coinType,
            unit = param.candleUnit,
            from = param.from,
            to = param.to,
        )


        val isNeedVirtualCandle = false
        if (isNeedVirtualCandle) {

        }
        //FIXME
        // 각 N봉 캔들의 마지막 값을 1분봉으로 맞춰야해
        // 어쩌면 캔들이 없을수도 있어... 필요하면 만들어야할지도 몰라
        // 누적 물량,금액은 어떻게 계산을 해줄까?

        return ExchangeChart(
            from = param.from,
            to = param.to,
            currentPrice = 0, //이거는 1분봉껄로 조회하자
            candles = candles.map {
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
            }
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

    companion object {
        val EXCHANGE_TYPE = ExchangeType.BACKTEST
    }
}