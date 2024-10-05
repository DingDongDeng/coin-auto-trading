<template>

    <v-list height="90%">
        <v-list-item v-for="item in items" :key="item.title" :to="item.route" :prepend-icon="item.icon"
                     :title="item.title">

        </v-list-item>
    </v-list>

    <v-divider></v-divider>

    <v-list>
        <v-list-item prepend-icon="mdi-account-circle" title="admin"></v-list-item>
    </v-list>


</template>


<script>


    export default {
        name: 'NavbarView',

        data() {
            return {
                items: [
                    {route: "/home", icon: "mdi-home", title: "홈"},
                    {route: "/auth-exchange-key", icon: "mdi-key", title: "거래소 인증키"},
                    {route: "/autotrade", icon: "mdi-autorenew", title: "자동매매"},
                    {route: "/backtest", icon: "mdi-history", title: "백테스트"},
                    {route: "/setting", icon: "mdi-cog", title: "내 설정"},

                ]
            }
        }
    }
</script>