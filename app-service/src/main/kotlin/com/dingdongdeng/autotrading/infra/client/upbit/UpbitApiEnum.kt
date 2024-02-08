package com.dingdongdeng.autotrading.infra.client.upbit

import com.dingdongdeng.autotrading.infra.common.type.CoinType
import com.dingdongdeng.autotrading.infra.common.type.OrderType
import com.dingdongdeng.autotrading.infra.common.type.PriceType
import com.dingdongdeng.autotrading.infra.common.type.TradeState

enum class State(
    val desc: String,
    val tradeState: TradeState
) {
    wait("체결 대기", TradeState.WAIT),
    watch("예약주문 대기", TradeState.WAIT),
    done("전체 체결 완료", TradeState.DONE),
    cancel("주문 취소", TradeState.CANCEL),
    ;
}

enum class MarketType(
    val desc: String,
    val code: String,
    val coinType: CoinType,
) {
    // 2024-02-08 기준
    KRW_BTC("원화 비트코인", "KRW-BTC", CoinType.BITCOIN),
    KRW_ETH("원화 이더리움", "KRW-ETH", CoinType.ETHEREUM),
    KRW_NEO("원화 네오", "KRW-NEO", CoinType.NEO),
    KRW_MTL("원화 메탈", "KRW-MTL", CoinType.METAL),
    KRW_XRP("원화 리플", "KRW-XRP", CoinType.RIPPLE),
    KRW_ETC("원화 이더리움클래식", "KRW-ETC", CoinType.ETHEREUM_CLASSIC),
    KRW_SNT("원화 스테이터스네트워크토큰", "KRW-SNT", CoinType.STATUS_NETWORK_TOKEN),
    KRW_WAVES("원화 웨이브", "KRW-WAVES", CoinType.WAVES),
    KRW_XEM("원화 넴", "KRW-XEM", CoinType.NEM),
    KRW_QTUM("원화 퀀텀", "KRW-QTUM", CoinType.QTUM),
    KRW_LSK("원화 리스크", "KRW-LSK", CoinType.LISK),
    KRW_STEEM("원화 스팀", "KRW-STEEM", CoinType.STEEM),
    KRW_XLM("원화 스텔라루멘", "KRW-XLM", CoinType.LUMEN),
    KRW_ARDR("원화 아더", "KRW-ARDR", CoinType.ARDOR),
    KRW_ARK("원화 아크", "KRW-ARK", CoinType.ARK),
    KRW_STORJ("원화 스토리지", "KRW-STORJ", CoinType.STORJ),
    KRW_GRS("원화 그로스톨코인", "KRW-GRS", CoinType.GROESTLCOIN),
    KRW_ADA("원화 에이다", "KRW-ADA", CoinType.ADA),
    KRW_SBD("원화 스팀달러", "KRW-SBD", CoinType.STEEMDOLLARS),
    KRW_POWR("원화 파워렛저", "KRW-POWR", CoinType.POWER_LEDGER),
    KRW_BTG("원화 비트코인골드", "KRW-BTG", CoinType.BITCOIN_GOLD),
    KRW_ICX("원화 아이콘", "KRW-ICX", CoinType.ICON),
    KRW_EOS("원화 이오스", "KRW-EOS", CoinType.EOS),
    KRW_TRX("원화 트론", "KRW-TRX", CoinType.TRON),
    KRW_SC("원화 시아코인", "KRW-SC", CoinType.SIACOIN),
    KRW_ONT("원화 온톨로지", "KRW-ONT", CoinType.ONTOLOGY),
    KRW_ZIL("원화 질리카", "KRW-ZIL", CoinType.ZILLIQA),
    KRW_POLYX("원화 폴리매쉬", "KRW-POLYX", CoinType.POLYMESH),
    KRW_ZRX("원화 제로엑스", "KRW-ZRX", CoinType.ZERO_X_PROTOCOL),
    KRW_LOOM("원화 룸네트워크", "KRW-LOOM", CoinType.LOOM_NETWORK),
    KRW_BCH("원화 비트코인캐시", "KRW-BCH", CoinType.BITCOIN_CASH),
    KRW_BAT("원화 베이직어텐션토큰", "KRW-BAT", CoinType.BASIC_ATTENTION_TOKEN),
    KRW_IOST("원화 아이오에스티", "KRW-IOST", CoinType.IOST),
    KRW_CVC("원화 시빅", "KRW-CVC", CoinType.CIVIC),
    KRW_IQ("원화 아이큐", "KRW-IQ", CoinType.IQ_WIKI),
    KRW_IOTA("원화 아이오타", "KRW-IOTA", CoinType.IOTA),
    KRW_HIFI("원화 하이파이", "KRW-HIFI", CoinType.HIFI_FINANCE),
    KRW_ONG("원화 온톨로지가스", "KRW-ONG", CoinType.ONG),
    KRW_GAS("원화 가스", "KRW-GAS", CoinType.GAS),
    KRW_UPP("원화 센티넬프로토콜", "KRW-UPP", CoinType.SENTINEL_PROTOCOL),
    KRW_ELF("원화 엘프", "KRW-ELF", CoinType.AELF),
    KRW_KNC("원화 카이버네트워크", "KRW-KNC", CoinType.KYBER_NETWORK),
    KRW_BSV("원화 비트코인에스브이", "KRW-BSV", CoinType.BITCOIN_SV),
    KRW_THETA("원화 쎄타토큰", "KRW-THETA", CoinType.THETA_TOKEN),
    KRW_QKC("원화 쿼크체인", "KRW-QKC", CoinType.QUARKCHAIN),
    KRW_BTT("원화 비트토렌트", "KRW-BTT", CoinType.BITTORRENT),
    KRW_MOC("원화 모스코인", "KRW-MOC", CoinType.MOSS_COIN),
    KRW_TFUEL("원화 쎄타퓨엘", "KRW-TFUEL", CoinType.THETA_FUEL),
    KRW_MANA("원화 디센트럴랜드", "KRW-MANA", CoinType.DECENTRALAND),
    KRW_ANKR("원화 앵커", "KRW-ANKR", CoinType.ANKR),
    KRW_AERGO("원화 아르고", "KRW-AERGO", CoinType.AERGO),
    KRW_ATOM("원화 코스모스", "KRW-ATOM", CoinType.COSMOS),
    KRW_TT("원화 썬더코어", "KRW-TT", CoinType.THUNDERCORE),
    KRW_CRE("원화 캐리프로토콜", "KRW-CRE", CoinType.CARRY_PROTOCOL),
    KRW_MBL("원화 무비블록", "KRW-MBL", CoinType.MOVIEBLOC),
    KRW_WAXP("원화 왁스", "KRW-WAXP", CoinType.WAX),
    KRW_HBAR("원화 헤데라", "KRW-HBAR", CoinType.HEDERA),
    KRW_MED("원화 메디블록", "KRW-MED", CoinType.MEDIBLOC),
    KRW_MLK("원화 밀크", "KRW-MLK", CoinType.MILK),
    KRW_STPT("원화 에스티피", "KRW-STPT", CoinType.STANDARD_TOKENIZATION_PROTOCOL),
    KRW_ORBS("원화 오브스", "KRW-ORBS", CoinType.ORBS),
    KRW_VET("원화 비체인", "KRW-VET", CoinType.VECHAIN),
    KRW_CHZ("원화 칠리즈", "KRW-CHZ", CoinType.CHILIZ),
    KRW_STMX("원화 스톰엑스", "KRW-STMX", CoinType.STORMX),
    KRW_DKA("원화 디카르고", "KRW-DKA", CoinType.DKARGO),
    KRW_HIVE("원화 하이브", "KRW-HIVE", CoinType.HIVE),
    KRW_KAVA("원화 카바", "KRW-KAVA", CoinType.KAVA),
    KRW_AHT("원화 아하토큰", "KRW-AHT", CoinType.AHATOKEN),
    KRW_LINK("원화 체인링크", "KRW-LINK", CoinType.CHAINLINK),
    KRW_XTZ("원화 테조스", "KRW-XTZ", CoinType.TEZOS),
    KRW_BORA("원화 보라", "KRW-BORA", CoinType.BORA),
    KRW_JST("원화 저스트", "KRW-JST", CoinType.JUST),
    KRW_CRO("원화 크로노스", "KRW-CRO", CoinType.CRONOS),
    KRW_TON("원화 톤", "KRW-TON", CoinType.TON),
    KRW_SXP("원화 솔라", "KRW-SXP", CoinType.SXP),
    KRW_HUNT("원화 헌트", "KRW-HUNT", CoinType.HUNT),
    KRW_PLA("원화 플레이댑", "KRW-PLA", CoinType.PLAYDAPP),
    KRW_DOT("원화 폴카닷", "KRW-DOT", CoinType.POLKADOT),
    KRW_MVL("원화 엠블", "KRW-MVL", CoinType.MVL),
    KRW_STRAX("원화 스트라티스", "KRW-STRAX", CoinType.STRATIS),
    KRW_AQT("원화 알파쿼크", "KRW-AQT", CoinType.ALPHA_QUARK_TOKEN),
    KRW_GLM("원화 골렘", "KRW-GLM", CoinType.GOLEM),
    KRW_SSX("원화 썸씽", "KRW-SSX", CoinType.SOMESING),
    KRW_META("원화 메타디움", "KRW-META", CoinType.METADIUM),
    KRW_FCT2("원화 피르마체인", "KRW-FCT2", CoinType.FIRMACHAIN),
    KRW_CBK("원화 코박토큰", "KRW-CBK", CoinType.COBAK_TOKEN),
    KRW_SAND("원화 샌드박스", "KRW-SAND", CoinType.THE_SANDBOX),
    KRW_HPO("원화 히포크랏", "KRW-HPO", CoinType.HIPPOCRAT),
    KRW_DOGE("원화 도지코인", "KRW-DOGE", CoinType.DOGECOIN),
    KRW_STRK("원화 스트라이크", "KRW-STRK", CoinType.STRIKE),
    KRW_PUNDIX("원화 펀디엑스", "KRW-PUNDIX", CoinType.PUNDI_X),
    KRW_FLOW("원화 플로우", "KRW-FLOW", CoinType.FLOW),
    KRW_AXS("원화 엑시인피니티", "KRW-AXS", CoinType.AXIE_INFINITY),
    KRW_STX("원화 스택스", "KRW-STX", CoinType.STACKS),
    KRW_XEC("원화 이캐시", "KRW-XEC", CoinType.ECASH),
    KRW_SOL("원화 솔라나", "KRW-SOL", CoinType.SOLANA),
    KRW_MATIC("원화 폴리곤", "KRW-MATIC", CoinType.POLYGON),
    KRW_AAVE("원화 에이브", "KRW-AAVE", CoinType.AAVE),
    KRW_1INCH("원화 1인치네트워크", "KRW-1INCH", CoinType.ONE_INCH_NETWORK),
    KRW_ALGO("원화 알고랜드", "KRW-ALGO", CoinType.ALGORAND),
    KRW_NEAR("원화 니어프로토콜", "KRW-NEAR", CoinType.NEAR_PROTOCOL),
    KRW_AVAX("원화 아발란체", "KRW-AVAX", CoinType.AVALANCHE),
    KRW_T("원화 쓰레스홀드", "KRW-T", CoinType.THRESHOLD),
    KRW_CELO("원화 셀로", "KRW-CELO", CoinType.CELO),
    KRW_GMT("원화 스테픈", "KRW-GMT", CoinType.STEPN),
    KRW_APT("원화 앱토스", "KRW-APT", CoinType.APTOS),
    KRW_SHIB("원화 시바이누", "KRW-SHIB", CoinType.SHIBA_INU),
    KRW_MASK("원화 마스크네트워크", "KRW-MASK", CoinType.MASK_NETWORK),
    KRW_ARB("원화 아비트럼", "KRW-ARB", CoinType.ARBITRUM),
    KRW_EGLD("원화 멀티버스엑스", "KRW-EGLD", CoinType.MULTIVERSX),
    KRW_SUI("원화 수이", "KRW-SUI", CoinType.SUI),
    KRW_GRT("원화 더그래프", "KRW-GRT", CoinType.THE_GRAPH),
    KRW_BLUR("원화 블러", "KRW-BLUR", CoinType.BLUR),
    KRW_IMX("원화 이뮤터블엑스", "KRW-IMX", CoinType.IMMUTABLE_X),
    KRW_SEI("원화 세이", "KRW-SEI", CoinType.SEI),
    KRW_MINA("원화 미나", "KRW-MINA", CoinType.MINA),
    KRW_CTC("원화 크레딧코인", "KRW-CTC", CoinType.CREDITCOIN),
    KRW_ASTR("원화 아스타", "KRW-ASTR", CoinType.ASTAR),
    KRW_ID("원화 스페이스아이디", "KRW-ID", CoinType.SPACE_ID),
    ;


    companion object {
        fun of(coinType: CoinType): MarketType {
            return values().first { it.coinType === coinType }
        }

        fun of(code: String): MarketType {
            return values().first { it.code == code }
        }
    }
}

enum class OrdType(
    val desc: String,
    val priceType: PriceType,
    val orderTypeList: List<OrderType>,
) {
    limit("지정가 주문", PriceType.LIMIT, listOf(OrderType.BUY, OrderType.SELL)),
    price("시장가 주문(매수)", PriceType.MARKET, listOf(OrderType.BUY)),
    market("시장가 주문(매도)", PriceType.MARKET, listOf(OrderType.SELL));

    companion object {
        fun of(priceType: PriceType, orderType: OrderType?): OrdType {
            return values().first { it.priceType == priceType && it.orderTypeList.contains(orderType) }
        }
    }
}


enum class Side(
    val desc: String,
    val orderType: OrderType,
) {
    bid("매수", OrderType.BUY),
    ask("매도", OrderType.SELL),
    ;

    companion object {
        fun of(orderType: OrderType): Side {
            return values().first { it.orderType == orderType }
        }
    }
}

