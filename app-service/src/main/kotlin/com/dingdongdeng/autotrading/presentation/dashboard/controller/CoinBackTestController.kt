package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.application.backtest.CoinBackTestUseCase
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestProcessorDto
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestResultDto
import com.dingdongdeng.autotrading.infra.web.CommonResponse
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinAutotradeChartLoadRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinBackTestRegisterRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/coin")
@RestController
class CoinBackTestController(
    private val coinBackTestUseCase: CoinBackTestUseCase,
) {

    // 프로세서 백테스트 목록 조회
    @GetMapping("/processor/backtest")
    fun processBackTestList(
        //@SessionAttribute userId: Long,
    ): CommonResponse<List<CoinBackTestProcessorDto>> {
        return CommonResponse(
            body = coinBackTestUseCase.getList(userId = 12345) //FIXME
        )
    }

    // 거래소 차트 데이터 다운로드
    @PostMapping("/chart/load")
    fun chartLoad(
        @Valid @RequestBody request: CoinAutotradeChartLoadRequest,
    ): CommonResponse<Boolean> {
        coinBackTestUseCase.loadCharts(
            exchangeType = request.exchangeType!!,
            coinTypes = request.coinTypes!!,
            startDateTime = request.startDateTime!!,
            endDateTime = request.endDateTime!!,
            candleUnits = request.candleUnits!!,
            keyPairId = request.keyPairId!!,
        )
        return CommonResponse(true)
    }

    // 프로세서 백테스트 등록 및 실행
    @PostMapping("/processor/backtest")
    fun backTest(
        @Valid @RequestBody request: CoinBackTestRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinBackTestUseCase.backTest(
                title = request.title!!,
                startDateTime = request.startDateTime!!,
                endDateTime = request.endDateTime!!,
                durationUnit = request.durationUnit!!,
                userId = 12345, //FIXME
                coinStrategyType = request.coinStrategyType!!,
                exchangeType = request.exchangeType!!,
                coinTypes = request.coinTypes!!,
                candleUnits = request.candleUnits!!,
                config = request.config!!,
            ),
        )
    }

    // 프로세서 백테스트 결과 조회
    @GetMapping("/processor/backtest/{processorId}")
    fun getBackTestResult(@PathVariable processorId: String): CommonResponse<CoinBackTestResultDto> {
        return CommonResponse(
            body = coinBackTestUseCase.getResult(processorId)
        )
    }

    /**
     * 백테스트 차트 조회 API
     * - 코인 타입 받아야함? (아니면 프로세서 ID로? <<< 이게 맞으려나??)
     * - 어떤 캔들로 볼껀지 (백테스트 진행 단위와 무관하게 ㅇㅇ)
     * - 매수/매도 주문도 같이 응답되어야함
     * - 보조지표 포함 여부 (일단은 spec out)
     * - 날짜데이터는 timestamp를 포함해서 내리자 (클라에서 컨버팅하면 너무 힘들어)
     *
     */
}