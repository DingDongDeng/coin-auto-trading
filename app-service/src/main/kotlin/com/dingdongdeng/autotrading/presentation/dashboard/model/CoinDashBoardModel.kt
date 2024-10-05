package com.dingdongdeng.autotrading.presentation.dashboard.model

import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CoinExchangeKeyRegisterRequest(
    @field:NotNull(message = "exchangeType는 필수 값입니다.")
    val exchangeType: ExchangeType? = null,

    @field:NotBlank(message = "accessKey는 필수 값입니다.")
    val accessKey: String? = null,

    @field:NotBlank(message = "secretKey는 필수 값입니다.")
    val secretKey: String? = null,
)

data class CoinAutotradeRegisterRequest(
    @field:NotBlank(message = "title은 필수값 입니다.")
    val title: String? = null,

    @field:NotNull(message = "coinStrategyType는 필수 값입니다.")
    val coinStrategyType: CoinStrategyType? = null,

    @field:NotNull(message = "exchangeType는 필수 값입니다.")
    val exchangeType: ExchangeType? = null,

    @field:Size(message = "coinTypes는 최소 1개 이상 요소가 필요합니다.", min = 1)
    @field:NotNull(message = "coinTypes는 필수 값입니다.")
    val coinTypes: List<CoinType>? = null,

    @field:Size(message = "candleUnits는 최소 1개 이상 요소가 필요합니다.", min = 1)
    @field:NotNull(message = "candleUnits는 필수 값입니다.")
    val candleUnits: List<CandleUnit>? = null,

    @field:NotBlank(message = "keyPairId는 필수 값입니다.")
    val keyPairId: String? = null,

    @field:NotNull(message = "config는 필수 값입니다.")
    val config: Map<String, Any>? = null,

    @field:NotNull(message = "duration는 필수 값입니다.")
    @field:Min(value = 1000, message = "duration가 너무 작습니다.")
    val duration: Long? = null,
)

data class CoinAutotradeChartLoadRequest(
    @field:NotNull(message = "exchangeType는 필수 값입니다.")
    val exchangeType: ExchangeType? = null,

    @field:Size(message = "coinTypes는 최소 1개 이상 요소가 필요합니다.", min = 1)
    @field:NotNull(message = "coinTypes는 필수 값입니다.")
    val coinTypes: List<CoinType>? = null,

    @field:Size(message = "candleUnits는 최소 1개 이상 요소가 필요합니다.", min = 1)
    @field:NotNull(message = "candleUnits는 필수 값입니다.")
    val candleUnits: List<CandleUnit>? = null,

    @field:Past(message = "startDateTime는 현재보다 과거 시간이어야 합니다.")
    @field:NotNull(message = "startDateTime는 필수 값입니다.")
    val startDateTime: LocalDateTime? = null,

    @field:Past(message = "endDateTime는 현재보다 과거 시간이어야 합니다.")
    @field:NotNull(message = "endDateTime는 필수 값입니다.")
    val endDateTime: LocalDateTime? = null,

    @field:NotBlank(message = "keyPairId는 필수 값입니다.")
    val keyPairId: String? = null,
)

data class CoinBackTestRegisterRequest(
    @field:NotBlank(message = "title은 필수값 입니다.")
    val title: String? = null,

    @field:Past(message = "endDateTime는 현재보다 과거 시간이어야 합니다.")
    @field:NotNull(message = "endDateTime는 필수 값입니다.")
    val startDateTime: LocalDateTime? = null,

    @field:Past(message = "endDateTime는 현재보다 과거 시간이어야 합니다.")
    @field:NotNull(message = "endDateTime는 필수 값입니다.")
    val endDateTime: LocalDateTime? = null,

    @field:NotNull(message = "durationUnit는 필수 값입니다.")
    val durationUnit: CandleUnit? = null, // 백테스트 시간 간격

    @field:NotNull(message = "coinStrategyType는 필수 값입니다.")
    val coinStrategyType: CoinStrategyType? = null,

    @field:NotNull(message = "exchangeType는 필수 값입니다.")
    val exchangeType: ExchangeType? = null,

    @field:Size(message = "coinTypes는 최소 1개 이상 요소가 필요합니다.", min = 1)
    @field:NotNull(message = "coinTypes는 필수 값입니다.")
    val coinTypes: List<CoinType>? = null,

    @field:Size(message = "candleUnits는 최소 1개 이상 요소가 필요합니다.", min = 1)
    @field:NotNull(message = "candleUnits는 필수 값입니다.")
    val candleUnits: List<CandleUnit>? = null,

    @field:NotNull(message = "config는 필수 값입니다.")
    val config: Map<String, Any>? = null,
)