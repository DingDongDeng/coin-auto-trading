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

async function removeExchangeKey(keyPairId) {
    const response = await axios.delete(`/coin/exchange-key/${keyPairId}`);
    return response.data;
}

export const useAuthExchangeKeyStore = defineStore("authExchangeKey", {

    state: () => ({
        exchangeKeys: [],
        register: {
            visibleDialog: false,
            exchangeType: '',
            accessKey: '',
            secretKey: '',
        },
        remove: {
            keyPairId: '',
        }
    }),

    getters: {},

    actions: {
        async loadExchangeKeys() {
            this.exchangeKeys = (await getExchangeKeys()).body
        },
        async registerExchangeKey() {
            await registerExchangeKey(this.register)
            await this.loadExchangeKeys()
            this.registerReset()
        },
        async removeExchangeKey() {
            await removeExchangeKey(this.remove.keyPairId)
            await this.loadExchangeKeys()
        },
        registerReset() {
            this.register.visibleDialog = false;
            this.register.exchangeType = '';
            this.register.accessKey = '';
            this.register.secretKey = '';
        }
    }
});