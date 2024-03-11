package com.dingdongdeng.autotrading.domain.backtest.service.impl

import com.dingdongdeng.autotrading.domain.backtest.repository.VirtualCoinCandleRepository
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChart
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.type.TradeState
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.round
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BackTestSpotCoinExchangeService(
    private val virtualCoinCandleRepository: VirtualCoinCandleRepository,
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
            fee = (param.volume * param.price * (FEE_RATE / 100)).round(),
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
        val candles = virtualCoinCandleRepository.findAllCoinCandle(
            exchangeType = EXCHANGE_TYPE_FOR_BACKTEST,
            coinType = param.coinType,
            unit = param.candleUnit,
            from = param.from,
            to = param.to,
        ).map {
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

        if (candles.isEmpty()) {
            throw CriticalException.of("백테스트 캔들 조회 결과가 존재하지 않음, exchangeType=$EXCHANGE_TYPE_FOR_BACKTEST,  unit=${param.candleUnit}, from=${param.from}, to=${param.to}")
        }

        return ExchangeChart(
            from = param.from,
            to = param.to,
            currentPrice = candles.last().closingPrice,
            candles = candles,
        )

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
        private val EXCHANGE_TYPE = ExchangeType.BACKTEST_UPBIT
        private val EXCHANGE_TYPE_FOR_BACKTEST = ExchangeType.UPBIT // 업비트 차트로 백테스트 진행
        private const val FEE_RATE = 0.05 // upbit 수수료 0.05% 적용
    }
}