package com.dingdongdeng.autotrading.infra.common.utils

import java.time.LocalDate
import java.time.YearMonth


object LocalDateUtils {
    fun atStartOfMonth(year: Int, month: Int): LocalDate {
        return LocalDate.of(year, month, 1)
    }

    fun atEndOfMonth(year: Int, month: Int): LocalDate {
        return YearMonth.of(year, month).atEndOfMonth()
    }
}