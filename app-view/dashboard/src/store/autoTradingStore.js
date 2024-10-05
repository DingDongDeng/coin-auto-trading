import {defineStore} from "pinia";
import axios from 'axios';

async function getAutoTradings() {
    const response = await axios.get('/coin/processor/autotrade');
    return response.data;
}

async function registerBackTest(body) {
    const response = await axios.post('/coin/processor/autotrade', body);
    return response.data;
}

async function removeAutoTrading(processorId) {
    const response = await axios.delete(`/coin/processor/${processorId}/terminate`);
    return response.data;
}

export const useAutoTradingStore = defineStore("autoTrading", {

    state: () => ({
        autoTradings: [],
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
        async loadAutoTradings() {
            this.autoTradings = (await getAutoTradings()).body
        },
        async registerAutoTrading() {
            await registerBackTest(this.register)
            await this.loadAutoTradings()
            this.registerReset()
        },
        async removeAutoTrading() {
            await removeAutoTrading(this.remove.processorId)
            await this.loadAutoTradings()
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