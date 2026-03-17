<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">预约审批</h2>
      <p class="page-desc">审批学生的预约申请，通过后学生可在「我的预约」中点击「领用」直接领用，无需再提交领用申请，领用记录将出现在学生的「我的领用」中。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <!-- <span class="filter-label">状态</span> -->
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: statusFilter === '0' }" @click="statusFilter = '0'">待确认</span>
        <span class="filter-tag" :class="{ active: statusFilter === '' }" @click="statusFilter = ''">全部</span>
        <span class="filter-tag" :class="{ active: statusFilter === '1' }" @click="statusFilter = '1'">已确认</span>
        <span class="filter-tag" :class="{ active: statusFilter === '2' }" @click="statusFilter = '2'">已取消</span>
        <!-- <span class="filter-tag" :class="{ active: statusFilter === '3' }" @click="statusFilter = '3'">已完成</span> -->
        <span class="filter-tag" :class="{ active: statusFilter === '4' }" @click="statusFilter = '4'">逻辑删除</span>
      </div>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="filteredList" class="page-table table-hover-actions" border style="width: 100%" empty-text="暂无预约记录">
      <el-table-column prop="equipmentName" label="设备名称" width="140" show-overflow-tooltip />
      <el-table-column label="数量" width="72" align="center">
        <template #default="{ row }">{{ row.reserveQuantity != null ? row.reserveQuantity : 1 }}</template>
      </el-table-column>
      <el-table-column prop="userName" label="预约人" width="100" />
      <el-table-column prop="reserveDate" label="预约日期" width="120" />
      <el-table-column prop="purpose" label="预约用途" width="120" show-overflow-tooltip />
      <el-table-column label="开始时段" width="160">
        <template #default="{ row }">{{ formatReserveTime(row.reserveDate, row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="结束时段" width="160">
        <template #default="{ row }">{{ formatReserveTime(row.reserveDate, row.endTime) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待确认</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已确认</el-tag>
          <el-tag v-else-if="row.status === 2" type="info">已取消</el-tag>
          <el-tag v-else-if="row.status === 3" type="success">已完成</el-tag>
          <el-tag v-else-if="row.status === 4" type="info">逻辑删除</el-tag>
          <el-tag v-else type="info">-</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="success" @click="approve(row, 1)">通过</el-button>
          <el-button v-if="row.status === 0" size="small" type="danger" @click="approve(row, 2)">驳回</el-button>
          <el-button v-if="row.status === 1 || row.status === 2" size="small" type="info" @click="softDelete(row)">删除</el-button>
          <el-button v-if="row.status === 4" size="small" type="success" @click="restore(row)">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
import { formatDateTime, formatReserveTime } from '../utils/format'
import { getRoles, getSafeHeaderValue } from '../utils/auth'
// import ElTableColumn from 'element-plus/es/components/table/src/tableColumn';

export default {
  name: 'ReserveApprove',
  data() {
    return { list: [], statusFilter: '0' }
  },
  computed: {
    filteredList() {
      if (!this.statusFilter) return this.list
      const s = parseInt(this.statusFilter, 10)
      if (Number.isNaN(s)) return this.list
      return this.list.filter(row => row.status === s)
    }
  },
  created() {
    this.load()
  },
  methods: {
    formatDateTime,
    formatReserveTime,
    async load() {
      try {
        const r = await axios.get('/api/reserve/queryAllReserve')
        if (r.data && r.data.code === 200) this.list = r.data.data || []
        else this.list = []
      } catch (e) {
        this.list = []
        this.$message.error(e.response?.data?.msg || e.message || '加载预约列表失败')
      }
    },
    async approve(row, status) {
      const roles = getRoles()
      const rolesStr = Array.isArray(roles) ? roles.join(',') : (roles || '')
      try {
        await axios.put('/api/reserve/approve/' + row.id + '?status=' + status, null, {
          headers: { 'X-Roles': getSafeHeaderValue(rolesStr) }
        })
        this.$message.success(status === 1 ? '已通过' : '已驳回')
        this.load()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || e.message || '操作失败')
      }
    },
    async softDelete(row) {
      try {
        await axios.put(`/api/reserve/modifyReserve/${row.id}`, {
          id: row.id,
          status: 4
        })
        this.$message.success('已删除（已隐藏）')
        this.load()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || e.message || '删除失败')
      }
    },
    async restore(row) {
      try {
        await axios.put(`/api/reserve/restore/${row.id}`)
        this.$message.success('已恢复')
        this.load()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || e.message || '恢复失败')
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
.page-filter {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  padding: 14px 20px;
  border: 1px solid #F0F2F5;
  border-radius: 8px;
  background-color: #FFFFFF;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  margin-bottom: 20px;
}
.filter-tags { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.filter-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  color: #64748B;
  background: #F8F9FA;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}
.filter-tag:hover { background: #E9ECEF; color: #303133; }
.filter-tag.active { background: var(--el-color-primary); color: #fff; }
.table-wrap { margin-top: 0; }
.page-table :deep(.el-table__body tr) { transition: background-color 0.15s; }
.page-table :deep(.el-table__body tr:hover) { background-color: var(--el-table-row-hover-bg-color, #f5f7fa); }
/* .table-hover-actions :deep(.el-table__body .el-table__cell:last-child .cell) {
  opacity: 0;
  transition: opacity 0.2s;
} */
/* .table-hover-actions :deep(.el-table__body tr:hover .el-table__cell:last-child .cell) { opacity: 1; }
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; } */
</style>
