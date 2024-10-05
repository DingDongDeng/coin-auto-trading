import {defineStore} from "pinia";
import axios from 'axios';

async function getAutoTrades() {
    const response = await axios.get('/coin/processor/autotrade');
    return response.data;
}

async function registerAutoTrade(body) {
    const response = await axios.post('/coin/processor/autotrade', body);
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
            coinStrategyType: '',
            exchangeType: '',
            coinTypes: [],
            candleUnits: [],
            keyPairId: '',
            config: {},
            duration: 0,
        },
        remove: {
            processorId: '',
        }
    }),

    getters: {},

    actions: {
        async loadAutoTrades() {
            this.autoTrades = (await getAutoTrades()).body
        },
        async loadAutoTradeDetail(autoTradeProcessorId) {
            console.log('loadAutoTradeDetail...', autoTradeProcessorId)
            const detail = (await getBackTestResult(autoTradeProcessorId)).body
            this.detail = {
                ...this.detail,
                ...detail,
            };
            if (!this.detail.isRunningRefreshScheduler) {
                this.detail.isRunningRefreshScheduler = true
                setInterval(async () => {
                    const backTestProcessorId = this.detail.backTestProcessorId
                    const isExistsBackTestProcessorId = backTestProcessorId && backTestProcessorId !== ''
                    const isRunning = this.detail.status.type === 'RUNNING'
                    if (isExistsBackTestProcessorId && isRunning) {
                        await this.loadBackTestDetail(backTestProcessorId);
                    }
                }, 3000);
            }
        },
        async registerAutoTrade() {
            await registerAutoTrade(this.register)
            await this.loadAutoTrades()
            this.registerReset()
        },
        async removeAutoTrade() {
            await removeAutoTrade(this.remove.processorId)
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