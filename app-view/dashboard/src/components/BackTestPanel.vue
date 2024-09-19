<script setup>
    import {defineProps} from 'vue';
    import {useBackTestStore} from "@/store/backTestStore";
    import {storeToRefs} from "pinia";
    import TradingChart from "@/components/TradingChart.vue";

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
                                <strong>진행 정보</strong>
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
                            <v-col cols="6">{{
                                    detail.totalAccProfitValuePrice > 0 ? '+' + detail.totalAccProfitValuePrice.toLocaleString() : detail.totalAccProfitValuePrice.toLocaleString()
                                }}원
                            </v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="4">총 수수료</v-col>
                            <v-col cols="6">{{
                                    detail.totalFee > 0 ? '-' + detail.totalFee.toLocaleString() : detail.totalFee.toLocaleString()
                                }}원
                            </v-col>
                        </v-row>
                        <v-row no-gutters>
                            <v-col cols="4">미실현 평가금액</v-col>
                            <v-col cols="6">{{
                                    detail.totalProfitPrice > 0 ? '+' + detail.totalProfitPrice.toLocaleString() : detail.totalProfitPrice.toLocaleString()
                                }}원
                            </v-col>
                        </v-row>
                        <v-row no-gutters>
                            <v-col cols="4">미실현 손익률</v-col>
                            <v-col cols="6">{{
                                    detail.totalProfitRate > 0 ? '+' + detail.totalProfitRate : detail.totalProfitRate
                                }}%
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col cols="10">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong>차트</strong>
                            </v-col>
                        </v-row>
                        <v-row>
                            <v-col cols="6">
                                <trading-chart/>
                            </v-col>
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
                        <v-row>
                            <v-col cols="6">
                                <v-text-field
                                    v-model="detail.tradeHistoriesSearchText"
                                    density="compact"
                                    label="Search"
                                    prepend-inner-icon="mdi-magnify"
                                    variant="solo-filled"
                                    flat
                                    hide-details
                                    single-line
                                ></v-text-field>
                            </v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="12">
                                <v-data-table
                                    :search="detail.tradeHistoriesSearchText"
                                    :headers="[
                                        { title: '코인 종류', value: 'coinType.desc' },
                                        { title: '거래 유형', value: 'orderType.desc' },
                                        { title: '수량', value: 'volume' },
                                        { title: '가격', value: 'price' },
                                        { title: '이익', value: 'profit' },
                                        { title: '거래 시각', value: 'tradeAt' },
                                    ]"
                                    :items="Object.values(detail.tradeHistoriesMap).flat().sort((a, b) => new Date(b.tradeAt) - new Date(a.tradeAt))"
                                    items-per-page="10"
                                >
                                    <template v-slot:[`item.price`]="{ item }">
                                        <div> {{ item.price.toLocaleString() }}원</div>
                                    </template>
                                    <template v-slot:[`item.profit`]="{ item }">
                                        <div v-if="item.orderType.type !== 'BUY'">
                                            {{
                                                item.profit > 0 ? '+' + item.profit.toLocaleString() : item.profit.toLocaleString()
                                            }}원
                                        </div>
                                        <div v-else>
                                            -
                                        </div>
                                    </template>
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
                        <v-row>
                            <v-col cols="6">
                                <v-text-field
                                    v-model="detail.tradeStatisticsSearchText"
                                    density="compact"
                                    label="Search"
                                    prepend-inner-icon="mdi-magnify"
                                    variant="solo-filled"
                                    flat
                                    hide-details
                                    single-line
                                ></v-text-field>
                            </v-col>
                        </v-row>
                        <v-row class="mb-1" no-gutters>
                            <v-col cols="12">
                                <v-data-table
                                    :search="detail.tradeStatisticsSearchText"
                                    :headers="[
                                        { title: '코인 종류', value: 'coinType.desc' },
                                        { title: '시작일', value: 'from' },
                                        { title: '종료일', value: 'to' },
                                        { title: '이익금', value: 'totalAccProfitPrice' },
                                    ]"
                                    :items="Object.values(detail.tradeStatisticsMap).flat().sort((a, b) => new Date(b.from) - new Date(a.from))"
                                    items-per-page="10"
                                >
                                    <template v-slot:[`item.totalAccProfitPrice`]="{ item }">
                                        <div>
                                            {{
                                                item.totalAccProfitPrice > 0 ? '+' + item.totalAccProfitPrice.toLocaleString() : item.totalAccProfitPrice.toLocaleString()
                                            }}원
                                        </div>
                                    </template>
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