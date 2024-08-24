<script setup>
    import {useAuthExchangeKeyStore} from '@/store/authExchangeKeyStore';
    import {storeToRefs} from 'pinia'
    import PanelComponent from "@/components/AuthExchangeKeyDetailPanel.vue";

    const authExchangeKey = useAuthExchangeKeyStore()
    storeToRefs(authExchangeKey)
    storeToRefs(authExchangeKey.exchangeKeys)
    authExchangeKey.loadData()
</script>

<template>
    <v-container>
        <v-row>
            <v-col v-for="(exchangeKey, i) in authExchangeKey.exchangeKeys" :key="i" cols="auto">
                <PanelComponent :title="exchangeKey.exchangeType.desc" :keyPairId="exchangeKey.keyPairId"/>
            </v-col>
            <v-col cols="auto">
                <v-icon icon="mdi-plus-circle"
                        @click="authExchangeKey.showRegisterDialog()"
                ></v-icon>
            </v-col>
        </v-row>
    </v-container>
    <v-dialog
        v-model="authExchangeKey.visibleRegisterDialog"
        width="500">
        <v-card>
            <v-card-title class="headline black" primary-title>
                Compose Message222
            </v-card-title>
            <v-card-text class="pa-5">
            </v-card-text>
            <v-card-actions class="pa-5">
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>