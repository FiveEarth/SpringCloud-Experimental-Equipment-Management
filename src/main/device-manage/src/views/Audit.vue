<template>
  <div class="page">
    <h2 class="page-title">日志审计</h2>
    <p class="page-desc">按设备、用户、时间范围查询预约、维修、领用记录，便于追溯与统计。</p>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="预约审计" name="reserve">
        <el-form inline>
          <el-form-item label="设备ID"><el-input v-model="params.equipmentId" placeholder="可选" clearable style="width:100px" /></el-form-item>
          <el-form-item label="用户ID"><el-input v-model="params.userId" placeholder="可选" clearable style="width:100px" /></el-form-item>
          <el-form-item label="开始"><el-date-picker v-model="params.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
          <el-form-item label="结束"><el-date-picker v-model="params.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
          <el-form-item><el-button type="primary" @click="loadReserve">查询</el-button></el-form-item>
        </el-form>
        <el-table :data="reserveList" border style="margin-top:12px" empty-text="暂无预约记录">
          <el-table-column prop="equipmentName" label="设备名称" width="140" show-overflow-tooltip />
          <el-table-column prop="userName" label="预约人" width="100" />
          <el-table-column prop="reserveDate" label="日期" width="120" />
          <el-table-column label="开始" width="160"><template #default="{ row }">{{ formatDateTime(row.startTime) }}</template></el-table-column>
          <el-table-column label="结束" width="160"><template #default="{ row }">{{ formatDateTime(row.endTime) }}</template></el-table-column>
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0" type="warning" size="small">待审批</el-tag>
              <el-tag v-else-if="row.status === 1" type="success" size="small">待领用</el-tag>
              <el-tag v-else-if="row.status === 2" type="info" size="small">已取消</el-tag>
              <el-tag v-else-if="row.status === 3" type="success" size="small">已完成</el-tag>
              <el-tag v-else-if="row.status === 4" type="danger" size="small">已隐藏</el-tag>
              <el-tag v-else-if="row.status === 5" type="danger" size="small">已删除</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button v-if="row.status === 4" size="small" type="success" @click="restoreReserve(row)">恢复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="维修审计" name="maintain">
        <el-form inline>
          <el-form-item label="设备ID"><el-input v-model="params.equipmentId" clearable style="width:100px" /></el-form-item>
          <el-form-item label="用户ID"><el-input v-model="params.userId" clearable style="width:100px" /></el-form-item>
          <el-form-item label="申请人"><el-input v-model="params.applyUserName" clearable style="width:120px" /></el-form-item>
          <el-form-item label="维修员"><el-input v-model="params.maintainUserName" clearable style="width:100px" /></el-form-item>
          <el-form-item label="开始"><el-date-picker v-model="params.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
          <el-form-item label="结束"><el-date-picker v-model="params.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
          <el-form-item><el-button type="primary" @click="loadMaintain">查询</el-button></el-form-item>
        </el-form>
        <el-table :data="maintainList" border style="margin-top:12px">
          <el-table-column prop="equipmentName" label="设备名称" width="140" show-overflow-tooltip />
          <el-table-column prop="applyUserName" label="申请人" width="120" />
          <el-table-column prop="maintainContent" label="内容" show-overflow-tooltip min-width="120" />
          <el-table-column prop="progressStatus" label="进度" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.progressStatus === 0" type="warning" size="small">待处理</el-tag>
              <el-tag v-else-if="row.progressStatus === 1" type="primary" size="small">进行中</el-tag>
              <el-tag v-else-if="row.progressStatus === 4" type="info" size="small">已隐藏</el-tag>
              <el-tag v-else type="success" size="small">已完成</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button v-if="row.progressStatus === 4" size="small" type="success" @click="restoreMaintain(row)">恢复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="领用审计" name="apply">
        <el-form inline>
          <el-form-item label="设备ID"><el-input v-model="params.equipmentId" clearable style="width:100px" /></el-form-item>
          <el-form-item label="用户ID"><el-input v-model="params.userId" clearable style="width:100px" /></el-form-item>
          <el-form-item label="开始"><el-date-picker v-model="params.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
          <el-form-item label="结束"><el-date-picker v-model="params.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
          <el-form-item><el-button type="primary" @click="loadApply">查询</el-button></el-form-item>
        </el-form>
        <el-table :data="applyList" border style="margin-top:12px" empty-text="暂无领用记录">
          <el-table-column prop="equipment_name" label="设备名称" width="140" show-overflow-tooltip />
          <el-table-column prop="user_name" label="申请人" width="120" />
          <el-table-column label="申请时间" width="160"><template #default="{ row }">{{ formatDateTime(row.apply_time) }}</template></el-table-column>
          <el-table-column prop="approve_status" label="审批状态" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.approve_status === 0" type="warning" size="small">待审批</el-tag>
              <el-tag v-else-if="row.approve_status === 1" type="success" size="small">已通过</el-tag>
              <el-tag v-else-if="row.approve_status === 2" type="danger" size="small">已驳回</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button v-if="row.status === 4" size="small" type="success" @click="restoreApply(row)">恢复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import axios from 'axios'
import { formatDateTime } from '../utils/format'
// import { el } from 'element-plus/es/locale';
export default {
  name: 'Audit',
  data() {
    return {
      activeTab: 'reserve',
      params: { equipmentId: '', userId: '', startTime: '', endTime: '' },
      reserveList: [], maintainList: [], applyList: []
    }
  },
  methods: {
    formatDateTime,
    async loadReserve() {
      const r = await axios.get('/api/reserve/audit', { params: this.params })
      if (r.data?.code === 200) this.reserveList = r.data.data || []
    },
    async loadMaintain() {
      const r = await axios.get('/api/maintain/maintains/audit', { params: this.params })
      if (r.data?.code === 200) this.maintainList = r.data.data || []
    },
    async loadApply() {
      const r = await axios.get('/api/apply/apply/audit', { params: this.params })
      if (r.data?.code === 200) this.applyList = r.data.data || []
    },
    async restoreReserve(row) {
      try {
        await axios.put(`/api/reserve/restore/${row.id}`)
        this.$message.success('已恢复')
        this.loadReserve()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || e.message || '恢复失败')
      }
    },
    async restoreMaintain(row) {
      try {
        await axios.put(`/api/maintain/maintain/restore/${row.id}`)
        this.$message.success('已恢复')
        this.loadMaintain()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || e.message || '恢复失败')
      }
    },
    async restoreApply(row) {
      try {
        await axios.put(`/api/apply/apply/restore/${row.id}`)
        this.$message.success('已恢复')
        this.loadApply()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || e.message || '恢复失败')
      }
    }
  }
}
</script>
<style scoped>
.page { padding: 0; }
.page-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
.page-desc { margin: 0 0 16px 0; font-size: 13px; color: #909399; }
</style>
