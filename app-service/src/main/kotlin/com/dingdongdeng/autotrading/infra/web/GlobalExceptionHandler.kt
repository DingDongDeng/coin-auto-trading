package com.dingdongdeng.autotrading.infra.web

import com.dingdongdeng.autotrading.infra.client.slack.SlackSender
import com.dingdongdeng.autotrading.infra.common.exception.CriticalException
import com.dingdongdeng.autotrading.infra.common.exception.WarnException
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    private val slackSender: SlackSender,
) {

    @ExceptionHandler(BindException::class)
    fun handleNotValidException(
        e: BindException,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<Unit>> {
        log.warn("${e.message}, validation errors : ${bindingResult.fieldErrors}", e)
        return ResponseEntity(
            CommonResponse(message = bindingResult.fieldError?.defaultMessage),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(WarnException::class)
    fun handler(e: WarnException): ResponseEntity<CommonResponse<Unit>> {
        log.warn(e.systemMessage, e)
        return ResponseEntity(
            CommonResponse(message = e.userMessage),
            e.status,
        )
    }

    @ExceptionHandler(CriticalException::class)
    fun handler(e: CriticalException): ResponseEntity<CommonResponse<Unit>> {
        log.error(e.systemMessage, e)
        slackSender.send(e)
        return ResponseEntity(
            CommonResponse(message = e.userMessage),
            e.status,
        )
    }

    @ExceptionHandler(Exception::class)
    fun handler(e: Exception): ResponseEntity<CommonResponse<Unit>> {
        log.error(e.message, e)
        slackSender.send(e)
        return ResponseEntity(
            CommonResponse(message = "시스템 에러 발생"),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }
}