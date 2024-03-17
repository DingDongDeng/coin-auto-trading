package com.dingdongdeng.autotrading.presentation.dashboard.model

import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CoinExchangeKeyRegisterRequest(
    val exchangeType: ExchangeType,
    val accessKey: String,
    val secretKey: String,
)

data class CoinAutotradeRegisterRequest(
    @field:NotNull(message = "coinStrategyType는 필수 입력값입니다.")
    val coinStrategyType: CoinStrategyType? = null,
    @field:NotNull(message = "exchangeType는 필수 입력값입니다.")
    val exchangeType: ExchangeType? = null,
    @field:Size(message = "coinTypes는 최소 1개 이상 요소가 필요합니다.", min = 1)
    @field:NotNull(message = "coinTypes는 필수 입력값입니다.")
    val coinTypes: List<CoinType>? = null,
    @field:Size(message = "candleUnits는 최소 1개 이상 요소가 필요합니다.", min = 1)
    @field:NotNull(message = "candleUnits는 필수 입력값입니다.")
    val candleUnits: List<CandleUnit>? = null,
    @field:NotNull(message = "keyPairId는 필수 입력값입니다.")
    val keyPairId: String? = null,
    @field:NotNull(message = "config는 필수 입력값입니다.")
    val config: Map<String, Any>? = null,
) {
}

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