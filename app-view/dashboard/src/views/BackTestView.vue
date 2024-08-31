<script setup>
    import BackTestDetailPanel from "@/components/BackTestDetailPanel.vue";
    import {useBackTestStore} from "@/store/backTestStore";
    import {storeToRefs} from "pinia";
    import {onMounted} from "vue";

    const backTest = useBackTestStore()

    const {register} = storeToRefs(backTest);

    onMounted(() => {
        backTest.loadBackTests();
    })

</script>
<template>
    <v-container>
        <v-row>
            <v-col v-for="(variant, i) in variants" :key="i" cols="auto">
                <BackTestDetailPanel :title="variant"/>
            </v-col>
            <v-col cols="auto">
                <v-icon icon="mdi-plus-circle"
                        @click="register.visibleDialog = true"
                ></v-icon>
            </v-col>
        </v-row>
    </v-container>
    <v-dialog
        v-model="register.visibleDialog"
        width="500">
        <v-card>
            <v-card-title class="headline black" primary-title>
                백테스트 등록
            </v-card-title>
            <v-card-text class="pa-5">
                인풋 텍스트
            </v-card-text>
            <v-card-actions class="pa-5">
                버튼
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>