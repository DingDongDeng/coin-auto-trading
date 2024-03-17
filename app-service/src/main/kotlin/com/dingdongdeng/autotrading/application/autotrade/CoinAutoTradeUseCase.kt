package com.dingdongdeng.autotrading.application.autotrade

import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeProcessorDto
import com.dingdongdeng.autotrading.domain.autotrade.factory.AutoTradeProcessorFactory
import com.dingdongdeng.autotrading.domain.autotrade.model.CoinAutoTradeProcessor
import com.dingdongdeng.autotrading.domain.process.repository.ProcessRepository
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.annotation.UseCase
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

@UseCase
class CoinAutoTradeUseCase(
    private val processRepository: ProcessRepository,
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

    fun start(processorId: String): String {
        val processor = processRepository.findById(processorId)
        processor.start()
        return processorId
    }

    fun stop(processorId: String): String {
        val processor = processRepository.findById(processorId)
        processor.stop()
        return processorId
    }

    fun terminate(processorId: String): String {
        val processor = processRepository.findById(processorId)
        processor.terminate()
        return processorId
    }
}