import {useDashboardStore} from '../store/dashboard.js'
import dashboardGnb from "./dashboard-gnb.js"
import keyList from "./keyList.js"
import autoTradingList from "./autoTradingList.js";

export default Vue.component('dashboard', {
  template: `
<v-container>
  <!-- 헤더 -->
  <dashboardGnb/>

  <!-- 키 리스트 -->
  <keyList/> 

  <!-- 자동매매 리스트 -->
  <autoTradingList/>

</v-container>
`,
  components: {
    dashboardGnb,
    keyList,
    autoTradingList
  },
  created() {
    this.refresh();
  },
  setup() {
    const dashboard = useDashboardStore();
    const {refresh} = Pinia.storeToRefs(dashboard)

    return {
      refresh
    }
  }
});