<script setup>
    import {defineProps} from 'vue';
    import {useAutoTradeStore} from "@/store/autoTradeStore";
    import {storeToRefs} from "pinia";

    defineProps({
        autoTrade: Object,
    });

    const autoTradeStore = useAutoTradeStore()
    const {remove, detail} = storeToRefs(autoTradeStore)
</script>
<template>
    <v-card color="#181B1F" variant="elevated" class="mx-auto" width="350" max-width="350">
        <v-card-item>
            <div>
                <div class="text-overline mb-1">
                    [{{ autoTrade.status.desc }}] {{ autoTrade.strategyType.desc }}
                </div>
                <div class="text-h6 mb-1">
                    {{ autoTrade.title }}
                </div>
                <div class="text-caption">
                    {{ autoTrade.id }}
                </div>
            </div>
        </v-card-item>
        <v-card-actions>
            <v-btn @click="(() => {
                autoTradeStore.loadAutoTradeDetail(autoTrade.id)
                detail.visibleDialog = true
            })">
                상세보기
            </v-btn>
            <v-icon icon="mdi-delete" @click="(() => {
                remove.processorId = autoTrade.id;
                autoTradeStore.removeAutoTrade()
            })"></v-icon>
        </v-card-actions>
    </v-card>
</template>