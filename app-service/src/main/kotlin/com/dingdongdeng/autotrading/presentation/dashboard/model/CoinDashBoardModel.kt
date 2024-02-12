package com.dingdongdeng.autotrading.presentation.dashboard.model

import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import java.time.LocalDateTime

data class CoinExchangeKeyRegisterRequest(
    val exchangeType: ExchangeType,
    val accessKey: String,
    val secretKey: String,
)

data class CoinAutotradeRegisterRequest(
    val coinStrategyType: CoinStrategyType,
    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val candleUnits: List<CandleUnit>,
    val keyPairId: String,
    val config: Map<String, Any>,
)

data class CoinAutotradeChartLoadRequest(
    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val candleUnits: List<CandleUnit>,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val keyPairId: String,
)

data class CoinBackTestRegisterRequest(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val durationUnit: CandleUnit, // 백테스트 시간 간격
    val coinStrategyType: CoinStrategyType,
    val exchangeType: ExchangeType,
    val coinTypes: List<CoinType>,
    val candleUnits: List<CandleUnit>,
    val config: Map<String, Any>
)