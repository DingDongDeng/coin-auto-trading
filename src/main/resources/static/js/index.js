import dashboard from "./component/dashboard/dashboard.js"

// global variable
Vue.prototype.api = api();

new Vue({
  el: '#app',
  vuetify: new Vuetify(),
  components: {
    dashboard
  },
  async created() {
  },
  methods: {},
});

function api() {
  const api = axios.create({
    timeout: 5000,
  });
  api.interceptors.response.use((response) => response, (error) => {
    alert(error);
    throw error;
  });
  return api;
}