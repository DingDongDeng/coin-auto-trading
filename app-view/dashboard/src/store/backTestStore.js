import {defineStore} from "pinia";
import axios from 'axios';

async function getBackTests() {
    const response = await axios.get('/coin/processor/backtest');
    return response.data;
}

async function getBackTestResult(backTestProcessorId) {
    const response = await axios.get(`/coin/processor/backtest/${backTestProcessorId}`);
    return response.data;
}

async function registerBackTest(body) {
    const response = await axios.post('/coin/processor/backtest', body);
    return response.data;
}

async function removeBackTest(processorId) {
    const response = await axios.delete(`/coin/processor/${processorId}/terminate`);
    return response.data;
}

export const useBackTestStore = defineStore("backTest", {

    state: () => ({
        backTests: [],
        detail: {
            visibleDialog: false,
            tradeHistoriesSearchText: '',
            tradeStatisticsSearchText: '',
            title: '',
            strategyType: {},
            status: {},
            backTestProcessorId: '',
            config: {},
            progressRate: 0,
            startDateTime: '',
            endDateTime: '',
            totalProfitRate: 0,
            totalProfitPrice: 0,
            totalAccProfitValuePrice: 0,
            totalFee: 0,
            tradeHistoriesMap: {},
            tradeStatisticsMap: {},
        },
        register: {
            visibleDialog: false,
            title: '',
            startDateTime: '',
            endDateTime: '',
            durationUnit: '',
            coinStrategyType: '',
            exchangeType: '',
            coinTypes: [],
            candleUnits: [],
            config: {},
        },
        remove: {
            processorId: '',
        }
    }),

    getters: {},

    actions: {
        async loadBackTests() {
            this.backTests = (await getBackTests()).body
        },
        async loadBackTestDetail(backTestProcessorId) {
            const detail = (await getBackTestResult(backTestProcessorId)).body
            this.detail = {
                ...this.detail,
                ...detail,
            };
        },
        async registerBackTest() {
            await registerBackTest(this.register)
            await this.loadBackTests()
            this.registerReset()
        },
        async removeBackTest() {
            await removeBackTest(this.remove.processorId)
            await this.loadBackTests()
        },
        registerReset() {
            this.register.visibleDialog = false;
            this.register.title = ''
            this.register.startDateTime = ''
            this.register.endDateTime = ''
            this.register.durationUnit = ''
            this.register.coinStrategyType = ''
            this.register.exchangeType = ''
            this.register.coinTypes = []
            this.register.candleUnits = []
            this.register.config = {}
        }
    }
});