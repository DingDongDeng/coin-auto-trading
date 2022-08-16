import {useDashboardStore} from '../../store/dashboard.js'

export default Vue.component('key-list', {
  template: `
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
      `,
  setup() {
    const dashboard = useDashboardStore();
    const {user, register, type, refresh} = Pinia.storeToRefs(dashboard)

    return {
      user,
      register,
      type,
      refresh
    }
  },
  methods: {
    addKeyInputBox() {
      this.register.keyPair.keyList.push({name: "", value: ""});
    },
    deleteKeyInputBox(index) {
      this.register.keyPair.keyList.splice(index, 1);
    },
    async registerKeyPair(callback) {
      const body = {
        coinExchangeType: this.register.keyPair.coinExchangeType,
        keyList: this.register.keyPair.keyList
      };
      const response = await this.api.post("/key/pair", body);
      callback();
      return response.data.body;
    },
    async deleteUserPairKey(pairKeyId, callback) {
      const response = await this.api.delete("/key/pair/" + pairKeyId);
      callback();
      return response.data.body;
    }
  }

});