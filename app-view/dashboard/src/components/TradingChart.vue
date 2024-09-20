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
            type: 'candlestick', // 'ohlc', 'candlestick'도 가능
            data: {
                datasets: [
                    {
                        label: 'OHLC Data',
                        data: [
                            {x: candleDate('2024-09-10T09:15:00'), o: 60, h: 70, l: 55, c: 65},
                            {x: candleDate('2024-09-10T09:30:00'), o: 65, h: 80, l: 60, c: 75},
                            {x: candleDate('2024-09-10T09:45:00'), o: 75, h: 85, l: 70, c: 80},
                            {x: candleDate('2024-09-10T10:00:00'), o: 80, h: 90, l: 75, c: 85},
                            {x: candleDate('2024-09-10T10:15:00'), o: 85, h: 88, l: 82, c: 84},
                            {x: candleDate('2024-09-10T10:30:00'), o: 84, h: 87, l: 80, c: 83},
                            {x: candleDate('2024-09-10T10:45:00'), o: 83, h: 86, l: 79, c: 82},
                            {x: candleDate('2024-09-10T11:00:00'), o: 160, h: 235, l: 160, c: 205},
                        ],
                        borderColor: 'black',
                        color: {
                            up: 'green',
                            down: 'red',
                            unchanged: 'gray',
                        },
                    },
                    {
                        type: 'scatter', // scatter 데이터셋을 추가
                        label: 'Buy Order',
                        data: [
                            {x: candleDate('2024-09-10T09:30:00'), y: 75}, // 매수 주문 시점과 가격
                        ],
                        pointBackgroundColor: 'blue', // 매수 주문을 나타낼 점의 색
                        pointRadius: 5, // 점의 크기
                    },
                ],
            },
            options: {
                scales: {
                    x: {
                        type: 'time', // 시간 스케일 사용
                        time: {
                            unit: 'minute', // 시간 단위를 분으로 설정
                        },
                    },
                    y: {
                        type: 'linear',
                    },
                },
            },
        })
    })

    function candleDate(dateStr) {
        return new Date(dateStr).valueOf();
    }
</script>