<template>
    <canvas ref="financialChart"></canvas>
</template>

<script setup>
    import {onMounted, ref} from 'vue'
    import {Chart, registerables} from 'chart.js'
    import 'chartjs-chart-financial' // OHLC 및 Candlestick 차트를 위한 플러그인
    import 'chartjs-adapter-date-fns' // 날짜 어댑터 불러오기
    import {CandlestickController, CandlestickElement, OhlcController, OhlcElement} from 'chartjs-chart-financial'

    // Chart.js에서 필요한 요소 및 컨트롤러, 엘리먼트를 등록
    Chart.register(...registerables, CandlestickController, OhlcController, CandlestickElement, OhlcElement)

    const financialChart = ref(null)

    onMounted(() => {
        const ctx = financialChart.value.getContext('2d')

        // 차트 생성
        new Chart(ctx, {
            type: 'ohlc', // 'ohlc', 'candlestick'도 가능
            data: {
                datasets: [
                    {
                        label: 'OHLC Data',
                        data: [
                            {x: new Date('2024-09-10'), o: 60, h: 70, l: 55, c: 65},
                            {x: new Date('2024-09-11'), o: 65, h: 80, l: 60, c: 75},
                            {x: new Date('2024-09-12'), o: 75, h: 85, l: 70, c: 80},
                            {x: new Date('2024-09-13'), o: 80, h: 90, l: 75, c: 85},
                            {x: new Date('2024-09-14'), o: 85, h: 88, l: 82, c: 84},
                            {x: new Date('2024-09-15'), o: 84, h: 87, l: 80, c: 83},
                            {x: new Date('2024-09-16'), o: 83, h: 86, l: 79, c: 82},
                            {x: new Date('2024-09-17'), o: 82, h: 84, l: 78, c: 80},
                        ],
                        borderColor: 'black',
                        color: {
                            up: 'green',
                            down: 'red',
                            unchanged: 'gray',
                        },
                    },
                ],
            },
            options: {
                scales: {
                    x: {
                        type: 'time', // 시간 스케일 사용
                        time: {
                            unit: 'day',
                        },
                    },
                    y: {
                        type: 'linear',
                    },
                },
            },
        })
    })
</script>