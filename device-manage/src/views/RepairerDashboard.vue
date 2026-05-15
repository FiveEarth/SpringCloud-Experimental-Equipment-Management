<template>
  <div class="page repairer-dashboard">
    <header class="page-header">
      <h2 class="page-title">维修员工作台</h2>
      <p class="page-desc">查看待处理与进行中的维修任务，接单并完成维修或转报废。</p>
    </header>
    <el-card class="list-card" shadow="hover">
      <el-row :gutter="16" class="summary-row">
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-value">{{ pendingCount }}</div>
          <div class="summary-label">条</div>
          <div class="summary-desc">待处理</div>
          <div class="summary-sub">待接单的报修/检修任务</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-value">{{ inProgressCount }}</div>
          <div class="summary-label">条</div>
          <div class="summary-desc">进行中</div>
          <div class="summary-sub">已接单未完成的维修任务</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-value">{{ stats.completed }}</div>
          <div class="summary-label">条</div>
          <div class="summary-desc">本月已完成</div>
          <div class="summary-sub">统计时间范围内已完成</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" class="action-row">
      <el-col :span="12">
        <el-card shadow="hover" class="action-card" @click="$router.push('/maintain-pending')">
          <div class="action-icon">🔧</div>
          <div class="action-title">维修列表</div>
          <div class="action-desc">接单、修复完成或转报废</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="action-card" @click="$router.push('/maintain-stats')">
          <div class="action-icon">📊</div>
          <div class="action-title">维修统计</div>
          <div class="action-desc">按时间查看完成数、总成本</div>
        </el-card>
      </el-col>
    </el-row>
      <el-alert v-if="overdueCount > 0" type="warning" :title="`有 ${overdueCount} 条任务超过 24 小时未接单，请及时处理`" show-icon style="margin-top:16px" />
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'RepairerDashboard',
  data() {
    return {
      list: [],
      overdue: [],
      stats: { total: 0, completed: 0, totalCost: 0 }
    }
  },
  computed: {
    pendingCount() {
      return this.list.filter(m => m.progressStatus === 0).length
    },
    inProgressCount() {
      return this.list.filter(m => m.progressStatus === 1).length
    },
    overdueCount() {
      return this.overdue.length
    }
  },
  created() {
    this.loadList()
    this.loadOverdue()
    this.loadStats()
  },
  methods: {
    async loadList() {
      try {
        const r = await axios.get('/api/maintain/maintains/repairer')
        if (r.data?.code === 200) this.list = r.data.data || []
      } catch (e) {
        this.list = []
      }
    },
    async loadOverdue() {
      try {
        const r = await axios.get('/api/maintain/maintains/pendingOverdue')
        if (r.data?.code === 200) this.overdue = r.data.data || []
      } catch (e) {
        this.overdue = []
      }
    },
    async loadStats() {
      try {
        const end = new Date()
        const start = new Date(end.getFullYear(), end.getMonth(), 1)
        const r = await axios.get('/api/maintain/maintains/stats', {
          params: {
            startTime: start.toISOString().slice(0, 19).replace('T', ' '),
            endTime: end.toISOString().slice(0, 19).replace('T', ' ')
          }
        })
        if (r.data?.code === 200) this.stats = r.data.data || this.stats
      } catch (e) {
        // ignore
      }
    }
  }
}
</script>
<style scoped>
.repairer-dashboard { padding: 0; }
.page-header { margin-bottom: 16px; }
.page-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
.page-desc { margin: 0 0 16px 0; font-size: 13px; color: #909399; }
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
.summary-row { margin-bottom: 20px; }
.summary-card { text-align: center; cursor: default; }
.summary-value { font-size: 32px; font-weight: 700; color: #409eff; }
.summary-label { font-size: 14px; color: #909399; }
.summary-desc { font-size: 16px; margin-top: 8px; color: #303133; }
.summary-sub { font-size: 12px; color: #909399; margin-top: 4px; }
.action-row { margin-top: 16px; }
.action-card { cursor: pointer; text-align: center; }
.action-card:hover { border-color: #409eff; }
.action-icon { font-size: 40px; margin-bottom: 8px; }
.action-title { font-size: 16px; font-weight: 600; color: #303133; }
.action-desc { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
