package com.dingdongdeng.autotrading.domain.statistics.model

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import java.time.LocalDate

class CoinMonthlyStat(
    val coinType: CoinType,
    val yearMonth: LocalDate,
    val profitRate: Double, // 수익율
    val profitPrice: Double, // 수익금
) {

}