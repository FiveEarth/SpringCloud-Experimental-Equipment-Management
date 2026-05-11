import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getRoles, isSessionIdleExpired, clearAuth, touchSession } from './utils/auth'
import Login from './views/login.vue'
import Register from './views/Register.vue'
import Layout from './views/Layout.vue'
import DeviceList from './views/DeviceList.vue'
import MyReserve from './views/MyReserve.vue'
import MyApply from './views/MyApply.vue'
import Labs from './views/Labs.vue'
import EquipmentManage from './views/EquipmentManage.vue'
import MaintainPending from './views/MaintainPending.vue'
import ReserveApprove from './views/ReserveApprove.vue'
import ApplyApprove from './views/ApplyApprove.vue'
import ScrapApprove from './views/ScrapApprove.vue'
import UsageStats from './views/UsageStats.vue'
import MaintainStats from './views/MaintainStats.vue'
import RepairerDashboard from './views/RepairerDashboard.vue'
import RepairerDeviceList from './views/RepairerDeviceList.vue'
import Audit from './views/Audit.vue'
import UserManage from './views/UserManage.vue'
import StudentDashboard from './views/StudentDashboard.vue'
import StudentMaintain from './views/StudentMaintain.vue'
import MaintainRecords from './views/MaintainRecords.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', name: 'Login', component: Login },
  {
    path: '/',
    component: Layout,
    children: [
      { path: 'dashboard', name: 'StudentDashboard', component: StudentDashboard },
      { path: 'devices', name: 'DeviceList', component: DeviceList },
      { path: 'my-reserve', name: 'MyReserve', component: MyReserve },
      { path: 'my-apply', name: 'MyApply', component: MyApply },
      { path: 'my-maintain', name: 'StudentMaintain', component: StudentMaintain },
      { path: 'my-scrap', redirect: '/my-maintain' },
      { path: 'labs', name: 'Labs', component: Labs },
      { path: 'equipment-manage', name: 'EquipmentManage', component: EquipmentManage },
      { path: 'repairer-dashboard', name: 'RepairerDashboard', component: RepairerDashboard },
      { path: 'repairer-devices', name: 'RepairerDeviceList', component: RepairerDeviceList },
      { path: 'maintain-pending', name: 'MaintainPending', component: MaintainPending },
      { path: 'reserve-approve', name: 'ReserveApprove', component: ReserveApprove },
      { path: 'apply-approve', name: 'ApplyApprove', component: ApplyApprove },
      { path: 'scrap-approve', name: 'ScrapApprove', component: ScrapApprove },
      { path: 'usage-stats', name: 'UsageStats', component: UsageStats },
      { path: 'maintain-stats', name: 'MaintainStats', component: MaintainStats },
      { path: 'audit', name: 'Audit', component: Audit },
      { path: 'maintain-records', name: 'MaintainRecords', component: MaintainRecords },
      { path: 'user-manage', name: 'UserManage', component: UserManage },
      { path: 'register', name: 'Register', component: Register }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }
  const token = getToken()
  if (!token) {
    next('/login')
    return
  }
  if (isSessionIdleExpired()) {
    clearAuth()
    next('/login')
    return
  }
  touchSession()
  if (to.path === '/') {
    const roles = getRoles()
    if (roles.includes('ADMIN')) next('/user-manage')
    else if (roles.includes('TEACHER')) next('/apply-approve')
    else if (roles.includes('REPAIR')) next('/repairer-dashboard')
    else next('/dashboard')
    return
  }
  next()
})

export default router