<template>
    <v-container>
        <v-row>
            <v-col v-for="(variant, i) in variants" :key="i" cols="auto">
                <PanelComponent :title="variant"/>
            </v-col>
        </v-row>
    </v-container>
</template>
<script>

    import PanelComponent from "@/components/PanelComponent.vue";

    export default {
        name: 'BackTestView',
        components: {PanelComponent},

        data() {
            return {
                variants: ['비트코인', '이더리움', '솔라나', '도지투더문', '일론머스크'],
            }

        }
    }
</script>