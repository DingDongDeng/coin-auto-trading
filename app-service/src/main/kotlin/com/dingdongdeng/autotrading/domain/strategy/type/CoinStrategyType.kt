package com.dingdongdeng.autotrading.domain.strategy.type

import com.dingdongdeng.autotrading.domain.strategy.component.annotation.GuideDescription
import com.dingdongdeng.autotrading.domain.strategy.component.impl.EmptyStrategyConfig
import com.dingdongdeng.autotrading.domain.strategy.component.impl.ExampleSpotCoinStrategyConfig
import com.dingdongdeng.autotrading.domain.strategy.component.impl.ProtoSpotCoinStrategyConfig
import com.dingdongdeng.autotrading.domain.strategy.component.impl.StrategyConfig
import com.dingdongdeng.autotrading.infra.common.type.DescriptionType
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

enum class CoinStrategyType(
    override val desc: String,
    private val configClass: KClass<out StrategyConfig>,
) : DescriptionType {
    PROTO(
        desc = "연구중",
        configClass = ProtoSpotCoinStrategyConfig::class,
    ),
    EXAMPLE(
        desc = "코인 현물 전략 예제(RSI 매매)",
        configClass = ExampleSpotCoinStrategyConfig::class,
    ),
    UPBIT_CHART_VALIDATE(
        desc = "[테스트 코드 사용] 업비트 차트 기반 벨리데이션",
        configClass = EmptyStrategyConfig::class,
    ),
    ;

    val configMap = createConfigMap()

    private fun createConfigMap(): Map<String, String> {
        return configClass.memberProperties
            .filter { it.findAnnotation<GuideDescription>() != null }
            .associate { it.name to it.findAnnotation<GuideDescription>()!!.desc }
    }
}