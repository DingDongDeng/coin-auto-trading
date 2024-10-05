import {defineStore} from "pinia";
import axios from 'axios';

async function getAutoTradings() {
    const response = await axios.get('/coin/processor/autotrade');
    return response.data;
}

export const useAutoTradingStore = defineStore("autoTrading", {

    state: () => ({
        autoTradings: [],
    }),

    getters: {},

    actions: {
        async loadBackTests() {
            this.autoTradings = (await getAutoTradings()).body
        },
    }
});