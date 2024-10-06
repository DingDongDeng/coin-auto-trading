<script setup>
    import BackTestPanel from "@/components/BackTestPanel.vue";
    import {useBackTestStore} from "@/store/backTestStore";
    import {useCodeStore} from "@/store/codeStore";
    import {storeToRefs} from "pinia";
    import {computed, onMounted, ref, watch} from "vue";
    import TradingChart from "@/components/TradingChart.vue";

    const code = useCodeStore()
    const backTest = useBackTestStore()
    const {detail} = storeToRefs(backTest)

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
        if (dateTimeRange.value[dateTimeRange.value.length - 1]) {
            return new Date(dateTimeRange.value[dateTimeRange.value.length - 1]).toISOString().slice(0, 19)
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
            <v-col v-for="(backTest) in backTests" :key="backTest.id" cols="auto">
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
                <v-text-field
                    v-model="register.title" label="제목"/>

                <v-divider class="mt-5"/>
                <v-card-text>
                    <div>백테스트 실행 단위는 자유롭게 선택가능합니다.</div>
                    <div>(차트 다운로드와 관련 없음)</div>
                </v-card-text>
                <v-select v-model="register.durationUnit"
                          label="백테스트 실행 시간 단위"
                          :items="candleUnits"
                          item-title="desc"
                          item-value="type"
                ></v-select>
                <v-divider class="mt-5"/>
                <v-card-text>
                    <div>사용 할 코인별, 캔들을 사전에 다운받아야 합니다.</div>
                    <div>전략에서 사용안하더라도 1분봉은 필수로 다운받아야합니다.</div>
                    <div>(1분당 캔들을 상세하게 재현하기 위함)</div>
                </v-card-text>
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
                          label="전략에서 사용할 차트 종류"
                          :items="candleUnits"
                          item-title="desc"
                          item-value="type"
                          chips
                          multiple
                ></v-select>
                <v-divider class="mt-5"/>
                <v-card-text>
                    전략을 선택하고, 상세 파라미터를 정의해주세요.
                </v-card-text>
                <v-select v-model="register.coinStrategyType"
                          label="자동매매 전략"
                          :items="coinStrategyTypes"
                          item-title="desc"
                          item-value="type"
                ></v-select>
                <v-text-field
                    v-for="(guideDescription, key) in configMap" :key="key"
                    v-model="register.config[key]" :label="guideDescription"/>
                <v-divider class="mt-5"/>
                <v-card-text>
                    <div>백테스트를 실행한 기간을 설정해주세요.</div>
                    <div>캔들이 다운되어 있지 않은 구간을 설정하면 실패하거나 결과가 적절하지 않을 수 있습니다.</div>
                </v-card-text>
                <v-date-picker
                    title=""
                    multiple="range"
                    v-model="dateTimeRange"
                />
                <v-divider/>
            </v-card-text>
            <v-card-actions class="pa-5">
                <v-btn @click="backTest.registerBackTest()"
                       variant="outlined"
                >실행
                </v-btn>
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
                            <v-col cols="6">{{ detail.backTestProcessorId }}</v-col>
                        </v-row>
                        <v-row class="pl-5" no-gutters>
                            <v-col cols="3">진행률</v-col>
                            <v-col cols="6">[{{ detail.status.desc }}] {{ detail.progressRate }}%</v-col>
                        </v-row>
                        <v-row class="pl-5" no-gutters>
                            <v-col cols="3">시작일</v-col>
                            <v-col cols="6">{{ detail.startDateTime }}</v-col>
                        </v-row>
                        <v-row class="pl-5" no-gutters>
                            <v-col cols="3">종료일</v-col>
                            <v-col cols="6">{{ detail.endDateTime }}</v-col>
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
                    <v-col cols="12">
                        <v-row class="mb-2" no-gutters>
                            <v-col cols="3">
                                <strong class="text-h6">차트</strong>
                            </v-col>
                        </v-row>
                        <v-row no-gutters>
                            <v-col cols="12">
                                <TradingChart :processor-id="detail.backTestProcessorId"
                                              :key="detail.backTestProcessorId"/>
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