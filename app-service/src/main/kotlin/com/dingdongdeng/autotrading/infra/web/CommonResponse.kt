package com.dingdongdeng.autotrading.infra.web

data class CommonResponse<T>(
    val body: T? = null,
    val message: String? = null,
)
