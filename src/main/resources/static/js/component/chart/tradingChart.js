// 공식문서 :  https://github.com/tvjsx/trading-vue-js/tree/master/docs/guide
// data 포멧 문서 : https://github.com/tvjsx/trading-vue-js/tree/master/docs/api#data-structure
// 참고하던 샘플 : https://codesandbox.io/s/vigorous-marco-4x1z11?file=/src/Grin.js

export default Vue.component('trading-chart', {
  props: {
    recordContextList: Array
  },
  template:
      `
<!--:overlays="overlays" fixme 이거 안에 넣어야함 -->
<trading-vue :data="chart" :width="this.width" :height="this.height"
    
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
      this.height = window.innerHeight;
    }
  },
  mounted() {
    window.addEventListener('resize', this.onResize)
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
        const chart = {
          type: "Candles",
          data: []
        };
        const onchart = [
          /*{
            name: "Trades",
            type: "PerfectTrades",
            data: [
              [1552550400000, 1, 3935.4046923289475],
            ],
            settings: {}
          }*/
        ];
        const rsiOffchart = {
              name: "RSI",
              type: "RSI",
              data: [],
              settings: {
                upper: 70,
                lower: 30
              }
            }
        ;

        const macdOffchart = {
              name: "MACD",
              type: "RSI", //FIXME 찾아봐야함
              data: [],
              settings: {
                upper: 25,
                lower: -25
              }
            }
        ;
        const offchart = [rsiOffchart, macdOffchart]

        for (let recordContext of recordContextList) {
          const candle = recordContext.currentCandle;
          const timestamp = candle.timestamp;

          // chart 세팅
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

          // offchart 세팅(보조지표)
          rsiOffchart.data.push([timestamp, recordContext.index.rsi * 100])
          macdOffchart.data.push([timestamp, recordContext.index.macd])
        }

        this.chart = {chart, onchart, offchart}
      }
    }

  },
  data() {
    return {
      chart: {
        chart: {},
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
      },
      //overlays: [Grin]  fixme 이것도 수정해야함 퍼펙트트레이드스 인가 뭔가
    }
  }
});