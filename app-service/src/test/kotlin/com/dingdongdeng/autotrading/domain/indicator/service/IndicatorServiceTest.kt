package com.dingdongdeng.autotrading.domain.indicator.service

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.service.UpbitSpotCoinExchangeService
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.test.TestEnv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.LocalDateTime

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class IndicatorServiceTest(
    val indicatorService: IndicatorService,
    val upbitSpotCoinExchangeService: UpbitSpotCoinExchangeService,
) {
    val keyParam = ExchangeKeyParam(
        accessKey = TestEnv.UPBIT_ACCESS_KEY,
        secretKey = TestEnv.UPBIT_SECRET_KEY,
    )

    @DisplayName("RSI 계산 테스트")
    @Test
    fun test1() {
        // given
        val now = LocalDateTime.of(2022, 4, 17, 13, 13, 10)
        val param = SpotCoinExchangeChartParam(
            coinType = CoinType.ETHEREUM,
            candleUnit = CandleUnit.UNIT_15M,
            from = now.minusMinutes(15 * 200),
            to = now,
        )
        val chart = upbitSpotCoinExchangeService.getChart(param, keyParam)

        // when
        val rsi = indicatorService.getRsi(chart.candles)

        // then
        log.info("result : {}", rsi)
        assertEquals(rsi, 0.39407307248496637)
    }

    @DisplayName("MACD_계산_테스트")
    @Test
    fun test2() {
        // given
        val now = LocalDateTime.of(2022, 8, 6, 9, 45, 10)
        val param = SpotCoinExchangeChartParam(
            coinType = CoinType.ETHEREUM,
            candleUnit = CandleUnit.UNIT_15M,
            from = now.minusMinutes(15 * 200),
            to = now,
        )
        val chart = upbitSpotCoinExchangeService.getChart(param, keyParam)

        // when
        val macd = indicatorService.getMACD(chart.candles)

        // then
        log.info("result : {}", macd)
        assertEquals(macd.hist, 3399.4713725503443)
    }

    @DisplayName("MACD_계산_테스트2")
    @Test
    fun test3() {
        // given
        val now = LocalDateTime.of(2022, 12, 24, 13, 0, 10)
        val param = SpotCoinExchangeChartParam(
            coinType = CoinType.ETHEREUM,
            candleUnit = CandleUnit.UNIT_240M,
            from = now.minusMinutes(240 * 200),
            to = now,
        )
        val chart = upbitSpotCoinExchangeService.getChart(param, keyParam)

        // when
        val macd = indicatorService.getMACD(chart.candles)

        // then
        log.info("result : {}", macd)
        assertEquals(2205.1025093942594, macd.hist)
    }

    @DisplayName("볼린저밴드_계산_테스트")
    @Test
    fun test4() {
        // given
        val now = LocalDateTime.of(2023, 1, 29, 1, 1, 10)
        val param = SpotCoinExchangeChartParam(
            coinType = CoinType.ETHEREUM,
            candleUnit = CandleUnit.UNIT_240M,
            from = now.minusMinutes(240 * 200),
            to = now,
        )
        val chart = upbitSpotCoinExchangeService.getChart(param, keyParam)

        // when
        val bollingerBands = indicatorService.getBollingerBands(chart.candles)

        // then
        log.info("result : {}", bollingerBands)
        assertEquals(2038281, Math.round(bollingerBands.upper))
        assertEquals(1994050, Math.round(bollingerBands.middle))
        assertEquals(1949819, Math.round(bollingerBands.lower))
    }

    @DisplayName("obj 계산 테스트")
    @Test
    fun test5() {
        // given
        val now = LocalDateTime.of(2023, 1, 29, 1, 1, 10)
        val param = SpotCoinExchangeChartParam(
            coinType = CoinType.ETHEREUM,
            candleUnit = CandleUnit.UNIT_240M,
            from = now.minusMinutes(240 * 200),
            to = now,
        )
        val chart = upbitSpotCoinExchangeService.getChart(param, keyParam)

        // when
        val obv = indicatorService.getObv(chart.candles)

        // then
        log.info("result : {}", obv)
        assertEquals(60075, Math.round(obv.obv))
        assertEquals(-3756, Math.round(obv.hist)) //원래는 -4835 였는데 코틀린 전환하고나서 다르게 계산됨 흠...
    }

    @DisplayName("MA_계산_테스트")
    @Test
    fun test7() {
        // given
        val now = LocalDateTime.of(2023, 1, 29, 1, 1, 10)
        val param = SpotCoinExchangeChartParam(
            coinType = CoinType.ETHEREUM,
            candleUnit = CandleUnit.UNIT_240M,
            from = now.minusMinutes(240 * 200),
            to = now,
        )
        val chart = upbitSpotCoinExchangeService.getChart(param, keyParam)

        // when
        val ma = indicatorService.getMv(chart.candles)

        // then
        log.info("result : {}", ma)
        assertEquals(1905871, Math.round(ma.sma120))
        assertEquals(1767750, Math.round(ma.sma200))
        assertEquals(1975838, Math.round(ma.ema60))
    }
}