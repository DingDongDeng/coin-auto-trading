import {defineStore} from "pinia";
import axios from 'axios';

async function getExchangeTypes() {
    const response = await axios.get('/code/exchange-type');
    return response.data;
}

export const useCodeStore = defineStore("code", {

    state: () => ({
        exchangeTypes: [],
    }),

    getters: {},

    actions: {
        async loadExchangeTypes() {
            this.exchangeTypes = (await getExchangeTypes()).body
        },
    }
});