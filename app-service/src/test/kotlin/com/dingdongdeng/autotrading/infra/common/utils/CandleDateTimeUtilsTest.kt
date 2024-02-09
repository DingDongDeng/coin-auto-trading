package com.dingdongdeng.autotrading.infra.common.utils

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CandleDateTimeUtilsTest {

    @DisplayName("단위 시간을 적절히 계산해야 한다.")
    @Nested
    inner class Test1 {
        @DisplayName("기준 시간이 적절히 계산되어야 한다. (분봉)")
        @Test
        fun test1() {
            val now = LocalDateTime.of(2024, 1, 29, 15, 7, 50)
            val prevUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_15M)
            val nextUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_15M, true)
            assertEquals(prevUnitDateTime, LocalDateTime.of(2024, 1, 29, 15, 0, 0))
            assertEquals(nextUnitDateTime, LocalDateTime.of(2024, 1, 29, 15, 15, 0))
        }

        @DisplayName("기준 시간이 적절히 계산되어야 한다. (분봉, 기준 시간 동일)")
        @Test
        fun test2() {
            val now = LocalDateTime.of(2024, 1, 29, 15, 15, 0)
            val prevUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_15M)
            val nextUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_15M, true)
            assertEquals(prevUnitDateTime, LocalDateTime.of(2024, 1, 29, 15, 15, 0))
            assertEquals(nextUnitDateTime, LocalDateTime.of(2024, 1, 29, 15, 15, 0))
        }

        @DisplayName("기준 시간이 적절히 계산되어야 한다. (4시간봉)")
        @Test
        fun test3() {
            val now = LocalDateTime.of(2024, 1, 29, 15, 7, 50)
            val prevUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_240M)
            val nextUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_240M, true)
            assertEquals(prevUnitDateTime, LocalDateTime.of(2024, 1, 29, 13, 0, 0))
            assertEquals(nextUnitDateTime, LocalDateTime.of(2024, 1, 29, 17, 0, 0))
        }

        @DisplayName("기준 시간이 적절히 계산되어야 한다. (4시간봉, 기준 시간 동일)")
        @Test
        fun test33() {
            val now = LocalDateTime.of(2024, 1, 29, 13, 0, 0)
            val prevUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_240M)
            val nextUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_240M, true)
            assertEquals(prevUnitDateTime, LocalDateTime.of(2024, 1, 29, 13, 0, 0))
            assertEquals(nextUnitDateTime, LocalDateTime.of(2024, 1, 29, 13, 0, 0))
        }

        @DisplayName("기준 시간이 적절히 계산되어야 한다. (일봉)")
        @Test
        fun test4() {
            val now = LocalDateTime.of(2024, 1, 29, 15, 7, 50)
            val prevUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_1D)
            val nextUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_1D, true)
            assertEquals(prevUnitDateTime, LocalDateTime.of(2024, 1, 29, 9, 0, 0))
            assertEquals(nextUnitDateTime, LocalDateTime.of(2024, 1, 30, 9, 0, 0))
        }

        @DisplayName("기준 시간이 적절히 계산되어야 한다. (일봉, 기준 시간과 동일)")
        @Test
        fun test44() {
            val now = LocalDateTime.of(2024, 1, 29, 9, 0, 0)
            val prevUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_1D)
            val nextUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_1D, true)
            assertEquals(prevUnitDateTime, LocalDateTime.of(2024, 1, 29, 9, 0, 0))
            assertEquals(nextUnitDateTime, LocalDateTime.of(2024, 1, 29, 9, 0, 0))
        }

        @DisplayName("기준 시간이 적절히 계산되어야 한다. (주봉)")
        @Test
        fun test5() {
            val now = LocalDateTime.of(2024, 1, 31, 15, 7, 50)
            val prevUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_1W)
            val nextUnitDateTime = CandleDateTimeUtils.makeUnitDateTime(now, CandleUnit.UNIT_1W, true)
            assertEquals(prevUnitDateTime, LocalDateTime.of(2024, 1, 29, 9, 0, 0))
            assertEquals(nextUnitDateTime, LocalDateTime.of(2024, 2, 5, 9, 0, 0))
        }
    }

    @DisplayName("누락된 시간을 적절히 탐색할 수 있어야 한다.")
    @Nested
    inner class Test2 {
        @DisplayName("from, to가 단위 시간과 동일하더라도 누락 캔들을 찾을 수 있어야한다 (분봉)")
        @Test
        fun test5() {
            val unit = CandleUnit.UNIT_15M
            val from = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
            val to = LocalDateTime.of(2024, 1, 1, 1, 15, 0)
            val list = listOf(
                LocalDateTime.of(2024, 1, 1, 0, 15, 0),
                LocalDateTime.of(2024, 1, 1, 0, 30, 0),
                LocalDateTime.of(2024, 1, 1, 0, 45, 0),
                LocalDateTime.of(2024, 1, 1, 1, 0, 0),
            )
            val missingDateTimes = CandleDateTimeUtils.findMissingDateTimes(unit, from, to, list)

            assertEquals(
                missingDateTimes,
                listOf(
                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2024, 1, 1, 1, 15, 0),
                )
            )
        }

        @DisplayName("중간에 누락된 캔들을 찾을 수 있어야한다 (분봉)")
        @Test
        fun test6() {
            val unit = CandleUnit.UNIT_15M
            val from = LocalDateTime.of(2024, 1, 1, 0, 1, 0)
            val to = LocalDateTime.of(2024, 1, 1, 1, 16, 0)
            val list = listOf(
                LocalDateTime.of(2024, 1, 1, 0, 15, 0),
                LocalDateTime.of(2024, 1, 1, 1, 0, 0),
            )
            val missingDateTimes = CandleDateTimeUtils.findMissingDateTimes(unit, from, to, list)

            assertEquals(
                missingDateTimes,
                listOf(
                    LocalDateTime.of(2024, 1, 1, 0, 30, 0),
                    LocalDateTime.of(2024, 1, 1, 0, 45, 0),
                    LocalDateTime.of(2024, 1, 1, 1, 15, 0)
                )
            )
        }

        @DisplayName("중간에 누락된 캔들을 찾을 수 있어야한다 (일봉)")
        @Test
        fun test7() {
            val unit = CandleUnit.UNIT_1D
            val from = LocalDateTime.of(2024, 1, 1, 8, 50, 0)
            val to = LocalDateTime.of(2024, 1, 2, 10, 50, 0)
            val list = listOf(
                LocalDateTime.of(2024, 1, 2, 9, 0, 0),
            )
            val missingDateTimes = CandleDateTimeUtils.findMissingDateTimes(unit, from, to, list)

            assertEquals(
                missingDateTimes,
                listOf(
                    LocalDateTime.of(2024, 1, 1, 9, 0, 0),
                )
            )
        }

        @DisplayName("중간에 누락된 캔들을 찾을 수 있어야한다 (주봉)")
        @Test
        fun test8() {
            val unit = CandleUnit.UNIT_1W
            val from = LocalDateTime.of(2024, 1, 29, 0, 1, 0)
            val to = LocalDateTime.of(2024, 2, 5, 10, 16, 0)
            val list = listOf(
                LocalDateTime.of(2024, 1, 29, 9, 0, 0),
            )
            val missingDateTimes = CandleDateTimeUtils.findMissingDateTimes(unit, from, to, list)

            assertEquals(
                missingDateTimes,
                listOf(
                    LocalDateTime.of(2024, 2, 5, 9, 0, 0),
                )
            )
        }

        @DisplayName("빈 리스트가 전달되더라도 누락된 캔들을 찾아내야한다")
        @Test
        fun test9() {
            val unit = CandleUnit.UNIT_15M
            val from = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
            val to = LocalDateTime.of(2024, 1, 1, 1, 16, 0)
            val list = listOf<LocalDateTime>()
            val missingDateTimes = CandleDateTimeUtils.findMissingDateTimes(unit, from, to, list)

            assertEquals(
                missingDateTimes,
                listOf(
                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2024, 1, 1, 0, 15, 0),
                    LocalDateTime.of(2024, 1, 1, 0, 30, 0),
                    LocalDateTime.of(2024, 1, 1, 0, 45, 0),
                    LocalDateTime.of(2024, 1, 1, 1, 0, 0),
                    LocalDateTime.of(2024, 1, 1, 1, 15, 0),
                )
            )
        }
    }
}