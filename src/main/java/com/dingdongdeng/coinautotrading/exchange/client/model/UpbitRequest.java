package com.dingdongdeng.coinautotrading.exchange.client.model;

import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.OrdType;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.Side;
import com.dingdongdeng.coinautotrading.exchange.client.model.UpbitEnum.State;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UpbitRequest {

    @ToString
    @Getter
    @Builder
    public static class OrderChanceRequest {

        @JsonProperty("market")
        private String market; // 마켓 ID
    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class OrderInfoRequest {

        @JsonProperty("uuid")
        private String uuid; // 주문 UUID
        @JsonProperty("identifier")
        private String identifier; // 조회용 사용자 지정 값
    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class OrderInfoListRequest {

        @JsonProperty("market")
        private String market; // 마켓 아이디
        @JsonProperty("uuids")
        private List<String> uuidList; // 주문 UUID의 목록
        @JsonProperty("identifiers")
        private List<String> identifierList; // 주문 identifier의 목록
        @JsonProperty("state")
        private State state; // 주문 상태
        @JsonProperty("states")
        private List<State> stateList; // 주문 상태의 목록 //* 미체결 주문(wait, watch)과 완료 주문(done, cancel)은 혼합하여 조회하실 수 없습니다.
        @JsonProperty("page")
        private Integer page; // 페이지 수, default: 1
        @JsonProperty("limit")
        private Integer limit; // 요청 개수, default: 100
        @JsonProperty("order_by")
        private String orderBy; // 정렬 방식 - asc : 오름차순 - desc : 내림차순 (default)
    }

    @ToString
    @Getter
    @Builder
    public static class MarketCodeRequest {

        @JsonProperty("isDetail")
        private boolean isDetail; // 유의종목 필드과 같은 상세 정보 노출 여부
    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class OrderRequest {

        @JsonProperty("market")
        private String market; // 마켓 ID (필수)
        @JsonProperty("side")
        private Side side; // 주문 종류 (필수) - bid : 매수 - ask : 매도
        @JsonProperty("volume")
        private Double volume; // 주문량 (지정가, 시장가 매도 시 필수)
        @JsonProperty("price")
        private Double price; // 주문 가격. (지정가, 시장가 매수 시 필수)  ex) KRW-BTC 마켓에서 1BTC당 1,000 KRW로 거래할 경우, 값은 1000 이 된다. ex) KRW-BTC 마켓에서 1BTC당 매도 1호가가 500 KRW 인 경우, 시장가 매수 시 값을 1000으로 세팅하면 2BTC가 매수된다. (수수료가 존재하거나 매도 1호가의 수량에 따라 상이할 수 있음)
        @JsonProperty("ord_type")
        private OrdType ordType; // 주문 타입 (필수) - limit : 지정가 주문 - price : 시장가 주문(매수) - market : 시장가 주문(매도)
        @JsonProperty("identifier")
        private String identifier; // 조회용 사용자 지정값 (선택) (Uniq 값 사용)
    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class OrderCancelRequest {

        @JsonProperty("uuid")
        private String uuid; // 취소할 주문의 UUID
        @JsonProperty("identifier")
        private String identifier; // 조회용 사용자 지정값
    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class CandleRequest {

        private Integer unit; // 분 단위. 가능한 값 : 1, 3, 5, 15, 10, 30, 60, 240
        private String market; // 마켓 ID (필수)
        @JsonIgnore
        private LocalDateTime toKst;
        private Integer count; // 캔들 개수(최대 200개)

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime getTo() { // 마지막 캔들 시각 (비우면 가장 최근 시각), UTC 기준
            if (Objects.isNull(toKst)) {
                return null;
            }
            return toKst.atZone(ZoneId.of("Asia/Seoul"))
                .withZoneSameInstant(ZoneId.of("UTC"))
                .toLocalDateTime();
        }
    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class OrderBookRequest {

        @JsonProperty("markets")
        private List<String> marketList;
    }

    @ToString
    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class TickerRequest {

        @JsonProperty("markets")
        private List<String> marketList;
    }
}
