// 공식문서 :  https://github.com/tvjsx/trading-vue-js/tree/master/docs/guide
// data 포멧 문서 : https://github.com/tvjsx/trading-vue-js/tree/master/docs/api#data-structure
// 참고하던 샘플 : https://codesandbox.io/s/vigorous-marco-4x1z11?file=/src/Grin.js

export default Vue.component('trading-chart', {
  props: {
    recordContextList: Array
  },
  template:
      `
<trading-vue :data="charts" :width="this.width" :height="this.height"
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
    getMacdOffchart() {
      return {
        name: "MACD",
        type: "RSI", //FIXME 찾아봐야함
        data: [],
        settings: {
          upper: 25,
          lower: -25
        }
      };
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
  watch: {
    recordContextList: {
      immediate: true,
      deep: true,
      handler(recordContextList, oldValue) {
        if (!recordContextList) {
          return;
        }

        // 차트 초기화
        const chart = this.getChart();
        const tradesOnchart = this.getTradesOnchart();
        const onchart = [tradesOnchart];
        const rsiOffchart = this.getRsiOffchart();
        const macdOffchart = this.getMacdOffchart();
        const offchart = [rsiOffchart, macdOffchart];

        // 차트 데이터 세팅
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
            //fixme 편하게 하려고 일단 timestamp 이렇게 해놨음
            tradesOnchart.data.push(
                [timestamp, trade.orderType === 'BUY' ? 0 : 1, trade.price]);
          }

          // 보조지표 세팅
          rsiOffchart.data.push([timestamp, recordContext.index.rsi * 100])
          macdOffchart.data.push([timestamp, recordContext.index.macd])
        }

        this.charts = {chart, onchart, offchart}
      }
    }

  },
  data() {
    return {
      charts: {
        chart: {
          type: "Candles",
          data: []
        },
        onchart: [],
        offchart: []
      }
      ,
      width: window.innerWidth,
      height: window.innerHeight,
      colors: {
        colorBack: '#fff',
        colorGrid: '#eee',
        colorText: '#333',
      }
    }
  }
});