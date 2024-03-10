package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.application.auth.AuthUseCase
import com.dingdongdeng.autotrading.application.autotrade.CoinAutoTradeUseCase
import com.dingdongdeng.autotrading.application.backtest.CoinBackTestUseCase
import com.dingdongdeng.autotrading.application.backtest.model.CoinBackTestResultDto
import com.dingdongdeng.autotrading.infra.web.CommonResponse
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinAutotradeChartLoadRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinAutotradeRegisterRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinBackTestRegisterRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinExchangeKeyRegisterRequest
import jakarta.validation.Valid
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

    @PostMapping("/exchange-key/register")
    fun exchangeKeyRegister(
        @Valid @RequestBody request: CoinExchangeKeyRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = authUseCase.registerKey(
                exchangeType = request.exchangeType,
                accessKey = request.accessKey,
                secretKey = request.secretKey,
                userId = 12345, //FIXME
            )
        )
    }

    @PostMapping("/autotrade/register")
    fun autotradeRegister(
        @Valid @RequestBody request: CoinAutotradeRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.register(
                userId = 12345, //FIXME
                coinStrategyType = request.coinStrategyType,
                exchangeType = request.exchangeType,
                coinTypes = request.coinTypes,
                candleUnits = request.candleUnits,
                keyPairId = request.keyPairId,
                config = request.config,
            )
        )
    }

    @PostMapping("/chart/load")
    fun chartLoad(
        @Valid @RequestBody request: CoinAutotradeChartLoadRequest,
    ): CommonResponse<Boolean> {
        coinAutoTradeUseCase.loadCharts(
            exchangeType = request.exchangeType,
            coinTypes = request.coinTypes,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            candleUnits = request.candleUnits,
            keyPairId = request.keyPairId,
        )
        return CommonResponse(true)
    }

    @PostMapping("/autotrade/backtest")
    fun backTest(
        @Valid @RequestBody request: CoinBackTestRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinBackTestUseCase.backTest(
                startDateTime = request.startDateTime,
                endDateTime = request.endDateTime,
                durationUnit = request.durationUnit,
                userId = 1234, //FIXME
                coinStrategyType = request.coinStrategyType,
                exchangeType = request.exchangeType,
                coinTypes = request.coinTypes,
                candleUnits = request.candleUnits,
                config = request.config,
            ),
        )
    }

    @GetMapping("/backtest/{processorId}")
    fun getBackTestResult(@PathVariable processorId: String): CommonResponse<CoinBackTestResultDto> {
        return CommonResponse(
            body = coinBackTestUseCase.getResult(processorId)
        )
    }

    @PostMapping("/autotrade/{autoTradeProcessorId}/start")
    fun autotradeStart(
        @PathVariable autoTradeProcessorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.start(autoTradeProcessorId)
        )
    }

    @PostMapping("/autotrade/{autoTradeProcessorId}/stop")
    fun autotradeStop(
        @PathVariable autoTradeProcessorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.stop(autoTradeProcessorId)
        )
    }

    @PostMapping("/autotrade/{autoTradeProcessorId}/terminate")
    fun autotradeTerminate(
        @PathVariable autoTradeProcessorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.terminate(autoTradeProcessorId)
        )
    }
}