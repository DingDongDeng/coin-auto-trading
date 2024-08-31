package com.dingdongdeng.autotrading.presentation.dashboard.controller

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
}