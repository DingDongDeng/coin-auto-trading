import {defineStore} from "pinia";
import axios from 'axios';

async function getAutoTrades() {
    const response = await axios.get('/coin/processor/autotrade');
    return response.data;
}

async function getAutoTradeResult(autoTradeProcessorId) {
    const response = await axios.get(`/coin/processor/autotrade/${autoTradeProcessorId}`);
    return response.data;
}

async function registerAutoTrade(body) {
    const response = await axios.post('/coin/processor/autotrade', body);
    return response.data;
}

async function startAutoTrade(processorId) {
    const response = await axios.post(`/coin/processor/${processorId}/start`);
    return response.data;
}

async function stopAutoTrade(processorId) {
    const response = await axios.post(`/coin/processor/${processorId}/stop`);
    return response.data;
}

async function removeAutoTrade(processorId) {
    const response = await axios.delete(`/coin/processor/${processorId}/terminate`);
    return response.data;
}

export const useAutoTradeStore = defineStore("autoTrade", {

    state: () => ({
        autoTrades: [],
        detail: {
            isRunningRefreshScheduler: false,
            visibleDialog: false,
            tradeHistoriesSearchText: '',
            tradeStatisticsSearchText: '',
            title: '',
            strategyType: {},
            status: {},
            autoTradeProcessorId: '',
            config: {},
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
            coinStrategyType: '',
            exchangeType: '',
            coinTypes: [],
            candleUnits: [],
            keyPairId: '',
            config: {},
            duration: 0,
        },
    }),

    getters: {},

    actions: {
        async loadAutoTrades() {
            this.autoTrades = (await getAutoTrades()).body
        },
        async loadAutoTradeDetail(autoTradeProcessorId) {
            console.log('loadAutoTradeDetail...', autoTradeProcessorId)
            const detail = (await getAutoTradeResult(autoTradeProcessorId)).body
            this.detail = {
                ...this.detail,
                ...detail,
            };

            // 현재 detail에 포커스된 processorId에 대해 주기적으로 리프레시 (processorId가 바뀌면 해당 항목에 대해 리프레시
            if (!this.detail.isRunningRefreshScheduler) {
                this.detail.isRunningRefreshScheduler = true
                setInterval(async () => {
                    const autoTradeProcessorId = this.detail.autoTradeProcessorId
                    const isExists = autoTradeProcessorId && autoTradeProcessorId !== ''
                    const isInit = this.detail.status.type === 'INIT'
                    const isRunning = this.detail.status.type === 'RUNNING'
                    const isStopped = this.detail.status.type === 'STOPPED'
                    if (isExists && (isInit || isRunning || isStopped)) {
                        await this.loadAutoTradeDetail(autoTradeProcessorId);
                    }
                }, 3000);
            }
        },
        async registerAutoTrade() {
            await registerAutoTrade(this.register)
            await this.loadAutoTrades()
            this.registerReset()
        },
        async startAutoTrade(autoTradeProcessorId) {
            await startAutoTrade(autoTradeProcessorId)
            await this.loadAutoTrades()
        },
        async stopAutoTrade(autoTradeProcessorId) {
            await stopAutoTrade(autoTradeProcessorId)
            await this.loadAutoTrades()
        },
        async removeAutoTrade(autoTradeProcessorId) {
            await removeAutoTrade(autoTradeProcessorId)
            await this.loadAutoTrades()
        },
        registerReset() {
            this.register.visibleDialog = false;
            this.register.title = ''
            this.register.coinStrategyType = ''
            this.register.exchangeType = ''
            this.register.coinTypes = []
            this.register.candleUnits = []
            this.register.keyPairId = ''
            this.register.config = {}
            this.register.duration = 0
        },
    }
});