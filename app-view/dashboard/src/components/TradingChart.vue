<script setup>
    import {Line} from 'vue-chartjs'
    import {
        CategoryScale,
        Chart as ChartJS,
        Legend,
        LinearScale,
        LineElement,
        PointElement,
        Title,
        Tooltip
    } from 'chart.js'
    import {ref} from 'vue'

    // Chart.js 구성 요소 등록
    ChartJS.register(Title, Tooltip, Legend, LineElement, CategoryScale, LinearScale, PointElement)

    // 차트 데이터
    const chartData = ref({
        labels: ['2024-09-01', '2024-09-02', '2024-09-03', '2024-09-04'], // 날짜
        datasets: [
            {
                label: 'Stock Price',
                data: [120, 130, 125, 140], // 주식 가격 데이터
                borderColor: '#42A5F5',
                backgroundColor: 'rgba(66, 165, 245, 0.2)',
                fill: true
            }
        ]
    })

    // 차트 옵션
    const chartOptions = ref({
        responsive: true,
        scales: {
            x: {
                beginAtZero: true
            },
            y: {
                beginAtZero: true
            }
        }
    })
</script>

<template>
    <div>
        <Line :data="chartData" :options="chartOptions"/>
    </div>
</template>

<style scoped>
    /* 여기에 스타일을 추가할 수 있습니다. */
</style>