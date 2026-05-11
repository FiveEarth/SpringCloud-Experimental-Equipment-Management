<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">维修列表</h2>
      <p class="page-desc">接单后可在本列表完成维修并填写成本；「无法修复」仅适用于<strong>带设备实例</strong>的维修单（由领用记录发起报修）。超过 24 小时未接单将显示在下方提醒中。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: statusFilter === 'pending' }" @click="statusFilter = 'pending'">待维修</span>
        <span class="filter-tag" :class="{ active: statusFilter === '' }" @click="statusFilter = ''">全部</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'done' }" @click="statusFilter = 'done'">已完成</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'hidden' }" @click="statusFilter = 'hidden'">已隐藏</span>
      </div>
      <el-alert v-if="overdue.length" type="warning" :closable="false" style="margin: 0;">
        <template #title>24 小时未接单：{{ overdue.length }} 条</template>
      </el-alert>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="filteredList" class="page-table" border style="width: 100%" empty-text="暂无维修记录">
      <el-table-column prop="id" label="ID" width="64" align="center" />
      <el-table-column prop="maintainType" label="类型" width="88" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.maintainType === 0" type="info" size="small">日常维护</el-tag>
          <el-tag v-else type="warning" size="small">维修</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="equipmentName" label="设备名称" min-width="120" show-overflow-tooltip />
      <el-table-column label="实例ID" width="88" align="center">
        <template #default="{ row }">{{ row.assetId != null ? row.assetId : '-' }}</template>
      </el-table-column>
      <el-table-column prop="applyUserName" label="申请人" width="100" show-overflow-tooltip />
      <el-table-column prop="maintainContent" label="维修/维护内容" min-width="160" show-overflow-tooltip />
      <el-table-column label="维修时间" width="140">
        <template #default="{ row }">{{ formatDateTime(row.maintainTime) }}</template>
      </el-table-column>
      <el-table-column prop="cost" label="成本(元)" width="90" align="right">
        <template #default="{ row }">{{ row.cost != null ? row.cost : '-' }}</template>
      </el-table-column>
      <el-table-column prop="progressStatus" label="进度" width="90" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.progressStatus === 0" type="warning" size="small">待处理</el-tag>
          <el-tag v-else-if="row.progressStatus === 1" type="primary" size="small">进行中</el-tag>
          <el-tag v-else-if="row.progressStatus === 2" type="success" size="small">已完成</el-tag>
          <el-tag v-else-if="row.progressStatus === 4" type="info" size="small">已隐藏</el-tag>
          <el-tag v-else type="info" size="small">-</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="140">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right" align="center">
        <template #default="{ row }">
          <el-button v-if="row.progressStatus === 0" size="small" type="primary" @click="accept(row)">接单</el-button>
          <el-button v-if="row.progressStatus === 0" size="small" @click="reject(row)">拒绝</el-button>
          <el-button v-if="row.progressStatus === 1" size="small" type="success" @click="complete(row, true)">修复完成</el-button>
          <el-tooltip v-if="row.progressStatus === 1" :disabled="canScrap(row)" content="仅维修类且已绑定设备实例的单据可转报废；请从领用记录发起报修。" placement="top">
            <span style="display:inline-block;margin-left:8px;">
              <el-button size="small" type="danger" :disabled="!canScrap(row)" @click="complete(row, false)">无法修复</el-button>
            </span>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>
    <el-dialog v-model="rejectVisible" title="拒绝理由">
      <el-input v-model="rejectReason" type="textarea" placeholder="选填" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="completeVisible" :title="completeSuccess ? '修复完成' : '无法修复（转报废）'" width="480px">
      <el-form label-width="112px">
        <el-form-item v-if="!completeSuccess" label="说明">
          <el-input v-model="completeRemark" type="textarea" :rows="2" placeholder="选填，将作为报废原因" />
        </el-form-item>
        <el-form-item v-if="!completeSuccess" label="报废残值(元)">
          <el-input-number v-model="completeResidualValue" :min="0" :precision="2" :step="100" style="width:100%" />
          <div class="form-hint">写入报废单「残值」，与下方维修成本不同</div>
        </el-form-item>
        <el-form-item label="维修成本(元)">
          <el-input-number v-model="completeCost" :min="0" :precision="2" :step="10" style="width:100%" />
          <div v-if="!completeSuccess" class="form-hint">记入本单维修记录，不写入报废残值</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { formatDateTime } from '../utils/format'
import { getUserId, getUserName, getSafeHeaderValue } from '../utils/auth'
export default {
  name: 'MaintainPending',
  data() {
    return {
      list: [],
      statusFilter: 'pending',
      overdue: [],
      rejectVisible: false,
      rejectReason: '',
      rejectId: null,
      completeVisible: false,
      completeSuccess: true,
      completeRemark: '',
      completeCost: 0,
      completeRow: null
    }
  },
  computed: {
    filteredList() {
      const list = this.list || []
      if (!this.statusFilter) return list
      if (this.statusFilter === 'pending') return list.filter(r => r.progressStatus === 0 || r.progressStatus === 1)
      if (this.statusFilter === 'done') return list.filter(r => r.progressStatus === 2)
      if (this.statusFilter === 'hidden') return list.filter(r => r.progressStatus === 4)
      return list
    }
  },
  created() { this.load(); this.loadOverdue() },
  methods: {
    /** 与后端一致：仅维修类且含 assetId 可走报废 */
    canScrap(row) {
      return row && row.maintainType === 1 && row.assetId != null
    },
    formatDateTime,
    async load() {
      try {
        const r = await axios.get('/api/maintain/maintains/repairer')
        if (r.data?.code === 200) this.list = r.data.data || []
        else this.list = []
      } catch (e) {
        this.list = []
        this.$message.error(e.response?.data?.msg || e.message || '加载失败')
      }
    },
    async loadOverdue() {
      const r = await axios.get('/api/maintain/maintains/pendingOverdue')
      if (r.data?.code === 200) this.overdue = r.data.data || []
    },
    async accept(row) {
      const headers = {
        'X-User-Id': getUserId() || '',
        'X-User-Name': getSafeHeaderValue(getUserName() || '')
      }
      await axios.post(`/api/maintain/maintain/${row.id}/accept`, null, { headers })
      this.$message.success('接单成功')
      this.load(); this.loadOverdue()
    },
    reject(row) { this.rejectId = row.id; this.rejectReason = ''; this.rejectVisible = true },
    async submitReject() {
      const headers = { 'X-User-Id': getUserId() || '' }
      await axios.post(`/api/maintain/maintain/${this.rejectId}/reject`, null, { params: { reason: this.rejectReason }, headers })
      this.$message.success('已拒绝')
      this.rejectVisible = false
      this.load(); this.loadOverdue()
    },
    complete(row, success) {
      this.completeRow = row
      this.completeSuccess = success
      this.completeRemark = ''
      this.completeCost = success ? 0 : 10
      this.completeResidualValue = 0
      this.completeVisible = true
    },
    async submitComplete() {
      try {
        const payload = {
          success: this.completeSuccess,
          cost: this.completeCost,
          maintainContent: this.completeRemark || (this.completeSuccess ? '修复完成' : '维修无法修复，转报废')
        }
        if (!this.completeSuccess) {
          payload.residualValue = this.completeResidualValue
        }
        await axios.post(`/api/maintain/maintain/${this.completeRow.id}/complete`, payload)
        this.$message.success(this.completeSuccess ? '设备已恢复正常' : '已提交报废申请')
        this.completeVisible = false
        this.load(); this.loadOverdue()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '操作失败')
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
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
.form-hint { font-size: 12px; color: #909399; line-height: 1.4; margin-top: 4px; }
</style>
