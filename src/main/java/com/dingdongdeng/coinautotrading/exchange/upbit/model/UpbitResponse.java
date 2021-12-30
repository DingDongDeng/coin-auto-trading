package com.dingdongdeng.coinautotrading.exchange.upbit.model;

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
        private long balance; // 주문가능 금액/수량
        @JsonProperty("locked")
        private long locked; // 주문 중 묶여있는 금액/수량
        @JsonProperty("avg_buy_price")
        private long avgBuyPrice; // 매수평균가
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

}
