import {defineStore} from "pinia";
import axios from 'axios';

async function replayBackTest(processorId, replayCandleUnit, replayDateTime) {
    const response = await axios.get(`/coin/processor/backtest/${processorId}/replay`, {
        params: {
            replayCandleUnit,
            replayDateTime,
        }
    });
    return response.data;
}


export const useTradingChartStore = defineStore("tradingChart", {

    state: () => ({
        processors: []
    }),

    getters: {},

    actions: {
        async loadTradingChart(processorId, replayCandleUnit, replayStartDateTime) {
            const processor = this.processors.find(p => p.processorId === processorId)
            const response = (await replayBackTest(processorId, replayCandleUnit, replayStartDateTime)).body
            if (processor) {
                response.charts.forEach(chart => {
                    let existingChart = processor.charts.find(
                        c => c.exchangeType === chart.exchangeType &&
                            c.coinType === chart.coinType &&
                            c.candleUnit === chart.candleUnit
                    )

                    // 기존 차트가 있으면 캔들과 거래 데이터를 append
                    existingChart.candles.push(...chart.candles)
                    existingChart.trades.push(...chart.trades)
                });
            } else {
                this.processors.push(
                    {
                        isLoading: response.next,
                        processorId: response.backTestProcessorId,
                        charts: response.charts,
                    }
                )
            }

            if (response.next) {
                await this.loadTradingChart(processorId, replayCandleUnit, response.replayEndDateTime)
            }
        }
    }
});