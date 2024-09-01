<script setup>
    import BackTestDetailPanel from "@/components/BackTestDetailPanel.vue";
    import {useBackTestStore} from "@/store/backTestStore";
    import {useCodeStore} from "@/store/codeStore";
    import {storeToRefs} from "pinia";
    import {computed, onMounted, ref, watch} from "vue";

    const code = useCodeStore()
    const backTest = useBackTestStore()

    const {
        exchangeTypes,
        // candleUnits,
        // coinStrategyTypes,
        // coinTypes
    } = storeToRefs(code);
    const {register} = storeToRefs(backTest);

    const dateRange = ref([]);
    const startDate = computed(() => dateRange.value[0] || '날짜를 선택하세요');
    const endDate = computed(() => dateRange.value[1] || '날짜를 선택하세요');

    watch([startDate, endDate], (newValues) => {
        register.value.startDate = newValues[0]
        register.value.endDate = newValues[1]
    })

    onMounted(() => {
        code.loadCoinTypes();
        code.loadExchangeTypes();
        code.loadCoinStrategyTypes();
        code.loadCandleUnits();
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
                <v-date-picker
                    title="백테스트 시작일/종료일"
                    multiple="range"
                    v-model="dateRange"
                />
                <v-select v-model="register.exchangeType"
                          label="거래소"
                          :items="exchangeTypes"
                          item-title="desc"
                          item-value="type"
                ></v-select>
                <v-text-field v-model="register.accessKey" label="액세스 키"/>
                <v-text-field v-model="register.secretKey" label="시크릿 키"/>
            </v-card-text>
            <v-card-actions class="pa-5">
                버튼
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>