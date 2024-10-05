<script setup>
    import {defineProps} from 'vue';

    defineProps({
        autoTrading: Object,
    });
</script>
<template>
    <v-card color="#181B1F" variant="elevated" class="mx-auto" width="350" max-width="350">
        <v-card-item>
            <div>
                <div class="text-overline mb-1">
                    [{{ autoTrading.status.desc }}] {{ autoTrading.strategyType.desc }}
                </div>
                <div class="text-h6 mb-1">
                    {{ autoTrading.title }}
                </div>
                <div class="text-caption">
                    {{ autoTrading.id }}
                </div>
            </div>
        </v-card-item>

        <v-card-actions>
        </v-card-actions>
    </v-card>
</template>