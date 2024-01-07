package com.dingdongdeng.autotrading.infra.client.upbit

import com.dingdongdeng.autotrading.infra.client.common.QueryParamsConverter
import com.dingdongdeng.autotrading.infra.client.common.ResponseHandler
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriBuilder

@Component
class UpbitApiClient(
    val upbitRestClient: RestClient,
    val upbitApiRateLimiter: UpbitApiRateLimiter,
    val queryParamsConverter: QueryParamsConverter,
) {
    fun getAccounts(token: String): List<AccountsResponse> {
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri("/v1/accounts")
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<AccountsResponse>>() {})!!
        }
    }

    fun getOrdersChance(request: OrderChanceRequest, token: String): OrdersChanceResponse {
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder ->
                    uriBuilder.path("/v1/orders/chance")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(OrdersChanceResponse::class.java)!!
        }
    }

    fun getMarketList(request: MarketCodeRequest, token: String): List<MarketCodeResponse> {
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/market/all")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<MarketCodeResponse>>() {})!!
        }
    }

    fun getOrderInfo(request: OrderInfoRequest, token: String): OrderResponse {
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/order")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body<OrderResponse>(OrderResponse::class.java)!!
        }
    }

    fun getOrderInfoList(request: OrderInfoListRequest, token: String): List<OrderResponse> {
        upbitApiRateLimiter.waitForReady()
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/orders")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<OrderResponse>>() {})!!
        }
    }

    fun order(request: OrderRequest, token: String): OrderResponse {
        return ResponseHandler.handle {
            upbitRestClient.post()
                .uri("/v1/orders")
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .body(request)
                .retrieve()
                .body(OrderResponse::class.java)!!
        }
    }

    fun orderCancel(request: OrderCancelRequest, token: String): OrderCancelResponse {
        return ResponseHandler.handle {
            upbitRestClient.delete()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/order")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(OrderCancelResponse::class.java)!!
        }
    }

    fun getCandle(request: CandleRequest, token: String): List<CandleResponse> {
        upbitApiRateLimiter.waitForReady()
        return when (request.candleUnit) {
            CandleUnit.UNIT_1D -> this.getDayCandle(request, token)
            CandleUnit.UNIT_1W -> this.getWeekCandle(request, token)
            else -> this.getMinuteCandle(request, token)
        }
    }

    private fun getDayCandle(request: CandleRequest, token: String): List<CandleResponse> {
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/candles/days")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<CandleResponse>>() {})!!
        }
    }

    private fun getWeekCandle(request: CandleRequest, token: String): List<CandleResponse> {
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/candles/weeks")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<CandleResponse>>() {})!!
        }
    }

    private fun getMinuteCandle(request: CandleRequest, token: String): List<CandleResponse> {
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/candles/minutes/{unit}")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build(request.unit)
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<CandleResponse>>() {})!!
        }
    }

    fun getOrderBook(request: OrderBookRequest, token: String): List<OrderBookResponse> {
        upbitApiRateLimiter.waitForReady()
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/orderbook")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<OrderBookResponse>>() {})!!
        }
    }

    fun getTicker(request: TickerRequest, token: String): List<TickerResponse> {
        return ResponseHandler.handle {
            upbitRestClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/ticker")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<TickerResponse>>() {})!!

        }
    }

    private fun makeHeaders(token: String): HttpHeaders {
        return HttpHeaders().also {
            it.add("Authorization", token)
        }
    }
}


