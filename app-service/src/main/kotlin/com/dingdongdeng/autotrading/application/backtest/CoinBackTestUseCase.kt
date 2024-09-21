package com.dingdongdeng.autotrading.application.backtest

import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestProcessorDto
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestReplayChartDto
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestReplayDto
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestResultDto
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestTradeHistory
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestTradeStatistics
import com.dingdongdeng.autotrading.domain.autotrade.factory.AutoTradeProcessorFactory
import com.dingdongdeng.autotrading.domain.backtest.factory.BackTestProcessorFactory
import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.process.repository.ProcessorRepository
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.annotation.UseCase
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeModeType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.AsyncUtils
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import com.dingdongdeng.autotrading.infra.common.utils.round
import java.time.LocalDateTime

@UseCase
class CoinBackTestUseCase(
    private val processorRepository: ProcessorRepository,
    private val autoTradeProcessorFactory: AutoTradeProcessorFactory,
    private val backTestProcessorFactory: BackTestProcessorFactory,
    private val coinTradeService: CoinTradeService,
    private val coinChartService: CoinChartService,
) {
    fun backTest(
        title: String,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        durationUnit: CandleUnit, // 백테스트 시간 간격
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        config: Map<String, Any>
    ): String {
        val autoTradeProcessor = autoTradeProcessorFactory.of(
            userId = userId,
            title = title,
            exchangeType = exchangeType,
            exchangeModeType = ExchangeModeType.BACKTEST,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            config = config,
            coinStrategyType = coinStrategyType,
        )
        val backTestProcessor = backTestProcessorFactory.of(
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationUnit = durationUnit,
            autoTradeProcessor = autoTradeProcessor,
        )
        processorRepository.save(backTestProcessor)
        backTestProcessor.start()
        return backTestProcessor.id
    }

    fun loadCharts(
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
    ) {
        if (startDateTime > endDateTime) {
            throw WarnException.of("시작 시간은 끝 시간보다 이전이어야 합니다.")
        }
        AsyncUtils.joinAll(coinTypes) { coinType ->
            coinChartService.loadCharts(
                coinType = coinType,
                keyPairId = keyPairId,
                exchangeType = exchangeType,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                candleUnits = candleUnits,
            )
        }
    }

    fun getList(userId: Long): List<CoinBackTestProcessorDto> {
        val processors = processorRepository.findAll(userId)
        return processors.filterIsInstance<CoinBackTestProcessor>().map {
            CoinBackTestProcessorDto(
                id = it.id,
                title = it.title,
                strategyType = it.strategyType,
                userId = it.userId,
                status = it.status,
                duration = it.duration,
                exchangeType = it.exchangeType,
                coinTypes = it.coinTypes,
                config = it.config,
            )
        }
    }

    fun getResult(backTestProcessorId: String): CoinBackTestResultDto {
        val processor = processorRepository.findById(backTestProcessorId) as CoinBackTestProcessor
        val tradeResult = coinTradeService.getTradeResult(
            exchangeType = processor.exchangeType,
            exchangeModeType = ExchangeModeType.BACKTEST,
            processorId = processor.id,
            keyPairId = "",
            coinTypes = processor.coinTypes,
            now = processor.now(),
        )

        return CoinBackTestResultDto(
            title = processor.title,
            backTestProcessorId = processor.id,
            strategyType = processor.strategyType,
            status = processor.status,
            config = processor.config,
            progressRate = processor.progressRate().round(2.0),
            startDateTime = processor.startDateTime,
            endDateTime = processor.endDateTime,
            totalProfitRate = tradeResult.totalProfitRate.round(2.0),
            totalProfitPrice = tradeResult.totalProfitPrice.round(),
            totalAccProfitValuePrice = tradeResult.totalAccProfitPrice.round(),
            totalFee = tradeResult.totalFee.round(),
            tradeHistoriesMap = tradeResult.details
                .map { it.summary }
                .associate {
                    val coinType = it.coinType
                    val histories = it.tradeHistories.map { history ->
                        CoinBackTestTradeHistory(
                            coinType = history.coinType,
                            orderType = history.orderType,
                            volume = history.volume.round(8.0),
                            price = history.price.round(),
                            profit = history.profit.round(),
                            tradeAt = history.tradedAt,
                        )
                    }
                    coinType to histories
                },
            tradeStatisticsMap = tradeResult.details
                .associate {
                    val coinType = it.summary.coinType
                    val statistics = it.statistics.map { stat ->
                        CoinBackTestTradeStatistics(
                            coinType = stat.coinType,
                            from = stat.from,
                            to = stat.to,
                            totalAccProfitPrice = stat.totalAccProfitPrice.round(),
                        )
                    }
                    coinType to statistics
                },
        )

    }

    fun getReplay(
        backTestProcessorId: String,
        replayCandleUnit: CandleUnit,
        replayStartDateTime: LocalDateTime, // replayDateTime 보다 큰 시간대의 정보를 조회
        limit: Int,
    ): CoinBackTestReplayDto {

        val processor = processorRepository.findById(backTestProcessorId) as CoinBackTestProcessor
        val replayEndDateTime = CandleDateTimeUtils
            .makeUnitDateTime(replayStartDateTime, replayCandleUnit)
            .plusSeconds(limit * replayCandleUnit.getSecondSize())

        val charts = processor.coinTypes.map { coinType ->
            CoinBackTestReplayChartDto.of(
                replayStartDateTime = replayStartDateTime,
                exchangeType = processor.exchangeType,
                coinType = coinType,
                chart = coinChartService.getCharts(
                    exchangeType = processor.exchangeType,
                    exchangeModeType = ExchangeModeType.BACKTEST,
                    coinType = coinType,
                    candleUnits = listOf(replayCandleUnit),
                    count = limit,
                    to = replayEndDateTime,
                ).first(),
                tradeSummary = coinTradeService.getTradeSummary(
                    exchangeType = processor.exchangeType,
                    exchangeModeType = ExchangeModeType.BACKTEST,
                    autoTradeProcessorId = processor.id,
                    coinType = coinType,
                    now = replayEndDateTime,
                )
            )
        }

        return CoinBackTestReplayDto(
            backTestProcessorId = processor.id,
            replayStartDateTime = replayStartDateTime,
            replayEndDateTime = replayEndDateTime,
            next = replayEndDateTime < processor.endDateTime,
            charts = charts,
        )
    }
}