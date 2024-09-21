package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.application.auth.AuthUseCase
import com.dingdongdeng.autotrading.application.auth.model.AuthExchangeKey
import com.dingdongdeng.autotrading.infra.web.CommonResponse
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
class CoinExchangeKeyController(
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
}