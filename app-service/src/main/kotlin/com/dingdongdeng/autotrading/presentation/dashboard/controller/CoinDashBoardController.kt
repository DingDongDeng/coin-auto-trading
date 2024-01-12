package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.infra.common.web.CommonResponse
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinAutotradeRegisterRequest
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinExchangeKeyRegisterRequest
import com.dingdongdeng.autotrading.usecase.autotrade.AutoTradeUsecase
import com.dingdongdeng.autotrading.usecase.key.KeyUsecase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CoinDashBoardController(
    private val autoTradeUsecase: AutoTradeUsecase,
    private val keyUsecase: KeyUsecase,
) {

    @PostMapping("/exchange-key/register")
    fun exchangeKeyRegister(
        @Valid @RequestBody request: CoinExchangeKeyRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = keyUsecase.registerCoinExchangeKey(
                exchangeType = request.exchangeType,
                accessKey = request.accessKey,
                secretKey = request.secretKey,
                userId = 12345,
            )
        )
    }

    @PostMapping("/autotrade/register")
    fun autotradeRegister(
        @Valid @RequestBody request: CoinAutotradeRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = autoTradeUsecase.registerCoinAutoTrade(
                userId = 12345,
                coinStrategyType = request.coinStrategyType,
                exchangeType = request.exchangeType,
                coinTypes = request.coinTypes,
                candleUnits = request.candleUnits,
                keyPairId = request.keyPairId,
                config = request.config,
            )
        )
    }

    @PostMapping("/autotrade/{autoTradeProcessorId}/start")
    fun autotradeStart(
        @PathVariable autoTradeProcessorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = autoTradeUsecase.start(autoTradeProcessorId)
        )
    }

    @PostMapping("/autotrade/{autoTradeProcessorId}/stop")
    fun autotradeStop(
        @PathVariable autoTradeProcessorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = autoTradeUsecase.stop(autoTradeProcessorId)
        )
    }

    @PostMapping("/autotrade/{autoTradeProcessorId}/terminate")
    fun autotradeTerminate(
        @PathVariable autoTradeProcessorId: String,
    ): CommonResponse<String> {
        return CommonResponse(
            body = autoTradeUsecase.terminate(autoTradeProcessorId)
        )
    }
}