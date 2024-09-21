package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
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

fun LocalDateTime.atStartOfMonth(): LocalDate {
    val localDate = this.toLocalDate()
    return LocalDate.of(localDate.year, localDate.month, 1)
}

fun LocalDateTime.atEndOfMonth(): LocalDate {
    val localDate = this.toLocalDate()
    return YearMonth.of(localDate.year, localDate.month).atEndOfMonth()
}

fun minDate(d1: LocalDateTime, d2: LocalDateTime): LocalDateTime {
    return if (d1 < d2) {
        return d1
    } else {
        d2
    }
}

fun LocalDateTime.toKstTimestamp(): Long {
    val zoneId = ZoneId.of("Asia/Seoul")
    return this.atZone(zoneId).toInstant().toEpochMilli()
}