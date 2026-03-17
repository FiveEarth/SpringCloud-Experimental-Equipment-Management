<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">归还审批</h2>
      <p class="page-desc">学生提交归还申请后在此批准。点击「确认归还」通过归还申请，可选设备正常/故障，故障将置为故障待修并加回库存。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: statusFilter === 'returning' }" @click="statusFilter = 'returning'">待审批</span>
        <span class="filter-tag" :class="{ active: statusFilter === '' }" @click="statusFilter = ''">全部</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'using' }" @click="statusFilter = 'using'">领用中</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'returned' }" @click="statusFilter = 'returned'">已归还</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'hidden' }" @click="statusFilter = 'hidden'">已隐藏</span>
      </div>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="filteredList" class="page-table table-hover-actions" border style="width: 100%" empty-text="暂无记录">
      <el-table-column prop="equipment_name" label="设备名称" width="140" show-overflow-tooltip />
      <el-table-column label="实例编号" width="140">
        <template #default="{ row }">{{ row.asset_code || '-' }}</template>
      </el-table-column>
      <el-table-column prop="user_name" label="申请人" width="100" />
      <el-table-column label="领用时间" width="160">
        <template #default="{ row }">{{ formatDateTime(row.use_time) }}</template>
      </el-table-column>
      <el-table-column label="申请归还时间" width="160">
        <template #default="{ row }">{{ formatDateTime(row.return_apply_time) }}</template>
      </el-table-column>
      <el-table-column prop="purpose" label="用途" show-overflow-tooltip />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.status === 4" type="info" size="small">已隐藏</el-tag>
          <el-tag v-else-if="row.return_status === 2" type="success" size="small">已归还</el-tag>
          <el-tag v-else-if="row.return_status === 1" type="warning" size="small">待归还审批</el-tag>
          <el-tag v-else type="primary" size="small">领用中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.status === 4" size="small" type="primary" @click="restoreApply(row)">恢复</el-button>
          <el-button v-if="row.return_status === 1 && row.status !== 4" size="small" type="success" @click="openApproveReturn(row)">确认归还</el-button>
          <el-button v-if="row.return_status === 1 && row.status !== 4" size="small" type="danger" @click="openRejectReturn(row)">驳回归还</el-button>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>

    <el-dialog v-model="returnVisible" title="确认归还" width="400px">
      <el-form label-width="100px">
        <el-form-item label="归还时设备">
          <el-radio-group v-model="returnForm.equipmentStatus">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">故障（将置为故障待修）</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApproveReturn">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rejectVisible" title="驳回归还" width="400px">
      <el-input v-model="rejectRemarks" type="textarea" :rows="3" placeholder="选填驳回原因" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRejectReturn">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { formatDateTime } from '../utils/format'
import { getUserName } from '../utils/auth'
export default {
  name: 'ApplyApprove',
  data() {
    return {
      list: [],
      statusFilter: 'returning',
      rejectVisible: false,
      rejectRemarks: '',
      rejectRow: null,
      returnVisible: false,
      returnRow: null,
      returnForm: { equipmentStatus: 0 }
    }
  },
  computed: {
    /** 按状态筛选：全部/待审批/领用中/已归还为已通过且非隐藏；已隐藏为 status=4 */
    filteredList() {
      const list = this.list || []
      if (this.statusFilter === 'hidden') {
        return list.filter(r => r.status === 4)
      }
      if (this.statusFilter === 'returning') {
        return list.filter(r => r.approve_status === 1 && r.return_status === 1)
      }
      const approved = list.filter(r => r.approve_status === 1 && r.status !== 4)
      if (!this.statusFilter) return approved
      if (this.statusFilter === 'using') return approved.filter(r => r.return_status !== 1 && r.return_status !== 2)
      if (this.statusFilter === 'returned') return approved.filter(r => r.return_status === 2)
      return approved
    }
  },
  created() {
    this.load()
  },
  methods: {
    formatDateTime,
    async load() {
      try {
        const r = await axios.get('/api/apply/applies')
        if (r.data?.code === 200) this.list = r.data.data || []
        else this.list = []
      } catch (e) {
        this.list = []
        this.$message.error(e.response?.data?.msg || e.message || '加载列表失败')
      }
    },
    openApproveReturn(row) {
      this.returnRow = row
      this.returnForm.equipmentStatus = 0
      this.returnVisible = true
    },
    async submitApproveReturn() {
      try {
        await axios.post('/api/apply/apply/return/approve/' + this.returnRow.id, {
          approved: true,
          equipmentStatus: this.returnForm.equipmentStatus,
          approvalUserName: getUserName() || ''
        })
        this.$message.success('已确认归还，库存已加回')
        this.returnVisible = false
        this.load()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '操作失败')
      }
    },
    openRejectReturn(row) {
      this.rejectRow = row
      this.rejectRemarks = ''
      this.rejectVisible = true
    },
    async submitRejectReturn() {
      try {
        await axios.post('/api/apply/apply/return/approve/' + this.rejectRow.id, {
          approved: false,
          remarks: this.rejectRemarks,
          approvalUserName: getUserName() || ''
        })
        this.$message.success('已驳回归还')
        this.rejectVisible = false
        this.load()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '操作失败')
      }
    },
    /** 恢复软删除的领用记录（将 status 恢复为 original_status） */
    async restoreApply(row) {
      try {
        await this.$confirm('确定恢复该记录？恢复后将重新显示在学生的「我的领用」中。', '恢复', { type: 'info' })
        await axios.put('/api/apply/apply/restore/' + row.id)
        this.$message.success('已恢复')
        this.load()
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error(e.response?.data?.msg || '恢复失败')
        }
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
.filter-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
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
/* .table-hover-actions :deep(.el-table__body tr:hover .el-table__cell:last-child .cell) { opacity: 1; } */
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
</style>
