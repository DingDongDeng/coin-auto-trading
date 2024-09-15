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
                </div>
                <div class="text-h6 mb-1">
                    [{{ backTestDetail.status.desc }}] {{ backTestDetail.title }}
                </div>
                <div class="text-caption">
                    {{ backTestDetail.id }}
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
                <!-- FIXME 프로세서의 상세 정보가 필요, 백테스트 진행결과도 필요               -->
            </v-card-text>
            <v-card-actions class="pa-5">


            </v-card-actions>
        </v-card>
    </v-dialog>
</template>