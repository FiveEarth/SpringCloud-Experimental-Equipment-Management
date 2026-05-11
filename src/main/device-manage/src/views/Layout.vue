<template>
  <div class="layout-wrap">
    <!-- 顶栏：参考效果图 -->
    <header class="top-header">
      <div class="header-left">
        <div class="logo-icon">实</div>
        <span class="platform-name">实验设备管理平台</span>
        <el-tag size="small" class="role-tag" type="info" ef  fect="plain">{{ roleLabel }}</el-tag>
      </div>
      <div class="header-right">
        <span class="user-name">{{ userDisplayName }}</span>
        <span class="user-email">{{ userEmail }}</span>
        <el-button type="primary" class="logout-btn" @click="logout">
          <el-icon><Right /></el-icon>
          退出登录
        </el-button>
      </div>
    </header>

    <!-- 学生端步骤指示：预约 → 领用 → 归还 -->
    <div v-if="isStudentRole" class="step-bar">
      <router-link to="/my-reserve" class="step-item" :class="{ active: stepActive === 'reserve' }">
        <span class="step-num">1</span>
        <span class="step-label">预约</span>
      </router-link>
      <span class="step-arrow">→</span>
      <router-link to="/my-apply" class="step-item" :class="{ active: stepActive === 'apply' }">
        <span class="step-num">2</span>
        <span class="step-label">领用</span>
      </router-link>
      <span class="step-arrow">→</span>
      <span class="step-item step-item-static" :class="{ active: stepActive === 'apply' }">
        <span class="step-num">3</span>
        <span class="step-label">归还</span>
      </span>
    </div>

    <el-container class="layout-body">
      <!-- 侧栏：仅展示当前角色可见菜单 -->
      <el-aside :width="asideWidth" class="aside">
        <el-menu
          :default-active="$route.path"
          :router="false"
          class="side-menu"
          background-color="#fff"
          text-color="#606266"
          active-text-color="#409eff"
          @select="handleMenuSelect"
        >
          <template v-for="item in menuItems" :key="item.path">
            <el-menu-item :index="item.path">
              <el-icon v-if="iconMap[item.icon]"><component :is="iconMap[item.icon]" /></el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <router-view :key="`${$route.fullPath}-${viewRefreshKey}`" />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { Right, House, Box, List, Setting, User, DataBoard, Document, Tools } from '@element-plus/icons-vue'
import { getRoles, getUserName, getUsername, clearAuth } from '../utils/auth'

export default {
  name: 'Layout',
  components: { Right, House, Box, List, Setting, User, DataBoard, Document, Tools },
  data() {
    return {
      roles: [],
      asideWidth: '200px',
      viewRefreshKey: 0,
      iconMap: {
        House,
        Box,
        List,
        Setting,
        User,
        DataBoard,
        Document,
        Tools
      }
    }
  },
  computed: {
    isStudentRole() {
      return this.roles.includes('STUDENT') && !this.roles.includes('ADMIN') && !this.roles.includes('TEACHER') && !this.roles.includes('REPAIR')
    },
    stepActive() {
      if (this.$route.path === '/my-reserve') return 'reserve'
      if (this.$route.path === '/my-apply') return 'apply'
      return ''
    },
    roleLabel() {
      const map = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生', REPAIR: '维修员' }
      if (this.roles.includes('ADMIN')) return map.ADMIN
      if (this.roles.includes('TEACHER')) return map.TEACHER
      if (this.roles.includes('REPAIR')) return map.REPAIR
      return map.STUDENT
    },
    userDisplayName() {
      return getUserName() || '未登录'
    },
    userEmail() {
      const u = getUsername()
      return u ? u + '@lab.com' : ''
    },
    menuItems() {
      const items = []
      if (this.roles.includes('STUDENT') && !this.roles.includes('ADMIN') && !this.roles.includes('TEACHER') && !this.roles.includes('REPAIR')) {
        items.push({ path: '/dashboard', title: '工作台', icon: 'House' })
        items.push({ path: '/devices', title: '设备列表', icon: 'Box' })
        items.push({ path: '/my-reserve', title: '我的预约', icon: 'List' })
        items.push({ path: '/my-apply', title: '我的领用', icon: 'Document' })
        items.push({ path: '/my-maintain', title: '维修申请', icon: 'Tools' })
        return items
      }
      if (this.roles.includes('REPAIR') && !this.roles.includes('ADMIN')) {
        items.push({ path: '/repairer-dashboard', title: '工作台', icon: 'House' })
        items.push({ path: '/repairer-devices', title: '设备列表', icon: 'Box' })
        items.push({ path: '/maintain-pending', title: '维修列表', icon: 'Tools' })
        items.push({ path: '/maintain-stats', title: '维修统计', icon: 'DataBoard' })
        return items
      }
      if (this.roles.includes('TEACHER') && !this.roles.includes('ADMIN')) {
        items.push({ path: '/apply-approve', title: '归还审批', icon: 'Document' })
        items.push({ path: '/reserve-approve', title: '预约审批', icon: 'List' })
        items.push({ path: '/scrap-approve', title: '报废审批', icon: 'Document' })
        items.push({ path: '/maintain-records', title: '维修记录', icon: 'Tools' })
        items.push({ path: '/usage-stats', title: '使用统计', icon: 'DataBoard' })
        items.push({ path: '/devices', title: '设备列表', icon: 'Box' })
        return items
      }
      if (this.roles.includes('ADMIN')) {
        items.push({ path: '/user-manage', title: '用户管理', icon: 'User' })
        items.push({ path: '/equipment-manage', title: '设备管理', icon: 'Box' })
        items.push({ path: '/labs', title: '实验室管理', icon: 'Setting' })
        items.push({ path: '/apply-approve', title: '归还审批', icon: 'Document' })
        items.push({ path: '/reserve-approve', title: '预约审批', icon: 'List' })
        items.push({ path: '/scrap-approve', title: '报废审批', icon: 'Document' })
        items.push({ path: '/usage-stats', title: '使用统计', icon: 'DataBoard' })
        items.push({ path: '/maintain-records', title: '维修申请', icon: 'Tools' })
        items.push({ path: '/audit', title: '日志审计', icon: 'DataBoard' })
        return items
      }
      return items
    }
  },
  created() {
    this.roles = getRoles()
  },
  methods: {
    handleMenuSelect(path) {
      this.viewRefreshKey += 1
      if (this.$route.path !== path) {
        this.$router.push(path)
      }
    },
    logout() {
      clearAuth()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.layout-wrap {
  min-height: 100vh;
  background: #f5f7fa;
}
.top-header {
  height: 56px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}
.step-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 24px;
  background: #fff;
  border-bottom: 1px solid #f0f2f5;
}
.step-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  color: #909399;
  text-decoration: none;
  font-size: 13px;
  transition: all 0.2s;
}
.step-item:hover { color: #409eff; }
.step-item.active { background: #ecf5ff; color: #409eff; font-weight: 500; }
.step-item-static { cursor: default; }
.step-num {
  width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  border-radius: 50%;
  background: #e4e7ed;
  font-size: 12px;
}
.step-item.active .step-num { background: #409eff; color: #fff; }
.step-arrow { color: #c0c4cc; font-size: 12px; }
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.logo-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
}
.platform-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.role-tag {
  margin-left: 8px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-name {
  font-size: 14px;
  color: #303133;
}
.user-email {
  font-size: 12px;
  color: #909399;
}
.logout-btn {
  margin-left: 8px;
}
.layout-body {
  height: calc(100vh - 56px);
}
.aside {
  background: #fff;
  box-shadow: 1px 0 4px rgba(0, 0, 0, 0.06);
}
.side-menu {
  border-right: none;
  padding-top: 8px;
}
.main-content {
  padding: 20px;
  overflow: auto;
  background: #f5f7fa;
}
</style>
