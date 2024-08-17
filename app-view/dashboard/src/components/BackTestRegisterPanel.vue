<template>
    <v-card color="#181B1F" variant="elevated" class="mx-auto" width="350" max-width="350">
        <v-card-actions>
            <v-btn @click="showDetail()" size="x-large">
                백테스트 등록
            </v-btn>
        </v-card-actions>
    </v-card>
    <v-dialog
        v-model="dialog"
        width="500">
        <v-card>
            <v-card-title class="headline black" primary-title>
                백테스트 등록
            </v-card-title>
            <v-card-text class="pa-5">
            </v-card-text>
            <v-card-actions class="pa-5">


            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        name: 'BackTestDetailPanel',

        props: {
            title: String
        },
        data() {
            return {
                dialog: false,
            }
        },
        methods: {
            showDetail() {
                this.dialog = true
            }
        }
    }
</script>