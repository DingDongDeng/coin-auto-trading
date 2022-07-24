package com.dingdongdeng.coinautotrading.trading.exchange.spot.service.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class SpotExchangeTradingInfo { //fixme 사용중인 모델만 남도록 리팩토링

    private CoinExchangeType coinExchangeType;
    private CoinType coinType;
    private TradingTerm tradingTerm;
    /**
     * 계좌
     **/
    private String currency; // 화폐를 의미하는 영문 대문자 코드
    private Double balance; // 주문가능 금액/수량
    private Double locked; // 주문 중 묶여있는 금액/수량

    /**
     * 주문 정보
     **/
    private Double avgBuyPrice; // 매수평균가
    private boolean avgBuyPriceModified; // 매수평균가 수정 여부
    private String unitCurrency; // 평단가 기준 화폐

    private ExchangeCandles candles; // 캔들 정보
    private SpotExchangeTicker ticker; // 현재가 정보

    /**
     * 보조 지표
     **/
    private Double rsi;
    private List<Double> resistancePriceList;

}
