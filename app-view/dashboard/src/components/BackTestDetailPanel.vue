<script setup>
    import {defineProps} from 'vue';
    import {useBackTestStore} from "@/store/backTestStore";
    import {storeToRefs} from "pinia";

    const backTest = useBackTestStore()
    const {detail, remove} = storeToRefs(backTest)
    defineProps({
        backTestDetail: Object,
    });
</script>
<template>
    <v-card color="#181B1F" variant="elevated" class="mx-auto" width="350" max-width="350">
        <v-card-item>
            <div>
                <div class="text-overline mb-1">
                    {{ backTestDetail.id }}
                </div>
                <div class="text-h6 mb-1">
                    {{ backTestDetail.status }}
                </div>
                <div class="text-caption">
                    {{ backTestDetail.coinTypes }}
                </div>
            </div>
        </v-card-item>

        <v-card-actions>
            <v-btn @click="detail.visibleDialog = true">
                상세보기
            </v-btn>
            <v-icon icon="mdi-delete" @click="(() => {
                remove.processorId = backTestDetail.id;
                backTest.removeBackTest()
            })"></v-icon>
        </v-card-actions>
    </v-card>
    <v-dialog
        v-model="detail.visibleDialog"
        width="500">
        <v-card>
            <v-card-title class="headline black" primary-title>
                Compose Message
            </v-card-title>
            <v-card-text class="pa-5">
            </v-card-text>
            <v-card-actions class="pa-5">


            </v-card-actions>
        </v-card>
    </v-dialog>
</template>