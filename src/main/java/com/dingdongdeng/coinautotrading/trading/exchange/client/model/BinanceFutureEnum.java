package com.dingdongdeng.coinautotrading.trading.exchange.client.model;

import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.TimeInForceType;
import com.dingdongdeng.coinautotrading.trading.exchange.client.model.UpbitEnum.MarketType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class BinanceFutureEnum {

    @Getter
    @AllArgsConstructor
    public enum Type {
        LIMIT("지정가 주문", PriceType.LIMIT_PRICE),
        MARKET("시장가 주문", PriceType.SELLING_MARKET_PRICE),
        STOP("스탑 주문(지정가)", null),
        STOP_MARKET("스탑 주문(시장가)", null),
        TAKE_PROFIT("이익 실현 주문(시장가)", null),
        TAKE_PROFIT_MARKET("이익 실현 주문(시장가)", null),
        TRAILING_STOP_MARKET("콜백 주문", null),
        ;

        private String desc;
        private PriceType priceType;

        public static Type of(PriceType priceType) {
            return Arrays.stream(Type.values())
                    .filter(type -> type.getPriceType() == priceType)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(priceType.name()));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Side {
        BUY("매수", OrderType.BUY),
        SELL("매도", OrderType.SELL),
        ;

        private String desc;
        private OrderType orderType;

        public static BinanceFutureEnum.Side of(OrderType orderType) {
            return Arrays.stream(BinanceFutureEnum.Side.values())
                    .filter(type -> type.getOrderType() == orderType)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(orderType.name()));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum TimeInForce {
        GTC("Good Till Cancel", TimeInForceType.GTC),
        FOK("Fill Or Kill", TimeInForceType.FOK),
        GTX("Immediate or Cancel", TimeInForceType.GTX),
        IOC("Good Till Crossing", TimeInForceType.IOC),
        ;

        private String desc;
        private TimeInForceType timeInForceType;

        public static BinanceFutureEnum.TimeInForce of(TimeInForceType timeInForceType) {
            return Arrays.stream(BinanceFutureEnum.TimeInForce.values())
                    .filter(type -> type.getTimeInForceType() == timeInForceType)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(timeInForceType.name()));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Symbol {
        USDT_ADA("달러 에이다", "ADAUSDT", CoinType.ADA),
        USDT_SOL("달러 솔라나", "SOLUSDT", CoinType.SOLANA),
        USDT_XRP("달러 리플", "XRPUSDT", CoinType.XRP),
        USDT_ETH("달러 이더리움", "ETHUSDT", CoinType.ETHEREUM),
        USDT_AVAX("달러 아발란체", "AVAXUSDT", CoinType.AVALANCHE),
        USDT_DOT("달러 폴카닷", "DOTUSDT", CoinType.POLKADOT),
        USDT_DOGE("달러 도지", "DOGEUSDT", CoinType.DOGE),
        USDT_BTC("달러 비트코", "BTCUSDT", CoinType.BITCOIN),

        ;
        private String desc;
        private String code;
        private CoinType coinType;

        public static Symbol of(CoinType coinType) {
            return Arrays.stream(Symbol.values())
                .filter(type -> type.getCoinType() == coinType)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(coinType.name()));
        }

        public static Symbol of(String code) {
            return Arrays.stream(Symbol.values())
                .filter(type -> type.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(code));

        }
    }

}
