package com.dingdongdeng.autotrading.domain.exchange.service

import com.dingdongdeng.autotrading.domain.exchange.model.ExchangeKeyParam
import com.dingdongdeng.autotrading.domain.exchange.model.SpotCoinExchangeOrderParam
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import com.dingdongdeng.autotrading.test.TestEnv
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class UpbitSpotCoinExchangeServiceTest(
    private val upbitSpotCoinExchangeService: UpbitSpotCoinExchangeService,
) {
    val keyParam = ExchangeKeyParam(
        accessKey = TestEnv.UPBIT_ACCESS_KEY,
        secretKey = TestEnv.UPBIT_SECRET_KEY,
    )

    @DisplayName("주문, 조회, 주문취소가 문제 없이 동작해야한다.")
    @Test
    fun test1() {
        val orderRequest = SpotCoinExchangeOrderParam(
            coinType = CoinType.ETHEREUM,
            orderType = OrderType.BUY,
            volume = 1.0,
            price = 5000,
            priceType = PriceType.LIMIT,
        )
        val orderResponse = upbitSpotCoinExchangeService.order(orderRequest, keyParam)
        val getOrderResponse = upbitSpotCoinExchangeService.getOrder(orderResponse.orderId, keyParam)
        val cancelResponse = upbitSpotCoinExchangeService.cancel(orderResponse.orderId, keyParam)
    }

    @DisplayName("차트 정보를 조회할때 범위는 'from <= 조회범위 <= to'를 만족해야 한다.")
    @Nested
    inner class Test1 {
        @DisplayName("현재 실시간으로 생성중인 캔들에 대해서도 잘 조회되어야한다. (15:05 캔들을 조회했는데)")
        @Test
        fun test22() {
            // given

            // when

            // then
        }

        @DisplayName("[분봉] N분봉 일때 to의 시간이 분봉의 단위와 일치하더라도 잘 조회되어야한다. (예를 들어, 5분봉일때 to=15:05)")
        @Test
        fun test2() {
            // given

            // when

            // then
        }

        @DisplayName("[분봉] N분봉 일때 to의 시간이 분봉의 단위와 일치하더라도 잘 조회되어야한다. (예를 들어, 5분봉일때 to=15:05)")
        @Test
        fun test3() {
            // given

            // when

            // then
        }

        @DisplayName("주봉 차트 정보를 조회할때 범위는 'from <= 조회범위 <= to'를 만족해야 한다.")
        @Test
        fun test4() {
            // given

            // when

            // then
        }
    }

}