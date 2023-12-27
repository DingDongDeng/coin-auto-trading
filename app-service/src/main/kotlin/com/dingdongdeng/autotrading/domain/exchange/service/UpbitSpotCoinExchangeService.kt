package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartResult
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderResult
import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType
import org.springframework.stereotype.Service

@Service
class UpbitSpotCoinExchangeService : SpotCoinExchangeService {

    override fun order(param: SpotCoinExchangeOrderParam): SpotCoinExchangeOrderResult {
        TODO("Not yet implemented")
    }

    override fun cancel(orderId: String): SpotCoinExchangeOrderResult {
        TODO("Not yet implemented")
    }

    override fun getOrder(orderId: String): SpotCoinExchangeOrderResult {
        TODO("Not yet implemented")
    }

    override fun getChart(param: SpotCoinExchangeChartParam): SpotCoinExchangeChartResult {
        TODO("Not yet implemented")
    }

    override fun support(exchangeType: CoinExchangeType): Boolean {
        return exchangeType == CoinExchangeType.UPBIT
    }
}