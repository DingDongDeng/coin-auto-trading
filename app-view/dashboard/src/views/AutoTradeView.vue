<script setup>

    import AutoTradePanel from "@/components/AutoTradePanel.vue";
    import {useAutoTradeStore} from "@/store/autoTradeStore";
    import {storeToRefs} from "pinia";
    import {useCodeStore} from "@/store/codeStore";
    import {useAuthExchangeKeyStore} from "@/store/authExchangeKeyStore";
    import {computed, onMounted, ref, watch} from "vue";

    const code = useCodeStore()
    const authExchangeKey = useAuthExchangeKeyStore()
    const autoTrade = useAutoTradeStore()

    const {exchangeKeys} = storeToRefs(authExchangeKey)
    const {autoTrades, register, detail} = storeToRefs(autoTrade)

    const filteredKeys = computed(() => exchangeKeys.value.filter(key => key.exchangeType.type === register.value.exchangeType))
    const {
        exchangeTypes,
        candleUnits,
        coinStrategyTypes,
        coinTypes
    } = storeToRefs(code);

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
        code.loadCandleUnits()
        code.loadCoinStrategyTypes();
        authExchangeKey.loadExchangeKeys();
        autoTrade.loadAutoTrades()
    })
</script>
<template>
    <v-container>
        <v-row>
            <v-col v-for="(autoTrade) in autoTrades" :key="autoTrade.id" cols="auto">
                <AutoTradePanel :auto-trade="autoTrade"></AutoTradePanel>
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
                자동매매 등록
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
                <v-btn @click="autoTrade.registerAutoTrade()">등록</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <v-dialog
        v-model="detail.visibleDialog"
        width="1500">
        <v-card>
            <v-card-title class="headline black" primary-title>
                <div class="text-h5 mb-1">
                    [{{ detail.status.desc }}] {{ detail.strategyType.desc }} : {{ detail.title }}
                </div>
            </v-card-title>
            <v-divider class="mb-5"/>

            <v-card-actions class="pa-5">
                <v-btn @click="(() => {
                    autoTrade.startAutoTrade(detail.autoTradeProcessorId)
                })"> 시작하기
                </v-btn>
                <v-btn @click="(() => {
                    autoTrade.stopAutoTrade(detail.autoTradeProcessorId)
                })"> 정지하기
                </v-btn>
            </v-card-actions>

            <v-card-text class="pa-5">
                <v-row>
                    <v-col cols="5">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong class="text-h6">진행 정보</strong>
                            </v-col>
                        </v-row>
                        <v-row class="pl-5" no-gutters>
                            <v-col cols="3">ID</v-col>
                            <v-col cols="6">{{ detail.autoTradeProcessorId }}</v-col>
                        </v-row>
                    </v-col>
                    <v-col cols="5">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong class="text-h6">진행 상황</strong>
                            </v-col>
                        </v-row>
                        <v-row class="pl-5" no-gutters>
                            <v-col cols="5">총 누적 이익금 <br><span class="text-caption">(수수료 제외)</span></v-col>
                            <v-col cols="6">{{
                                    detail.totalAccProfitValuePrice > 0 ? '+' + detail.totalAccProfitValuePrice.toLocaleString() : detail.totalAccProfitValuePrice.toLocaleString()
                                }}원
                            </v-col>
                        </v-row>
                        <v-row class="pl-5" no-gutters>
                            <v-col cols="5">총 수수료</v-col>
                            <v-col cols="6">{{
                                    detail.totalFee > 0 ? '-' + detail.totalFee.toLocaleString() : detail.totalFee.toLocaleString()
                                }}원
                            </v-col>
                        </v-row>
                        <v-row class="pl-5" no-gutters>
                            <v-col cols="5">미실현 평가금액</v-col>
                            <v-col cols="6">{{
                                    detail.totalProfitPrice > 0 ? '+' + detail.totalProfitPrice.toLocaleString() : detail.totalProfitPrice.toLocaleString()
                                }}원
                            </v-col>
                        </v-row>
                        <v-row class="pl-5" no-gutters>
                            <v-col cols="5">미실현 손익률</v-col>
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
                                <strong class="text-h6">거래 이력</strong>
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
                                <strong class="text-h6">통계</strong>
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
                <v-row>
                    <v-col cols="10">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong class="text-h6">상세 설정</strong>
                            </v-col>
                        </v-row>
                        <v-row>
                            <v-col>
                                <div class="text-caption">
                                    <div></div>
                                    <div style="white-space: pre-wrap">{{
                                            JSON.stringify(detail.config, null, 4)
                                        }}
                                    </div>
                                </div>
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>