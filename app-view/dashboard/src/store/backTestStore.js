import {defineStore} from "pinia";
import axios from 'axios';

async function getExchangeKeys() {
    const response = await axios.get('/coin/processor/backtest');
    return response.data;
}

export const useBackTestStore = defineStore("backTest", {

    state: () => ({
        backTests: [],
        register: {
            visibleDialog: false,
        },
        remove: {
            keyPairId: '',
        }
    }),

    getters: {},

    actions: {
        async loadBackTests() {
            this.exchangeKeys = (await getExchangeKeys()).body
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