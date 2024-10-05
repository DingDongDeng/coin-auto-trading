<script setup>
    import {computed, defineProps, onMounted, ref} from 'vue'
    import {Chart, registerables} from 'chart.js'
    import 'chartjs-chart-financial' // OHLC 및 Candlestick 차트를 위한 플러그인
    import 'chartjs-adapter-date-fns' // 날짜 어댑터 불러오기
    import {CandlestickController, CandlestickElement, OhlcController, OhlcElement} from 'chartjs-chart-financial'
    import {useTradingChartStore} from "@/store/tradingChartStore";
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
                    yAxisID: yAxisID,
                },
                {
                    type: 'scatter', // scatter 데이터셋을 추가
                    label: `${title} 매수`,
                    data: chart.buyTrades,
                    pointBackgroundColor: 'green', // 주문을 나타낼 점의 색
                    pointRadius: 5, // 점의 크기
                    yAxisID: yAxisID,
                },
                {
                    type: 'scatter',
                    label: `${title} 매도`,
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
                plugins: {
                    legend: {
                        position: 'right', // 'bottom', 'left', 'right'로 변경 가능
                        align: 'center', // 'start', 'center', 'end'로 변경 가능
                        labels: {
                            boxWidth: 20, // 라벨 앞에 있는 상자의 너비
                            padding: 10, // 라벨 간 간격
                            color: 'white', // 텍스트 색상
                        }
                    }
                },
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
                    // BITCOIN: {display:false} y축 숨김
                },
            },
        })
    })
</script>

<template>
    <v-container>
        <v-row no-gutters>
            <v-col cols="2">
                <v-select v-model="selectedReplayCandleUnit"
                          :disabled="isDisabledReplay"
                          label="캔들"
                          :items="candleUnits"
                          item-title="desc"
                          item-value="type"
                ></v-select>
            </v-col>
            <v-col cols="2">
                <div class="ml-2">
                    <v-btn :disabled="isDisabledReplay"
                           variant="outlined"
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