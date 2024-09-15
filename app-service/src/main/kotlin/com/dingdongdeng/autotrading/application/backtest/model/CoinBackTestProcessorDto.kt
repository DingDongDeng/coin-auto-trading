package com.dingdongdeng.autotrading.application.backtest.model

import com.dingdongdeng.autotrading.domain.process.type.ProcessStatus
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

data class CoinBackTestProcessorDto(
    val id: String,
    val title: String,
    val strategyType: CoinStrategyType,
    val userId: Long,
    val status: ProcessStatus,
    val duration: Long, // milliseconds
    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val config: Map<String, Any>,
)
