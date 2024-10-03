<script setup>
    import {computed, defineProps, onMounted, ref} from 'vue'
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
    const {getProcessorById} = storeToRefs(tradingChart)
    const isDisabledReplay = computed(() => {
        const processor = getProcessorById.value(props.processorId)
        if (processor?.charts) {
            return true
        }
        return processor?.isLoading
    });
    const selectedReplayCandleUnit = ref(null)

    // Chart.js 관련
    Chart.register(...registerables, CandlestickController, OhlcController, CandlestickElement, OhlcElement)
    const financialChart = ref(null)
    let chart = null
    const datasets = []

    function addDatasets(processorId) {
        if (datasets.length === 0) {
            datasets.push(...createCharts(processorId))
        }
        return datasets
    }

    function createCharts(processorId) {
        const processor = tradingChart.getProcessorById(processorId)
        if (!processor) {
            return []
        }
        return processor.charts.flatMap(chart => {
            const yAxisID = chart.coinType.type // 동일한 y 축 사용
            const title = chart.coinType.desc;
            return [
                {
                    label: title,
                    data: chart.candles,
                    backgroundColor: 'white',
                    color: {
                        up: 'red',
                        down: 'blue',
                        unchanged: 'gray',
                    },
                    yAxisID: yAxisID,
                },
                {
                    type: 'scatter', // scatter 데이터셋을 추가
                    label: `Buy ${title}`,
                    data: chart.buyTrades,
                    pointBackgroundColor: 'green', // 주문을 나타낼 점의 색
                    pointRadius: 5, // 점의 크기
                    yAxisID: yAxisID,
                },
                {
                    type: 'scatter',
                    label: `Sell ${title}`,
                    data: chart.sellTrades,
                    pointBackgroundColor: 'yellow',
                    pointRadius: 5,
                    yAxisID: yAxisID,
                }
            ]
        })
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
                            tooltipFormat: 'yyyy-MM-dd HH:mm:ss', // 툴팁 포맷 설정
                            displayFormats: {
                                minute: 'yyyy-MM-dd HH:mm:ss', // x축 레이블 포맷 설정
                                hour: 'yyyy-MM-dd HH:mm:ss',
                            },
                        },
                    },
                    y: {
                        display: false,
                    },
                    yAxis: {
                        display: false,
                    }
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
                               await tradingChart.loadTradingChart(processorId, selectedReplayCandleUnit, null, () => { chart.update('none')})
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