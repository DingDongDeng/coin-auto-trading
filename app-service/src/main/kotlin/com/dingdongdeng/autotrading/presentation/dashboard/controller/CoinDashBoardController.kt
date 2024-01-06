package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.infra.common.web.CommonResponse
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinDashBoardRequest
import com.dingdongdeng.autotrading.usecase.autotrade.CoinAutoTradeUsecase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute

@RestController
class CoinDashBoardController(
    private val coinAutoTradeUsecase: CoinAutoTradeUsecase,
) {

    @PostMapping("/user/exchange-key/register")
    fun register(

    ): CommonResponse<String> {
        return CommonResponse(

        )
    }

    @PostMapping("/user/autotrade/register")
    fun register(
        @Valid @RequestBody request: CoinDashBoardRequest,
        @SessionAttribute userId: Long
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUsecase.registerCoinAutoTrade(
                userId = userId,
                coinStrategyType = request.coinStrategyType,
                exchangeType = request.exchangeType,
                coinTypes = request.coinTypes,
                candleUnits = request.candleUnits,
                keyPairId = request.keyPairId,
            )
        )
    }
}