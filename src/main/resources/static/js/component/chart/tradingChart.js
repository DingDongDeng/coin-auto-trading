// 공식문서 :  https://github.com/tvjsx/trading-vue-js/tree/master/docs/guide
// data 포멧 문서 : https://github.com/tvjsx/trading-vue-js/tree/master/docs/api#data-structure
// 참고하던 샘플 : https://codesandbox.io/s/vigorous-marco-4x1z11?file=/src/Grin.js

export default Vue.component('trading-chart', {
  props: {
    recordContextList: Array,
  },
  template:
      `
<trading-vue :data="charts" :width="this.width" :height="this.height"
    :timezone="this.timezone"
    :color-back="colors.colorBack"
    :color-grid="colors.colorGrid"
    :color-text="colors.colorText">
</trading-vue>
`,
  components: {
    TradingVue: TradingVueJs.TradingVue
  },
  methods: {
    onResize(event) {
      this.width = this.$el.parentElement.offsetWidth; // 부모 컴포넌트 길이 만큼
      this.height = 500;
    },
    getChart() {
      return {
        type: "Candles",
        data: []
      };
    },
    getTradesOnchart() {
      return {
        name: "Trades",
        type: "Trades",
        data: [],
        settings: {
          markerSize: 4,
        },
      }
    },
    getResistanceOnchartList() {
      const resistanceOnchartList = [];
      for (let i = 0; i < 10; i++) {
        resistanceOnchartList.push(
            {
              name: "Resistance" + (i + 1),
              type: "EMA",
              data: [],
              settings: {
                "color": "#42b28a"
              },
            }
        )
      }
      return resistanceOnchartList;
    },
    getBollingerBandsOnchartList() {
      const bollingerBandsOnchartList = [];
      bollingerBandsOnchartList.push(
          {
            name: "bbands upper",
            type: "EMA",
            data: [],
            settings: {
              "color": "#FF5733"
            },
          }
      );
      bollingerBandsOnchartList.push(
          {
            name: "bbands middle",
            type: "EMA",
            data: [],
            settings: {
              "color": "#21871C"
            },
          }
      );
      bollingerBandsOnchartList.push(
          {
            name: "bbands lower",
            type: "EMA",
            data: [],
            settings: {
              "color": "#3347FF"
            },
          }
      );
      return bollingerBandsOnchartList;
    },
    getMaOnchartList() {
      const mvOnchartList = [];
      mvOnchartList.push(
          {
            name: "EMA 60",
            type: "EMA",
            data: [],
            settings: {
              "color": "#6D23A5FF"
            },
          },
          {
            name: "SMA 120",
            type: "EMA",
            data: [],
            settings: {
              "color": "#c66e10"
            },
          }
      );
      return mvOnchartList;
    },
    getRsiOffchart() {
      return {
        name: "RSI",
        type: "RSI",
        data: [],
        settings: {
          upper: 70,
          lower: 30
        }
      }
          ;
    },
    getMacdHistOffchart() {
      return {
        name: "MACD_HIST",
        type: "RSI", //FIXME 찾아봐야함
        data: [],
        settings: {
          upper: 25,
          lower: -25
        }
      };
    },
    getMacdMacdOffchart() {
      return {
        name: "MACD_MACD",
        type: "RSI",
        data: [],
        settings: {
          upper: 25,
          lower: -25
        }
      };
    },
    getObvHistOffchart() {
      return {
        name: "OBV_HIST",
        type: "RSI",
        data: [],
        settings: {
          upper: 25,
          lower: -25
        }
      }
    },
    getBollingerBandsHeightHistOffchart() {
      return {
        name: "BBANDS_HEIGHT_HIST",
        type: "RSI",
        data: [],
        settings: {
          upper: 25,
          lower: -25
        }
      }
    }

  },
  mounted() {
    window.addEventListener('resize', this.onResize)

    // vuetify가 width를 0으로 덮어써서 비동기로 호출하게함
    setTimeout(() => {
      this.width = this.$el.parentElement.offsetWidth;
      this.height = 500;
    }, 0);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize)
  },
  data() {
    return {
      width: window.innerWidth,
      height: window.innerHeight,
      timezone: 9,
      colors: {
        colorBack: '#fff',
        colorGrid: '#eee',
        colorText: '#333',
      }
    }
  },
  computed: {
    charts() {
      const recordContextList = this.recordContextList;
      if (!recordContextList) {
        return {
          chart: {
            type: "Candles",
            data: []
          },
          onchart: [],
          offchart: []
        };
      }

      // 차트 초기화

      // chart
      const chart = this.getChart();

      // onchart
      const tradesOnchart = this.getTradesOnchart();
      const onchart = [tradesOnchart];
      const maOnchartList = this.getMaOnchartList();
      onchart.push(maOnchartList[0]); // ema60
      onchart.push(maOnchartList[1]); // sma120
      const bollingerBandsOnchartList = this.getBollingerBandsOnchartList();
      onchart.push(bollingerBandsOnchartList[0]); // upper
      onchart.push(bollingerBandsOnchartList[1]); // middle
      onchart.push(bollingerBandsOnchartList[2]); // lower
      const resistanceOnchartList = this.getResistanceOnchartList();
      // 지지/저항 차트에서 생략
      for (let resistanceOnchart of resistanceOnchartList) {
        onchart.push(resistanceOnchart);
      }

      // offchart
      const rsiOffchart = this.getRsiOffchart();
      const macdHistOffchart = this.getMacdHistOffchart();
      const macdMacdOffchart = this.getMacdMacdOffchart();
      const obvHistOffchart = this.getObvHistOffchart();
      const bollingerBandsHeightHistOffchart = this.getBollingerBandsHeightHistOffchart();
      const offchart = [rsiOffchart, macdHistOffchart, macdMacdOffchart,
        obvHistOffchart, bollingerBandsHeightHistOffchart];

      // 차트들 데이터 세팅
      for (let recordContext of recordContextList) {
        const candle = recordContext.currentCandle;
        const timestamp = candle.timestamp;

        // 캔들 세팅
        chart.data.push(
            [
              timestamp,
              candle.openingPrice,
              candle.highPrice,
              candle.lowPrice,
              candle.tradePrice,
              candle.candleAccTradeVolume
            ]
        )

        // 주문 세팅
        for (let trade of recordContext.tradingResultList) {
          tradesOnchart.data.push(
              [timestamp, trade.orderType === 'BUY' ? 1 : 0, trade.price]);
        }

        // 보조지표 세팅(offChart)
        rsiOffchart.data.push([timestamp, recordContext.index.rsi * 100]);
        macdHistOffchart.data.push([timestamp, recordContext.index.macd.hist]);
        macdMacdOffchart.data.push([timestamp, recordContext.index.macd.macd]);
        obvHistOffchart.data.push([timestamp, recordContext.index.obv.hist]);
        bollingerBandsHeightHistOffchart.data.push(
            [timestamp, recordContext.index.bollingerBands.heightHist])

        // 보조지표 세팅(onChart)
        for (let i = 0; i < resistanceOnchartList.length; i++) {
          if (i < recordContext.index.resistance.resistancePriceList.length) {
            resistanceOnchartList[i].data.push(
                [timestamp,
                  recordContext.index.resistance.resistancePriceList[i]]
            );
          }
        }
        bollingerBandsOnchartList[0].data.push(
            [timestamp, recordContext.index.bollingerBands.upper])
        bollingerBandsOnchartList[1].data.push(
            [timestamp, recordContext.index.bollingerBands.middle])
        bollingerBandsOnchartList[2].data.push(
            [timestamp, recordContext.index.bollingerBands.lower])
        maOnchartList[0].data.push([timestamp, recordContext.index.ma.ema60])
        maOnchartList[1].data.push([timestamp, recordContext.index.ma.sma120])
      }

      return {chart, onchart, offchart};
    }
  }
});