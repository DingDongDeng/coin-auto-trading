import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        alias: ['/home'],
        name: 'home',
        component: () => import(/* webpackChunkName: "home" */ '../views/HomeView.vue')
    },

    {
        path: '/auth-exchange-key',
        name: 'authExchangeKey',
        component: () => import(/* webpackChunkName: "authKey" */ '../views/AuthExchangeKeyView.vue')
    },

    {
        path: '/autotrade',
        name: 'autotrade',
        component: () => import(/* webpackChunkName: "autotrade" */ '../views/AutoTradeView.vue')
    },

    {
        path: '/backtest',
        name: 'backtest',
        component: () => import(/* webpackChunkName: "backtest" */ '../views/BackTestView.vue')
    },

    {
        path: '/setting',
        name: 'setting',
        component: () => import(/* webpackChunkName: "setting" */ '../views/SettingView.vue')
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})


export default router