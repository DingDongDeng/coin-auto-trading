import {defineStore} from "pinia";
import axios from 'axios';

async function replayBackTest(processorId, replayCandleUnit, replayStartDateTime) {
    const response = await axios.get(`/coin/processor/backtest/${processorId}/replay`, {
        params: {
            replayCandleUnit,
            replayStartDateTime,
        }
    });
    return response.data;
}


export const useTradingChartStore = defineStore("tradingChart", {

    state: () => ({
        processors: []
    }),

    getters: {
        getProcessorById: (state) => {
            return (processorId) => state.processors.find(p => p.processorId === processorId)
        }
    },

    actions: {
        async loadTradingChart(processorId, replayCandleUnit, replayStartDateTime, callback) {
            const processor = this.getProcessorById(processorId)
            const data = (await replayBackTest(processorId, replayCandleUnit, replayStartDateTime)).body

            if (processor) {
                data.charts.forEach(chart => {
                    let existingChart = processor.charts.find(
                        c => c.exchangeType.type === chart.exchangeType.type &&
                            c.coinType.type === chart.coinType.type &&
                            c.candleUnit.type === chart.candleUnit.type
                    )

                    if (existingChart) {
                        // 기존 차트가 있으면 캔들과 거래 데이터를 append
                        existingChart.candles.push(...chart.candles)
                        existingChart.trades.push(...chart.trades)
                        return
                    }

                    throw new Error("차트를 찾지 못함")
                });
            } else {
                this.processors.push(
                    {
                        isLoading: data.next,
                        processorId: data.backTestProcessorId,
                        charts: data.charts,
                    }
                )
            }

            if (data.next) {
                setTimeout(async () => {
                    await this.loadTradingChart(processorId, replayCandleUnit, data.replayEndDateTime, callback)
                }, 100)
            } else {
                processor.isLoading = false
                callback()
                console.log("completed loadTradingChart processorId=", processor.processorId)
            }
        }
    }
});