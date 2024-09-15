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
        width="500">
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
                <div> 기간 : {{ detail.startDateTime }} ~ {{ detail.endDateTime }}</div>
                <div> 진행율 : {{ detail.progressRate }}</div>
                <div> 총 누적 이익금(수수료 제외) : {{ detail.totalAccProfitValuePrice }}</div>
                <div> 총 수수료 : {{ detail.totalFee }}</div>
                <div> 현재 미실현 이익금 : {{ detail.totalProfitPrice }}</div>
                <div> 현재 미실현 이익율 : {{ detail.totalProfitRate }}</div>
                <div style="white-space: pre-wrap"> 거래 이력 : {{
                        JSON.stringify(detail.tradeHistoriesMap, null, 4)
                    }}
                </div>
                <div style="white-space: pre-wrap"> 통계 : {{ JSON.stringify(detail.tradeStatisticsMap, null, 4) }}</div>
                <div style="white-space: pre-wrap"> 전략 상세 설정 : {{ JSON.stringify(detail.config, null, 4) }}</div>
            </v-card-text>
            <v-card-actions class="pa-5">


            </v-card-actions>
        </v-card>
    </v-dialog>
</template>