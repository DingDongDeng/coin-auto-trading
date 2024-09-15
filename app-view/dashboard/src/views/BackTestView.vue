<script setup>
    import BackTestPanel from "@/components/BackTestPanel.vue";
    import {useBackTestStore} from "@/store/backTestStore";
    import {useCodeStore} from "@/store/codeStore";
    import {storeToRefs} from "pinia";
    import {computed, onMounted, ref, watch} from "vue";

    const code = useCodeStore()
    const backTest = useBackTestStore()

    const {
        exchangeTypes,
        candleUnits,
        coinStrategyTypes,
        coinTypes
    } = storeToRefs(code);
    const {backTests, register} = storeToRefs(backTest);

    const dateTimeRange = ref([]);

    const startDateTime = computed(() => {
        if (dateTimeRange.value[0]) {
            return new Date(dateTimeRange.value[0]).toISOString().slice(0, 19)
        }
        return ''
    });
    const endDateTime = computed(() => {
        if (dateTimeRange.value[1]) {
            return new Date(dateTimeRange.value[1]).toISOString().slice(0, 19)
        }
        return ''
    });
    watch([startDateTime, endDateTime], (newValues) => {
        register.value.startDateTime = newValues[0]
        register.value.endDateTime = newValues[1]
    })

    const configMap = ref({});
    watch(() => register.value.coinStrategyType, (newValue) => {
        const strategy = coinStrategyTypes.value.find(it => it.type === newValue);
        if (strategy) {
            configMap.value = strategy.configMap;
        }
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
            <v-col v-for="(backTest, i) in backTests" :key="i" cols="auto">
                <BackTestPanel :back-test="backTest"/>
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
                백테스트 실행
            </v-card-title>
            <v-card-text class="pa-5">
                <v-date-picker
                    title="백테스트 시작일/종료일"
                    multiple="range"
                    v-model="dateTimeRange"
                />
                <v-text-field
                    v-model="register.title" label="제목"/>
                <v-select v-model="register.durationUnit"
                          label="백테스트 실행 시간 단위"
                          :items="candleUnits"
                          item-title="desc"
                          item-value="type"
                ></v-select>
                <v-select v-model="register.coinStrategyType"
                          label="자동매매 전략"
                          :items="coinStrategyTypes"
                          item-title="desc"
                          item-value="type"
                ></v-select>
                <v-select v-model="register.exchangeType"
                          label="거래소"
                          :items="exchangeTypes"
                          item-title="desc"
                          item-value="type"
                ></v-select>
                <v-select v-model="register.coinTypes"
                          label="코인 종류"
                          :items="coinTypes"
                          item-title="desc"
                          item-value="type"
                          chips
                          multiple
                ></v-select>
                <v-select v-model="register.candleUnits"
                          label="전략에서 사용하는 차트 종류"
                          :items="candleUnits"
                          item-title="desc"
                          item-value="type"
                          chips
                          multiple
                ></v-select>
                <v-text-field
                    v-for="(guideDescription, key) in configMap" :key="key"
                    v-model="register.config[key]" :label="guideDescription"/>
            </v-card-text>
            <v-card-actions class="pa-5">
                <v-btn @click="backTest.registerBackTest()">실행</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>