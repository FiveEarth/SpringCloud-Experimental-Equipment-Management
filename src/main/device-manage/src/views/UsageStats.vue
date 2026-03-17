<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">使用统计</h2>
      <p class="page-desc">按条件查询预约使用情况，可切换按设备或按用户查看统计结果。</p>
    </header>
    <div class="page-filter">
      <el-form inline class="filter-inline">
        <el-form-item label="设备ID"><el-input v-model="equipmentId" placeholder="可选" clearable style="width:120px" /></el-form-item>
        <el-form-item label="用户ID"><el-input v-model="userId" placeholder="可选" clearable style="width:120px" /></el-form-item>
        <el-form-item label="开始"><el-date-picker v-model="startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="开始时间" /></el-form-item>
        <el-form-item label="结束"><el-date-picker v-model="endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="结束时间" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="loading" @click="load">查询</el-button></el-form-item>
      </el-form>
    </div>
    <el-card class="list-card" shadow="hover">
      <el-descriptions title="汇总" :column="1" border class="summary">
        <el-descriptions-item label="总条数">{{ stats.total }}</el-descriptions-item>
      </el-descriptions>
      <div class="stats-section">
        <div class="stats-toggle">
          <span class="toggle-label">统计维度：</span>
          <el-radio-group v-model="statsMode" size="default">
            <el-radio-button label="equipment">按设备统计</el-radio-button>
            <el-radio-button label="user">按用户统计</el-radio-button>
          </el-radio-group>
        </div>
        <el-table v-show="statsMode === 'equipment'" :data="stats.byEquipment" class="page-table" border max-height="400" empty-text="暂无数据">
          <el-table-column prop="equipmentId" label="设备ID" width="90" />
          <el-table-column prop="equipmentName" label="设备名称" show-overflow-tooltip />
          <el-table-column prop="count" label="预约次数" width="110" align="center" />
        </el-table>
        <el-table v-show="statsMode === 'user'" :data="stats.byUser" class="page-table" border max-height="400" empty-text="暂无数据">
          <el-table-column prop="userId" label="用户ID" width="90" />
          <el-table-column prop="userName" label="用户姓名" show-overflow-tooltip />
          <el-table-column prop="count" label="预约次数" width="110" align="center" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'UsageStats',
  data() {
    return {
      equipmentId: '',
      userId: '',
      startTime: '',
      endTime: '',
      loading: false,
      statsMode: 'equipment',
      stats: { total: 0, byEquipment: [], byUser: [] }
    }
  },
  created() {
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const r = await axios.get('/api/reserve/usageStats', {
          params: { equipmentId: this.equipmentId || undefined, userId: this.userId || undefined, startTime: this.startTime || undefined, endTime: this.endTime || undefined }
        })
        if (r.data?.code === 200) this.stats = r.data.data || { total: 0, byEquipment: [], byUser: [] }
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '查询失败，请稍后重试')
      } finally {
        this.loading = false
      }
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
.summary { margin-bottom: 16px; }
.stats-section { margin-top: 16px; }
.stats-toggle { margin-bottom: 12px; display: flex; align-items: center; gap: 12px; }
.toggle-label { color: #606266; font-size: 14px; }
.page-table :deep(.el-table__body tr) { transition: background-color 0.15s; }
.page-table :deep(.el-table__body tr:hover) { background-color: var(--el-table-row-hover-bg-color, #f5f7fa); }
</style>
