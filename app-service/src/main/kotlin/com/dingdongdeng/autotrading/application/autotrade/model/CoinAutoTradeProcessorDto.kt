package com.dingdongdeng.autotrading.application.autotrade.model

import com.dingdongdeng.autotrading.domain.process.type.ProcessStatus
import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType

data class CoinAutoTradeProcessorDto(
    val id: String,
    val title: String,
    val userId: Long,
    val status: ProcessStatus,
    val duration: Long, // milliseconds
    val exchangeType: ExchangeType,
    val strategyType: CoinStrategyType,
    val coinTypes: List<CoinType>,
    val config: Map<String, Any>,
)
