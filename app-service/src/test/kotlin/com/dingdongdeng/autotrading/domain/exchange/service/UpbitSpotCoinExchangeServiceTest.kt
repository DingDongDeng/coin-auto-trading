package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import com.dingdongdeng.autotrading.test.TestEnv
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.LocalDateTime

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class UpbitSpotCoinExchangeServiceTest(
    private val upbitSpotCoinExchangeService: UpbitSpotCoinExchangeService,
) {
    val keyParam = ExchangeKeyPair(
        accessKey = TestEnv.UPBIT_ACCESS_KEY,
        secretKey = TestEnv.UPBIT_SECRET_KEY,
    )

    @DisplayName("주문, 조회, 주문취소가 문제 없이 동작해야한다.")
    @Test
    fun test1() {
        val orderRequest = SpotCoinExchangeOrderParam(
            coinType = CoinType.ETHEREUM,
            orderType = OrderType.BUY,
            volume = 1.0,
            price = 5000.0,
            priceType = PriceType.LIMIT,
        )
        val orderResponse = upbitSpotCoinExchangeService.order(orderRequest, keyParam)
        log.info("orderResponse={}", orderResponse)
        val getOrderResponse = upbitSpotCoinExchangeService.getOrder(orderResponse.orderId, keyParam)
        log.info("getOrderResponse={}", getOrderResponse)
        val cancelResponse = upbitSpotCoinExchangeService.cancel(orderResponse.orderId, keyParam)
        log.info("cancelResponse={}", cancelResponse)
    }

    @DisplayName("차트 정보를 조회할때 범위는 'from <= 조회범위 <= to'를 만족해야 한다.")
    @Nested
    inner class Test1 {
        @DisplayName("[1분봉] 현재 실시간으로 생성중인 캔들에 대해서도 잘 조회되어야한다. (to=15:05:30 일때, 마지막 캔들은 15:05시간을 갖고 있어야함)")
        @Test
        fun test22() {
            // given
            val now = LocalDateTime.now()
            val param = SpotCoinExchangeChartParam(
                coinType = CoinType.ETHEREUM,
                candleUnit = CandleUnit.UNIT_1M,
                from = now.minusMinutes(5),
                to = now,
                chunkSize = 3
            )

            // when
            val result = upbitSpotCoinExchangeService.getChart(param, keyParam)
            log.info("result={}", result)

            // then
            Assertions.assertEquals(5, result.candles.size)
            Assertions.assertEquals(
                param.from.withSecond(0).withNano(0).plusMinutes(1),
                result.candles.first().candleDateTimeKst
            )
            Assertions.assertEquals(param.to.withSecond(0).withNano(0), result.candles.last().candleDateTimeKst)
        }

        @DisplayName("[1분봉] 과거 캔들들도 적절한 범위로 조회되어야 한다.")
        @Test
        fun test23() {
            // given
            val now = LocalDateTime.of(2023, 12, 25, 0, 0, 0)
            val param = SpotCoinExchangeChartParam(
                coinType = CoinType.ETHEREUM,
                candleUnit = CandleUnit.UNIT_1M,
                from = now.minusMinutes(5),
                to = now,
                chunkSize = 3
            )

            // when
            val result = upbitSpotCoinExchangeService.getChart(param, keyParam)
            log.info("result={}", result)

            // then
            Assertions.assertEquals(6, result.candles.size)
            Assertions.assertEquals(param.from, result.candles.first().candleDateTimeKst)
            Assertions.assertEquals(param.to, result.candles.last().candleDateTimeKst)
        }

        @DisplayName("[5분봉 테스트] 과거 캔들들도 적절한 범위로 조회되어야 한다.")
        @Test
        fun test24() {
            // given
            val now = LocalDateTime.of(2023, 12, 25, 0, 0, 0)
            val param = SpotCoinExchangeChartParam(
                coinType = CoinType.ETHEREUM,
                candleUnit = CandleUnit.UNIT_5M,
                from = now.minusMinutes(53),
                to = now,
                chunkSize = 3
            )

            // when
            val result = upbitSpotCoinExchangeService.getChart(param, keyParam)
            log.info("result={}", result)

            // then
            Assertions.assertEquals(11, result.candles.size)
            Assertions.assertEquals(param.from.plusMinutes(3), result.candles.first().candleDateTimeKst)
            Assertions.assertEquals(param.to, result.candles.last().candleDateTimeKst)
        }

        @DisplayName("[일봉 테스트] 과거 캔들들도 적절한 범위로 조회되어야 한다. (UTC +09 기준이라 생각한거랑 다를 수 있음)")
        @Test
        fun test123() {
            // given
            val now = LocalDateTime.of(2023, 12, 25, 9, 0, 0)
            val param = SpotCoinExchangeChartParam(
                coinType = CoinType.ETHEREUM,
                candleUnit = CandleUnit.UNIT_1D,
                from = now.minusDays(5),
                to = now,
                chunkSize = 3
            )

            // when
            val result = upbitSpotCoinExchangeService.getChart(param, keyParam)
            log.info("result={}", result)

            // then
            Assertions.assertEquals(6, result.candles.size)
            Assertions.assertEquals(param.from, result.candles.first().candleDateTimeKst)
            Assertions.assertEquals(param.to, result.candles.last().candleDateTimeKst)
        }

        @DisplayName("[주봉 테스트] 과거 캔들들도 적절한 범위로 조회되어야 한다. (UTC +09 기준이라 생각한거랑 다를 수 있음)")
        @Test
        fun test124() {
            // given
            val now = LocalDateTime.of(2023, 12, 25, 9, 0, 0)
            val param = SpotCoinExchangeChartParam(
                coinType = CoinType.ETHEREUM,
                candleUnit = CandleUnit.UNIT_1W,
                from = now.minusWeeks(5),
                to = now,
                chunkSize = 3
            )

            // when
            val result = upbitSpotCoinExchangeService.getChart(param, keyParam)
            log.info("result={}", result)

            // then
            Assertions.assertEquals(6, result.candles.size)
            Assertions.assertEquals(param.from, result.candles.first().candleDateTimeKst)
            Assertions.assertEquals(param.to, result.candles.last().candleDateTimeKst)
        }
    }

}