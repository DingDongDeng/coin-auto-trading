package com.dingdongdeng.autotrading.domain.backtest.service

import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestProcessor
import com.dingdongdeng.autotrading.domain.backtest.model.CoinBackTestResult
import com.dingdongdeng.autotrading.domain.chart.service.CoinChartService
import com.dingdongdeng.autotrading.domain.trade.service.CoinTradeService
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import org.springframework.stereotype.Service

@Service
class CoinBackTestService(
    private val coinTradeService: CoinTradeService,
    private val coinChartService: CoinChartService,
) {

    fun getResults(coinBackTestProcessor: CoinBackTestProcessor): CoinBackTestResult {
        /**
         * FIXME 아래 내용을 해보자
         *  - 백테스트 결과 조회
         *      - 진행율


         *      - 주문 리스트 (매수, 익절, 손절)
         *      - 시물레이션한 차트 리스트
         *          - 캔들 정보
         *          - 보조지표 정보
         *          - 주문정보
         */

        coinTypes.map { coinType ->
            //FIXME 아래에 시간을 못넣으니까 불편하네 스펙 수정해야겠다
            val charts = coinChartService.getCharts(exchangeType, "", coinType, listOf(CandleUnit.min()))
            coinTradeService.getTradeInfo(id, coinType, charts.first().currentPrice)
        }
    }
}