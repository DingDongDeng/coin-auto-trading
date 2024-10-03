<script setup>
    import {defineProps, onMounted, ref} from 'vue'
    import {Chart, registerables} from 'chart.js'
    import 'chartjs-chart-financial' // OHLC 및 Candlestick 차트를 위한 플러그인
    import 'chartjs-adapter-date-fns' // 날짜 어댑터 불러오기
    import {CandlestickController, CandlestickElement, OhlcController, OhlcElement} from 'chartjs-chart-financial'
    import {useTradingChartStore} from "@/store/TradingChartStore";
    import {storeToRefs} from "pinia";
    import {useCodeStore} from "@/store/codeStore";

    //  변수
    const props = defineProps({processorId: String});
    const code = useCodeStore()
    const {candleUnits} = storeToRefs(code)
    const tradingChart = useTradingChartStore()
    const isDisabledReplay = ref(false);
    const selectedReplayCandleUnit = ref(null)

    // Chart.js 관련
    Chart.register(...registerables, CandlestickController, OhlcController, CandlestickElement, OhlcElement)
    const financialChart = ref(null)
    let chart = null
    const datasets = [];

    function addDatasets(processorId) {
        if (datasets.length === 0) {
            datasets.push(...createCharts(processorId))
            datasets.push(...createTrades(processorId))
        }
        return datasets
    }

    function createCharts(processorId) {
        const processor = tradingChart.getProcessor(processorId)
        if (!processor) {
            return []
        }
        return processor.charts.map(chart => {
            return {
                label: chart.coinType.desc,
                data: chart.candles,
                borderColor: 'black',
                color: {
                    up: 'red',
                    down: 'blue',
                    unchanged: 'gray',
                },
                yAxisID: 'candlestick', // Y축을 분리
            }
        })
    }

    function createTrades(processorId) {
        console.log(processorId)
        // const processor = tradingChart.processors
        //     .find(p => p.processorId === processorId)
        // const buys = processor.charts.map(chart => {
        //     return {
        //         type: 'scatter', // scatter 데이터셋을 추가
        //         label: 'Buy',
        //         data: [
        //             {x: candleDate('2024-09-10T09:30:00'), y: 75}, // 매수 주문 시점과 가격
        //         ],
        //         pointBackgroundColor: 'green', // 주문을 나타낼 점의 색
        //         pointRadius: 5, // 점의 크기
        //         yAxisID: 'candlestick', // Y축을 분리
        //     }
        // })
        //
        // const sells = processor.charts.map(chart => {
        //     return {
        //         type: 'scatter', // scatter 데이터셋을 추가
        //         label: 'Sell',
        //         data: [
        //             {x: candleDate('2024-09-10T09:30:00'), y: 75}, // 매수 주문 시점과 가격
        //         ],
        //         pointBackgroundColor: 'yellow', // 주문을 나타낼 점의 색
        //         pointRadius: 5, // 점의 크기
        //         yAxisID: 'candlestick', // Y축을 분리
        //     }
        // })
        // // 주문 정보
        // return [buys, sells]
        return []
    }

    onMounted(() => {
        const ctx = financialChart.value.getContext('2d')
        // 차트 생성
        chart = new Chart(ctx, {
            type: 'candlestick', // 'ohlc', 'candlestick'도 가능
            data: {
                datasets: addDatasets(props.processorId),
            },
            options: {
                responsive: true,
                scales: {
                    x: {
                        type: 'time', // 시간 스케일 사용
                        time: {
                            unit: 'minute', // 시간 단위를 분으로 설정
                        },
                    },
                    y: {
                        candlestick: {
                            type: 'linear',
                            position: 'left',
                        }
                    },
                },
            },
        })
    })
</script>

<template>
    <v-container>
        <v-row no-gutters>
            <v-col cols="3">
                <v-select v-model="selectedReplayCandleUnit"
                          :disabled="isDisabledReplay"
                          label="차트 캔들 단위"
                          :items="candleUnits"
                          item-title="desc"
                          item-value="type"
                ></v-select>
            </v-col>
            <v-col cols="2">
                <div class="ml-2">
                    <v-btn :disabled="isDisabledReplay"
                           @click="(async () => {
                               isDisabledReplay = true
                               await tradingChart.loadTradingChart(processorId, selectedReplayCandleUnit, null, ()=>{ chart.update()})
                               addDatasets(processorId)
                           })">
                        실행
                    </v-btn>
                </div>
            </v-col>
        </v-row>
        <v-row>
            <canvas ref="financialChart"></canvas>
        </v-row>
    </v-container>
</template>