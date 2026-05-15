const { defineConfig } = require('@vue/cli-service')
// 开发时网关建议用 8888 端口，避免 80 需要管理员权限；若网关在 80，可改为 target: 'http://localhost:80'
const API_TARGET = process.env.VUE_APP_API_TARGET || 'http://localhost:8888'
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: API_TARGET,
        changeOrigin: true
      }
    }
  }
})
