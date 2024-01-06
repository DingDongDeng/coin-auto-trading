package com.dingdongdeng.autotrading.presentation.common

data class CommonResponse<T>(
    val body: T? = null,
    val message: String? = null,
)
