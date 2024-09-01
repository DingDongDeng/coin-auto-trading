import {defineStore} from "pinia";
import axios from 'axios';

async function getBackTests() {
    const response = await axios.get('/coin/processor/backtest');
    return response.data;
}

export const useBackTestStore = defineStore("backTest", {

    state: () => ({
        backTests: [],
        register: {
            visibleDialog: false,
            startDate: '',
            endDate: '',
            durationUnit: '',
            coinStrategyType: '',
            exchangeType: '',
            coinTypes: [],
            candleUnits: [],
            config: {},
        },
        remove: {
            keyPairId: '',
        }
    }),

    getters: {},

    actions: {
        async loadBackTests() {
            this.backTests = (await getBackTests()).body
        },
        // async registerBackTest() {
        //     await registerExchangeKey(this.register)
        //     await this.loadExchangeKeys()
        //     this.registerReset()
        // },
        // async removeBackTest() {
        //     await removeExchangeKey(this.remove.keyPairId)
        //     await this.loadExchangeKeys()
        // },
        // registerReset() {
        //     this.register.visibleDialog = false;
        // }
    }
});