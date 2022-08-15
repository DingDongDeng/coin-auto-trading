import Vue from "./lib/vue.js"
import Pinia from "./lib/pinia.js"
import Axios from "./lib/axios.js"
import dashboard from "./component/dashboard/dashboard.js"

const axios = Axios.create({
  timeout: 5000,
});
axios.interceptors.response.use((response) => response, (error) => {
  alert(error);
  throw error;
});

Vue.prototype.api = axios;

Vue.use(Pinia.PiniaVuePlugin)

new Vue({
  el: '#app',
  vuetify: new Vuetify(),
  pinia: Pinia.createPinia(),
  components: {
    dashboard
  },
  async created() {
  },
  methods: {},
});
