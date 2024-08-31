<script setup>
    import {useAuthExchangeKeyStore} from '@/store/authExchangeKeyStore';
    import {storeToRefs} from 'pinia'
    import PanelComponent from "@/components/AuthExchangeKeyDetailPanel.vue";

    const authExchangeKey = useAuthExchangeKeyStore()

    // data
    const {
        exchangeKeys,
        register
    } = storeToRefs(authExchangeKey);

    authExchangeKey.loadExchangeKeys()
</script>

<template>
    <v-container>
        <v-row>
            <v-col v-for="(exchangeKey, i) in exchangeKeys" :key="i" cols="auto">
                <PanelComponent :title="exchangeKey.exchangeType.desc" :keyPairId="exchangeKey.keyPairId"/>
            </v-col>
            <v-col cols="auto">
                <v-icon icon="mdi-plus-circle"
                        @click="register.visibleRegisterDialog = true"
                ></v-icon>
            </v-col>
        </v-row>
    </v-container>
    <v-dialog
        v-model="register.visibleRegisterDialog"
        width="500">
        <v-card>
            <v-card-title class="headline black" primary-title>
                거래소 인증키 등록하기
            </v-card-title>
            <v-card-text class="pa-5">
                <v-text-field v-model="register.exchangeType" label="거래소"/>
                <v-text-field v-model="register.accessKey" label="액세스 키"/>
                <v-text-field v-model="register.secretKey" label="시크릿 키"/>
            </v-card-text>
            <v-card-actions class="pa-5">
                <v-btn @click="authExchangeKey.registerExchangeKey()">등록</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>