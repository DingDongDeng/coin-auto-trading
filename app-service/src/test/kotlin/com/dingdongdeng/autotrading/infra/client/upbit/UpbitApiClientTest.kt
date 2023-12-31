package com.dingdongdeng.autotrading.infra.client.upbit

import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import com.dingdongdeng.autotrading.infra.common.type.CandleUnit
import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.test.TestEnv
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.time.LocalDateTime

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class UpbitApiClientTest(
    val upbitApiClient: UpbitApiClient,
    val upbitTokenGenerator: UpbitTokenGenerator,
) {

    @DisplayName("전체 계좌 조회 테스트")
    @Test
    fun test1() {
        val response = upbitApiClient.getAccounts(makeToken())
        log.info("result : {}", response)
        val element = response.first()
        log.info("result of first element : {}", element)
    }

    @DisplayName("마켓_정보_조회_테스트")
    @Test
    fun test2() {
        val request = MarketCodeRequest(isDetail = true)
        val response = upbitApiClient.getMarketList(request, makeToken(request))
        log.info("result : {}", response)
        val element = response.first()
        log.info("result of first element : {}", element)
    }

    @DisplayName("주문_가능_정보_조회_테스트")
    @Test
    fun test3() {
        val request = OrderChanceRequest(market = MarketType.of(CoinType.ETHEREUM).code)
        log.info("result : {}", upbitApiClient.getOrdersChance(request, makeToken(request)))
    }

    @DisplayName("주문_리스트_조회_테스트")
    @Test
    fun test4() {
        val request = OrderInfoListRequest(
            market = "KRW-ETH",
            identifierList = null,
            state = null,
            stateList = listOf(State.wait, State.watch),
            page = 1,
            limit = 100,
            orderBy = "desc",
        )
        val orderInfoListResponse = upbitApiClient.getOrderInfoList(request, makeToken(request))
        log.info("result orderInfoListResponse: {}", orderInfoListResponse)
    }

    @DisplayName("주문과_조회와_취소_테스트")
    @Test
    fun test5() {
        // 주문
        val orderRequest = OrderRequest(
            market = "KRW-ETH",
            side = Side.bid,
            volume = 1.0,
            price = 5000.0,
            ordType = OrdType.limit,
        )
        val orderResponse = upbitApiClient.order(orderRequest, makeToken(orderRequest))
        log.info("result orderResponse: {}", orderResponse)

        // 조회
        val orderInfoRequest = OrderInfoRequest(uuid = orderResponse.uuid)
        val orderInfoResponse = upbitApiClient.getOrderInfo(orderInfoRequest, makeToken(orderInfoRequest))
        log.info("result orderInfoResponse: {}", orderInfoResponse)

        // 리스트 조회
        val orderInfoListRequest = OrderInfoListRequest(
            market = "KRW-ETH",
            uuidList = listOf(orderInfoResponse.uuid),
            identifierList = null,
            state = null,
            stateList = listOf(State.wait, State.watch),
            page = 1,
            limit = 100,
            orderBy = "desc",
        )

        val orderInfoListResponse =
            upbitApiClient.getOrderInfoList(orderInfoListRequest, makeToken(orderInfoListRequest))
        log.info("result orderInfoListResponse: {}", orderInfoListResponse)

        // 취소
        val orderCancelRequest = OrderCancelRequest(
            uuid = orderResponse.uuid
        )
        log.info(
            "result orderCancelResponse: {}",
            upbitApiClient.orderCancel(orderCancelRequest, makeToken(orderCancelRequest))
        )
    }

    @DisplayName("분봉_캔들_조회_테스트")
    @Test
    fun test6() {
        val to = LocalDateTime.of(2022, 1, 13, 21, 9, 0)
        val request = CandleRequest(
            unit = 1,
            market = "KRW-ETH",
            timeAsKst = to,
            candleUnit = CandleUnit.UNIT_1M,
            count = 2,
        )
        val response = upbitApiClient.getCandle(request, makeToken(request))
        log.info("result : {}", response)
    }

    @DisplayName("일봉_캔들_조회_테스트")
    @Test
    fun test7() {
        val to = LocalDateTime.of(2022, 8, 9, 20, 9, 0)
        val request = CandleRequest(
            market = "KRW-ETH",
            timeAsKst = to,
            candleUnit = CandleUnit.UNIT_1D,
            count = 2,
        )
        val response = upbitApiClient.getCandle(request, makeToken(request))
        log.info("result : {}", response)
    }


    @DisplayName("주문_호가_조회_테스트")
    @Test
    fun test8() {
        val request = OrderBookRequest(marketList = listOf(MarketType.KRW_ETH.code))
        val response = upbitApiClient.getOrderBook(request, makeToken(request))
        log.info("result : {}", response)
    }

    @DisplayName("현재가_조회_테스트")
    @Test
    fun test9() {
        val request = TickerRequest(marketList = listOf(MarketType.KRW_ETH.code))
        val response = upbitApiClient.getTicker(request, makeToken(request))
        log.info("result : {}", response)
    }

    private fun makeToken(request: Any? = null): String {
        return upbitTokenGenerator.makeToken(
            request,
            TestEnv.UPBIT_ACCESS_KEY,
            TestEnv.UPBIT_SECRET_KEY
        )
    }
}