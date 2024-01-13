package com.dingdongdeng.autotrading.infra.common.exception

import org.springframework.http.HttpStatus

class CriticalException(
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    val systemMessage: String = "시스템 오류",
    val userMessage: String = "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
    val throwable: Throwable? = null,
) : RuntimeException(systemMessage, throwable)

class WarnException(
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
    val systemMessage: String = "시스템 오류",
    val userMessage: String = "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
    val throwable: Throwable? = null,
) : RuntimeException(systemMessage, throwable)