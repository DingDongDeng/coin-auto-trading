package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.application.auth.AuthUseCase
import com.dingdongdeng.autotrading.application.auth.model.AuthExchangeKey
import com.dingdongdeng.autotrading.application.autotrade.CoinAutoTradeUseCase
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeProcessorDto
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeResultDto
import com.dingdongdeng.autotrading.application.backtest.CoinBackTestUseCase
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestProcessorDto
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestResultDto
import com.dingdongdeng.autotrading.infra.web.CommonResponse
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinAutotradeChartLoadRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinAutotradeRegisterRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinBackTestRegisterRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinExchangeKeyRegisterRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/coin")
@RestController
class CoinDashBoardController(
    private val coinAutoTradeUseCase: CoinAutoTradeUseCase,
    private val coinBackTestUseCase: CoinBackTestUseCase,
    private val authUseCase: AuthUseCase,
) {

    // 거래소 키 조회
    @GetMapping("/exchange-key")
    fun getExchangeKeys(
        //@SessionAttribute userId: Long,
    ): CommonResponse<List<AuthExchangeKey>> {
        return CommonResponse(
            body = authUseCase.getKeys(userId = 12345) //FIXME
        )
    }

    // 거래소 키 등록
    @PostMapping("/exchange-key")
    fun exchangeKeyRegister(
        @Valid @RequestBody request: CoinExchangeKeyRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = authUseCase.registerKey(
                exchangeType = request.exchangeType!!,
                accessKey = request.accessKey!!,
                secretKey = request.secretKey!!,
                userId = 12345, //FIXME
            )
        )
    }


    // 거래소 키 삭제
    @DeleteMapping("/exchange-key/{keyPairId}")
    fun exchangeKeyRemove(
        @PathVariable keyPairId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = authUseCase.removeKey(keyPairId)
        )
    }

    // 자동매매 프로세서 등록
    @PostMapping("/processor/autotrade")
    fun autotradeRegister(
        @Valid @RequestBody request: CoinAutotradeRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.register(
                userId = 12345, //FIXME
                coinStrategyType = request.coinStrategyType!!,
                exchangeType = request.exchangeType!!,
                coinTypes = request.coinTypes!!,
                candleUnits = request.candleUnits!!,
                keyPairId = request.keyPairId!!,
                config = request.config!!,
            )
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

    // 프로세서 자동매매 등록
    @GetMapping("/processor/autotrade")
    fun processAutoTradeList(
        //@SessionAttribute userId: Long,
    ): CommonResponse<List<CoinAutoTradeProcessorDto>> {
        return CommonResponse(
            body = coinAutoTradeUseCase.getList(userId = 12345) //FIXME
        )
    }

    // 프로세서 백테스트 목록 조회
    @GetMapping("/processor/backtest")
    fun processBackTestList(
        //@SessionAttribute userId: Long,
    ): CommonResponse<List<CoinBackTestProcessorDto>> {
        return CommonResponse(
            body = coinBackTestUseCase.getList(userId = 12345) //FIXME
        )
    }

    // 프로세서 실행
    @PostMapping("/processor/{processorId}/start")
    fun processorStart(
        @PathVariable processorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.start(processorId)
        )
    }

    // 프로세서 정지
    @PostMapping("/processor/{processorId}/stop")
    fun processorStop(
        @PathVariable processorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.stop(processorId)
        )
    }

    // 프로세서 제거
    @DeleteMapping("/processor/{processorId}/terminate")
    fun processorTerminate(
        @PathVariable processorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.terminate(processorId)
        )
    }

    // 자동매매 프로세서 결과 조회
    @GetMapping("/processor/autotrade/{processorId}")
    fun getAutoTradeResult(@PathVariable processorId: String): CommonResponse<CoinAutoTradeResultDto> {
        return CommonResponse(
            body = coinAutoTradeUseCase.getResult(processorId)
        )
    }
}