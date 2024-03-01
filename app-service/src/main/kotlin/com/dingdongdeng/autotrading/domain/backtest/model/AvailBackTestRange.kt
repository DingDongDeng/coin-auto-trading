package com.dingdongdeng.autotrading.domain.backtest.model

import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.ExchangeType
import com.dingdongdeng.autotrading.infra.common.utils.CandleDateTimeUtils
import java.time.LocalDateTime

data class AvailBackTestRange(
    val exchangeType: ExchangeType,
    val coinType: CoinType,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
) {
    fun isRanged(coinType: CoinType, startDateTime: LocalDateTime, endDateTime: LocalDateTime): Boolean {
        return this.coinType == coinType && this.startDateTime <= startDateTime && this.endDateTime >= endDateTime
    }

    companion object {
        fun merge(list: List<AvailBackTestRange>, gapSeconds: Long): List<AvailBackTestRange> {
            if (list.size <= 1) return list // 리스트 크기가 1 이하이면 그대로 반환

            return list.sortedBy { it.startDateTime }.fold(emptyList()) { acc, range ->
                if (acc.isEmpty()) {
                    listOf(range)
                } else {
                    val lastRange = acc.last()
                    val timeGap = CandleDateTimeUtils.diffSeconds(lastRange.endDateTime, range.startDateTime)
                    if (timeGap <= gapSeconds) {
                        acc.dropLast(1) + AvailBackTestRange(
                            lastRange.exchangeType,
                            lastRange.coinType,
                            lastRange.startDateTime,
                            range.endDateTime
                        )
                    } else {
                        acc + range
                    }
                }
            }
        }

        fun fromMissingDateTimes(
            exchangeType: ExchangeType,
            coinType: CoinType,
            candleUnit: CandleUnit,
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            missingDateTimes: List<LocalDateTime>
        ): List<AvailBackTestRange> {
            val availBackTestRanges = mutableListOf<AvailBackTestRange>()

            missingDateTimes.windowed(2, 1) { subList ->
                val firstMissingDateTime = subList.first()
                val lastMissingDateTime = subList.last()

                // 누락된 캔들이 연속된 시간에 존재하면 생략
                if (firstMissingDateTime.plusSeconds(candleUnit.getSecondSize()) >= lastMissingDateTime) {
                    return@windowed
                }

                availBackTestRanges.add(
                    AvailBackTestRange(
                        exchangeType = exchangeType,
                        coinType = coinType,
                        startDateTime = firstMissingDateTime.plusSeconds(candleUnit.getSecondSize()),
                        endDateTime = lastMissingDateTime.minusSeconds(candleUnit.getSecondSize()),
                    )
                )
            }

            // 누락된 캔들 구간이 없어서 availBackTestRanges 범위를 계산하지 못함
            if (availBackTestRanges.isEmpty()) {
                availBackTestRanges.add(
                    AvailBackTestRange(
                        exchangeType = exchangeType,
                        coinType = coinType,
                        startDateTime = startDateTime,
                        endDateTime = endDateTime,
                    )
                )
                return availBackTestRanges
            }

            // 백테스트 시작 날짜를 추가
            if (availBackTestRanges.first().startDateTime != startDateTime) {
                availBackTestRanges.add(
                    0,
                    AvailBackTestRange(
                        exchangeType = exchangeType,
                        coinType = coinType,
                        startDateTime = startDateTime,
                        endDateTime = missingDateTimes.first().minusSeconds(candleUnit.getSecondSize()),
                    )
                )
            }

            // 백테스트 끝 날짜를 추가
            if (availBackTestRanges.last().endDateTime != endDateTime) {
                availBackTestRanges.add(
                    0,
                    AvailBackTestRange(
                        exchangeType = exchangeType,
                        coinType = coinType,
                        startDateTime = missingDateTimes.last().plusSeconds(candleUnit.getSecondSize()),
                        endDateTime = endDateTime,
                    )
                )
            }
            return availBackTestRanges
        }
    }
}
