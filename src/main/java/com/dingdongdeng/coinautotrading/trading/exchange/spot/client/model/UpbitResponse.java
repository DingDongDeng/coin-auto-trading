package com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model;

import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.trading.exchange.spot.client.model.UpbitEnum.State;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@ToString
public class UpbitResponse {

    @ToString
    @Getter
    public static class AccountsResponse {

        @JsonProperty("currency")
        private String currency; // 화폐를 의미하는 영문 대문자 코드
        @JsonProperty("balance")
        private Double balance; // 주문가능 금액/수량
        @JsonProperty("locked")
        private Double locked; // 주문 중 묶여있는 금액/수량
        @JsonProperty("avg_buy_price")
        private Double avgBuyPrice; // 매수평균가
        @JsonProperty("avg_buy_price_modified")
        private boolean avgBuyPriceModified; // 매수평균가 수정 여부
        @JsonProperty("unit_currency")
        private String unitCurrency; // 평단가 기준 화폐
    }

    @ToString
    @Getter
    public static class OrdersChanceResponse {

        @JsonProperty("ask_fee")
        private String askFee; // 매도 수수료 비율
        @JsonProperty("market")
        private MarketResponse market; // 마켓에 대한 정보
        @JsonProperty("bid_account")
        private BidAccountResponse bidAccount; // 매수 시 사용하는 화폐의 계좌 상태
        @JsonProperty("ask_account")
        private AskAccountResponse askAccount; // 매도 시 사용하는 화폐의 계좌 상태
    }

    @ToString
    @Getter
    public static class MarketResponse {

        @JsonProperty("id")
        private String id; // 마켓의 유일 키
        @JsonProperty("name")
        private String name; // 마켓 이름
        @JsonProperty("order_types")
        private List<String> orderTypes; // 지원 주문 방식
        @JsonProperty("order_sides")
        private List<String> orderSides; //	지원 주문 종류
        @JsonProperty("bid")
        private BidResponse bid; //	매수 시 제약사항
        @JsonProperty("ask")
        private AskResponse ask; //	매도 시 제약사항
        @JsonProperty("max_total")
        private Double maxTotal; // 최대 매도/매수 금액
        @JsonProperty("state")
        private String state; // 마켓 운영 상태
    }

    @ToString
    @Getter
    public static class BidResponse {

        @JsonProperty("currency	")
        private String currency; // 화폐를 의미하는 영문 대문자 코드
        @JsonProperty("price_unit")
        private String priceUnit; // 주문금액 단위
        @JsonProperty("min_total")
        private Double minTotal; // 최소 매도/매수 금액
    }

    @ToString
    @Getter
    public static class AskResponse {

        @JsonProperty("currency	")
        private String currency; // 화폐를 의미하는 영문 대문자 코드
        @JsonProperty("price_unit")
        private String priceUnit; // 주문금액 단위
        @JsonProperty("min_total")
        private Double minTotal; // 최소 매도/매수 금액
    }

    @ToString
    @Getter
    public static class BidAccountResponse {

        @JsonProperty("currency")
        private String currency; // 화폐를 의미하는 영문 대문자 코드
        @JsonProperty("balance")
        private Double balance; // 주문가능 금액/수량
        @JsonProperty("locked")
        private Double locked; // 주문 중 묶여있는 금액/수량
        @JsonProperty("avg_buy_price")
        private Double avgBuyPrice; //	매수평균가
        @JsonProperty("avg_buy_price_modified")
        private boolean avgBuyPriceModified; //	매수평균가 수정 여부
        @JsonProperty("unit_currency")
        private String unitCurrency; //	평단가 기준 화폐
    }

    @ToString
    @Getter
    public static class AskAccountResponse {

        @JsonProperty("currency")
        private String currency; //	화폐를 의미하는 영문 대문자 코드
        @JsonProperty("balance")
        private Double balance; // 주문가능 금액/수량
        @JsonProperty("locked")
        private Double locked; // 주문 중 묶여있는 금액/수량
        @JsonProperty("avg_buy_price")
        private Double avgBuyPrice; // 매수평균가
        @JsonProperty("avg_buy_price_modified")
        private boolean avgBuyPriceModified; // 매수평균가 수정 여부
        @JsonProperty("unit_currency")
        private String unitCurrency; // 평단가 기준 화폐
    }

    @ToString
    @Getter
    public static class MarketCodeResponse {

        @JsonProperty("market")
        private String market; // 업비트에서 제공중인 시장 정보
        @JsonProperty("korean_name")
        private String koreanName; // 거래 대상 암호화폐 한글명
        @JsonProperty("english_name")
        private String englishName; // 거래 대상 암호화폐 영문명
        @JsonProperty("market_warning")
        private String marketWarning; // 유의 종목 여부 NONE (해당 사항 없음), CAUTION(투자유의)
    }

    @ToString
    @Getter
    @JsonInclude(Include.NON_NULL)
    public static class OrderResponse {

        @JsonProperty("uuid")
        private String uuid; // 주문의 고유 아이디
        @JsonProperty("side")
        private Side side; // 주문 종류
        @JsonProperty("ord_type")
        private OrdType ordType; // 주문 방식
        @JsonProperty("price")
        private Double price; // 주문 당시 화폐 가격
        @JsonProperty("avg_price")
        private Double avgPrice; // 체결 가격의 평균가
        @JsonProperty("state")
        private State state; // 주문 상태
        @JsonProperty("market")
        private String market; // 마켓의 유일키
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        @JsonProperty("created_at")
        private LocalDateTime createdAt; // 주문 생성 시간
        @JsonProperty("volume")
        private Double volume; // 사용자가 입력한 주문 양
        @JsonProperty("remaining_volume")
        private Double remainingVolume; // 체결 후 남은 주문 양
        @JsonProperty("reserved_fee")
        private Double reservedFee; // 수수료로 예약된 비용
        @JsonProperty("remaining_fee")
        private Double remainingFee; // 남은 수수료
        @JsonProperty("paid_fee")
        private Double paidFee; // 사용된 수수료
        @JsonProperty("locked")
        private Double locked; // 거래에 사용중인 비용
        @JsonProperty("executed_volume")
        private Double executedVolume; // 체결된 양
        @JsonProperty("trade_count")
        private Long tradeCount; // 해당 주문에 걸린 체결 수
        @JsonProperty("trades")
        private List<TradeResponse> tradeList; // 체결
    }

    @ToString
    @Getter
    public static class TradeResponse {

        @JsonProperty("market")
        private String market; // 마켓의 유일 키
        @JsonProperty("uuid")
        private String uuid; // 체결의 고유 아이디
        @JsonProperty("price")
        private Double price; // 체결 가격
        @JsonProperty("volume")
        private String volume; // 체결 양
        @JsonProperty("funds")
        private String funds; // 체결된 총 가격
        @JsonProperty("side")
        private Side side; // 체결 종류
        @JsonProperty("created_at")
        private String createdAt; // 체결 시각
    }

    @ToString
    @Getter
    public static class OrderCancelResponse {

        @JsonProperty("uuid")
        private String uuid; // 주문의 고유 아이디
        @JsonProperty("side")
        private Side side; // 주문 종류
        @JsonProperty("ord_type")
        private OrdType ordType; // 주문 방식
        @JsonProperty("price")
        private Double price; // 주문 당시 화폐 가격
        @JsonProperty("state")
        private State state; // 주문 상태
        @JsonProperty("market")
        private String market; // 마켓의 유일키
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        @JsonProperty("created_at")
        private LocalDateTime createdAt; // 주문 생성 시간
        @JsonProperty("volume")
        private Double volume; // 사용자가 입력한 주문 양
        @JsonProperty("remaining_volume")
        private Double remainingVolume; // 체결 후 남은 주문 양
        @JsonProperty("reserved_fee")
        private Double reservedFee; // 수수료로 예약된 비용
        @JsonProperty("remaining_fee")
        private Double remainingFee; // 남은 수수료
        @JsonProperty("paid_fee")
        private Double paidFee; // 사용된 수수료
        @JsonProperty("locked")
        private Double locked; // 거래에 사용중인 비용
        @JsonProperty("executed_volume")
        private Double executedVolume; // 체결된 양
        @JsonProperty("trade_count")
        private Long tradeCount; // 해당 주문에 걸린 체결 수
    }

    @ToString
    @Getter
    public static class CandleResponse {

        @JsonProperty("market")
        private String market; // 마켓명
        @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
        @JsonProperty("candle_date_time_utc")
        private LocalDateTime candleDateTimeUtc; // 캔들 기준 시각(UTC 기준)
        @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
        @JsonProperty("candle_date_time_kst")
        private LocalDateTime candleDateTimeKst; // 캔들 기준 시각(KST 기준)
        @JsonProperty("opening_price")
        private Double openingPrice; // 시가
        @JsonProperty("high_price")
        private Double highPrice; // 고가
        @JsonProperty("low_price")
        private Double lowPrice; // 저가
        @JsonProperty("trade_price")
        private Double tradePrice; // 종가
        @JsonProperty("timestamp")
        private Long timestamp; // 해당 캔들에서 마지막 틱이 저장된 시각
        @JsonProperty("candle_acc_trade_price")
        private Double candleAccTradePrice; // 누적 거래 금액
        @JsonProperty("candle_acc_trade_volume")
        private Double candleAccTradeVolume; // 누적 거래량
        @JsonProperty("unit")
        private Integer unit; // 분 단위(유닛)
    }

    @ToString
    @Getter
    public static class OrderBookResponse {

        @JsonProperty("market")
        private String market; // 마켓 코드
        @JsonProperty("timestamp")
        private Long timestamp; // 호가 생성 시각
        @JsonProperty("total_ask_size")
        private Double totalAskSize; // 호가 매도 총 잔량
        @JsonProperty("total_bid_size")
        private Double totalBidSize; // 호가 매수 총 잔량
        @JsonProperty("orderbook_units")
        private List<OrderBookUnitResponse> orderbookUnitList; // 호가
    }

    @ToString
    @Getter
    public static class OrderBookUnitResponse {

        @JsonProperty("ask_price")
        private Double askPrice; // 매도호가
        @JsonProperty("bid_price")
        private Double bidPrice; // 매수호가
        @JsonProperty("ask_size")
        private Double askSize; // 매도 잔량
        @JsonProperty("bid_size")
        private Double bidSize; // 매수 잔량
    }

    @ToString
    @Getter
    public static class TickerResponse {

        @JsonProperty("market")
        private String market;  //종목 구분 코드
        @JsonProperty("trade_date")
        private String tradeDate;  //최근 거래 일자(UTC)
        @JsonProperty("trade_time")
        private String tradeTime;  //최근 거래 시각(UTC)
        @JsonProperty("trade_date_kst")
        private String tradeDateKst;  //최근 거래 일자(KST)
        @JsonProperty("trade_time_kst")
        private String tradeTimeKst;  //최근 거래 시각(KST)
        @JsonProperty("opening_price")
        private Double openingPrice;  //시가
        @JsonProperty("high_price")
        private Double highPrice;  //고가
        @JsonProperty("low_price")
        private Double lowPrice;  //저가
        @JsonProperty("trade_price")
        private Double tradePrice;  //종가
        @JsonProperty("prev_closing_price")
        private Double prevClosingPrice;  //전일 종가
        @JsonProperty("change")
        private String change; //EVEN : 보합   RISE : 상승   FALL : 하락
        @JsonProperty("change_price")
        private Double changePrice;  //변화액의 절대값
        @JsonProperty("change_rate")
        private Double changeRate;  //변화율의 절대값
        @JsonProperty("signed_change_price")
        private Double signedChangePrice;  //부호가 있는 변화액
        @JsonProperty("signed_change_rate")
        private Double signedChangeRate;  //부호가 있는 변화율
        @JsonProperty("trade_volume")
        private Double tradeVolume;  //가장 최근 거래량
        @JsonProperty("acc_trade_price")
        private Double accTradePrice;  //누적 거래대금(UTC 0시 기준)
        @JsonProperty("acc_trade_price_24h")
        private Double accTradePrice24h;  //24시간 누적 거래대금
        @JsonProperty("acc_trade_volume")
        private Double accTradeVolume;  //누적 거래량(UTC 0시 기준)
        @JsonProperty("acc_trade_volume_24h")
        private Double accTradeVolume24h;  //24시간 누적 거래량
        @JsonProperty("highest_52_week_price")
        private Double highest52WeekPrice;  //52주 신고가
        @JsonProperty("highest_52_week_date")
        private String highest52WeekDate;  //52주 신고가 달성일
        @JsonProperty("lowest_52_week_price")
        private Double lowest52WeekPrice;  //52주 신저가
        @JsonProperty("lowest_52_week_date")
        private String lowest52WeekDate;  //52주 신저가 달성일
        @JsonProperty("timestamp")
        private Long timestamp;  //타임스탬프
    }
}
