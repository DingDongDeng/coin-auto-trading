package com.dingdongdeng.autotrading.domain.strategy.type

import com.dingdongdeng.autotrading.infra.common.type.DescriptionType

enum class CoinStrategyType(
    override val desc: String,
) : DescriptionType {
    EXAMPLE("코인 현물 전략 예제(RSI 매매)"),
    UPBIT_CHART_VALIDATE("백테스트 환경 교차 검증을 위한 업비트 차트 임의 구간 비교"),
    ;
}