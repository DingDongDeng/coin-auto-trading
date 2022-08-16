import {useDashboardStore} from '../../store/dashboard.js'

export default Vue.component('back-testing-register-modal', {
  template: `
  <v-dialog
      v-model="register.backTesting.isVisible"
      width="500"
  >
    <v-card>
      <v-toolbar dark color="primary">
        <v-toolbar-title>백테스팅 실행</v-toolbar-title>
      </v-toolbar>
      <v-card-text>
        <div>
          시작일 : <input type="datetime-local" v-model="register.backTesting.start">
        </div>
        <div>
          종료일 : <input type="datetime-local" v-model="register.backTesting.end">
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
            @click="registerBackTesting(register.backTesting.autoTradingProcessorId, register.backTesting.start, register.backTesting.end, refresh)"
        > 실행
        </v-btn>
        <v-btn
            @click="toggleBackTestingRegisterModal(this.register.backTesting.autoTradingProcessorId)"
        > 취소
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
      `,
  setup() {
    const dashboard = useDashboardStore();
    const {
      register,
      toggleBackTestingRegisterModal,
      refresh
    } = Pinia.storeToRefs(dashboard)

    return {
      register,
      toggleBackTestingRegisterModal,
      refresh
    }
  },
  methods: {
    async registerBackTesting(processorId, start, end, callback) {
      const body = {
        autoTradingProcessorId: processorId,
        start: start,
        end: end
      };

      const response = await this.api.post("/backtesting", body);
      callback();
      return response.data.body;
    }
  }

});