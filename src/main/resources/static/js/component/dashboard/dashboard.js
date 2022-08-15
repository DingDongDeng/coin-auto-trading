import dashboardGnb from "./dashboard-gnb.js"

export default Vue.component('dashboard', {
  template: `
<v-container>
  <!-- 헤더 -->
  <dashboard-gnb/>
  <!-- 모달 -->
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
            @click="toggleBackTestingRegisterUI()"
        > 취소
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 키 리스트 -->
  <v-container>
    <h2> 키 리스트 </h2>
    <v-row justify="start">
      <v-col
          xs="12" sm="8" md="4" lg="3"
          v-for="keyPair in user.keyPairList"
      >
        <v-card
            class="ma-3"
        >
          <v-card-text>
            <div>
              키 페어 ID : {{keyPair.pairId}}
            </div>
            <div>
              거래소 : {{keyPair.coinExchangeType}}
            </div>
            <div v-for="key in keyPair.keyList">
              <div>
                키 이름 : {{key.name}}
              </div>
            </div>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                color="primary"
                @click="deleteUserPairKey(keyPair.pairId, refresh)"
            > 삭제
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      <v-col xs="12" sm="8" md="4" lg="3">
        <v-card class="ma-3">
          <v-card-text>
            <div>
              <v-select
                  label="거래소를 선택해주세요."
                  solo
                  :items="type.coinExchangeTypeList"
                  item-text="name"
                  item-value="value"
                  v-model="register.keyPair.coinExchangeType"
              ></v-select>
              <v-row
                  align="center"
                  justify="center"
                  v-for="(key, i) in register.keyPair.keyList"
              >
                <v-col cols="4">
                  <v-text-field
                      label="키 이름"
                      v-model="key.name"
                  ></v-text-field>
                </v-col>
                <v-col cols="6">
                  <v-text-field
                      label="키 값"
                      v-model="key.value"
                  ></v-text-field>
                </v-col>
                <v-col cols="2">
                  <v-icon @click="deleteKeyInputBox(i)">delete</v-icon>
                </v-col>
              </v-row>
              <v-icon
                  x-large
                  @click="addKeyInputBox()"
              >add_circle
              </v-icon>
            </div>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn value="" @click="registerKeyPair(refresh)">키 등록</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>

  <!-- 자동매매 리스트 -->
  <v-container>
    <h2> 자동매매 리스트 </h2>
    <v-row justify="start">
      <v-col
          xs="12" sm="8" md="4" lg="4"
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
                    백테스팅 현황
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
            <v-btn @click="toggleBackTestingRegisterUI(autoTrading.processorId)"> 백테스팅</v-btn>
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
                @input="refresh"
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
  </v-container>
</v-container>
`,
  components: {
    dashboardGnb
  },
  data() {
    return {
      user: {
        keyPairList: [],
        autoTradingList: [],
        backTestingList: []
      },
      register: {
        keyPair: {},
        autoTrading: {},
        backTesting: {}
      },
      type: {}
    }
  },
  async created() {
    this.register.keyPair = this.resetKeyRegister();
    this.register.autoTrading = await this.resetAutoTradingRegister();
    this.register.backTesting = this.resetBackTestingRegister();
    this.type = this.resetTypeInfo();
    this.refresh();
  },
  methods: {
    /******* common *******/
    async refresh() {
      this.updateAllInformation();
    },
    async updateAllInformation() {
      const userId = this.user.id;
      this.user.keyPairList = await this.getUserKeyList(userId);
      this.user.autoTradingList = await this.getUserAutoTradingList(userId);
      this.user.backTestingList = await this.getUserBackTestingList(userId);
      this.type = this.resetTypeInfo();
      this.register.autoTrading.strategyCoreParamMetaList = await this.resetStrategyMeta(
          this.register.autoTrading.strategyCode);
    },
    async resetTypeInfo() {
      const response = await this.api.get("/type");
      this.type = response.data.body;
    },

    /******* keyPair *******/
    addKeyInputBox() {
      this.register.keyPair.keyList.push({name: "", value: ""});
    },
    deleteKeyInputBox(index) {
      this.register.keyPair.keyList.splice(index, 1);
    },
    async getUserKeyList(userId) {
      const response = await this.api.get("/user/" + userId + "/key/pair");
      return response.data.body;
    },
    async registerKeyPair(callback) {
      const body = {
        coinExchangeType: this.register.keyPair.coinExchangeType,
        keyList: this.register.keyPair.keyList
      };

      const response = await this.api.post("/key/pair", body);
      this.register.keyPair = this.resetKeyRegister();
      callback();
      return response.data.body;
    },
    async deleteUserPairKey(pairKeyId, callback) {
      const response = await this.api.delete("/key/pair/" + pairKeyId);
      callback();
      return response.data.body;
    },
    resetKeyRegister() {
      return {
        coinExchangeType: "",
        keyList: [
          {
            name: "",
            value: ""
          }
        ]
      }
    },

    /******* autoTrading *******/
    async getUserAutoTradingList(userId) {
      const response = await this.api.get("/user/" + userId + "/autotrading");
      return response.data.body;
    },
    async registerAutoTrading(callback) {
      const body = this.register.autoTrading;
      const response = await this.api.post("/autotrading/register", body);
      this.register.autoTrading = await this.resetAutoTradingRegister();
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
    async resetAutoTradingRegister() {
      return {
        title: "",
        coinType: "",
        coinExchangeType: "",
        tradingTerm: "",
        strategyCode: "",
        keyPairId: "",
        strategyCoreParamMap: {},
        strategyCoreParamMetaList: [],
      }
    },
    async resetStrategyMeta(strategyCode) {
      if (strategyCode) {
        const response = await this.api.get("/" + strategyCode + "/meta");
        return response.data.body.paramMetaList;
      }
      return [];
    },

    /******* backTesting *******/
    async getUserBackTestingList(userId) {
      const response = await this.api.get("/user/" + userId + "/backtesting");
      return response.data.body;
    },
    async registerBackTesting(processorId, start, end, callback) {
      const body = {
        autoTradingProcessorId: processorId,
        start: start,
        end: end
      };

      const response = await this.api.post("/backtesting", body);
      this.register.backTesting = this.resetBackTestingRegister();
      callback();
      return response.data.body;
    },
    toggleBackTestingRegisterUI(autoTradingProcessorId) {
      this.register.backTesting.autoTradingProcessorId = autoTradingProcessorId;
      this.register.backTesting.isVisible = !this.register.backTesting.isVisible;
    },
    resetBackTestingRegister() {
      return {
        isVisible: false,
        autoTradingProcessorId: "",
        start: "",
        end: "",
      }
    },
  }

});