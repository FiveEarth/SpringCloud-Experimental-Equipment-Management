<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">实验室管理</h2>
      <p class="page-desc">支持实验室新增、编辑、启停与批量删除。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <el-button type="primary" size="default" @click="showAdd">新增实验室</el-button>
      <el-button type="danger" size="default" :disabled="!selected.length" @click="batchDelete">批量删除</el-button>
      <el-input
        v-model.trim="keyword"
        placeholder="搜索名称/位置/管理员"
        clearable
        style="width: 280px"
      />
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: statusFilter === '' }" @click="statusFilter = ''">全部</span>
        <span class="filter-tag" :class="{ active: statusFilter === '1' }" @click="statusFilter = '1'">启用</span>
        <span class="filter-tag" :class="{ active: statusFilter === '0' }" @click="statusFilter = '0'">停用</span>
      </div>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="filteredList" class="page-table" border style="width: 100%" @selection-change="selected = $event">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="labName" label="名称" />
      <el-table-column prop="labLocation" label="位置" />
      <el-table-column prop="labManager" label="管理员" />
      <el-table-column label="状态" width="110" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="快捷操作" width="120" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="Number(row.status) === 1"
            active-text="启用"
            inactive-text="停用"
            @change="toggleStatus(row, $event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-button type="primary" link @click="showEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>
    <el-dialog v-model="addVisible" title="新增实验室">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="addForm.labName" placeholder="实验室名称" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="addForm.labLocation" placeholder="位置" /></el-form-item>
        <el-form-item label="管理员"><el-input v-model="addForm.labManager" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="addForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑实验室">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="editForm.labName" placeholder="实验室名称" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="editForm.labLocation" placeholder="位置" /></el-form-item>
        <el-form-item label="管理员"><el-input v-model="editForm.labManager" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
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
  name: 'Labs',
  data() {
    return {
      list: [],
      selected: [],
      keyword: '',
      statusFilter: '',
      addVisible: false,
      editVisible: false,
      addForm: { labName: '', labLocation: '', labManager: '', status: 1 },
      editForm: { id: null, labName: '', labLocation: '', labManager: '', status: 1 }
    }
  },
  computed: {
    filteredList() {
      let rows = this.list || []
      if (this.statusFilter !== '') rows = rows.filter(r => String(r.status) === this.statusFilter)
      if (!this.keyword) return rows
      const k = this.keyword.toLowerCase()
      return rows.filter(r =>
        String(r.labName || '').toLowerCase().includes(k) ||
        String(r.labLocation || '').toLowerCase().includes(k) ||
        String(r.labManager || '').toLowerCase().includes(k)
      )
    }
  },
  created() { this.load() },
  methods: {
    statusText(status) {
      return Number(status) === 1 ? '启用' : '停用'
    },
    statusTagType(status) {
      return Number(status) === 1 ? 'success' : 'info'
    },
    async load() {
      try {
        const r = await axios.get('/api/lab/lab/list')
        if (r.data?.code === 200) this.list = r.data.data || []
        else this.$message.error(r.data?.msg || '加载实验室失败')
      } catch (e) {
        this.$message.error('加载实验室失败')
      }
    },
    showAdd() { this.addForm = { labName: '', labLocation: '', labManager: '', status: 1 }; this.addVisible = true },
    showEdit(row) {
      this.editForm = {
        id: row.id,
        labName: row.labName || '',
        labLocation: row.labLocation || '',
        labManager: row.labManager || '',
        status: Number(row.status) === 1 ? 1 : 0
      }
      this.editVisible = true
    },
    async submitAdd() {
      if (!this.addForm.labName?.trim()) {
        this.$message.warning('请填写实验室名称')
        return
      }
      await axios.post('/api/lab/lab/batchAdd', [{ ...this.addForm }])
      this.$message.success('添加成功')
      this.addVisible = false
      this.load()
    },
    async submitEdit() {
      if (!this.editForm.id) return
      if (!this.editForm.labName?.trim()) {
        this.$message.warning('请填写实验室名称')
        return
      }
      await axios.put(`/api/lab/lab/${this.editForm.id}`, {
        labName: this.editForm.labName,
        labLocation: this.editForm.labLocation,
        labManager: this.editForm.labManager,
        status: this.editForm.status
      })
      this.$message.success('更新成功')
      this.editVisible = false
      this.load()
    },
    async toggleStatus(row, enabled) {
      const nextStatus = enabled ? 1 : 0
      await axios.put(`/api/lab/lab/${row.id}`, {
        labName: row.labName,
        labLocation: row.labLocation,
        labManager: row.labManager,
        status: nextStatus
      })
      this.$message.success(nextStatus === 1 ? '已启用' : '已停用')
      row.status = nextStatus
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
.page-desc { margin: 0; font-size: 13px; color: #909399; }
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
.filter-tags { display: inline-flex; gap: 8px; margin-left: 4px; }
.filter-tag {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  color: #64748B;
  background: #F8F9FA;
  cursor: pointer;
}
.filter-tag.active { background: var(--el-color-primary); color: #fff; }
</style>
