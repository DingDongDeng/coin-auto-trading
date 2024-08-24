const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        port: 8090,
        proxy: {
            '/coin': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
        },
    }
})
