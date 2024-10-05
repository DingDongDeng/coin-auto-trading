package com.dingdongdeng.autotrading.application.autotrade

import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeHistory
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeProcessorDto
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeResultDto
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeStatistics
import com.dingdongdeng.autotrading.domain.autotrade.factory.AutoTradeProcessorFactory
import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.process.repository.ProcessorRepository
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.annotation.UseCase
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeModeType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.round

@UseCase
class CoinAutoTradeUseCase(
    private val processorRepository: ProcessorRepository,
    private val coinTradeService: CoinTradeService,
    private val autoTradeProcessorFactory: AutoTradeProcessorFactory,
) {

    fun register(
        userId: Long,
        title: String,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        config: Map<String, Any>,
        duration: Long,
    ): String {
        // 자동매매 등록
        val processor = autoTradeProcessorFactory.of(
            userId = userId,
            title = title,
            exchangeType = exchangeType,
            exchangeModeType = ExchangeModeType.PRODUCTION,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            keyPairId = keyPairId,
            config = config,
            coinStrategyType = coinStrategyType,
            duration = duration,
        )
        return processorRepository.save(processor)
    }

    fun getList(userId: Long): List<CoinAutoTradeProcessorDto> {
        val processors = processorRepository.findAll(userId)
        return processors.filterIsInstance<CoinAutoTradeProcessor>().map {
            CoinAutoTradeProcessorDto(
                id = it.id,
                title = it.title,
                userId = it.userId,
                status = it.status,
                duration = it.duration,
                exchangeType = it.exchangeType,
                strategyType = it.strategyType,
                coinTypes = it.coinTypes,
                config = it.config,
            )
        }
    }

    fun start(autoTradeProcessorId: String): String {
        val processor = processorRepository.findById(autoTradeProcessorId)
        processor.start()
        return autoTradeProcessorId
    }

    fun stop(autoTradeProcessorId: String): String {
        val processor = processorRepository.findById(autoTradeProcessorId)
        processor.stop()
        return autoTradeProcessorId
    }

    fun terminate(autoTradeProcessorId: String): String {
        val processor = processorRepository.findById(autoTradeProcessorId)
        processor.terminate()
        processorRepository.delete(processor)
        return autoTradeProcessorId
    }

    fun getResult(autoTradeProcessorId: String): CoinAutoTradeResultDto {
        val processor = processorRepository.findById(autoTradeProcessorId) as CoinAutoTradeProcessor
        val tradeResult = coinTradeService.getTradeResult(
            exchangeType = processor.exchangeType,
            exchangeModeType = ExchangeModeType.PRODUCTION,
            processorId = processor.id,
            keyPairId = processor.keyPairId,
            coinTypes = processor.coinTypes,
            now = TimeContext.now(),
        )

        return CoinAutoTradeResultDto(
            title = processor.title,
            autoTradeProcessorId = processor.id,
            strategyType = processor.strategyType,
            status = processor.status,
            config = processor.config,
            totalProfitRate = tradeResult.totalProfitRate.round(2.0),
            totalProfitPrice = tradeResult.totalProfitPrice.round(),
            totalAccProfitValuePrice = tradeResult.totalAccProfitPrice.round(),
            totalFee = tradeResult.totalFee.round(),
            tradeHistoriesMap = tradeResult.details
                .map { it.summary }
                .associate {
                    val coinType = it.coinType
                    val histories = it.tradeHistories.map { history ->
                        CoinAutoTradeHistory(
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
                        CoinAutoTradeStatistics(
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
}