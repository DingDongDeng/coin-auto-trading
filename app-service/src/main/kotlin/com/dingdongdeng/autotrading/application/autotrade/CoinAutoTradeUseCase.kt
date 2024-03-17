package com.dingdongdeng.autotrading.application.autotrade

import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeHistory
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeProcessorDto
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeResultDto
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeStatistics
import com.dingdongdeng.autotrading.domain.autotrade.factory.AutoTradeProcessorFactory
import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.process.repository.ProcessRepository
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.annotation.UseCase
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.TimeContext
import com.dingdongdeng.autotrading.infra.common.utils.round

@UseCase
class CoinAutoTradeUseCase(
    private val processRepository: ProcessRepository,
    private val coinTradeService: CoinTradeService,
    private val autoTradeProcessorFactory: AutoTradeProcessorFactory,
) {

    fun register(
        userId: Long,
        coinStrategyType: CoinStrategyType,
        exchangeType: ExchangeType,
        coinTypes: List<CoinType>,
        candleUnits: List<CandleUnit>,
        keyPairId: String,
        config: Map<String, Any>
    ): String {
        // 자동매매 등록
        val processor = autoTradeProcessorFactory.of(
            userId = userId,
            exchangeType = exchangeType,
            coinTypes = coinTypes,
            candleUnits = candleUnits,
            keyPairId = keyPairId,
            config = config,
            coinStrategyType = coinStrategyType,
        )
        return processRepository.save(processor)
    }

    fun getList(userId: Long): List<CoinAutoTradeProcessorDto> {
        val processors = processRepository.findAll(userId)
        return processors.filterIsInstance<CoinAutoTradeProcessor>().map {
            CoinAutoTradeProcessorDto(
                id = it.id,
                userId = it.userId,
                status = it.status,
                duration = it.duration,
                exchangeType = it.exchangeType,
                coinTypes = it.coinTypes,
                config = it.config,
            )
        }
    }

    fun start(autoTradeProcessorId: String): String {
        val processor = processRepository.findById(autoTradeProcessorId)
        processor.start()
        return autoTradeProcessorId
    }

    fun stop(autoTradeProcessorId: String): String {
        val processor = processRepository.findById(autoTradeProcessorId)
        processor.stop()
        return autoTradeProcessorId
    }

    fun terminate(autoTradeProcessorId: String): String {
        val processor = processRepository.findById(autoTradeProcessorId)
        processor.terminate()
        return autoTradeProcessorId
    }

    fun getResult(autoTradeProcessorId: String): CoinAutoTradeResultDto {
        val processor = processRepository.findById(autoTradeProcessorId) as CoinAutoTradeProcessor
        val tradeResult = coinTradeService.getTradeResult(
            exchangeType = processor.exchangeType,
            processorId = processor.id,
            keyPairId = processor.keyPairId,
            coinTypes = processor.coinTypes,
            now = TimeContext.now(),
        )

        return CoinAutoTradeResultDto(
            autoTradeProcessorId = processor.id,
            totalProfitRate = tradeResult.totalProfitRate.round(2.0),
            totalProfitPrice = tradeResult.totalProfitPrice.round(),
            totalAccProfitValuePrice = tradeResult.totalAccProfitPrice.round(),
            totalFee = tradeResult.totalFee.round(),
            tradeHistoriesMap = tradeResult.details
                .map { it.summary }
                .filter { it.tradeHistories.isNotEmpty() }
                .associate {
                    val coinType = it.tradeHistories.first().coinType
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
                .map { it.statistics }
                .associate {
                    val coinType = it.first().coinType
                    val statistics = it.map { stat ->
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