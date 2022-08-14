import api from "./api.js"
import dashboard from "./component/dashboard/dashboard.js"

// use : {vuex}
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

// global variable
Vue.prototype.api = api.axios;


