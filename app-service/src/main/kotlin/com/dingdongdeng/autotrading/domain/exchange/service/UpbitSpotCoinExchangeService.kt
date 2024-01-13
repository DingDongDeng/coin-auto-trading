package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.entity.ExchangeCandle
import com.dingdongdeng.autotrading.domain.exchange.entity.ExchangeKey
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChart
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.repository.ExchangeCandleRepository
import com.dingdongdeng.autotrading.domain.exchange.repository.ExchangeKeyRepository
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
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UpbitSpotCoinExchangeService(
    private val upbitApiClient: UpbitApiClient,
    private val upbitTokenGenerator: UpbitTokenGenerator,
    private val exchangeKeyRepository: ExchangeKeyRepository,
    private val exchangeCandleRepository: ExchangeCandleRepository,
) : SpotCoinExchangeService {

    override fun order(param: SpotCoinExchangeOrderParam, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder {
        val request = OrderRequest(
            market = MarketType.of(param.coinType).code,
            side = Side.of(param.orderType),
            volume = param.volume,
            price = param.price,
            ordType = OrdType.of(param.priceType, param.orderType),
        )
        val response = upbitApiClient.order(request, makeToken(request, keyParam))
        return SpotCoinExchangeOrder(
            orderId = response.uuid,
            orderType = response.side.orderType,
            priceType = response.ordType.priceType,
            price = response.price,
            volume = response.volume,
            tradeState = response.state.tradeState,
            exchangeType = EXCHANGE_TYPE,
            coinType = MarketType.of(response.market).coinType,
            fee = response.paidFee,
            orderDateTime = response.getCreatedAt(),
        )
    }

    override fun cancel(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder {
        val request = OrderCancelRequest(
            uuid = orderId
        )
        val response = upbitApiClient.orderCancel(request, makeToken(request, keyParam))
        return SpotCoinExchangeOrder(
            orderId = response.uuid,
            orderType = response.side.orderType,
            priceType = response.ordType.priceType,
            price = response.price,
            volume = response.volume,
            tradeState = response.state.tradeState,
            exchangeType = EXCHANGE_TYPE,
            coinType = MarketType.of(response.market).coinType,
            fee = response.paidFee,
            cancelDateTime = response.createdAt,
        )
    }

    override fun getOrder(orderId: String, keyParam: ExchangeKeyPair): SpotCoinExchangeOrder {
        val request = OrderInfoRequest(uuid = orderId)
        val response = upbitApiClient.getOrderInfo(request, makeToken(request, keyParam))
        return SpotCoinExchangeOrder(
            orderId = response.uuid,
            orderType = response.side.orderType,
            priceType = response.ordType.priceType,
            price = response.price,
            volume = response.volume,
            tradeState = response.state.tradeState,
            exchangeType = EXCHANGE_TYPE,
            coinType = MarketType.of(response.market).coinType,
            fee = response.paidFee,
            orderDateTime = response.getCreatedAt(),
        )
    }

    // from <= 조회범위 <= to
    override fun getChart(param: SpotCoinExchangeChartParam, keyParam: ExchangeKeyPair): ExchangeChart {
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

        return ExchangeChart(
            from = param.from,
            to = param.to,
            currentPrice = totalResponse.last().tradePrice,
            candles = totalResponse.map {
                ExchangeChartCandle(
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

    // from <= 저장 범위 <= to
    override fun loadChart(
        param: SpotCoinExchangeChartParam,
        keyParam: ExchangeKeyPair
    ) {
        val from = param.from
        val to = param.to

        val count = exchangeCandleRepository.countByExchangeCandle(
            exchangeType = EXCHANGE_TYPE,
            coinType = param.coinType,
            unit = param.candleUnit,
            startDateTime = param.from,
            endDateTime = param.to,
        )

        if (count > 0) {
            throw WarnException(userMessage = "이미 캔들이 존재하는 구간입니다. $from ~ $to")
        }

        var now = to
        while (true) {
            val isCompleted = now.isBefore(from) || now.isEqual(from)
            if (isCompleted) {
                log.info("캔들 저장 완료, from={}, to={}", from, to)
                break
            }
            val response = requestCandles(
                unit = param.candleUnit,
                coinType = param.coinType,
                to = now.plusMinutes(2), // 조회 범위 버퍼
                candleUnit = param.candleUnit,
                chunkSize = param.chunkSize,
                keyParam = keyParam,
            ).filter { it.candleDateTimeKst.isAfter(now).not() } // 버퍼에 의해 추가로 검색된 범위 필터

            exchangeCandleRepository.saveAll(
                response
                    .filter {
                        it.candleDateTimeKst.isEqual(to) // <= to 범위를 위해 필터 (엣지 범위)
                                || it.candleDateTimeKst.isEqual(now).not() // 범위 < now 형태로 만들어서 이전 검색 범위와 겹치지 않도록함
                                && it.candleDateTimeKst.isBefore(from).not() // from <= 범위를 위해 필터 (엣지 범위)
                    }
                    .map {
                        ExchangeCandle(
                            exchangeType = EXCHANGE_TYPE,
                            coinType = param.coinType,
                            unit = param.candleUnit,
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

            now = response.first().candleDateTimeKst
        }
    }

    override fun getKeyPair(keyPairId: String): ExchangeKeyPair {
        val exchangeKeys = exchangeKeyRepository.findByExchangeTypeAndKeyPairId(EXCHANGE_TYPE, keyPairId)
        return ExchangeKeyPair(
            accessKey = exchangeKeys.first { it.name == ACCESS_KEY_NAME }.value,
            secretKey = exchangeKeys.first { it.name == SECRET_KEY_NAME }.value,
        )
    }

    override fun registerKeyPair(accessKey: String, secretKey: String, userId: Long): String {
        val keyPairId = UUID.randomUUID().toString()
        val accessExchangeKey = ExchangeKey(
            keyPairId = keyPairId,
            exchangeType = EXCHANGE_TYPE,
            name = ACCESS_KEY_NAME,
            value = accessKey,
            userId = userId,
        )
        val secretExchangeKey = ExchangeKey(
            keyPairId = keyPairId,
            exchangeType = EXCHANGE_TYPE,
            name = SECRET_KEY_NAME,
            value = secretKey,
            userId = userId,
        )
        exchangeKeyRepository.saveAll(
            listOf(accessExchangeKey, secretExchangeKey)
        )
        return keyPairId
    }

    override fun support(exchangeType: ExchangeType): Boolean {
        return exchangeType == EXCHANGE_TYPE
    }

    private fun requestCandles(
        unit: CandleUnit,
        coinType: CoinType,
        to: LocalDateTime,
        candleUnit: CandleUnit,
        chunkSize: Int,
        keyParam: ExchangeKeyPair,
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

    private fun makeToken(request: Any? = null, keyParam: ExchangeKeyPair): String {
        return upbitTokenGenerator.makeToken(request, keyParam.accessKey, keyParam.secretKey)
    }

    companion object {
        val EXCHANGE_TYPE = ExchangeType.UPBIT
        const val ACCESS_KEY_NAME = "access_key"
        const val SECRET_KEY_NAME = "secret_key"
    }
}