<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">维修申请</h2>
      <p class="page-desc">查看您提交的维修/维护工单进度；新报修请在「我的领用」中对借用中的设备发起。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <span class="filter-label">进度</span>
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: progressFilter === '' }" @click="progressFilter = ''">全部</span>
        <span class="filter-tag" :class="{ active: progressFilter === '0' }" @click="progressFilter = '0'">待处理</span>
        <span class="filter-tag" :class="{ active: progressFilter === '1' }" @click="progressFilter = '1'">进行中</span>
        <span class="filter-tag" :class="{ active: progressFilter === '2' }" @click="progressFilter = '2'">已完成</span>
      </div>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
        <el-table :data="filteredList" class="page-table" border style="width: 100%" empty-text="暂无维修记录">
          <el-table-column prop="id" label="单号" width="72" align="center" />
          <el-table-column label="类型" width="88" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.maintainType === 0" type="info" size="small">日常维护</el-tag>
              <el-tag v-else type="warning" size="small">维修</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="equipmentName" label="设备名称" min-width="120" show-overflow-tooltip />
          <el-table-column prop="assetId" label="实例ID" width="88" align="center">
            <template #default="{ row }">{{ row.assetId != null ? row.assetId : '-' }}</template>
          </el-table-column>
          <el-table-column prop="maintainContent" label="内容说明" min-width="160" show-overflow-tooltip />
          <el-table-column prop="assignUserName" label="维修员" width="100" show-overflow-tooltip>
            <template #default="{ row }">{{ row.assignUserName || '-' }}</template>
          </el-table-column>
          <el-table-column prop="cost" label="成本(元)" width="90" align="right">
            <template #default="{ row }">{{ row.cost != null ? row.cost : '-' }}</template>
          </el-table-column>
          <el-table-column label="进度" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.progressStatus === 0" type="warning" size="small">待处理</el-tag>
              <el-tag v-else-if="row.progressStatus === 1" type="primary" size="small">进行中</el-tag>
              <el-tag v-else type="success" size="small">已完成</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160">
            <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="320" fixed="right" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="openDetail(row)">查看详情</el-button>
              <el-button size="small" @click="copyId(row)">复制单号</el-button>
              <el-button
                v-if="canRevoke(row)"
                size="small"
                type="warning"
                @click="doRevoke(row)"
              >撤销</el-button>
              <el-button
                v-if="canSoftHide(row)"
                size="small"
                type="danger"
                plain
                @click="doSoftHide(row)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="维修单详情" width="520px">
      <template v-if="detailRow">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="单号">{{ detailRow.id }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ detailRow.maintainType === 0 ? '日常维护' : '维修' }}</el-descriptions-item>
          <el-descriptions-item label="设备名称">{{ detailRow.equipmentName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="实例ID">{{ detailRow.assetId != null ? detailRow.assetId : '-' }}</el-descriptions-item>
          <el-descriptions-item label="说明">{{ detailRow.maintainContent || '-' }}</el-descriptions-item>
          <el-descriptions-item label="维修员">{{ detailRow.assignUserName || '待指派' }}</el-descriptions-item>
          <el-descriptions-item label="成本(元)">{{ detailRow.cost != null ? detailRow.cost : '-' }}</el-descriptions-item>
          <el-descriptions-item label="进度">
            <el-tag v-if="detailRow.progressStatus === 0" type="warning" size="small">待处理</el-tag>
            <el-tag v-else-if="detailRow.progressStatus === 1" type="primary" size="small">进行中</el-tag>
            <el-tag v-else type="success" size="small">已完成</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(detailRow.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(detailRow.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessageBox } from 'element-plus'
import { formatDateTime } from '../utils/format'

export default {
  name: 'StudentMaintain',
  data() {
    return {
      list: [],
      progressFilter: '',
      detailVisible: false,
      detailRow: null
    }
  },
  computed: {
    filteredList() {
      if (this.progressFilter === '') return this.list
      const p = Number(this.progressFilter)
      return (this.list || []).filter((r) => r.progressStatus === p)
    }
  },
  created() {
    this.load()
  },
  methods: {
    formatDateTime,
    async load() {
      try {
        const r = await axios.get('/api/maintain/maintains/mine')
        if (r.data?.code === 200) {
          this.list = r.data.data || []
        } else {
          this.list = []
        }
      } catch (e) {
        this.list = []
        this.$message.error(e.response?.data?.msg || e.message || '加载失败')
      }
    },
    openDetail(row) {
      this.detailRow = row
      this.detailVisible = true
    },
    copyId(row) {
      const t = String(row.id)
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(t).then(() => this.$message.success('已复制单号')).catch(() => this.$message.info('单号：' + t))
      } else {
        this.$message.info('单号：' + t)
      }
    },
    /** 未接单：待处理且无指派维修员 */
    canRevoke(row) {
      return row && row.progressStatus === 0 && (row.assignUserId == null || row.assignUserId === '')
    },
    /** 已完成：从我的列表软隐藏，后台仍可查 */
    canSoftHide(row) {
      return row && row.progressStatus === 2
    },
    async doRevoke(row) {
      try {
        await ElMessageBox.confirm('撤销后维修单将不再出现在待处理队列，关联设备将恢复为在库。确定撤销？', '撤销维修申请', {
          type: 'warning',
          confirmButtonText: '确定撤销',
          cancelButtonText: '取消'
        })
        const r = await axios.post(`/api/maintain/maintain/${row.id}/applicant-revoke`)
        if (r.data?.code === 200) {
          this.$message.success('已撤销')
          this.load()
        } else {
          this.$message.error(r.data?.msg || '撤销失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error(e.response?.data?.msg || e.message || '撤销失败')
        }
      }
    },
    async doSoftHide(row) {
      try {
        await ElMessageBox.confirm('将从「我的维修申请」中隐藏该记录；管理员与教师仍可在后台查看。确定删除？', '隐藏已完成记录', {
          type: 'warning',
          confirmButtonText: '确定隐藏',
          cancelButtonText: '取消'
        })
        const r = await axios.post(`/api/maintain/maintain/${row.id}/applicant-soft-hide`)
        if (r.data?.code === 200) {
          this.$message.success('已隐藏')
          this.load()
        } else {
          this.$message.error(r.data?.msg || '操作失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error(e.response?.data?.msg || e.message || '操作失败')
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
.filter-label { font-size: 13px; color: #606266; }
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
</style>
