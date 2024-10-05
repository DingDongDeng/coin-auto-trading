<script setup>

    import AutoTradingPanel from "@/components/AutoTradingPanel.vue";
    import {useAutoTradingStore} from "@/store/autoTradingStore";
    import {storeToRefs} from "pinia";

    const autoTrading = useAutoTradingStore()
    const {autoTradings} = storeToRefs(autoTrading)

</script>
<template>
    <v-container>
        <v-row>
            <v-col v-for="(autoTrading) in autoTradings" :key="autoTrading.id" cols="auto">
                <AutoTradingPanel :auto-trading="autoTrading"></AutoTradingPanel>
            </v-col>
        </v-row>
    </v-container>
</template>