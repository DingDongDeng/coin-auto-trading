package com.dingdongdeng.autotrading.domain.chart.service

import com.dingdongdeng.autotrading.application.auth.AuthUseCase
import com.dingdongdeng.autotrading.infra.common.log.LoggingUtils
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeModeType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import com.dingdongdeng.autotrading.test.TestEnv
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.LocalDateTime

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class CoinChartServiceTest(
    val coinChartService: CoinChartService,
    val authUseCase: AuthUseCase,
) {

    @DisplayName("코인 차트를 조회할때 캔들의 개수를 정확히 조회할 수 있어야한다. [1분봉]")
    @Test
    fun test1() {
        LoggingUtils.trace()
        val now = LocalDateTime.of(2024, 8, 13, 13, 14, 59)
        val exchangeType = ExchangeType.UPBIT
        val coinType = CoinType.BITCOIN
        val keyPairId = authUseCase.registerKey(
            exchangeType = exchangeType,
            accessKey = TestEnv.UPBIT_ACCESS_KEY,
            secretKey = TestEnv.UPBIT_SECRET_KEY,
            userId = 12345
        )
        val charts = coinChartService.getCharts(
            exchangeType = ExchangeType.UPBIT,
            exchangeModeType = ExchangeModeType.PRODUCTION,
            keyPairId = keyPairId,
            coinType = coinType,
            candleUnits = listOf(CandleUnit.UNIT_1M),
            count = 201,
            to = now
        )

        Assertions.assertEquals(
            charts.first().getLast(1).candleDateTimeKst,
            CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_1M)
        )
        Assertions.assertEquals(charts.first().candles.size, 201)
    }

    @DisplayName("코인 차트를 조회할때 캔들의 개수를 정확히 조회할 수 있어야한다. [15분봉]")
    @Test
    fun test2() {
        LoggingUtils.trace()
        val now = LocalDateTime.of(2024, 8, 13, 13, 14, 59)
        val exchangeType = ExchangeType.UPBIT
        val coinType = CoinType.BITCOIN
        val keyPairId = authUseCase.registerKey(
            exchangeType = exchangeType,
            accessKey = TestEnv.UPBIT_ACCESS_KEY,
            secretKey = TestEnv.UPBIT_SECRET_KEY,
            userId = 12345
        )
        val charts = coinChartService.getCharts(
            exchangeType = ExchangeType.UPBIT,
            exchangeModeType = ExchangeModeType.PRODUCTION,
            keyPairId = keyPairId,
            coinType = coinType,
            candleUnits = listOf(CandleUnit.UNIT_15M),
            count = 201,
            to = now
        )

        Assertions.assertEquals(
            charts.first().getLast(1).candleDateTimeKst,
            CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_15M)
        )
        Assertions.assertEquals(charts.first().candles.size, 201)
    }
}