<script setup>
    import {useAuthExchangeKeyStore} from '@/store/authExchangeKeyStore';
    import {storeToRefs} from 'pinia'
    import PanelComponent from "@/components/AuthExchangeKeyDetailPanel.vue";

    const authExchangeKey = useAuthExchangeKeyStore()
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
                <v-icon icon="mdi-plus-circle"></v-icon>
            </v-col>
        </v-row>
    </v-container>
</template>