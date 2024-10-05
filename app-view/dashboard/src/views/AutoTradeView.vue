<script setup>

    import AutoTradingPanel from "@/components/AutoTradingPanel.vue";
    import {useAutoTradingStore} from "@/store/autoTradingStore";
    import {storeToRefs} from "pinia";
    import {useCodeStore} from "@/store/codeStore";
    import {useAuthExchangeKeyStore} from "@/store/authExchangeKeyStore";
    import {computed, onMounted} from "vue";

    const code = useCodeStore()
    const authExchangeKey = useAuthExchangeKeyStore()
    const autoTrading = useAutoTradingStore()

    const {exchangeKeys} = storeToRefs(authExchangeKey)
    const {autoTradings, register} = storeToRefs(autoTrading)

    const filteredKeys = computed(() => {
        return exchangeKeys.value.filter(key => key.exchangeType === register.exchangeType)
    })
    const {
        exchangeTypes,
        candleUnits,
        coinStrategyTypes,
        coinTypes
    } = storeToRefs(code);

    onMounted(() => {
        code.loadCoinTypes();
        code.loadExchangeTypes();
        code.loadCoinStrategyTypes();
        authExchangeKey.loadExchangeKeys();
    })
</script>
<template>
    <v-container>
        <v-row>
            <v-col v-for="(autoTrading) in autoTradings" :key="autoTrading.id" cols="auto">
                <AutoTradingPanel :auto-trading="autoTrading"></AutoTradingPanel>
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
                <v-text-field v-model="register.title" label="제목"/>
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
                <v-select v-model="register.keyPairId"
                          label="인증키"
                          :items="filteredKeys"
                          item-title="keyPairId"
                          item-value="keyPairId"
                ></v-select>

                <v-text-field v-model="register.duration" label="실행 간격 (ms)"/>
                <v-text-field
                    v-for="(guideDescription, key) in configMap" :key="key"
                    v-model="register.config[key]" :label="guideDescription"/>
            </v-card-text>
            <v-card-actions class="pa-5">
                <v-btn @click="autoTrading.registerAutoTrading()">등록</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

</template>