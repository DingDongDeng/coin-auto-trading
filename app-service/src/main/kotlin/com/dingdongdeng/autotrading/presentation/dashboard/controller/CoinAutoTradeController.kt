package com.dingdongdeng.autotrading.presentation.dashboard.controller

import com.dingdongdeng.autotrading.application.autotrade.CoinAutoTradeUseCase
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeProcessorDto
import com.dingdongdeng.autotrading.application.autotrade.model.CoinAutoTradeResultDto
import com.dingdongdeng.autotrading.infra.web.CommonResponse
import com.dingdongdeng.autotrading.presentation.dashboard.model.CoinAutotradeRegisterRequest
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
class CoinAutoTradeController(
    private val coinAutoTradeUseCase: CoinAutoTradeUseCase,
) {

    // 자동매매 프로세서 등록
    @PostMapping("/processor/autotrade")
    fun autotradeRegister(
        @Valid @RequestBody request: CoinAutotradeRegisterRequest,
        //@SessionAttribute userId: Long,
    ): CommonResponse<String> {
        return CommonResponse(
            body = coinAutoTradeUseCase.register(
                userId = 12345, //FIXME
                title = request.title!!,
                coinStrategyType = request.coinStrategyType!!,
                exchangeType = request.exchangeType!!,
                coinTypes = request.coinTypes!!,
                candleUnits = request.candleUnits!!,
                keyPairId = request.keyPairId!!,
                config = request.config!!,
            )
        )
    }


    // 프로세서 자동매매 목록 조회
    @GetMapping("/processor/autotrade")
    fun processAutoTradeList(
        //@SessionAttribute userId: Long,
    ): CommonResponse<List<CoinAutoTradeProcessorDto>> {
        return CommonResponse(
            body = coinAutoTradeUseCase.getList(userId = 12345) //FIXME
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