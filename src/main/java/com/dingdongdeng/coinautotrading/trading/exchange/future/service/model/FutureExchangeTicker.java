package com.dingdongdeng.coinautotrading.trading.exchange.future.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FutureExchangeTicker {

    private String symbol;
    private Double open;    //오픈가격
    private Double high;    //봉최고가
    private Double low;     //봉최저가
    private Double close;   //마감가격
    private Double volume;  //거래량
    private Long closeTime; //캔들마감시간
    private Double quoteAssetVolume;    //거래금
    private Long numberOfTrades;    //거래횟수
    private Long timestamp;  //타임스탬프

}
