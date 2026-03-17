<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">我的预约</h2>
      <p class="page-desc">只管理预约生命周期：提交预约后等待审批，已通过后请到「我的领用」页面完成领用操作。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="loadData">刷新</el-button>
      <el-button type="success" size="default" @click="openAddReserve">新建预约</el-button>
      <span class="filter-label">状态</span>
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: statusFilter === '' }" @click="statusFilter = ''">全部</span>
        <span class="filter-tag" :class="{ active: statusFilter === '0' }" @click="statusFilter = '0'">待审批</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'pending' }" @click="statusFilter = 'pending'">已通过(待领用)</span>
        <span class="filter-tag" :class="{ active: statusFilter === '2' }" @click="statusFilter = '2'">已驳回/已取消</span>
      </div>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="filteredList" class="page-table table-hover-actions" border style="width: 100%" empty-text="暂无预约记录">
      <el-table-column prop="equipmentName" label="设备名称" width="140" show-overflow-tooltip />
      <el-table-column prop="reserveQuantity" label="数量" width="72" align="center">
        <template #default="{ row }">{{ row.reserveQuantity != null ? row.reserveQuantity : 1 }}</template>
      </el-table-column>
      <el-table-column prop="reserveDate" label="日期" width="120" />
      <el-table-column prop="purpose" label="预约用途" width="140" show-overflow-tooltip />
      <el-table-column label="开始时间" width="160">
        <template #default="{ row }">{{ formatReserveTime(row.reserveDate, row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="结束时间" width="160">
        <template #default="{ row }">{{ formatReserveTime(row.reserveDate, row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="140">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待审批</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已通过(待领用)</el-tag>
          <el-tag v-else-if="row.status === 2" type="info">已驳回/已取消</el-tag>
          <el-tag v-else type="info">已结束</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">查看详情</el-button>
          <el-button v-if="row.status === 0" size="small" @click="openEdit(row)">修改</el-button>
          <el-button v-if="row.status === 0 || row.status === 1" size="small" type="warning" @click="cancelReserve(row)">撤销</el-button>
          <el-button v-if="row.status === 2" size="small" type="info" @click="softDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>

    <el-dialog v-model="editVisible" title="修改预约">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="editForm.endTime"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="请选择结束时间"
          />
          <!-- 添加换行 -->
        </el-form-item>
        <el-form-item label="预约用途">
          <el-input v-model="editForm.purpose" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addReserveVisible" title="新建预约" width="480px">
      <el-form :model="addReserveForm" label-width="100px">
        <el-form-item label="设备" required>
          <el-select v-model="addReserveForm.equipmentId" filterable placeholder="请选择设备" style="width:100%">
            <el-option v-for="d in devices" :key="d.deviceId" :label="(d.deviceName || '设备#' + d.deviceId) + ' (' + (d.equipmentCode || '') + ')'" :value="d.deviceId" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期" required>
          <el-date-picker v-model="addReserveForm.reserveDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" :disabled-date="disabledReserveDate" />
          <span class="form-tip">仅可选今天起 7 天内</span>
        </el-form-item>
        <el-form-item label="开始时段" required>
          <el-time-picker v-model="addReserveForm.startTime" value-format="HH:mm:ss" format="HH:mm" placeholder="开始时间" style="width:100%" :disabled-hours="disabledStartHours" :disabled-minutes="disabledStartMinutes" />
          <span v-if="isReserveDateToday" class="form-tip">预约日期为今天时，开始时间不能早于当前时间</span>
        </el-form-item>
        <el-form-item label="结束时段" required>
          <el-time-picker v-model="addReserveForm.endTime" value-format="HH:mm:ss" format="HH:mm" placeholder="结束时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="预约数量" required>
          <el-input-number v-model="addReserveForm.reserveQuantity" :min="1" :max="addReserveMaxQuantity" placeholder="1" style="width:100%" />
          <span v-if="addReserveForm.equipmentId" class="form-tip">可选 1～{{ addReserveMaxQuantity }}（设备总数）</span>
        </el-form-item>
        <el-form-item label="预约用途">
          <el-input v-model="addReserveForm.purpose" type="textarea" :rows="2" placeholder="选填，与领用用途含义一致" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addReserveVisible = false">取消</el-button>
        <el-button type="primary" :loading="addReserveLoading" @click="submitAddReserve">提交预约</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="预约详情" width="480px">
      <template v-if="detailRow">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="设备名称">{{ detailRow.equipmentName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="数量">{{ detailRow.reserveQuantity != null ? detailRow.reserveQuantity : 1 }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ detailRow.reserveDate }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatReserveTime(detailRow.reserveDate, detailRow.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatReserveTime(detailRow.reserveDate, detailRow.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="预约用途">{{ detailRow.purpose || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="detailRow.status === 0" type="warning">待审批</el-tag>
            <el-tag v-else-if="detailRow.status === 1" type="success">已通过(待领用)</el-tag>
            <el-tag v-else-if="detailRow.status === 2" type="info">已驳回/已取消</el-tag>
            <el-tag v-else type="info">已结束</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="detailRow.status === 1" class="detail-guide">
          <p class="guide-text">请前往「我的领用」页面完成领用操作。</p>
          <el-button type="primary" @click="goToMyApply(detailRow)">去我的领用</el-button>
        </div>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { getUserId, getUserName } from '../utils/auth'
import { formatDateTime, formatReserveTime, buildEndDatetimeForPicker, toBackendDateTime } from '../utils/format'
// import { el } from 'element-plus/es/locale';

export default {
  name: 'MyReserve',
  data() {
    return {
      list: [],
      statusFilter: '',
      devices: [],
      addReserveVisible: false,
      addReserveLoading: false,
      addReserveForm: {
        equipmentId: null,
        equipmentName: '',
        reserveDate: '',
        startTime: '',
        endTime: ''
      },
      editVisible: false,
      editForm: {
        id: null,
        reserveDate: '',
        endTime: '',
        purpose: ''
      },
      detailVisible: false,
      detailRow: null
    }
  },
  computed: {
    /** 只显示预约生命周期：待审批、已通过(待领用)、已驳回/已取消；不显示已领用、已完成 */
    filteredList() {
      const list = (this.list || []).filter(r => r.status !== 4 && r.status !== 3 && !(r.status === 1 && r.isUsed === 1))
      if (!this.statusFilter) return list
      if (this.statusFilter === '0') return list.filter(r => r.status === 0)
      if (this.statusFilter === 'pending') return list.filter(r => r.status === 1)
      if (this.statusFilter === '2') return list.filter(r => r.status === 2)
      return list
    },
    addReserveMaxQuantity() {
      const id = this.addReserveForm?.equipmentId
      if (!id) return 1
      const d = this.devices.find(x => x.deviceId === id)
      if (!d) return 1
      const n = d.totalCount ?? d.count ?? 0
      return Math.max(1, Number(n) || 1)
    },
    isReserveDateToday() {
      const d = this.addReserveForm?.reserveDate
      if (!d) return false
      const t = new Date()
      const todayStr = `${t.getFullYear()}-${String(t.getMonth() + 1).padStart(2, '0')}-${String(t.getDate()).padStart(2, '0')}`
      return String(d).slice(0, 10) === todayStr
    }
  },
  created() {
    this.loadData()
    this.loadDevices()
  },
  methods: {
    formatDateTime,
    formatReserveTime,
    buildEndDatetimeForPicker,
    toBackendDateTime,
    async loadDevices() {
      try {
        const r = await axios.get('/api/device/devices', { params: { borrowable: true } })
        if (r.data?.code === 200) this.devices = r.data.data || []
      } catch (e) {
        this.devices = []
      }
    },
    getTodayStr() {
      const t = new Date()
      return `${t.getFullYear()}-${String(t.getMonth() + 1).padStart(2, '0')}-${String(t.getDate()).padStart(2, '0')}`
    },
    disabledReserveDate(date) {
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const d = new Date(date)
      d.setHours(0, 0, 0, 0)
      if (d < today) return true
      const max = new Date(today)
      max.setDate(max.getDate() + 7)
      return d > max
    },
    disabledStartHours() {
      if (!this.isReserveDateToday) return []
      const now = new Date()
      const hours = []
      for (let i = 0; i < now.getHours(); i++) hours.push(i)
      return hours
    },
    disabledStartMinutes(selectedHour) {
      if (!this.isReserveDateToday) return []
      const now = new Date()
      if (selectedHour !== now.getHours()) return []
      const minutes = []
      for (let i = 0; i <= now.getMinutes(); i++) minutes.push(i)
      return minutes
    },
    openAddReserve() {
      this.addReserveForm = {
        equipmentId: null,
        equipmentName: '',
        reserveDate: this.getTodayStr(),
        startTime: '',
        endTime: '',
        purpose: '',
        reserveQuantity: 1
      }
      this.addReserveVisible = true
    },
    async submitAddReserve() {
      const userId = getUserId()
      if (!userId) {
        this.$message.error('请先登录')
        return
      }
      const f = this.addReserveForm
      if (!f.equipmentId || !f.reserveDate || !f.startTime || !f.endTime) {
        this.$message.warning('请填写设备、预约日期、开始时段和结束时段')
        return
      }
      const d = this.devices.find(x => x.deviceId === f.equipmentId)
      const equipmentName = d ? (d.deviceName || '') : (f.equipmentName || '')
      const dateStr = typeof f.reserveDate === 'string' ? f.reserveDate : (f.reserveDate && f.reserveDate.toISOString ? f.reserveDate.toISOString().slice(0, 10) : '')
      const startStr = (typeof f.startTime === 'string' ? f.startTime : '09:00:00').slice(0, 8)
      const endStr = (typeof f.endTime === 'string' ? f.endTime : '11:00:00').slice(0, 8)
      const startTime = dateStr + ' ' + startStr
      const endTime = dateStr + ' ' + endStr
      if (endStr <= startStr) {
        this.$message.warning('结束时段须晚于开始时段')
        return
      }
      const todayStr = this.getTodayStr()
      if (dateStr === todayStr) {
        const now = new Date()
        const [sh, sm] = [parseInt(startStr.slice(0, 2), 10), parseInt(startStr.slice(3, 5), 10)]
        if (sh < now.getHours() || (sh === now.getHours() && sm <= now.getMinutes())) {
          this.$message.warning('预约日期为今天时，开始时间不能早于或等于当前时间')
          return
        }
      }
      if (new Date(dateStr + ' 00:00:00') < new Date(todayStr + ' 00:00:00')) {
        this.$message.warning('预约日期不能早于今天')
        return
      }
      const maxDate = new Date(todayStr)
      maxDate.setDate(maxDate.getDate() + 7)
      if (new Date(dateStr) > maxDate) {
        this.$message.warning('预约日期不得超过今天起 7 天')
        return
      }
      this.addReserveLoading = true
      try {
        const resp = await axios.post('/api/reserve/createReserve', {
          equipmentId: Number(f.equipmentId),
          equipmentName,
          userId: Number(userId),
          userName: getUserName() || '',
          reserveDate: dateStr,
          startTime,
          endTime,
          status: 0,
          purpose: (f.purpose && String(f.purpose).trim()) || null,
          reserveQuantity: Math.max(1, Math.min(this.addReserveMaxQuantity, Number(f.reserveQuantity) || 1))
        })
        if (resp.data?.code === 200) {
          this.$message.success('预约申请已提交，请等待管理员或教师审批')
          this.addReserveVisible = false
          this.loadData()
        } else {
          this.$message.error(resp.data?.msg || '提交失败')
        }
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '提交预约失败')
      } finally {
        this.addReserveLoading = false
      }
    },
    async loadData() {
      const userId = getUserId()
      if (!userId) {
        this.$message.error('未获取到用户信息，请重新登录')
        this.$router.push('/login')
        return
      }
      try {
        const resp = await axios.get('/api/reserve/queryReserveByUserId', {
          params: { userId }
        })
        if (resp.data && resp.data.code === 200) {
          this.list = resp.data.data || []
        } else {
          this.$message.error(resp.data?.msg || '加载预约失败')
        }
      } catch (e) {
        this.$message.error('加载预约失败')
      }
    },
    openEdit(row) {
      this.editForm.id = row.id
      this.editForm.reserveDate = row.reserveDate
      this.editForm.endTime = this.buildEndDatetimeForPicker(row.reserveDate, row.endTime)
      this.editForm.purpose = row.purpose != null ? row.purpose : ''
      this.editVisible = true
    },
    async submitEdit() {
      try {
        await axios.put(`/api/reserve/modifyReserve/${this.editForm.id}`, {
          id: this.editForm.id,
          endTime: this.toBackendDateTime(this.editForm.endTime),
          purpose: this.editForm.purpose != null ? String(this.editForm.purpose).trim() : null
        })
        this.$message.success('修改成功')
        this.editVisible = false
        this.loadData()
      } catch (e) {
        this.$message.error('修改失败')
      }
    },
    async cancelReserve(row) {
      try {
        await axios.put(`/api/reserve/modifyReserve/${row.id}`, {
          id: row.id,
          status: 2
        })
        this.$message.success('已撤销')
        this.loadData()
      } catch (e) {
        this.$message.error('撤销失败')
      }
    },
    async softDelete(row) {
      try {
        await this.$confirm('确定删除该条预约记录？', '删除', { type: 'warning' })
        await axios.put(`/api/reserve/modifyReserve/${row.id}`, {
          id: row.id,
          status: 4
        })
        this.$message.success('已删除')
        this.detailVisible = false
        this.loadData()
      } catch (e) {
        if (e !== 'cancel') this.$message.error('删除失败')
      }
    },
    openDetail(row) {
      this.detailRow = { ...row }
      this.detailVisible = true
    },
    /** 关闭详情并跳转到「我的领用」，定位到该预约的待领用记录 */
    goToMyApply(row) {
      this.detailVisible = false
      this.$router.push({ path: '/my-apply', query: { reserveId: row.id, fromReserve: '1' } })
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
.filter-label { color: #303133; font-size: 14px; font-weight: 500; }
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
  opacity: 1;
  transition: opacity 0.2s;
} */
/* .table-hover-actions :deep(.el-table__body tr:hover .el-table__cell:last-child .cell) { opacity: 1; } */
.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
.form-tip { margin-left: 8px; color: #909399; font-size: 12px; }
.detail-guide { margin-top: 16px; padding: 12px; background: #ecf5ff; border-radius: 8px; }
.detail-guide .guide-text { margin: 0 0 12px 0; color: #409eff; font-size: 14px; }
</style>

