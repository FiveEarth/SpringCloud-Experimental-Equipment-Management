<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">报废审批</h2>
      <p class="page-desc">审批设备报废申请，通过后设备状态将更新为已报废。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <!-- <span class="filter-label">状态</span> -->
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: tab === 'pending' }" @click="tab = 'pending'">待审批</span>
        <span class="filter-tag" :class="{ active: tab === 'all' }" @click="tab = 'all'">全部</span>
      </div>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="list" class="page-table table-hover-actions" border style="width: 100%" empty-text="暂无报废记录">
      <el-table-column prop="id" label="ID" width="72" align="center" />
      <el-table-column prop="equipmentName" label="设备名称" width="140" show-overflow-tooltip />
      <el-table-column prop="equipmentId" label="设备ID" width="90" align="center" />
      <el-table-column prop="assetId" label="实例ID" width="90" align="center">
        <template #default="{ row }">{{ row.assetId != null ? row.assetId : '-' }}</template>
      </el-table-column>
      <el-table-column prop="scrapReason" label="报废原因" min-width="140" show-overflow-tooltip />
      <el-table-column prop="residualValue" label="残值" width="100" align="right">
        <template #default="{ row }">{{ row.residualValue != null ? row.residualValue : '-' }}</template>
      </el-table-column>
      <el-table-column prop="applyUserName" label="申请人" width="90" />
      <el-table-column prop="approvalUserName" label="审批人" width="90">
        <template #default="{ row }">{{ row.approvalUserName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="disposalMethod" label="处置方式" width="120" show-overflow-tooltip>
        <template #default="{ row }">{{ row.disposalMethod || '-' }}</template>
      </el-table-column>
      <el-table-column label="处置时间" width="160">
        <template #default="{ row }">{{ row.disposalTime ? formatDateTime(row.disposalTime) : '-' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ row.createTime ? formatDateTime(row.createTime) : '-' }}</template>
      </el-table-column>
      <el-table-column prop="approvalStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.approvalStatus === 0" type="warning">待审批</el-tag>
          <el-tag v-else-if="row.approvalStatus === 1" type="success">已通过</el-tag>
          <el-tag v-else type="danger">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <template v-if="row.approvalStatus === 0">
            <el-button size="small" type="success" @click="approve(row, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="approve(row, 2)">驳回</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>
    <el-dialog v-model="disposalVisible" title="通过报废（选填处置方式）" width="400px">
      <el-input v-model="disposalMethod" placeholder="如：回收处理、销毁" />
      <template #footer>
        <el-button @click="disposalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprove(1)">确定通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { formatDateTime } from '../utils/format'
export default {
  name: 'ScrapApprove',
  data() {
    return {
      list: [],
      tab: 'pending',
      disposalVisible: false,
      disposalMethod: '',
      approveRow: null,
      approveStatus: null,
      equipment_name: '',
      approval_user_name: '',
      apply_user_name: '',
    }
  },
  watch: { tab() { this.load() } },
  created() { this.load() },
  methods: {
    formatDateTime,
    async load() {
      try {
        const url = this.tab === 'pending' ? '/api/scrap/scraps/pending' : '/api/scrap/scraps'
        const r = await axios.get(url)
        if (r.data?.code === 200) this.list = r.data.data || []
        else this.list = []
      } catch (e) {
        this.list = []
        this.$message.error(e.response?.data?.msg || e.message || '加载报废列表失败')
      }
    },
    approve(row, status) {
      this.approveRow = row
      this.approveStatus = status
      if (status === 1) {
        this.disposalMethod = ''
        this.disposalVisible = true
      } else {
        this.submitApprove(2)
      }
    },
    async submitApprove(status) {
      try {
        await axios.post(`/api/scrap/scraps/${this.approveRow.id}/approve`, {
          status,
          disposalMethod: status === 1 ? this.disposalMethod : null
        })
        this.$message.success(status === 1 ? '已通过，设备已报废' : '已驳回')
        this.disposalVisible = false
        this.load()
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
/* .table-hover-actions :deep(.el-table__body .el-table__cell:last-child .cell) {
  opacity: 0;
  transition: opacity 0.2s;
}
.table-hover-actions :deep(.el-table__body tr:hover .el-table__cell:last-child .cell) { opacity: 1; } */
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
</style>
