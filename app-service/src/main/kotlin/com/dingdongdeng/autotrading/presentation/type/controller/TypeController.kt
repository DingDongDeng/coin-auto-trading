package com.dingdongdeng.autotrading.presentation.type.controller

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.DescriptionType
import com.dingdongdeng.autotrading.infra.web.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/type")
@RestController
class TypeController {
    @GetMapping("/coin-type")
    fun types(): CommonResponse<List<DescriptionType>> {
        return CommonResponse(
            body = CoinType.entries.toTypedArray().toList()
        )
    }
}