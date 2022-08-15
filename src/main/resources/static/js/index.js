import dashboard from "./component/dashboard/dashboard.js"

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
