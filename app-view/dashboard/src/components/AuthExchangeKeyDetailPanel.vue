<script setup>
    import {useAuthExchangeKeyStore} from '@/store/authExchangeKeyStore';
    import {storeToRefs} from 'pinia';
    import {defineProps} from 'vue';

    const authExchangeKey = useAuthExchangeKeyStore();
    const {remove} = storeToRefs(authExchangeKey);

    defineProps({
        title: String,
        keyPairId: String,
    });
</script>
<template>
    <v-card color="#181B1F" variant="elevated" class="mx-auto" width="350" max-width="350">
        <v-card-item>
            <div>
                <div class="text-overline mb-1">
                </div>
                <div class="text-h6 mb-1">
                    {{ title }}
                </div>
                <div class="text-caption">
                    {{ keyPairId }}
                </div>
            </div>
        </v-card-item>

        <v-card-actions>
            <v-icon icon="mdi-delete" @click="(() => {
                remove.keyPairId = keyPairId;
                authExchangeKey.removeExchangeKey();
            })"></v-icon>
        </v-card-actions>
    </v-card>
</template>