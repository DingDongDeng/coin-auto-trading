const api = axios.create({
  timeout: 10000,
});
api.interceptors.response.use((response) => response, (error) => {
  alert(error);
  throw error;
});

async function getUserKeyList() {
  const response = await api.get("/user/key/pair");
  return response.data.body;
}

async function getUserAutoTradingList() {
  const response = await api.get("/user/autotrading");
  return response.data.body;
}

async function getUserBackTestingList() {
  const response = await api.get("/user/backtesting");
  return response.data.body;
}

async function getTypeInfo() {
  const response = await api.get("/type");
  return response.data.body;
}

export const useDashboardStore = Pinia.defineStore('dashboard', {
  state: () => ({
    user: {
      keyPairList: [],
      autoTradingList: [],
      backTestingList: []
    },
    register: {
      keyPair: {
        coinExchangeType: "",
        keyList: [
          {
            name: "",
            value: ""
          }
        ]
      },
      autoTrading: {
        title: "",
        coinType: "",
        coinExchangeType: "",
        tradingTerm: "",
        strategyCode: "",
        keyPairId: "",
        strategyCoreParamMap: {},
        strategyCoreParamMetaList: [],
      },
      backTesting: {
        isVisible: false,
        autoTradingProcessorId: "",
        start: "",
        end: "",
        baseCandleUnit: ""
      }
    },
    type: {}
  }),
  actions: {
    toggleBackTestingRegisterModal(autoTradingProcessorId) {
      this.register.backTesting.autoTradingProcessorId = autoTradingProcessorId;
      this.register.backTesting.isVisible = !this.register.backTesting.isVisible;
    },
    async refresh() {
      this.$reset();
      this.user.keyPairList = await getUserKeyList();
      this.user.autoTradingList = await getUserAutoTradingList();
      this.user.backTestingList = await getUserBackTestingList();
      this.type = await getTypeInfo();
    }
  },
  getters: {}
})
