package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


val yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun LocalDateTime.convertToString(): String {
    return this.format(yyyyMMddHHmmss)
}