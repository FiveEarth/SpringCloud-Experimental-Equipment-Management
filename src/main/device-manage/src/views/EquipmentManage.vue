<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">设备管理</h2>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="loadDevices">刷新设备</el-button>
      <el-button type="success" size="default" @click="openAdd">新增设备</el-button>
      <el-button type="danger" size="default" @click="batchDelete">批量删除</el-button>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="devices" class="page-table table-hover-actions" border style="width: 100%" @selection-change="selected = $event">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="deviceId" label="ID" width="80" />
      <el-table-column prop="deviceName" label="名称" width="120" show-overflow-tooltip />
      <el-table-column prop="equipmentCode" label="编号" width="140" />
      <el-table-column prop="model" label="型号" width="120" />
      <el-table-column prop="labId" label="实验室ID" width="90" />
      <el-table-column prop="labName" label="实验室" width="120" show-overflow-tooltip />
      <el-table-column prop="count" label="库存数量" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="success">{{ row.statusText || '在库' }}</el-tag>
          <el-tag v-else-if="row.status === 1" type="warning">{{ row.statusText || '领用中' }}</el-tag>
          <el-tag v-else-if="row.status === 2" type="danger">{{ row.statusText || '故障待修' }}</el-tag>
          <el-tag v-else type="info">{{ row.statusText || '已报废' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" @click="changeStatus(row, 0)">在库</el-button>
          <el-button size="small" @click="changeStatus(row, 2)">故障</el-button>
          <el-button size="small" @click="changeStatus(row, 3)">报废</el-button>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>

    <el-dialog v-model="addVisible" title="新增设备" width="480px">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="设备编号" required>
          <el-input v-model="addForm.equipment_code" placeholder="如 EQ-2024001" />
        </el-form-item>
        <el-form-item label="设备名称" required>
          <el-input v-model="addForm.equipment_name" placeholder="设备名称" />
        </el-form-item>
        <el-form-item label="型号" required>
          <el-input v-model="addForm.model" placeholder="型号" />
        </el-form-item>
        <el-form-item label="实验室" required>
          <el-select v-model="addForm.lab_id" placeholder="请选择实验室" style="width: 100%">
            <el-option v-for="lab in labs" :key="lab.id" :label="lab.labName" :value="lab.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购日期" required>
          <el-date-picker
            v-model="addForm.purchase_date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="规格参数">
          <el-input v-model="addForm.specification" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
        <el-form-item label="库存数量" required>
          <el-input-number v-model="addForm.count" :min="1" :max="9999" placeholder="数量" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="addForm.status" placeholder="状态" style="width: 100%">
            <el-option label="在库" :value="0" />
            <el-option label="领用中" :value="1" />
            <el-option label="故障待修" :value="2" />
            <el-option label="已报废" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑设备" width="520px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="设备编号" required>
          <el-input v-model="editForm.equipmentCode" placeholder="如 EQ-2024001" />
        </el-form-item>
        <el-form-item label="设备名称" required>
          <el-input v-model="editForm.deviceName" placeholder="设备名称" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="editForm.model" placeholder="型号" />
        </el-form-item>
        <el-form-item label="实验室">
          <el-select v-model="editForm.labId" placeholder="请选择实验室" style="width: 100%">
            <el-option v-for="lab in labs" :key="lab.id" :label="lab.labName || lab.lab_name" :value="lab.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购日期">
          <el-date-picker v-model="editForm.purchaseDate" type="date" value-format="YYYY-MM-DD" placeholder="选填" style="width: 100%" />
        </el-form-item>
        <el-form-item label="规格参数">
          <el-input v-model="editForm.specification" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
        <el-form-item label="说明书路径">
          <el-input v-model="editForm.manualUrl" placeholder="如 /upload/manual/xxx.pdf" />
        </el-form-item>
        <el-form-item label="可借数量" required>
          <el-input-number v-model="editForm.count" :min="0" :max="9999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="状态" style="width: 100%">
            <el-option label="在库" :value="0" />
            <el-option label="领用中" :value="1" />
            <el-option label="故障待修" :value="2" />
            <el-option label="已报废" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'EquipmentManage',
  data() {
    return {
      devices: [],
      selected: [],
      labs: [],
      addVisible: false,
      addForm: {
        equipment_code: '',
        equipment_name: '',
        model: '',
        lab_id: null,
        purchase_date: '',
        specification: '',
        status: 0,
        count: 1
      },
      editVisible: false,
      editForm: {
        deviceId: null,
        equipmentCode: '',
        deviceName: '',
        model: '',
        labId: null,
        labName: '',
        purchaseDate: '',
        specification: '',
        manualUrl: '',
        status: 0,
        count: 1
      }
    }
  },
  created() {
    this.loadDevices()
    this.loadLabs()
  },
  methods: {
    async loadLabs() {
      try {
        const r = await axios.get('/api/lab/lab/list')
        if (r.data?.code === 200) this.labs = r.data.data || []
      } catch {
        // 实验室列表加载失败时静默忽略，下拉可为空
      }
    },
    async loadDevices() {
      try {
        const r = await axios.get('/api/device/devices')
        if (r.data?.code === 200) this.devices = r.data.data || []
      } catch (e) {
        this.$message.error('加载设备失败')
      }
    },
    openAdd() {
      this.addForm = {
        equipment_code: '',
        equipment_name: '',
        model: '',
        lab_id: null,
        purchase_date: '',
        specification: '',
        status: 0,
        count: 1
      }
      this.addVisible = true
    },
    openEdit(row) {
      this.editForm = {
        deviceId: row.deviceId,
        equipmentCode: row.equipmentCode || '',
        deviceName: row.deviceName || '',
        model: row.model || '',
        labId: row.labId,
        labName: row.labName || '',
        purchaseDate: row.purchaseDate ? (typeof row.purchaseDate === 'string' ? row.purchaseDate : row.purchaseDate) : '',
        specification: row.specification || '',
        manualUrl: row.manualUrl || '',
        status: row.status,
        statusText: row.statusText || '',
        count: row.count != null ? row.count : 1
      }
      this.editVisible = true
    },
    async submitEdit() {
      if (!this.editForm.equipmentCode || !this.editForm.deviceName) {
        this.$message.warning('请填写设备编号和名称')
        return
      }
      try {
        await axios.put(`/api/device/equipment/${this.editForm.deviceId}`, this.editForm)
        this.$message.success('保存成功')
        this.editVisible = false
        this.loadDevices()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '保存失败')
      }
    },
    async submitAdd() {
      if (!this.addForm.equipment_code || !this.addForm.equipment_name || !this.addForm.model) {
        this.$message.warning('请填写设备编号、名称和型号')
        return
      }
      if (this.addForm.lab_id == null) {
        this.$message.warning('请选择实验室')
        return
      }
      if (!this.addForm.purchase_date) {
        this.$message.warning('请选择采购日期')
        return
      }
      try {
        const lab = this.labs.find(l => l.id === this.addForm.lab_id)
        const payload = [{
          ...this.addForm,
          lab_name: lab ? lab.labName || lab.lab_name : '',
          status: Number(this.addForm.status),
          count: this.addForm.count != null ? this.addForm.count : 1
        }]
        await axios.post('/api/device/equipment/batchAdd', payload)
        this.$message.success('添加成功')
        this.addVisible = false
        this.loadDevices()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '添加失败')
      }
    },
    async batchDelete() {
      const ids = this.selected.map(s => s.deviceId)
      if (!ids.length) { this.$message.warning('请勾选要删除的设备'); return }
      try {
        await axios.post('/api/device/equipment/batchDelete', ids)
        this.$message.success('删除成功')
        this.loadDevices()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '删除失败')
      }
    },
    async changeStatus(row, status) {
      try {
        await axios.put(`/api/device/equipment/${row.deviceId}/status?status=${status}`)
        this.$message.success('状态已更新')
        this.loadDevices()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '更新失败')
      }
    }
  }
}
</script>
<style scoped>
.page { padding: 0; }
.page-header { margin-bottom: 16px; }
.page-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
.page-filter {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin-bottom: 16px;
}
.table-wrap { margin-top: 0; }
.page-table :deep(.el-table__body tr) { transition: background-color 0.15s; }
.page-table :deep(.el-table__body tr:hover) { background-color: var(--el-table-row-hover-bg-color, #f5f7fa); }
.table-hover-actions :deep(.el-table__body .el-table__cell:last-child .cell) {
  opacity: 0;
  transition: opacity 0.2s;
}
.table-hover-actions :deep(.el-table__body tr:hover .el-table__cell:last-child .cell) { opacity: 1; }
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
</style>
