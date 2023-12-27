package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.SpotCoinExchangeService
import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeChartResult
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderResult
import com.dingdongdeng.autotrading.infra.client.upbit.MarketType
import com.dingdongdeng.autotrading.infra.client.upbit.OrdType
import com.dingdongdeng.autotrading.infra.client.upbit.OrderCancelRequest
import com.dingdongdeng.autotrading.infra.client.upbit.OrderRequest
import com.dingdongdeng.autotrading.infra.client.upbit.Side
import com.dingdongdeng.autotrading.infra.client.upbit.UpbitApiClient
import com.dingdongdeng.autotrading.infra.client.upbit.UpbitTokenGenerator
import com.dingdongdeng.autotrading.infra.common.type.CoinExchangeType
import org.springframework.stereotype.Service

@Service
class UpbitSpotCoinExchangeService(
    private val upbitApiClient: UpbitApiClient,
    private val upbitTokenGenerator: UpbitTokenGenerator,
) : SpotCoinExchangeService {

    override fun order(param: SpotCoinExchangeOrderParam, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult {
        val request = OrderRequest(
            market = MarketType.of(param.coinType).code,
            side = Side.of(param.orderType),
            volume = param.volume,
            price = param.price,
            ordType = OrdType.of(param.priceType, param.orderType),
        )
        val response = upbitApiClient.order(request, makeToken(request, keyParam))
        return SpotCoinExchangeOrderResult(
            orderId = response.uuid,
            orderType = response.side.orderType,
            priceType = response.ordType.priceType,
            price = response.price,
            orderState = response.state.orderState,
            coinExchangeType = EXCHANGE_TYPE,
            coinType = MarketType.of(response.market).coinType,
            orderDateTime = response.getCreatedAt(),
        )
    }

    override fun cancel(orderId: String, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult {
        val request = OrderCancelRequest(
            uuid = orderId
        )
        val response = upbitApiClient.orderCancel(request, makeToken(request, keyParam))
        return SpotCoinExchangeOrderResult(
            orderId = response.uuid,
            orderType = response.side.orderType,
            priceType = response.ordType.priceType,
            price = response.price,
            orderState = response.state.orderState,
            coinExchangeType = EXCHANGE_TYPE,
            coinType = MarketType.of(response.market).coinType,
            cancelDateTime = response.createdAt,
        )
    }

    override fun getOrder(orderId: String, keyParam: ExchangeKeyParam): SpotCoinExchangeOrderResult {
        TODO("Not yet implemented")
    }

    override fun getChart(param: SpotCoinExchangeChartParam, keyParam: ExchangeKeyParam): SpotCoinExchangeChartResult {
        TODO("Not yet implemented")
    }

    override fun support(exchangeType: CoinExchangeType): Boolean {
        return exchangeType == EXCHANGE_TYPE
    }

    private fun makeToken(request: Any? = null, keyParam: ExchangeKeyParam): String {
        return upbitTokenGenerator.makeToken(request, keyParam.accessKey, keyParam.secretKey)
    }

    companion object {
        val EXCHANGE_TYPE = CoinExchangeType.UPBIT
    }
}