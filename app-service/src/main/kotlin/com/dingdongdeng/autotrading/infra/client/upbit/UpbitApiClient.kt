package com.dingdongdeng.autotrading.infra.client.upbit

import com.dingdongdeng.autotrading.infra.client.common.QueryParamsConverter
import com.dingdongdeng.autotrading.infra.client.common.ResponseHandler
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

@Component
class UpbitApiClient(
    val upbitWebClient: WebClient,
    val upbitApiRateLimiter: UpbitApiRateLimiter,
    val queryParamsConverter: QueryParamsConverter,
) {
    fun getAccounts(token: String): List<AccountsResponse> {
        upbitApiRateLimiter.wait()
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri("/v1/accounts")
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<AccountsResponse>>() {})
                .block()!!
        }
    }

    fun getOrdersChance(request: OrderChanceRequest, token: String): OrdersChanceResponse {
        upbitApiRateLimiter.wait()
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder ->
                    uriBuilder.path("/v1/orders/chance")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(OrdersChanceResponse::class.java)
                .block()!!
        }
    }

    fun getMarketList(request: MarketCodeRequest, token: String): List<MarketCodeResponse> {
        upbitApiRateLimiter.wait()
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/market/all")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<MarketCodeResponse>>() {})
                .block()!!
        }
    }

    fun getOrderInfo(request: OrderInfoRequest, token: String): OrderResponse {
        upbitApiRateLimiter.wait()
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/order")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono<OrderResponse>(OrderResponse::class.java)
                .block()!!
        }
    }

    fun getOrderInfoList(request: OrderInfoListRequest, token: String): List<OrderResponse> {
        upbitApiRateLimiter.wait()
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/orders")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<OrderResponse>>() {})
                .block()!!
        }
    }

    fun order(request: OrderRequest, token: String): OrderResponse {
        return ResponseHandler.handle {
            upbitWebClient.post()
                .uri("/v1/orders")
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderResponse::class.java)
                .block()!!
        }
    }

    fun orderCancel(request: OrderCancelRequest, token: String): OrderCancelResponse {
        return ResponseHandler.handle {
            upbitWebClient.delete()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/order")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(OrderCancelResponse::class.java)
                .block()!!
        }
    }

    fun getCandle(request: CandleRequest, candleUnit: CandleUnit, token: String): List<CandleResponse> {
        upbitApiRateLimiter.wait()
        return when (candleUnit) {
            CandleUnit.UNIT_1D -> this.getDayCandle(request, token)
            CandleUnit.UNIT_1W -> this.getWeekCandle(request, token)
            else -> this.getMinuteCandle(request, token)
        }
    }

    private fun getDayCandle(request: CandleRequest, token: String): List<CandleResponse> {
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/candles/days")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<CandleResponse>>() {})
                .retry(3)
                .block()!!
        }
    }

    private fun getWeekCandle(request: CandleRequest, token: String): List<CandleResponse> {
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/candles/weeks")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<CandleResponse>>() {})
                .retry(3)
                .block()!!
        }
    }

    private fun getMinuteCandle(request: CandleRequest, token: String): List<CandleResponse> {
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/candles/minutes/{unit}")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build(request.unit)
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<CandleResponse>>() {})
                .retry(3)
                .block()!!
        }
    }

    fun getOrderBook(request: OrderBookRequest, token: String): List<OrderBookResponse> {
        upbitApiRateLimiter.wait()
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/orderbook")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<OrderBookResponse>>() {})
                .block()!!
        }
    }

    fun getTicker(request: TickerRequest, token: String): List<TickerResponse> {
        upbitApiRateLimiter.wait()
        return ResponseHandler.handle {
            upbitWebClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path("/v1/ticker")
                        .queryParams(queryParamsConverter.convertMap(request))
                        .build()
                }
                .headers { headers -> headers.addAll(makeHeaders(token)) }
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<TickerResponse>>() {})
                .block()!!

        }
    }

    private fun makeHeaders(token: String): HttpHeaders {
        return HttpHeaders().also {
            it.add("Authorization", token)
        }
    }
}


