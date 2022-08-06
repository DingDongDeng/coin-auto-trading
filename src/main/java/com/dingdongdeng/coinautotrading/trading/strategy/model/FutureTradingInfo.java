package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.exchange.common.model.ExchangeCandles;
import com.dingdongdeng.coinautotrading.trading.index.Index;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureTradingInfo implements TradingInfo {

    private String identifyCode;

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
    private String unitCurrency; // 평단가 기준 화폐
    private Double currentPrice; // 현재 가격
    private ExchangeCandles candles; // 캔들 정보
    private Double liquidationPrice; // 청산가
    private Integer leverage; // 레버리지

    /**
     * 보조 지표
     **/
    private Index index;

}
