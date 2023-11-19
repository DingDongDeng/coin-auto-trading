package com.dingdongdeng.autotrading.common

import org.springframework.http.HttpStatus

data class ApiResponseException(
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    val body: String = "",
    override val cause: Throwable,
) : RuntimeException(cause)

