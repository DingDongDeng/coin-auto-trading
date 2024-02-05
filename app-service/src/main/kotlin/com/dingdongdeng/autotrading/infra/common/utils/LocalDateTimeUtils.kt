package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


val yyyyMMddHHmmss: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun LocalDateTime.convertToString(): String {
    return this.format(yyyyMMddHHmmss)
}

fun LocalDateTime.toUtc(): LocalDateTime {
    return this // 마지막 캔들 시각 (비우면 가장 최근 시각), UTC 기준
        .atZone(ZoneId.of("Asia/Seoul"))
        .withZoneSameInstant(ZoneId.of("UTC"))
        .toLocalDateTime()

}