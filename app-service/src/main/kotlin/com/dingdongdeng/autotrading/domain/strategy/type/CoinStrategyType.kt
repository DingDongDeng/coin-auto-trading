package com.dingdongdeng.autotrading.domain.strategy.type

import com.dingdongdeng.autotrading.domain.strategy.component.impl.EmptyStrategyConfig
import com.dingdongdeng.autotrading.domain.strategy.component.impl.ExampleSpotCoinStrategyConfig
import com.dingdongdeng.autotrading.domain.strategy.component.impl.StrategyConfig
import com.dingdongdeng.autotrading.infra.common.type.DescriptionType
import kotlin.reflect.KClass

enum class CoinStrategyType(
    override val desc: String,
    val config: KClass<out StrategyConfig>,
) : DescriptionType {
    EXAMPLE(
        desc = "코인 현물 전략 예제(RSI 매매)",
        config = ExampleSpotCoinStrategyConfig::class,
    ),
    UPBIT_CHART_VALIDATE(
        desc = "[테스트 코드 사용] 업비트 차트 기반 벨리데이션",
        config = EmptyStrategyConfig::class,
    ),
    ;
}