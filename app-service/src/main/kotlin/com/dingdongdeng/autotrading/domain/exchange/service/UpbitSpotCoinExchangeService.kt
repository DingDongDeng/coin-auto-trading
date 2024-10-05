package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.entity.ExchangeKey
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChart
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeChartCandle
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartByCountParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartByDateTimeParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrder
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
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
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeModeType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class UpbitSpotCoinExchangeService(
    private val upbitApiClient: UpbitApiClient,
    private val upbitTokenGenerator: UpbitTokenGenerator,
    private val exchangeKeyRepository: ExchangeKeyRepository,
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
    override fun getChartByDateTime(
        param: SpotCoinExchangeChartByDateTimeParam,
        keyParam: ExchangeKeyPair
    ): ExchangeChart {
        var totalResponse = requestCandles(
            unit = param.candleUnit,
            coinType = param.coinType,
            to = param.to.plusMinutes(2),  // upbit 캔들 조회 응답을 정확히 예측할 수 없기때문에 버퍼를 두어서 조회
            candleUnit = param.candleUnit,
            chunkSize = param.chunkSize,
            keyParam = keyParam,
        )

        // from <= 조회 범위 <= to 를 만족하도록 부족한 캔들을 조회
        while (totalResponse.isNotEmpty()) {
            val firstCandle = totalResponse.first()
            val isCompleted = firstCandle.candleDateTimeKst < param.from
            if (isCompleted) {
                log.info("캔들 조회 완료, exchangeType=${EXCHANGE_TYPE}, coinType=${param.coinType}, unit=${param.candleUnit}, from=${param.from}, to=${param.to}")
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
        // 범위를 넘는 캔들 제거
        totalResponse =
            totalResponse.filter { it.candleDateTimeKst >= param.from && it.candleDateTimeKst <= param.to }

        val resultCandles = totalResponse.map {
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

        if (totalResponse.isEmpty()) {
            log.warn("거래소 캔들 조회 결과가 존재하지 않음, exchangeType=${EXCHANGE_TYPE},  coinType=${param.coinType}, unit=${param.candleUnit}, from=${param.from}, to=${param.to}")
        }

        return ExchangeChart(
            from = param.from,
            to = param.to,
            candles = resultCandles,
        )
    }

    //FIXME 날짜 단위로 쪼개서 조회하게 했는데
    // 이게 아니라 upbit는 개수로 조회하기 핏한 api니까
    // 이를 고려해서 로직 최적화하여 다시 작성 필요
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
        val exchangeKeys = exchangeKeyRepository.findByKeyPairId(keyPairId)
        if (exchangeKeys.size < 2) {
            throw WarnException.of("사용한 거래소 key가 부족하거나 없습니다.")
        }
        return ExchangeKeyPair(
            exchangeType = EXCHANGE_TYPE,
            keyPairId = keyPairId,
            accessKey = exchangeKeys.first { it.name == ACCESS_KEY_NAME }.value,
            secretKey = exchangeKeys.first { it.name == SECRET_KEY_NAME }.value,
        )
    }

    override fun getKeyPairs(userId: Long): List<ExchangeKeyPair> {
        val exchangeKeys = exchangeKeyRepository.findAllByExchangeTypeAndUserId(EXCHANGE_TYPE, userId)
        return exchangeKeys
            .groupBy { it.keyPairId }
            .map {
                ExchangeKeyPair(
                    exchangeType = EXCHANGE_TYPE,
                    keyPairId = it.key,
                    accessKey = it.value.first { key -> key.name == ACCESS_KEY_NAME }.value,
                    secretKey = it.value.first { key -> key.name == SECRET_KEY_NAME }.value,
                )
            }
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

    override fun removeKeyPair(keyPairId: String): String {
        val keys = exchangeKeyRepository.findByKeyPairId(keyPairId)
        exchangeKeyRepository.deleteAll(keys)
        return keyPairId
    }

    override fun support(exchangeType: ExchangeType, modeType: ExchangeModeType): Boolean {
        return exchangeType == EXCHANGE_TYPE && modeType.isProduction()
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
        private val EXCHANGE_TYPE = ExchangeType.UPBIT
        private const val ACCESS_KEY_NAME = "access_key"
        private const val SECRET_KEY_NAME = "secret_key"
    }
}