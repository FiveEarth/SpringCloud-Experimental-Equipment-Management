<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">维修记录</h2>
      <p class="page-desc">
        查看全部维修/维护工单（含学生在「我的维修申请」中逻辑删除的记录）；
      </p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <span class="filter-label">进度</span>
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: progressFilter === '' }" @click="progressFilter = ''">全部</span>
        <span class="filter-tag" :class="{ active: progressFilter === '0' }" @click="progressFilter = '0'">待处理</span>
        <span class="filter-tag" :class="{ active: progressFilter === '1' }" @click="progressFilter = '1'">进行中</span>
        <span class="filter-tag" :class="{ active: progressFilter === '2' }" @click="progressFilter = '2'">已完成</span>
        <span class="filter-tag" :class="{ active: progressFilter === '4' }" @click="progressFilter = '4'">逻辑删除</span>
      </div>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
        <el-table :data="filteredList" class="page-table" border style="width: 100%" empty-text="暂无记录">
          <el-table-column prop="id" label="单号" width="72" align="center" />
          <el-table-column prop="applyUserName" label="申请人" width="100" show-overflow-tooltip />
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
          <el-table-column prop="maintainContent" label="内容说明" min-width="140" show-overflow-tooltip />
          <el-table-column prop="assignUserName" label="维修员" width="100" show-overflow-tooltip>
            <template #default="{ row }">{{ row.assignUserName || '-' }}</template>
          </el-table-column>
          <el-table-column prop="cost" label="成本(元)" width="90" align="right">
            <template #default="{ row }">{{ row.cost != null ? row.cost : '-' }}</template>
          </el-table-column>
          <el-table-column label="进度" width="120" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.progressStatus === 0" type="warning" size="small">待处理</el-tag>
              <el-tag v-else-if="row.progressStatus === 1" type="primary" size="small">进行中</el-tag>
              <el-tag v-else-if="row.progressStatus === 2" type="success" size="small">已完成</el-tag>
              <el-tooltip v-else-if="row.progressStatus === 4" :content="originalStatusHint(row)" placement="top">
                <el-tag type="info" size="small">逻辑删除</el-tag>
              </el-tooltip>
              <el-tag v-else type="info" size="small">其他</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160">
            <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="openDetail(row)">详情</el-button>
              <el-button
                v-if="row.progressStatus === 4"
                size="small"
                type="success"
                @click="doRestore(row)"
              >恢复</el-button>
              <el-button
                v-if="isAdmin"
                size="small"
                type="danger"
                plain
                @click="doHardDelete(row)"
              >彻底删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="维修单详情" width="520px">
      <template v-if="detailRow">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="单号">{{ detailRow.id }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ detailRow.applyUserName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ detailRow.maintainType === 0 ? '日常维护' : '维修' }}</el-descriptions-item>
          <el-descriptions-item label="设备名称">{{ detailRow.equipmentName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="实例ID">{{ detailRow.assetId != null ? detailRow.assetId : '-' }}</el-descriptions-item>
          <el-descriptions-item label="说明">{{ detailRow.maintainContent || '-' }}</el-descriptions-item>
          <el-descriptions-item label="维修员">{{ detailRow.assignUserName || '待指派' }}</el-descriptions-item>
          <el-descriptions-item label="成本(元)">{{ detailRow.cost != null ? detailRow.cost : '-' }}</el-descriptions-item>
          <el-descriptions-item label="进度">
            <template v-if="detailRow.progressStatus === 4">
              逻辑删除（原状态：{{ originalStatusLabel(detailRow.originalStatus) }}）
            </template>
            <template v-else>
              <el-tag v-if="detailRow.progressStatus === 0" type="warning" size="small">待处理</el-tag>
              <el-tag v-else-if="detailRow.progressStatus === 1" type="primary" size="small">进行中</el-tag>
              <el-tag v-else-if="detailRow.progressStatus === 2" type="success" size="small">已完成</el-tag>
            </template>
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
import { formatDateTime } from '../utils/format'
import { getRoles } from '../utils/auth'

export default {
  name: 'MaintainRecords',
  data() {
    return {
      list: [],
      progressFilter: '',
      detailVisible: false,
      detailRow: null
    }
  },
  computed: {
    isAdmin() {
      return (getRoles() || []).includes('ADMIN')
    },
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
    originalStatusLabel(os) {
      if (os === 0) return '待处理'
      if (os === 1) return '进行中'
      if (os === 2) return '已完成'
      return os != null ? String(os) : '—'
    },
    originalStatusHint(row) {
      if (row.progressStatus !== 4) return ''
      return '隐藏前：' + this.originalStatusLabel(row.originalStatus)
    },
    async load() {
      try {
        const r = await axios.get('/api/maintain/maintains')
        if (r.data?.code === 200) {
          this.list = r.data.data || []
        } else {
          this.list = []
          this.$message.error(r.data?.msg || '加载失败')
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
    async doRestore(row) {
      try {
        await this.$confirm('将恢复该记录在业务流程中的可见状态（按隐藏前原进度）。确定恢复？', '恢复维修记录', {
          type: 'info',
          confirmButtonText: '确定恢复',
          cancelButtonText: '取消'
        })
        const r = await axios.put(`/api/maintain/maintain/restore/${row.id}`)
        if (r.data?.code === 200) {
          this.$message.success('已恢复')
          this.load()
        } else {
          this.$message.error(r.data?.msg || '恢复失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error(e.response?.data?.msg || e.message || '恢复失败')
        }
      }
    },
    async doHardDelete(row) {
      try {
        await this.$confirm(
          '确定从数据库永久删除单号 ' + row.id + '？此操作不可恢复，且仅管理员可用。',
          '彻底删除',
          { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
        )
        const r = await axios.delete(`/api/maintain/maintain/${row.id}`)
        if (r.data?.code === 200) {
          this.$message.success('已彻底删除')
          this.load()
        } else {
          this.$message.error(r.data?.msg || '删除失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error(e.response?.data?.msg || e.message || '删除失败')
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
