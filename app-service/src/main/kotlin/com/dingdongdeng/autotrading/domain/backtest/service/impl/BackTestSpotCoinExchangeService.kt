package com.dingdongdeng.autotrading.domain.backtest.service.impl

import com.dingdongdeng.autotrading.domain.backtest.repository.VirtualCoinCandleRepository
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChart
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartByCountParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartByDateTimeParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.service.SpotCoinExchangeService
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.ExchangeModeType
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
    override fun getChartByDateTime(
        param: SpotCoinExchangeChartByDateTimeParam,
        keyParam: ExchangeKeyPair
    ): ExchangeChart {
        val candles = virtualCoinCandleRepository.findAllCoinCandle(
            exchangeType = EXCHANGE_TYPE,
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
            log.warn("백테스트 캔들 조회 결과가 존재하지 않음, exchangeType=$EXCHANGE_TYPE,  unit=${param.candleUnit}, from=${param.from}, to=${param.to}")
        }

        return ExchangeChart(
            from = param.from,
            to = param.to,
            candles = candles,
        )
    }

    override fun getChartByCount(param: SpotCoinExchangeChartByCountParam, keyParam: ExchangeKeyPair): ExchangeChart {
        var candles = emptyList<ExchangeChartCandle>()
        var loopCnt = 0
        // 필요한 캔들 숫자가 조회될때까지 반복
        while (candles.size < param.count) {
            if (loopCnt > 10) {
                throw CriticalException.of("무한 루프 발생 의심되어 에러 발생, coinType=${param.coinType}, candleUnit=${param.candleUnit}")
            }
            val endDateTime =
                param.to.minusSeconds(param.candleUnit.getSecondSize() * param.chunkSize * loopCnt++) // 루프마다 기준점이 달라짐
            val startDateTime = endDateTime.minusSeconds(param.candleUnit.getSecondSize() * (param.chunkSize - 1))
            val chartParam = SpotCoinExchangeChartByDateTimeParam(
                coinType = param.coinType,
                candleUnit = param.candleUnit,
                from = startDateTime,
                to = endDateTime,
            )
            candles = getChartByDateTime(chartParam, keyParam).candles + candles
        }
        candles = candles.takeLast(param.count)

        return ExchangeChart(
            from = candles.first().candleDateTimeKst,
            to = param.to,
            candles = candles,
        )
    }

    override fun getKeyPair(keyPairId: String): ExchangeKeyPair {
        return ExchangeKeyPair(
            exchangeType = EXCHANGE_TYPE,
            keyPairId = "",
            accessKey = "",
            secretKey = "",
        )
    }

    override fun getKeyPairs(userId: Long): List<ExchangeKeyPair> {
        throw WarnException.of("백테스트에서는 지원하지 않는 기능입니다. (key 리스트 조회)")
    }

    override fun registerKeyPair(accessKey: String, secretKey: String, userId: Long): String {
        throw WarnException.of("백테스트에서는 지원하지 않는 기능입니다. (key 등록)")
    }

    override fun removeKeyPair(keyPairId: String): String {
        throw WarnException.of("백테스트에서는 지원하지 않는 기능입니다. (key 삭제)")
    }

    override fun support(exchangeType: ExchangeType, modeType: ExchangeModeType): Boolean {
        return exchangeType == EXCHANGE_TYPE && modeType.isBackTest()
    }

    companion object {
        private val EXCHANGE_TYPE = ExchangeType.UPBIT
        private const val FEE_RATE = 0.05 // upbit 수수료 0.05% 적용
    }
}