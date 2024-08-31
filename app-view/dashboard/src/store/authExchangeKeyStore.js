import {defineStore} from "pinia";
import axios from 'axios';

async function getExchangeKeys() {
    const response = await axios.get('/coin/exchange-key');
    return response.data;
}

async function registerExchangeKey(body) {
    const response = await axios.post('/coin/exchange-key', body);
    return response.data;
}


export const useAuthExchangeKeyStore = defineStore("authExchangeKey", {

    state: () => ({
        exchangeKeys: [],
        register: {
            visibleRegisterDialog: false,
            exchangeType: '',
            accessKey: '',
            secretKey: '',
        }
    }),

    getters: {},

    actions: {
        async loadExchangeKeys() {
            this.exchangeKeys = (await getExchangeKeys()).body
        },
        async registerExchangeKey() {
            await registerExchangeKey(this.register)
            this.registerReset()
        },
        registerReset() {
            this.register.visibleRegisterDialog = false;
            this.register.exchangeType = '';
            this.register.accessKey = '';
            this.register.secretKey = '';
        }
    }
});