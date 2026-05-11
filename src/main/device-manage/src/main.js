import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import {
  getToken,
  getUserId,
  getUserName,
  getRoles,
  getSafeHeaderValue,
  clearAuth,
  touchSession,
  isSessionIdleExpired
} from './utils/auth'

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

let idleNotified = false
function forceLogoutByIdle() {
  clearAuth()
  if (!idleNotified) {
    idleNotified = true
    ElMessage.warning('登录已超时，请重新登录')
  }
  if (router.currentRoute.value.path !== '/login') router.push('/login')
}

// 用户有交互时更新活跃时间（节流到 10 秒）
if (typeof window !== 'undefined') {
  let lastTouch = 0
  const events = ['click', 'keydown', 'mousemove', 'scroll', 'touchstart']
  const onActivity = () => {
    const now = Date.now()
    if (now - lastTouch < 10000) return
    lastTouch = now
    if (getToken()) touchSession()
  }
  events.forEach((e) => window.addEventListener(e, onActivity, { passive: true }))
}

axios.interceptors.request.use((config) => {
  if (isSessionIdleExpired()) {
    forceLogoutByIdle()
    return Promise.reject(new Error('idle-timeout'))
  }
  const token = getToken()
  if (token) {
    touchSession()
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
    if (err && err.message === 'idle-timeout') {
      return Promise.reject(err)
    }
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
