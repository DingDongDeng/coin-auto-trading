package com.dingdongdeng.autotrading.application.backtest

import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyPair
import com.dingdongdeng.autotrading.domain.process.repository.ProcessorRepository
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.log.LoggingUtils
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.test.TestEnv
import com.dingdongdeng.autotrading.test.waitByCondition
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.LocalDateTime

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class CoinBackTestUseCaseTest(
    val suite: CoinBackTestUseCase,
    val processorRepository: ProcessorRepository
) {
    val keyParam = ExchangeKeyPair(
        accessKey = TestEnv.UPBIT_ACCESS_KEY,
        secretKey = TestEnv.UPBIT_SECRET_KEY,
    )

    @DisplayName("[업비트][비트코인][2024-02-27 ~ 2024-02-29] 보조 지표 일관성 검증, 로컬에 데이터 세팅이 되어있어야함")
    @Test
    fun test1() {
        // given
        LoggingUtils.trace()
        val startDateTime = LocalDateTime.of(2023, 2, 15, 0, 0, 0)
        val endDateTime = LocalDateTime.of(2023, 3, 31, 0, 0, 0)
        val durationUnit = CandleUnit.UNIT_1M
        val coinStrategyType = CoinStrategyType.UPBIT_CHART_VALIDATE
        val exchangeType = ExchangeType.BACKTEST_UPBIT
        val coinTypes = listOf(CoinType.BITCOIN)
        val candleUnits = listOf(CandleUnit.UNIT_1M, CandleUnit.UNIT_15M)
        val config = mutableMapOf<String, Any>().apply {
            this["onceTradeAmount"] = 100000
        }

        // when
        val backTestProcessorId = suite.backTest(
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            userId = 12345,
            coinStrategyType = coinStrategyType,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            config = config,
        )
        val backTestProcessor = processorRepository.findById(backTestProcessorId) as CoinBackTestProcessor

        // then
        waitByCondition { backTestProcessor.progressRate() >= 99.99 }
        Assertions.assertEquals(backTestProcessor.status.isStop(), true)
        Assertions.assertEquals(backTestProcessor.status.isFail(), false)
    }
}