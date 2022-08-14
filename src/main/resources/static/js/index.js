import api from "./api.js"
import dashboard from "./component/dashboard/dashboard.js"

// global variable
Vue.prototype.api = api.axios;

// pinia : https://pinia.vuejs.org/getting-started.html#installation
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
