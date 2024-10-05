<script setup>
    import {defineProps} from 'vue';
    import {useAutoTradingStore} from "@/store/autoTradingStore";
    import {storeToRefs} from "pinia";

    defineProps({
        autoTrading: Object,
    });

    const autoTradingStore = useAutoTradingStore()
    const {remove, detail} = storeToRefs(autoTradingStore)
</script>
<template>
    <v-card color="#181B1F" variant="elevated" class="mx-auto" width="350" max-width="350">
        <v-card-item>
            <div>
                <div class="text-overline mb-1">
                    [{{ autoTrading.status.desc }}] {{ autoTrading.strategyType.desc }}
                </div>
                <div class="text-h6 mb-1">
                    {{ autoTrading.title }}
                </div>
                <div class="text-caption">
                    {{ autoTrading.id }}
                </div>
            </div>
        </v-card-item>
        <v-card-actions>
            <v-btn @click="(() => {
                autoTradingStore.loadAutoTradingDetail(autoTrading.id)
                detail.visibleDialog = true
            })">
                상세보기
            </v-btn>
            <v-icon icon="mdi-delete" @click="(() => {
                remove.processorId = autoTrading.id;
                autoTradingStore.removeAutoTrading()
            })"></v-icon>
        </v-card-actions>
    </v-card>
</template>