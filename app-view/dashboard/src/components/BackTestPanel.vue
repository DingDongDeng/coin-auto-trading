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
                    <div>{{ detail.backTestProcessorId }}</div>
                    <div style="white-space: pre-wrap">{{ JSON.stringify(detail.config, null, 4) }}</div>
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
                    <v-col cols="10">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong>거래 이력</strong>
                            </v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="12">
                                <v-data-table
                                    :headers="[
                                        { title: '코인 종류', value: 'coinType.desc' },
                                        { title: '거래 유형', value: 'orderType.desc' },
                                        { title: '수량', value: 'volume' },
                                        { title: '가격', value: 'price' },
                                        { title: '이익', value: 'profit' },
                                        { title: '거래 시각', value: 'tradeAt' },
                                    ]"
                                    :items="Object.values(detail.tradeHistoriesMap).flat()"
                                    items-per-page="10"
                                >
                                </v-data-table>
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col cols="10">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong>통계</strong>
                            </v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="12">
                                <v-data-table
                                    :headers="[
                                        { title: '코인 종류', value: 'coinType.desc' },
                                        { title: '시작일', value: 'from' },
                                        { title: '종료일', value: 'to' },
                                        { title: '이익금', value: 'totalAccProfitPrice' },
                                    ]"
                                    :items="Object.values(detail.tradeStatisticsMap).flat()"
                                    items-per-page="10"
                                >
                                </v-data-table>
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>
            </v-card-text>
            <v-card-actions class="pa-5">


            </v-card-actions>
        </v-card>
    </v-dialog>
</template>