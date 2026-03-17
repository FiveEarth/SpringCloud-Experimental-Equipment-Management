<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">用户管理</h2>
      <p class="page-desc">管理系统用户与角色，支持新增、编辑、重置密码。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <el-button type="success" size="default" @click="openAdd">新增用户</el-button>
      <el-checkbox v-model="includeDisabled">含已禁用</el-checkbox>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="list" class="page-table" border style="width: 100%">
      <el-table-column prop="username" label="账号" width="120" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="phone" label="手机" width="120" />
      <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">正常</el-tag>
          <el-tag v-else type="info">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roleIds" label="角色" width="120">
        <template #default="{ row }">{{ formatRoles(row.roleIds) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" @click="openResetPwd(row)">重置密码</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>
    <el-dialog v-model="addVisible" title="新增用户" width="480px">
      <el-form :model="addForm" label-width="90px">
        <el-form-item label="账号" required><el-input v-model="addForm.username" placeholder="登录账号" /></el-form-item>
        <el-form-item label="密码" required><el-input v-model="addForm.password" type="password" placeholder="初始密码" show-password /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="addForm.realName" placeholder="真实姓名" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="addForm.phone" placeholder="手机号" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="addForm.email" placeholder="邮箱" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="addForm.roleIds" multiple placeholder="选择角色" style="width:100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="editVisible" title="编辑用户" width="480px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="账号"><el-input v-model="editForm.username" disabled /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="editForm.realName" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width:100%">
            <el-option label="正常" :value="1" /><el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.roleIds" multiple style="width:100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="pwdVisible" title="重置密码" width="360px">
      <el-form :model="pwdForm" label-width="90px">
        <el-form-item label="用户">{{ pwdForm.username }}</el-form-item>
        <el-form-item label="新密码" required><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResetPwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import axios from 'axios'
import { formatDateTime } from '../utils/format'
export default {
  name: 'UserManage',
  data() {
    return {
      list: [], roles: [], includeDisabled: false,
      addVisible: false, editVisible: false, pwdVisible: false,
      addForm: { username: '', password: '', realName: '', phone: '', email: '', roleIds: [] },
      editForm: { id: null, username: '', realName: '', phone: '', email: '', status: 1, roleIds: [] },
      pwdForm: { id: null, username: '', newPassword: '' }
    }
  },
  watch: { includeDisabled() { this.load() } },
  created() { this.loadRoles(); this.load() },
  methods: {
    formatDateTime,
    formatRoles(roleIds) {
      if (!roleIds || !roleIds.length) return '-'
      const map = { 1: '管理员', 2: '教师', 3: '学生', 4: '维修师傅' }
      return roleIds.map(id => map[id] || id).join('、')
    },
    async loadRoles() {
      const r = await axios.get('/api/user/roles')
      if (r.data?.code === 200) this.roles = r.data.data || []
    },
    async load() {
      const r = await axios.get('/api/user/users', { params: { includeDisabled: this.includeDisabled } })
      if (r.data?.code === 200) this.list = r.data.data || []
    },
    openAdd() { this.addForm = { username: '', password: '', realName: '', phone: '', email: '', roleIds: [] }; this.addVisible = true },
    async submitAdd() {
      if (!this.addForm.username?.trim()) { this.$message.warning('请输入账号'); return }
      if (!this.addForm.password?.trim()) { this.$message.warning('请输入密码'); return }
      try {
        await axios.post('/api/user/users', this.addForm)
        this.$message.success('新增成功'); this.addVisible = false; this.load()
      } catch (e) { this.$message.error(e.response?.data?.msg || '新增失败') }
    },
    openEdit(row) {
      this.editForm = { id: row.id, username: row.username, realName: row.realName, phone: row.phone, email: row.email, status: row.status, roleIds: row.roleIds ? [...row.roleIds] : [] }
      this.editVisible = true
    },
    async submitEdit() {
      try {
        await axios.put('/api/user/users/' + this.editForm.id, this.editForm)
        this.$message.success('保存成功'); this.editVisible = false; this.load()
      } catch (e) { this.$message.error(e.response?.data?.msg || '保存失败') }
    },
    openResetPwd(row) { this.pwdForm = { id: row.id, username: row.username, newPassword: '' }; this.pwdVisible = true },
    async submitResetPwd() {
      if (!this.pwdForm.newPassword?.trim()) { this.$message.warning('请输入新密码'); return }
      try {
        await axios.post('/api/user/users/' + this.pwdForm.id + '/reset-password', { newPassword: this.pwdForm.newPassword })
        this.$message.success('密码已重置'); this.pwdVisible = false
      } catch (e) { this.$message.error(e.response?.data?.msg || '重置失败') }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除用户 ' + row.username + ' ？', '提示', { type: 'warning' })
        await axios.delete('/api/user/users/' + row.id)
        this.$message.success('已删除'); this.load()
      } catch (e) { if (e !== 'cancel') this.$message.error(e.response?.data?.msg || '删除失败') }
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
