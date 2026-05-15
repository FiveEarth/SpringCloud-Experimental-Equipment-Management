<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">维修统计</h2>
      <p class="page-desc">按时间范围查询维修任务数量与总成本。</p>
    </header>
    <div class="page-filter">
      <el-form inline class="filter-inline">
        <el-form-item label="开始"><el-date-picker v-model="startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="开始时间" /></el-form-item>
        <el-form-item label="结束"><el-date-picker v-model="endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="结束时间" /></el-form-item>
        <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
      </el-form>
    </div>
    <el-card class="list-card" shadow="hover">
      <el-descriptions title="维修统计" :column="1" border>
        <el-descriptions-item label="总条数">{{ stats.total }}</el-descriptions-item>
        <el-descriptions-item label="已完成">{{ stats.completed }}</el-descriptions-item>
        <el-descriptions-item label="总成本">{{ stats.totalCost }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'MaintainStats',
  data() { return { startTime: '', endTime: '', stats: { total: 0, completed: 0, totalCost: 0 } } },
  created() { this.load() },
  methods: {
    async load() {
      const r = await axios.get('/api/maintain/maintains/stats', { params: { startTime: this.startTime || undefined, endTime: this.endTime || undefined } })
      if (r.data?.code === 200) this.stats = r.data.data || this.stats
    }
  }
}
</script>
<style scoped>
.page { padding: 0; }
.page-header { margin-bottom: 16px; }
.page-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
.page-desc { margin: 0 0 16px 0; font-size: 13px; color: #909399; }
.page-filter { padding: 12px 0; border-bottom: 1px solid var(--el-border-color-lighter); margin-bottom: 16px; }
.filter-inline { margin: 0; }
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
</style>
