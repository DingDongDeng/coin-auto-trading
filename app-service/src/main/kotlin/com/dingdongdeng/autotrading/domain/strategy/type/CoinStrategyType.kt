package com.dingdongdeng.autotrading.domain.strategy.type

import com.dingdongdeng.autotrading.infra.common.type.DescriptionType

enum class CoinStrategyType(
    override val desc: String,
) : DescriptionType {
    EXAMPLE("코인 현물 전략 예제(RSI 매매)"),
    UPBIT_CHART_VALIDATE("[테스트 코드 사용] 업비트 차트 기반 벨리데이션"),
    ;
}