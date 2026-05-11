<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">设备列表</h2>
      <p class="page-desc">维修员可查看设备状态与库存信息，辅助接单与故障排查。</p>
    </header>

    <div class="page-filter">
      <el-button type="primary" size="default" @click="loadDevices">刷新</el-button>
    </div>

    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
        <el-table :data="devices" class="page-table" border style="width: 100%" empty-text="暂无设备数据">
          <el-table-column prop="deviceName" label="设备名称" min-width="140" show-overflow-tooltip />
          <el-table-column prop="equipmentCode" label="设备编号" width="140" />
          <el-table-column prop="model" label="型号" width="120" />
          <el-table-column prop="labName" label="实验室" width="120" show-overflow-tooltip />
          <el-table-column label="可借数量" width="100" align="center">
            <template #default="{ row }">{{ row.count != null ? row.count : 0 }}</template>
          </el-table-column>
          <el-table-column label="设备总数" width="100" align="center">
            <template #default="{ row }">{{ row.totalCount != null ? row.totalCount : '-' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0" type="success">{{ row.statusText || '在库' }}</el-tag>
              <el-tag v-else-if="row.status === 1" type="warning">{{ row.statusText || '领用中' }}</el-tag>
              <el-tag v-else-if="row.status === 2" type="danger">{{ row.statusText || '故障待修' }}</el-tag>
              <el-tag v-else type="info">{{ row.statusText || '已报废' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'RepairerDeviceList',
  data() {
    return {
      devices: []
    }
  },
  created() {
    this.loadDevices()
  },
  methods: {
    async loadDevices() {
      try {
        const resp = await axios.get('/api/device/devices')
        if (resp.data?.code === 200) this.devices = resp.data.data || []
        else this.$message.error(resp.data?.msg || '加载设备失败')
      } catch (e) {
        this.$message.error('加载设备失败')
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
