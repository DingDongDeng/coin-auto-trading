// 샘플 : https://codesandbox.io/s/dgv2i?file=/src/PerfectTrades.js:0-1629

export default {
  name: "Trades",
  mixins: [TradingVueJs.TradingVue.Overlay],
  methods: {
    draw(ctx) {
      let layout = this.$props.layout; // Layout object (see API BOOK)
      ctx.lineWidth = 1.5;
      ctx.strokeStyle = "black";
      for (var i in this.$props.data) {
        let p = this.$props.data[i];
        // We use previos point here, but the profit should
        // be already in the data (in a real usecase)
        let prev = this.$props.data[i - 1];

        ctx.fillStyle = p[1] ? this.buy_color : this.sell_color;
        ctx.beginPath();
        let x = layout.t2screen(p[0]); // x - Mapping
        let y = layout.$2screen(p[2]); // y - Mapping
        ctx.arc(x, y, 5.5, 0, Math.PI * 2, true); // Trade point
        ctx.fill();
        ctx.stroke();

        // If this is a SELL, draw the profit label
        if (p[1] === 0 && prev) {
          let profit = p[2] / prev[2] - 1;
          profit = (profit * 100).toFixed(2) + "%";
          ctx.fillStyle = "#555";
          ctx.font = "16px Arial";
          ctx.textAlign = "center";
          ctx.fillText(profit, x, y - 25);
        }
      }
    },
    use_for() {
      return ["PerfectTrades"];
    }
  },
  computed: {
    sett() {
      // Just an alias
      return this.$props.settings;
    },
    buy_color() {
      return this.sett["buyColor"] || "#bfff00"; // default value
    },
    sell_color() {
      return this.sett["sellColor"] || "#ec4662";
    },
    marker_size() {
      return this.sett["markerSize"] || 5;
    },
    show_label() {
      return this.sett["showLabel"] !== false;
    }
  }
};
