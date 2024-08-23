import {defineStore} from "pinia";
import axios from 'axios';

async function getExchangeKeys(userId) {
    const response = await axios.get('http://localhost:8080/coin/exchange-key', {params: {userId}});
    return response.data;
}


export const useAuthExchangeKeyStore = defineStore("authExchangeKey", {

    state: () => ({
        exchangeKeys: []
    }),

    getters: {},

    actions: {
        async loadData(userId) {
            this.exchangeKeys = (await getExchangeKeys(userId)).body
        }
    }
});