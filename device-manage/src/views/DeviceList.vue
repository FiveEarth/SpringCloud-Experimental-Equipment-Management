<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">设备列表</h2>
      <p class="page-desc">仅显示在库且可借数量大于 0 的设备；领用审批通过后数量将扣减，归还确认后加回。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="loadDevices">刷新</el-button>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="devices" class="page-table" border style="width: 100%" empty-text="暂无可借设备或请刷新">
      <el-table-column prop="deviceName" label="设备名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="equipmentCode" label="编号" width="140" />
      <el-table-column prop="model" label="型号" width="120" />
      <el-table-column prop="labName" label="实验室" width="120" show-overflow-tooltip />
      <el-table-column label="可借数量" width="100" align="center">
        <template #default="{ row }">
          <strong>{{ row.count != null ? row.count : 0 }}</strong>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="success">{{ row.statusText || '在库' }}</el-tag>
          <el-tag v-else-if="row.status === 1" type="warning">{{ row.statusText || '领用中' }}</el-tag>
          <el-tag v-else-if="row.status === 2" type="danger">{{ row.statusText || '故障待修' }}</el-tag>
          <el-tag v-else type="info">{{ row.statusText || '已报废' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" type="info" @click="openDeviceDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="设备详情" width="520px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="设备编号">{{ detailRow?.equipmentCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ detailRow?.deviceName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ detailRow?.model || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实验室">{{ detailRow?.labName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="采购日期">{{ detailRow?.purchaseDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格参数">{{ detailRow?.specification || '-' }}</el-descriptions-item>
        <!-- <el-descriptions-item label="说明书路径"> -->
          <!-- <template v-if="detailRow?.manualUrl">
            <a :href="detailRow.manualUrl" target="_blank" rel="noreferrer">打开说明书</a>
          </template>
          <template v-else>-</template>
        </el-descriptions-item> -->
        <el-descriptions-item label="状态">{{ detailRow?.statusText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="可借数量">{{ detailRow?.count != null ? detailRow.count : '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备总数">{{ detailRow?.totalCount != null ? detailRow.totalCount : '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'DeviceList',
  data() {
    return {
      devices: [],
      detailVisible: false,
      detailRow: null
    }
  },
  created() {
    this.loadDevices()
  },
  methods: {
    async loadDevices() {
      try {
        const resp = await axios.get('/api/device/devices', { params: { borrowable: true } })
        if (resp.data && resp.data.code === 200) {
          this.devices = resp.data.data || []
        } else {
          this.$message.error(resp.data?.msg || '加载设备失败')
        }
      } catch (e) {
        this.$message.error('加载设备失败')
      }
    },
    openDeviceDetail(row) {
      this.detailRow = { ...(row || {}) }
      this.detailVisible = true
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
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin-bottom: 16px;
}
.table-wrap { margin-top: 0; }
.page-table :deep(.el-table__body tr) { transition: background-color 0.15s; }
.page-table :deep(.el-table__body tr:hover) { background-color: var(--el-table-row-hover-bg-color, #f5f7fa); }
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
</style>

