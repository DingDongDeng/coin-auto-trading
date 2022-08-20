package com.dingdongdeng.coinautotrading.trading.record;

import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.TradingTag;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Recorder {

    private final String id = UUID.randomUUID().toString();
    private final List<RecordContext> recordContextList = new ArrayList<>();

    private double totalBuyValue;
    private double totalProfitValue;
    private double totalLossValue;

    private double totalFee;
    private double marginPrice; // 이익금
    private double marginRate; // 이익율
    private String eventMessage;

    public void record(RecordContext recordContext) {
        recordContextList.add(recordContext);

        recordContext.getTradingResultList().forEach(tradingResult -> {
            OrderType orderType = tradingResult.getOrderType();
            TradingTag tradingTag = tradingResult.getTradingTag();

            // 주문 취소 일때
            if (orderType == OrderType.CANCEL) {
                switch (tradingResult.getTradingTag()) {
                    case BUY -> totalBuyValue -= tradingResult.getPrice() * tradingResult.getVolume();
                    case PROFIT -> totalProfitValue -= tradingResult.getPrice() * tradingResult.getVolume();
                    case LOSS -> totalLossValue -= tradingResult.getPrice() * tradingResult.getVolume();
                }
                return;

            } else { // 주문(매수,매도) 일때
                switch (tradingResult.getTradingTag()) {
                    case BUY -> totalBuyValue += tradingResult.getPrice() * tradingResult.getVolume();
                    case PROFIT -> totalProfitValue += tradingResult.getPrice() * tradingResult.getVolume();
                    case LOSS -> totalLossValue += tradingResult.getPrice() * tradingResult.getVolume();
                }

                // 수수료 계산(반올림)
                this.totalFee += Math.round(tradingResult.getFee());
            }

            // 마진 계산(매수만 한 시점에서는 이익/손실 실현이 안됐기때문에 계산하지 않음)
            if (tradingTag != TradingTag.BUY) {
                // 이익율 소수점 둘째자리 아래 반올림 (xx.xx%)
                this.marginRate = Math.round((((totalProfitValue + totalLossValue - totalFee) / totalBuyValue) * 100d - 100d) * 100) / 100.0;
                // 이익금 소수점 반올림
                this.marginPrice = Math.round((totalProfitValue + totalLossValue - totalFee) - totalBuyValue);
            }
        });
    }
}
