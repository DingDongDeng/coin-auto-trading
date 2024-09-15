<script setup>
    import {defineProps} from 'vue';
    import {useBackTestStore} from "@/store/backTestStore";
    import {storeToRefs} from "pinia";

    const backTestStore = useBackTestStore()
    const {detail, remove} = storeToRefs(backTestStore)
    defineProps({
        backTest: Object,
    });
</script>
<template>
    <v-card color="#181B1F" variant="elevated" class="mx-auto" width="350" max-width="350">
        <v-card-item>
            <div>
                <div class="text-overline mb-1">
                    [{{ backTest.status.desc }}] {{ backTest.strategyType.desc }}
                </div>
                <div class="text-h6 mb-1">
                    {{ backTest.title }}
                </div>
                <div class="text-caption">
                    {{ backTest.id }}
                </div>
            </div>
        </v-card-item>

        <v-card-actions>
            <v-btn @click="(() => {
                backTestStore.loadBackTestDetail(backTest.id)
                detail.visibleDialog = true
            })">
                상세보기
            </v-btn>
            <v-icon icon="mdi-delete" @click="(() => {
                remove.processorId = backTest.id;
                backTestStore.removeBackTest()
            })"></v-icon>
        </v-card-actions>
    </v-card>
    <v-dialog
        v-model="detail.visibleDialog"
        width="1000">
        <v-card>
            <v-card-title class="headline black" primary-title>
                <div class="text-overline mb-1">
                    [{{ detail.status.desc }}] {{ detail.strategyType.desc }}
                </div>
                <div class="text-h6 mb-1">
                    {{ detail.title }}
                </div>
                <div class="text-caption">
                    {{ detail.backTestProcessorId }}
                </div>
            </v-card-title>
            <v-card-text class="pa-5">
                <v-row>
                    <v-col cols="5">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong>기간 및 진행률</strong>
                            </v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="3">진행률</v-col>
                            <v-col cols="6">[{{ detail.status.desc }}] {{ detail.progressRate }}%</v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="3">시작일</v-col>
                            <v-col cols="6">{{ detail.startDateTime }}</v-col>
                        </v-row>
                        <v-row no-gutters>
                            <v-col cols="3">종료일</v-col>
                            <v-col cols="6">{{ detail.endDateTime }}</v-col>
                        </v-row>
                    </v-col>
                    <v-col cols="5">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong>진행 상황</strong>
                            </v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="4">총 누적 이익금 <br> (수수료 제외)</v-col>
                            <v-col cols="6">{{ detail.totalAccProfitValuePrice.toLocaleString() }}원</v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="4">총 수수료</v-col>
                            <v-col cols="6">{{ detail.totalFee.toLocaleString() }}원</v-col>
                        </v-row>
                        <v-row no-gutters>
                            <v-col cols="4">미실현 평가금액</v-col>
                            <v-col cols="6">{{ detail.totalProfitPrice.toLocaleString() }}원</v-col>
                        </v-row>
                        <v-row no-gutters>
                            <v-col cols="4">미실현 손익률</v-col>
                            <v-col cols="6">{{ detail.totalProfitRate }}%</v-col>
                        </v-row>
                    </v-col>
                </v-row>
                <v-row>
                    <v-row class="mb-2" no-gutters>
                        <v-col cols="3">
                            <div><strong>전략 상세 설정</strong></div>
                            <div style="white-space: pre-wrap">{{ JSON.stringify(detail.config, null, 4) }}</div>
                        </v-col>
                    </v-row>
                </v-row>

                <!-- 거래 이력 -->
                <div> 거래 이력 :</div>
                <table>
                    <thead>
                    <tr>
                        <th>코인 종류</th>
                        <th>주문 타입</th>
                        <th>거래량</th>
                        <th>가격</th>
                        <th>이익</th>
                        <th>거래 시간</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(histories, coinType) in detail.tradeHistoriesMap" :key="coinType">
                        <td v-for="history in histories" :key="history.tradeAt">
                            {{ history.coinType.desc }}
                            {{ history.orderType }}
                            {{ history.volume }}
                            {{ history.price }}
                            {{ history.profit }}
                            {{ history.tradeAt }}
                        </td>
                    </tr>
                    </tbody>
                </table>

                <!-- 통계 -->
                <div> 통계 :</div>
                <table>
                    <thead>
                    <tr>
                        <th>코인 종류</th>
                        <th>기간</th>
                        <th>총 누적 이익금</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(statistics, coinType) in detail.tradeStatisticsMap" :key="coinType">
                        <td v-for="stat in statistics" :key="stat.from">
                            {{ stat.coinType.desc }}
                            {{ stat.from }} ~ {{ stat.to }}
                            {{ stat.totalAccProfitPrice }}
                        </td>
                    </tr>
                    </tbody>
                </table>
            </v-card-text>
            <v-card-actions class="pa-5">


            </v-card-actions>
        </v-card>
    </v-dialog>
</template>