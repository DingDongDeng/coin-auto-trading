package com.dingdongdeng.autotrading.upbit

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AccountsResponse(
    @JsonProperty("currency")
    val currency: String, // 화폐를 의미하는 영문 대문자 코드
    @JsonProperty("balance")
    val balance: Double, // 주문가능 금액/수량
    @JsonProperty("locked")
    val locked: Double, // 주문 중 묶여있는 금액/수량
    @JsonProperty("avg_buy_price")
    val avgBuyPrice: Double, // 매수평균가
    @JsonProperty("avg_buy_price_modified")
    val avgBuyPriceModified: Boolean, // 매수평균가 수정 여부
    @JsonProperty("unit_currency")
    val unitCurrency: String, // 평단가 기준 화폐
)

class OrdersChanceResponse(
    @JsonProperty("ask_fee")
    val askFee: String, // 매도 수수료 비율
    @JsonProperty("market")
    val market: MarketResponse, // 마켓에 대한 정보
    @JsonProperty("bid_account")
    val bidAccount: BidAccountResponse, // 매수 시 사용하는 화폐의 계좌 상태
    @JsonProperty("ask_account")
    val askAccount: AskAccountResponse, // 매도 시 사용하는 화폐의 계좌 상태
)

class MarketResponse(
    @JsonProperty("id")
    val id: String, // 마켓의 유일 키
    @JsonProperty("name")
    val name: String, // 마켓 이름
    @JsonProperty("order_types")
    val orderTypes: List<String>, // 지원 주문 방식
    @JsonProperty("order_sides")
    val orderSides: List<String>, //	지원 주문 종류
    @JsonProperty("bid")
    val bid: BidResponse, //	매수 시 제약사항
    @JsonProperty("ask")
    val ask: AskResponse, //	매도 시 제약사항
    @JsonProperty("max_total")
    val maxTotal: Double, // 최대 매도/매수 금액
    @JsonProperty("state")
    val state: String, // 마켓 운영 상태
)

class BidResponse(
    @JsonProperty("currency")
    val currency: String, // 화폐를 의미하는 영문 대문자 코드
    @JsonProperty("price_unit")
    val priceUnit: String, // 주문금액 단위
    @JsonProperty("min_total")
    val minTotal: Double, // 최소 매도/매수 금액
)

class AskResponse(
    @JsonProperty("currency")
    val currency: String, // 화폐를 의미하는 영문 대문자 코드
    @JsonProperty("price_unit")
    val priceUnit: String, // 주문금액 단위
    @JsonProperty("min_total")
    val minTotal: Double, // 최소 매도/매수 금액
)

class BidAccountResponse(
    @JsonProperty("currency")
    val currency: String, // 화폐를 의미하는 영문 대문자 코드
    @JsonProperty("balance")
    val balance: Double, // 주문가능 금액/수량
    @JsonProperty("locked")
    val locked: Double, // 주문 중 묶여있는 금액/수량
    @JsonProperty("avg_buy_price")
    val avgBuyPrice: Double, //	매수평균가
    @JsonProperty("avg_buy_price_modified")
    val avgBuyPriceModified: Boolean, //	매수평균가 수정 여부
    @JsonProperty("unit_currency")
    val unitCurrency: String, //	평단가 기준 화폐
)

class AskAccountResponse(
    @JsonProperty("currency")
    val currency: String,//	화폐를 의미하는 영문 대문자 코드
    @JsonProperty("balance")
    val balance: Double,// 주문가능 금액/수량
    @JsonProperty("locked")
    val locked: Double,// 주문 중 묶여있는 금액/수량
    @JsonProperty("avg_buy_price")
    val avgBuyPrice: Double,// 매수평균가
    @JsonProperty("avg_buy_price_modified")
    val avgBuyPriceModified: Boolean,// 매수평균가 수정 여부
    @JsonProperty("unit_currency")
    val unitCurrency: String,// 평단가 기준 화폐
)


class MarketCodeResponse(
    @JsonProperty("market")
    val market: String, // 업비트에서 제공중인 시장 정보

    @JsonProperty("korean_name")
    val koreanName: String, // 거래 대상 암호화폐 한글명

    @JsonProperty("english_name")
    val englishName: String, // 거래 대상 암호화폐 영문명

    @JsonProperty("market_warning")
    val marketWarning: String, // 유의 종목 여부 NONE (해당 사항 없음), CAUTION(투자유의)
)

class OrderResponse(
    @JsonProperty("uuid")
    val uuid: String, // 주문의 고유 아이디

    @JsonProperty("side")
    val side: Side, // 주문 종류

    @JsonProperty("ord_type")
    val ordType: OrdType, // 주문 방식

    @JsonProperty("price")
    val price: Double, // 주문 당시 화폐 가격

    @JsonProperty("avg_price")
    val avgPrice: Double, // 체결 가격의 평균가

    @JsonProperty("state")
    val state: State, // 주문 상태

    @JsonProperty("market")
    val market: String, // 마켓의 유일키

    // 업비트가 갑자기 날짜포멧이 달라지더니 자기 맘대로 주기 시작했음...(22.11.04 부터)
    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssz")
    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSz")
    @JsonProperty("created_at")
    val createdAt: String, // 주문 생성 시간

    @JsonProperty("volume")
    val volume: Double, // 사용자가 입력한 주문 양

    @JsonProperty("remaining_volume")
    val remainingVolume: Double, // 체결 후 남은 주문 양

    @JsonProperty("reserved_fee")
    val reservedFee: Double, // 수수료로 예약된 비용

    @JsonProperty("remaining_fee")
    val remainingFee: Double, // 남은 수수료

    @JsonProperty("paid_fee")
    val paidFee: Double, // 사용된 수수료

    @JsonProperty("locked")
    val locked: Double, // 거래에 사용중인 비용

    @JsonProperty("executed_volume")
    val executedVolume: Double, // 체결된 양

    @JsonProperty("trade_count")
    val tradeCount: Long, // 해당 주문에 걸린 체결 수

    @JsonProperty("trades")
    val tradeList: List<TradeResponse>, // 체결
) {

    /*
     * 업비트 API가 갑자기, 날짜 포멧을 이상하게 내려줌 (22.11.4쯤부터...)
     * 그래서 이를 보완하기 위해 String으로 응답을 받고, 실제 서비스 로직에서는 getter를 이용해 LocalDateTime으로 다룸
     */
    fun getCreatedAt(): LocalDateTime {
        return LocalDateTime.parse(createdAt.substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    }
}

class TradeResponse(
    @JsonProperty("market")
    val market: String, // 마켓의 유일 키

    @JsonProperty("uuid")
    val uuid: String, // 체결의 고유 아이디

    @JsonProperty("price")
    val price: Double, // 체결 가격

    @JsonProperty("volume")
    val volume: String, // 체결 양

    @JsonProperty("funds")
    val funds: String, // 체결된 총 가격

    @JsonProperty("side")
    val side: Side, // 체결 종류

    @JsonProperty("created_at")
    val createdAt: String, // 체결 시각
)

class OrderCancelResponse(
    @JsonProperty("uuid")
    val uuid: String, // 주문의 고유 아이디

    @JsonProperty("side")
    val side: Side, // 주문 종류

    @JsonProperty("ord_type")
    val ordType: OrdType, // 주문 방식

    @JsonProperty("price")
    val price: Double, // 주문 당시 화폐 가격

    @JsonProperty("state")
    val state: State, // 주문 상태

    @JsonProperty("market")
    val market: String, // 마켓의 유일키

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("created_at")
    val createdAt: LocalDateTime, // 주문 생성 시간

    @JsonProperty("volume")
    val volume: Double, // 사용자가 입력한 주문 양

    @JsonProperty("remaining_volume")
    val remainingVolume: Double, // 체결 후 남은 주문 양

    @JsonProperty("reserved_fee")
    val reservedFee: Double, // 수수료로 예약된 비용

    @JsonProperty("remaining_fee")
    val remainingFee: Double, // 남은 수수료

    @JsonProperty("paid_fee")
    val paidFee: Double, // 사용된 수수료

    @JsonProperty("locked")
    val locked: Double, // 거래에 사용중인 비용

    @JsonProperty("executed_volume")
    val executedVolume: Double, // 체결된 양

    @JsonProperty("trade_count")
    val tradeCount: Long, // 해당 주문에 걸린 체결 수
)

class CandleResponse(
    @JsonProperty("market")
    val market: String, // 마켓명

    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @JsonProperty("candle_date_time_utc")
    val candleDateTimeUtc: LocalDateTime, // 캔들 기준 시각(UTC 기준)

    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @JsonProperty("candle_date_time_kst")
    val candleDateTimeKst: LocalDateTime, // 캔들 기준 시각(KST 기준)

    @JsonProperty("opening_price")
    val openingPrice: Double, // 시가

    @JsonProperty("high_price")
    val highPrice: Double, // 고가

    @JsonProperty("low_price")
    val lowPrice: Double, // 저가

    @JsonProperty("trade_price")
    val tradePrice: Double, // 종가

    @JsonProperty("timestamp")
    val timestamp: Long, // 해당 캔들에서 마지막 틱이 저장된 시각

    @JsonProperty("candle_acc_trade_price")
    val candleAccTradePrice: Double, // 누적 거래 금액

    @JsonProperty("candle_acc_trade_volume")
    val candleAccTradeVolume: Double, // 누적 거래량

    @JsonProperty("unit")
    val unit: Int, // 분 단위(유닛)
)

class OrderBookResponse(
    @JsonProperty("market")
    val market: String, // 마켓 코드

    @JsonProperty("timestamp")
    val timestamp: Long, // 호가 생성 시각

    @JsonProperty("total_ask_size")
    val totalAskSize: Double, // 호가 매도 총 잔량

    @JsonProperty("total_bid_size")
    val totalBidSize: Double, // 호가 매수 총 잔량

    @JsonProperty("orderbook_units")
    val orderbookUnitList: List<OrderBookUnitResponse>, // 호가
)

class OrderBookUnitResponse(
    @JsonProperty("ask_price")
    val askPrice: Double, // 매도호가

    @JsonProperty("bid_price")
    val bidPrice: Double, // 매수호가

    @JsonProperty("ask_size")
    val askSize: Double, // 매도 잔량

    @JsonProperty("bid_size")
    val bidSize: Double, // 매수 잔량
)

class TickerResponse(
    @JsonProperty("market")
    val market: String, //종목 구분 코드

    @JsonProperty("trade_date")
    val tradeDate: String, //최근 거래 일자(UTC)

    @JsonProperty("trade_time")
    val tradeTime: String, //최근 거래 시각(UTC)

    @JsonProperty("trade_date_kst")
    val tradeDateKst: String, //최근 거래 일자(KST)

    @JsonProperty("trade_time_kst")
    val tradeTimeKst: String, //최근 거래 시각(KST)

    @JsonProperty("opening_price")
    val openingPrice: Double, //시가

    @JsonProperty("high_price")
    val highPrice: Double, //고가

    @JsonProperty("low_price")
    val lowPrice: Double, //저가

    @JsonProperty("trade_price")
    val tradePrice: Double, //종가

    @JsonProperty("prev_closing_price")
    val prevClosingPrice: Double, //전일 종가

    @JsonProperty("change")
    val change: String, //EVEN : 보합   RISE : 상승   FALL : 하락

    @JsonProperty("change_price")
    val changePrice: Double, //변화액의 절대값

    @JsonProperty("change_rate")
    val changeRate: Double, //변화율의 절대값

    @JsonProperty("signed_change_price")
    val signedChangePrice: Double, //부호가 있는 변화액

    @JsonProperty("signed_change_rate")
    val signedChangeRate: Double, //부호가 있는 변화율

    @JsonProperty("trade_volume")
    val tradeVolume: Double, //가장 최근 거래량

    @JsonProperty("acc_trade_price")
    val accTradePrice: Double, //누적 거래대금(UTC 0시 기준)

    @JsonProperty("acc_trade_price_24h")
    val accTradePrice24h: Double, //24시간 누적 거래대금

    @JsonProperty("acc_trade_volume")
    val accTradeVolume: Double, //누적 거래량(UTC 0시 기준)

    @JsonProperty("acc_trade_volume_24h")
    val accTradeVolume24h: Double, //24시간 누적 거래량

    @JsonProperty("highest_52_week_price")
    val highest52WeekPrice: Double, //52주 신고가

    @JsonProperty("highest_52_week_date")
    val highest52WeekDate: String, //52주 신고가 달성일

    @JsonProperty("lowest_52_week_price")
    val lowest52WeekPrice: Double, //52주 신저가

    @JsonProperty("lowest_52_week_date")
    val lowest52WeekDate: String, //52주 신저가 달성일

    @JsonProperty("timestamp")
    val timestamp: Long, //타임스탬프
)

