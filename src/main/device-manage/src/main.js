import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import { getToken, getUserId, getUserName, getRoles, getSafeHeaderValue, clearAuth } from './utils/auth'

// 修复 ResizeObserver loop 报错：将回调推迟到下一帧执行，避免布局循环（Element Plus 等组件常见）
if (typeof window !== 'undefined' && window.ResizeObserver) {
  const NativeResizeObserver = window.ResizeObserver
  window.ResizeObserver = class ResizeObserver extends NativeResizeObserver {
    constructor(callback) {
      super((entries, observer) => {
        requestAnimationFrame(() => callback(entries, observer))
      })
    }
  }
}

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

axios.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    if (!config.headers.Authorization) config.headers.Authorization = `Bearer ${token}`
    const uid = getUserId()
    if (uid && !config.headers['X-User-Id']) config.headers['X-User-Id'] = uid
    const roles = getRoles()
    if (roles && roles.length && !config.headers['X-Roles']) {
      const rolesStr = Array.isArray(roles) ? roles.join(',') : String(roles)
      config.headers['X-Roles'] = getSafeHeaderValue(rolesStr)
    }
    const name = getUserName()
    if (name != null && name !== '' && !config.headers['X-User-Name']) config.headers['X-User-Name'] = getSafeHeaderValue(name)
  }
  return config
})

axios.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      clearAuth()
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

app.use(router)
app.use(ElementPlus)
app.config.globalProperties.$axios = axios
app.mount('#app')
