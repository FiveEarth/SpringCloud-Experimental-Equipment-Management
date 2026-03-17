<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">实验室管理</h2>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <el-button type="primary" size="default" @click="showAdd">批量添加</el-button>
      <el-button type="danger" size="default" @click="batchDelete">批量删除</el-button>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="list" class="page-table" border style="width: 100%" @selection-change="selected = $event">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="labName" label="名称" />
      <el-table-column prop="labLocation" label="位置" />
      <el-table-column prop="labManager" label="管理员" />
      <el-table-column prop="status" label="状态" width="80" />
    </el-table>
      </div>
    </el-card>
    <el-dialog v-model="addVisible" title="批量添加实验室">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="addForm.labName" placeholder="实验室名称" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="addForm.labLocation" placeholder="位置" /></el-form-item>
        <el-form-item label="管理员"><el-input v-model="addForm.labManager" /></el-form-item>
        <el-form-item label="状态"><el-input-number v-model="addForm.status" :min="0" :max="1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'Labs',
  data() {
    return { list: [], selected: [], addVisible: false, addForm: { labName: '', labLocation: '', labManager: '', status: 1 } }
  },
  created() { this.load() },
  methods: {
    async load() {
      const r = await axios.get('/api/lab/lab/list')
      if (r.data?.code === 200) this.list = r.data.data || []
    },
    showAdd() { this.addForm = { labName: '', labLocation: '', labManager: '', status: 1 }; this.addVisible = true },
    async submitAdd() {
      await axios.post('/api/lab/lab/batchAdd', [{ ...this.addForm }])
      this.$message.success('添加成功')
      this.addVisible = false
      this.load()
    },
    async batchDelete() {
      const ids = this.selected.map(s => s.id)
      if (!ids.length) { this.$message.warning('请勾选要删除的实验室'); return }
      await axios.post('/api/lab/lab/batchDelete', ids)
      this.$message.success('删除成功')
      this.load()
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
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
</style>
