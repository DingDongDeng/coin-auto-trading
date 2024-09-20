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
            type: 'candlestick', // 'ohlc'도 가능
            data: {
                datasets: [
                    {
                        label: 'OHLC Data',
                        data: [
                            {x: new Date('2024-09-10'), o: 60, h: 70, l: 50, c: 65},
                            {x: new Date('2024-09-11'), o: 65, h: 75, l: 55, c: 70},
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