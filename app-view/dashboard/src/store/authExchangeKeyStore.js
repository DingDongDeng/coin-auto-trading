import {defineStore} from "pinia";
import axios from 'axios';

async function getExchangeKeys() {
    const response = await axios.get('/coin/exchange-key');
    return response.data;
}


export const useAuthExchangeKeyStore = defineStore("authExchangeKey", {

    state: () => ({
        exchangeKeys: [],
        visibleRegisterDialog: false
    }),

    getters: {},

    actions: {
        async loadData() {
            this.exchangeKeys = (await getExchangeKeys()).body
        },
        showRegisterDialog() {
            this.visibleRegisterDialog = true
        }
    }
});