package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.application.backtest.CoinBackTestUseCase
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestProcessorDto
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestReplayDto
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestResultDto
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.web.CommonResponse
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinAutotradeChartLoadRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinBackTestRegisterRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

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

    @GetMapping("processor/backtest/{processorId}/replay")
    fun getBackTestCharts(
        @PathVariable processorId: String,
        @RequestParam replayCandleUnit: CandleUnit,
        @RequestParam replayStartDateTime: LocalDateTime,
        @RequestParam(defaultValue = "1000") limit: Int,
    ): CommonResponse<CoinBackTestReplayDto> {
        return CommonResponse(
            body = coinBackTestUseCase.getReplay(
                backTestProcessorId = processorId,
                replayCandleUnit = replayCandleUnit,
                replayStartDateTime = replayStartDateTime,
                limit = limit,
            ),
        )
    }
}