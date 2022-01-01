package com.dingdongdeng.coinautotrading.exchange.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

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
        private String side; // 주문 종류
        @JsonProperty("ord_type")
        private String ordType; // 주문 방식
        @JsonProperty("price")
        private Double price; // 주문 당시 화폐 가격
        @JsonProperty("avg_price")
        private Double avgPrice; // 체결 가격의 평균가
        @JsonProperty("state")
        private String state; // 주문 상태
        @JsonProperty("market")
        private String market; // 마켓의 유일키
        @JsonProperty("created_at")
        private String createdAt; // 주문 생성 시간
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
        private Integer tradeCount; // 해당 주문에 걸린 체결 수
        @JsonProperty("trades")
        private List<Trade> tradeList; // 체결
    }

    @ToString
    @Getter
    public static class Trade {

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
        private String side; // 체결 종류
        @JsonProperty("created_at")
        private String createdAt; // 체결 시각
    }

    @ToString
    @Getter
    public static class OrderCancelResponse {

        @JsonProperty("uuid")
        private String uuid; //	주문의 고유 아이디	String
        @JsonProperty("side")
        private String side; //	주문 종류	String
        @JsonProperty("ord_type")
        private String ordType; //	주문 방식	String
        @JsonProperty("price")
        private String price; //	주문 당시 화폐 가격	NumberString
        @JsonProperty("state")
        private String state; //	주문 상태	String
        @JsonProperty("market")
        private String market; //	마켓의 유일키	String
        @JsonProperty("created_at")
        private String createdAt; //	주문 생성 시간	String
        @JsonProperty("volume")
        private String volume; //	사용자가 입력한 주문 양	NumberString
        @JsonProperty("remaining_volume")
        private String remainingVolume; //	체결 후 남은 주문 양	NumberString
        @JsonProperty("reserved_fee")
        private String reservedFee; //	수수료로 예약된 비용	NumberString
        @JsonProperty("remaining_fee")
        private String remainingFee; //	남은 수수료	NumberString
        @JsonProperty("paid_fee")
        private String paidFee; //	사용된 수수료	NumberString
        @JsonProperty("locked")
        private String locked; //	거래에 사용중인 비용	NumberString
        @JsonProperty("executed_volume")
        private String executedVolume; //	체결된 양	NumberString
        @JsonProperty("trade_count")
        private String tradeCount; //	해당 주문에 걸린 체결 수	Integer
    }

}
