/**
 * 认证信息使用 sessionStorage，实现「同一浏览器多标签页多角色登录」：
 * 每个标签页独立存储 token/用户信息，互不覆盖。
 */
const KEY_TOKEN = 'token'
const KEY_USER_ID = 'userId'
const KEY_USER_NAME = 'userName'
const KEY_USERNAME = 'username'
const KEY_ROLES = 'roles'
const KEY_PERMS = 'perms'
const KEY_LAST_ACTIVE_AT = 'lastActiveAt'
const IDLE_TIMEOUT_MS = 30 * 60 * 1000

const storage = typeof window !== 'undefined' ? window.sessionStorage : null

export function getToken() {
  return storage ? storage.getItem(KEY_TOKEN) : null
}

export function setToken(token) {
  if (storage) storage.setItem(KEY_TOKEN, token || '')
}

export function getUserId() {
  return storage ? storage.getItem(KEY_USER_ID) : null
}

export function setUserId(id) {
  if (storage) storage.setItem(KEY_USER_ID, id != null ? String(id) : '')
}

export function getUserName() {
  return storage ? storage.getItem(KEY_USER_NAME) : ''
}

/**
 * 返回仅含 ISO-8859-1 的字符串，用于 HTTP 请求头（避免 setRequestHeader 报错）
 */
export function getSafeHeaderValue(str) {
  if (str == null || typeof str !== 'string') return ''
  return String(str)
    .split('')
    .map((c) => (c.codePointAt(0) > 255 ? '?' : c))
    .join('')
}

export function setUserName(name) {
  if (storage) storage.setItem(KEY_USER_NAME, name || '')
}

export function getUsername() {
  return storage ? storage.getItem(KEY_USERNAME) : ''
}

export function setUsername(name) {
  if (storage) storage.setItem(KEY_USERNAME, name || '')
}

export function getRoles() {
  if (!storage) return []
  try {
    const raw = storage.getItem(KEY_ROLES)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

export function setRoles(roles) {
  if (storage) storage.setItem(KEY_ROLES, JSON.stringify(roles || []))
}

export function getPerms() {
  if (!storage) return []
  try {
    const raw = storage.getItem(KEY_PERMS)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

export function setPerms(perms) {
  if (storage) storage.setItem(KEY_PERMS, JSON.stringify(perms || []))
}

export function setLoginData(data) {
  if (!data) return
  if (data.token) setToken(data.token)
  if (data.userId != null) setUserId(data.userId)
  setUserName(data.realName || data.userName || data.username || '')
  setUsername(data.username || '')
  setRoles(data.roles || [])
  setPerms(data.permissions || [])
  touchSession()
}

export function clearAuth() {
  if (!storage) return
  storage.removeItem(KEY_TOKEN)
  storage.removeItem(KEY_USER_ID)
  storage.removeItem(KEY_USER_NAME)
  storage.removeItem(KEY_USERNAME)
  storage.removeItem(KEY_ROLES)
  storage.removeItem(KEY_PERMS)
  storage.removeItem(KEY_LAST_ACTIVE_AT)
}

export function isLoggedIn() {
  return !!getToken()
}

export function touchSession() {
  if (!storage) return
  storage.setItem(KEY_LAST_ACTIVE_AT, String(Date.now()))
}

export function getLastActiveAt() {
  if (!storage) return 0
  const raw = storage.getItem(KEY_LAST_ACTIVE_AT)
  if (!raw) return 0
  const n = Number(raw)
  return Number.isFinite(n) ? n : 0
}

export function isSessionIdleExpired() {
  const token = getToken()
  if (!token) return false
  const last = getLastActiveAt()
  if (!last) return false
  return Date.now() - last > IDLE_TIMEOUT_MS
}

export function getIdleTimeoutMs() {
  return IDLE_TIMEOUT_MS
}
