package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeCandleResult
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartResult
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderResult
import com.dingdongdeng.autotrading.infra.client.upbit.CandleRequest
import com.dingdongdeng.autotrading.infra.client.upbit.CandleResponse
import com.dingdongdeng.autotrading.infra.client.upbit.MarketType
import com.dingdongdeng.autotrading.infra.client.upbit.OrdType
import com.dingdongdeng.autotrading.infra.client.upbit.OrderCancelRequest
import com.dingdongdeng.autotrading.infra.client.upbit.OrderInfoRequest
import com.dingdongdeng.autotrading.infra.client.upbit.OrderRequest
import com.dingdongdeng.autotrading.infra.client.upbit.Side
import com.dingdongdeng.autotrading.infra.client.upbit.UpbitApiClient
import com.dingdongdeng.autotrading.infra.client.upbit.UpbitTokenGenerator
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UpbitSpotCoinExchangeService(
    private val upbitApiClient: UpbitApiClient,
    private val upbitTokenGenerator: UpbitTokenGenerator,
) : SpotCoinExchangeService {

    override fun order(param: SpotCoinExchangeOrderParam, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult {
        val request = OrderRequest(
            market = MarketType.of(param.coinType).code,
            side = Side.of(param.orderType),
            volume = param.volume,
            price = param.price,
            ordType = OrdType.of(param.priceType, param.orderType),
        )
        val response = upbitApiClient.order(request, makeToken(request, keyParam))
        return SpotCoinExchangeOrderResult(
            orderId = response.uuid,
            orderType = response.side.orderType,
            priceType = response.ordType.priceType,
            price = response.price,
            orderState = response.state.orderState,
            coinExchangeType = EXCHANGE_TYPE,
            coinType = MarketType.of(response.market).coinType,
            orderDateTime = response.getCreatedAt(),
        )
    }

    override fun cancel(orderId: String, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult {
        val request = OrderCancelRequest(
            uuid = orderId
        )
        val response = upbitApiClient.orderCancel(request, makeToken(request, keyParam))
        return SpotCoinExchangeOrderResult(
            orderId = response.uuid,
            orderType = response.side.orderType,
            priceType = response.ordType.priceType,
            price = response.price,
            orderState = response.state.orderState,
            coinExchangeType = EXCHANGE_TYPE,
            coinType = MarketType.of(response.market).coinType,
            cancelDateTime = response.createdAt,
        )
    }

    override fun getOrder(orderId: String, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult {
        val request = OrderInfoRequest(uuid = orderId)
        val response = upbitApiClient.getOrderInfo(request, makeToken(request, keyParam))
        return SpotCoinExchangeOrderResult(
            orderId = response.uuid,
            orderType = response.side.orderType,
            priceType = response.ordType.priceType,
            price = response.price,
            orderState = response.state.orderState,
            coinExchangeType = EXCHANGE_TYPE,
            coinType = MarketType.of(response.market).coinType,
            orderDateTime = response.getCreatedAt(),
        )
    }

    // from <= 조회범위 <= to
    override fun getChart(param: SpotCoinExchangeChartParam, keyParam: ExchangeKeyParam): SpotCoinExchangeChartResult {
        var totalResponse = requestCandles(
            unit = param.candleUnit,
            coinType = param.coinType,
            to = param.to.plusMinutes(2),  // upbit 캔들 조회 응답을 정확히 예측할 수 없기때문에 버퍼를 두어서 조회
            candleUnit = param.candleUnit,
            chunkSize = param.chunkSize,
            keyParam = keyParam,
        )

        // from <= 조회 범위 <= to 를 만족하도록 부족한 캔들을 조회
        while (true) {
            val firstCandle = totalResponse.first()
            val isCompleted = firstCandle.candleDateTimeKst.isBefore(param.from)
            if (isCompleted) {
                log.info("캔들 조회 완료, from={}, to={}", param.from, param.to)
                break
            }
            val response = requestCandles(
                unit = param.candleUnit,
                coinType = param.coinType,
                to = firstCandle.candleDateTimeKst,
                candleUnit = param.candleUnit,
                chunkSize = param.chunkSize,
                keyParam = keyParam,
            )
            totalResponse = response + totalResponse
        }

        // TODO 중간에 빠지거나, 중복되는 캔들이 있는지 어떻게 쉽게 확인할수있을까??

        // 범위를 넘는 캔들 제거
        totalResponse =
            totalResponse.filter { (it.candleDateTimeKst.isAfter(param.to) || it.candleDateTimeKst.isBefore(param.from)).not() }

        return SpotCoinExchangeChartResult(
            coinExchangeType = EXCHANGE_TYPE,
            coinType = param.coinType,
            from = param.from,
            to = param.to,
            currentPrice = totalResponse.last().tradePrice,
            candles = totalResponse.map {
                SpotCoinExchangeCandleResult(
                    coinExchangeType = EXCHANGE_TYPE,
                    coinType = param.coinType,
                    candleUnit = param.candleUnit,
                    candleDateTimeUtc = it.candleDateTimeUtc,
                    candleDateTimeKst = it.candleDateTimeKst,
                    openingPrice = it.openingPrice,
                    highPrice = it.highPrice,
                    lowPrice = it.lowPrice,
                    closingPrice = it.tradePrice,
                    accTradePrice = it.candleAccTradePrice,
                    accTradeVolume = it.candleAccTradeVolume,
                )
            }
        )
    }

    override fun support(exchangeType: CoinExchangeType): Boolean {
        return exchangeType == EXCHANGE_TYPE
    }

    private fun requestCandles(
        unit: CandleUnit,
        coinType: CoinType,
        to: LocalDateTime,
        candleUnit: CandleUnit,
        chunkSize: Int,
        keyParam: ExchangeKeyParam,
    ): List<CandleResponse> {
        val request = CandleRequest(
            unit = unit.size,
            market = MarketType.of(coinType).code,
            timeAsKst = to,
            candleUnit = candleUnit,
            count = chunkSize
        )
        return upbitApiClient
            .getCandle(request, makeToken(request, keyParam))
            .sortedBy { it.candleDateTimeKst } // 가장 마지막 캔들이 현재 캔들
    }

    private fun makeToken(request: Any? = null, keyParam: ExchangeKeyParam): String {
        return upbitTokenGenerator.makeToken(request, keyParam.accessKey, keyParam.secretKey)
    }

    companion object {
        val EXCHANGE_TYPE = CoinExchangeType.UPBIT
    }
}