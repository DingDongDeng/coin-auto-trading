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
            <v-icon icon="mdi-delete" @click="showDetail()"></v-icon>
        </v-card-actions>
    </v-card>
    <v-dialog
        v-model="dialog"
        width="500">
        <v-card>
            <v-card-title class="headline black" primary-title>
                Compose Message
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
        name: 'AuthExchangeKeyDetailPanel',

        props: {
            title: String,
            keyPairId: String
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