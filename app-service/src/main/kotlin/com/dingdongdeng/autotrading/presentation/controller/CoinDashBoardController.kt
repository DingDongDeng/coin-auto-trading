package com.dingdongdeng.autotrading.presentation.controller

import com.dingdongdeng.autotrading.presentation.common.CommonResponse
import com.dingdongdeng.autotrading.usecase.dashboard.model.CoinAutotradeRegisterRequest
import com.dingdongdeng.autotrading.usecase.dashboard.service.CoinDashBoardService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute

@RestController
class CoinDashBoardController(
    private val coinDashBoardService: CoinDashBoardService,
) {

    @PostMapping("/user/exchange-key/register")
    fun register(

    ): CommonResponse<String> {
        return CommonResponse(

        )
    }

    @PostMapping("/user/autotrade/register")
    fun register(
        @Valid @RequestBody request: CoinAutotradeRegisterRequest,
        @SessionAttribute userId: Long
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinDashBoardService.registerCoinAutoTrade(
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