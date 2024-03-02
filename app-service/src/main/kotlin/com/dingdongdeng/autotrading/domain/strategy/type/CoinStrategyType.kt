package com.dingdongdeng.autotrading.domain.strategy.type

enum class CoinStrategyType(
    val desc: String,
) {
    EXAMPLE("코인 현물 전략 예제"),
    UPBIT_CHART_VALIDATE("백테스트 환경 교차 검증을 위한 업비트 차트 임의 구간 비교"),
    ;
}