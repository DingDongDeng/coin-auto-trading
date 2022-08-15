import {useDashboardStore} from '../../store/dashboard.js'

export default Vue.component('dashboard-gnb', {
  template: `
  <v-container>
    <v-toolbar dark color="primary" style="height:64px;">
      <v-toolbar-title class="white--text"></v-toolbar-title>

      <v-toolbar-items>
        <v-icon @click="refresh()">cached</v-icon>
      </v-toolbar-items>

      <v-spacer></v-spacer>

      <v-form action="/logout" method="post">
        <v-btn
            type="submit"
            text
        >Logout
        </v-btn>
      </v-form>
    </v-toolbar>
  </v-container>`,
  components: {},
  setup() {
    const dashboard = useDashboardStore()
    return {
      refresh() {
        dashboard.refresh();
      }
    }
  }
})
;