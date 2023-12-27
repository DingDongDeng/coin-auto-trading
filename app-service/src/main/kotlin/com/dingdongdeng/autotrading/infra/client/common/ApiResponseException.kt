package com.dingdongdeng.autotrading.infra.client.common

import org.springframework.http.HttpStatus

class ApiResponseException(
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    val body: String = "",
    override val cause: Throwable,
) : RuntimeException(cause)

