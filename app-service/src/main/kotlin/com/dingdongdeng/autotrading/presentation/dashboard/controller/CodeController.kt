package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.domain.strategy.type.CoinStrategyType
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.web.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/code")
@RestController
class CodeController {

    @GetMapping("/exchange-type")
    fun getExchangeKeys(): CommonResponse<List<ExchangeType>> {
        return CommonResponse(
            body = ExchangeType.ofNotBackTest()
        )
    }

    @GetMapping("/candle-unit")
    fun getCandleUnits(): CommonResponse<List<CandleUnit>> {
        return CommonResponse(
            body = CandleUnit.entries
        )
    }

    @GetMapping("/coin-strategy-type")
    fun getCoinStrategyTypes(): CommonResponse<List<CoinStrategyType>> {
        return CommonResponse(
            body = CoinStrategyType.entries
        )
    }

    @GetMapping("/coin-type")
    fun getCoinTypes(): CommonResponse<List<CoinType>> {
        return CommonResponse(
            body = CoinType.entries
        )
    }
}