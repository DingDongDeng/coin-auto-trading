package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.infra.common.web.CommonResponse
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinDashBoardRequest
import com.dingdongdeng.autotrading.usecase.autotrade.AutoTradeUsecase
import com.dingdongdeng.autotrading.usecase.key.KeyUsecase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute

@RestController
class CoinDashBoardController(
    private val autoTradeUsecase: AutoTradeUsecase,
    private val keyUsecase: KeyUsecase,
) {

    /**
     *  TODO
     *      코인, 주식 등등 으로 확장될 수 있는데
     *      url 설계를 어떤식으로 해야할까?? (AutoTradeUsecase로 하나로 추상화했으니 통합할수있는 방식을 고민해보자)
     */


//    @PostMapping("/user/exchange-key/register")
//    fun register(
//
//    ): CommonResponse<String> {
//        return CommonResponse(
//            body = keyUsecase.registerCoinExchangeKey()
//        )
//    }

    @PostMapping("/user/autotrade/register")
    fun register(
        @Valid @RequestBody request: CoinDashBoardRequest,
        @SessionAttribute userId: Long
    ): CommonResponse<String> {
        return CommonResponse(
            body = autoTradeUsecase.registerCoinAutoTrade(
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