<script setup>
    import {defineProps} from 'vue';

    defineProps({
        backTest: Object,
    });

</script>
<template>
    <v-card color="#181B1F" variant="elevated" class="mx-auto" width="350" max-width="350">
        <v-card-item>
            <div>
                <div class="text-overline mb-1">
                    {{ backTest.id }}
                </div>
                <div class="text-h6 mb-1">
                    {{ backTest }}
                </div>
                <div class="text-caption">Greyhound divisely hello coldly fonwderfully</div>
            </div>
        </v-card-item>

        <v-card-actions>
            <v-btn @click="showDetail()">
                상세보기
            </v-btn>
        </v-card-actions>
    </v-card>
</template>