import {defineStore} from "pinia";
import axios from 'axios';

async function getExchangeTypes() {
    const response = await axios.get('/code/exchange-type');
    return response.data;
}

async function getCandleUnits() {
    const response = await axios.get('/code/candle-unit');
    return response.data;
}

async function getCoinStrategyType() {
    const response = await axios.get('/code/coin-strategy-type');
    return response.data;
}

async function getCoinType() {
    const response = await axios.get('/code/coin-type');
    return response.data;
}

export const useCodeStore = defineStore("code", {

    state: () => ({
        exchangeTypes: [],
        candleUnits: [],
        coinStrategyTypes: [],
        coinTypes: [],
    }),

    getters: {},

    actions: {
        async loadExchangeTypes() {
            this.exchangeTypes = (await getExchangeTypes()).body
        },
        async loadCandleUnits() {
            this.candleUnits = (await getCandleUnits()).body
        },
        async loadCoinStrategyTypes() {
            this.coinStrategyTypes = (await getCoinStrategyType()).body
        },
        async loadCoinTypes() {
            this.coinTypes = (await getCoinType()).body
        },
    }
});