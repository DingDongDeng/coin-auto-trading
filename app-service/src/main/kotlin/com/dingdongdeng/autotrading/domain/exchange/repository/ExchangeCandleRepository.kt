package com.dingdongdeng.autotrading.domain.exchange.repository

import com.dingdongdeng.autotrading.domain.exchange.entity.ExchangeCandle
import org.springframework.data.jpa.repository.JpaRepository

interface ExchangeCandleRepository : JpaRepository<ExchangeCandle, Long> {

}