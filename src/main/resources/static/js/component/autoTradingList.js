import {useDashboardStore} from '../store/dashboard.js'
import backTestingRegisterModal from "./backTestingRegisterModal.js";
import tradingChart from "./chart/tradingChart.js";

export default Vue.component('auto-trading-list', {
  template: `
  <v-container>
    <h2> 자동매매 리스트 </h2>
    <v-row justify="start">
      <v-col
          xs="12" sm="12" md="12" lg="12"
          v-for="autoTrading in user.autoTradingList"
      >
        <v-card
            class="ma-3"
        >
          <v-card-text>
            <div>
              제목 : {{autoTrading.title}}
            </div>
            <div>
              전략 코드 : {{autoTrading.strategyIdentifyCode}}
            </div>
            <div>
              매매 타입 : {{autoTrading.tradingTerm}}
            </div>
            <div>
              거래소 : {{autoTrading.coinExchangeType}}
            </div>
            <div>
              코인 : {{autoTrading.coinType}}
            </div>
            <div>
              프로세스ID : {{autoTrading.processorId}}
            </div>
            <div>
              상태 : {{autoTrading.processStatus}}
            </div>

            <div>
              <v-expansion-panels>
                <v-expansion-panel>
                  <v-expansion-panel-header>
                    세부설정
                  </v-expansion-panel-header>
                  <v-expansion-panel-content>
                    <pre style="white-space: pre-wrap">
                      {{JSON.stringify(autoTrading, null, 4)}}
                    </pre>
                  </v-expansion-panel-content>
                </v-expansion-panel>
                <v-expansion-panel
                    v-for="backTesting in user.backTestingList"
                    v-if="backTesting.autoTradingProcessorId==autoTrading.processorId"
                >
                  <v-expansion-panel-header>
                    백테스팅 상세
                  </v-expansion-panel-header>
                  <v-expansion-panel-content>
                    <div>
                      기간 : {{backTesting.start}} ~ {{backTesting.end}}
                    </div>
                    <div>
                      실행율 : {{backTesting.result.executionRate}}%
                    </div>
                    <div>
                      상태 : {{backTesting.result.status}}
                    </div>
                    <div>
                      순수 손익 : {{backTesting.result.marginPrice}}원
                    </div>
                    <div>
                      손익율 : {{backTesting.result.marginRate}}%
                    </div>
                    <div>
                      수수료 : {{backTesting.result.totalFee}}원
                    </div>
                    <div>
                      <tradingChart/>
                    </div>
                    <div>
                      히스토리 :
                      <div v-html="backTesting.result.eventMessage"></div>
                    </div>
                  </v-expansion-panel-content>
                </v-expansion-panel>
              </v-expansion-panels>

            </div>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn @click="startAutoTrading(autoTrading.processorId,refresh)"> 시작</v-btn>
            <v-btn @click="stopAutoTrading(autoTrading.processorId,refresh)"> 정지</v-btn>
            <v-btn @click="terminateAutoTrading(autoTrading.processorId,refresh)"> 종료</v-btn>
            <v-btn @click="toggleBackTestingRegisterModal(autoTrading.processorId)"> 백테스팅</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      <v-col xs="12" sm="8" md="4" lg="4">
        <v-card class="ma-3">
          <v-card-text>
            <v-text-field
                label="자동 매매 이름을 입력해주세요."
                v-model="register.autoTrading.title"
            ></v-text-field>
            <v-select
                label="코인 종목을 선택해주세요."
                solo
                :items="type.coinTypeList"
                item-text="name"
                item-value="value"
                v-model="register.autoTrading.coinType"
            ></v-select>
            <v-select
                label="거래소를 선택해주세요."
                solo
                :items="type.coinExchangeTypeList"
                item-text="name"
                item-value="value"
                v-model="register.autoTrading.coinExchangeType"
            ></v-select>
            <v-select
                label="매매 전략을 선택해주세요."
                solo
                :items="type.tradingTermList"
                item-text="name"
                item-value="value"
                v-model="register.autoTrading.tradingTerm"
            ></v-select>
            <v-select
                label="전략 코드를 선택해주세요."
                solo
                :items="type.strategyCodeList"
                item-text="name"
                item-value="value"
                v-model="register.autoTrading.strategyCode"
                @input="setStrategyMeta"
            ></v-select>
            <v-select
                label="키 페어 ID를 선택해주세요."
                solo
                :items="user.keyPairList"
                item-text="pairId"
                item-value="pairId"
                v-model="register.autoTrading.keyPairId"
            ></v-select>
            <v-text-field
                v-for="(value,key) in register.autoTrading.strategyCoreParamMetaList"
                :label="value.guideMessage"
                v-model="register.autoTrading.strategyCoreParamMap[value.name]"
            ></v-text-field>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                color="primary"
                @click="registerAutoTrading(refresh)"
            > 자동매매 등록
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
    
    <!-- 모달 -->
    <backTestingRegisterModal/>
  
  </v-container>
      `,
  setup() {
    const dashboard = useDashboardStore();
    const {
      user,
      register,
      type,
      toggleBackTestingRegisterModal,
      refresh
    } = Pinia.storeToRefs(dashboard)

    return {
      user,
      register,
      type,
      toggleBackTestingRegisterModal,
      refresh
    }
  },
  components: {
    backTestingRegisterModal,
    tradingChart
  },
  methods: {
    async registerAutoTrading(callback) {
      const body = this.register.autoTrading;
      const response = await this.api.post("/autotrading/register", body);
      callback();
      return response.data.body;
    },
    async startAutoTrading(autoTradingProcessorId, callback) {
      const response = await this.api.post(
          "/autotrading/" + autoTradingProcessorId + "/start", {});
      callback();
      return response.data.body;
    },
    async stopAutoTrading(autoTradingProcessorId, callback) {
      const response = await this.api.post(
          "/autotrading/" + autoTradingProcessorId + "/stop", {});
      callback();
      return response.data.body;
    },
    async terminateAutoTrading(autoTradingProcessorId, callback) {
      if (!confirm("자동매매가 종료되고, 목록에서 제거됩니다. \n 종료하시겠습니까?")) {
        return;
      }
      const response = await this.api.post(
          "/autotrading/" + autoTradingProcessorId + "/terminate", {});
      callback();
      return response.data.body;
    },
    async setStrategyMeta() {
      this.register.autoTrading.strategyCoreParamMetaList = await this.getStrategyMeta(
          this.register.autoTrading.strategyCode
      );
    },
    async getStrategyMeta(strategyCode) {
      if (strategyCode) {
        const response = await this.api.get("/" + strategyCode + "/meta");
        return response.data.body.paramMetaList;
      }
      return [];
    }
  }
});