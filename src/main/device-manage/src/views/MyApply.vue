<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">我的领用</h2>
      <p class="page-desc">只管理领用/归还生命周期：待领用记录在此点击「领用」开始使用；领用中可申请归还，审批通过后完成归还。</p>
    </header>
    <div class="page-filter">
      <el-button type="primary" size="default" @click="load">刷新</el-button>
      <span class="filter-label">状态</span>
      <div class="filter-tags">
        <span class="filter-tag" :class="{ active: statusFilter === '' }" @click="statusFilter = ''">全部</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'pending' }" @click="statusFilter = 'pending'">待领用</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'using' }" @click="statusFilter = 'using'">领用中</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'returning' }" @click="statusFilter = 'returning'">待归还审批</span>
        <span class="filter-tag" :class="{ active: statusFilter === 'returned' }" @click="statusFilter = 'returned'">已归还</span>
      </div>
    </div>
    <el-card class="list-card" shadow="hover">
      <div class="table-wrap">
      <el-table :data="filteredMergedList" class="page-table table-hover-actions" border style="width: 100%" empty-text="暂无记录" :row-class-name="tableRowClassName">
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <span v-if="row._fromReserve">预约(待领用)</span>
          <span v-else>{{ row.apply_type === 1 ? '归还' : '领用' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="equipment_name" label="设备名称" width="140" show-overflow-tooltip />
      <el-table-column label="数量" width="72" align="center">
        <template #default="{ row }">{{ row._fromReserve ? (row.reserveQuantity != null ? row.reserveQuantity : 1) : (row.apply_quantity != null ? row.apply_quantity : 1) }}</template>
      </el-table-column>
      <el-table-column label="实例编号" width="140">
        <template #default="{ row }">{{ row._fromReserve ? '待领用后分配' : (row.asset_code || '-') }}</template>
      </el-table-column>
      <el-table-column label="预约时段" width="180">
        <template #default="{ row }">
          <span v-if="row._fromReserve">
            <template v-if="row.reserveDate || row.startTime || row.endTime">
              {{ formatReserveTime(row.reserveDate, row.startTime) }}～{{ formatReserveTime(row.reserveDate, row.endTime) }}
            </template>
            <span v-else>—</span>
          </span>
          <span v-else-if="row.reserveDate && (row.startTime != null || row.endTime != null)">
            {{ formatReserveTime(row.reserveDate, row.startTime) }}～{{ formatReserveTime(row.reserveDate, row.endTime) }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="领用时间" width="160">
        <template #default="{ row }">{{ row._fromReserve ? '-' : formatDateTime(row.use_time) }}</template>
      </el-table-column>
      <el-table-column prop="purpose" label="用途" show-overflow-tooltip />
      <el-table-column label="状态" width="140">
        <template #default="{ row }">
          <template v-if="row._fromReserve">
            <el-tag type="success">待领用</el-tag>
          </template>
          <template v-else>
            <el-tag v-if="row.approve_status === 0" type="warning">{{ row.status_text || '待审批' }}</el-tag>
            <el-tag v-else-if="row.approve_status === 2" type="danger">{{ row.status_text || '已驳回' }}</el-tag>
            <template v-else-if="row.approve_status === 1">
              <el-tag v-if="row.return_status === 2" type="success">已归还</el-tag>
              <el-tag v-else-if="row.return_status === 1" type="warning">待归还审批</el-tag>
              <el-tag v-else type="success">领用中</el-tag>
            </template>
            <el-tag v-else type="info">-</el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="归还时间" width="160">
        <template #default="{ row }">{{ row._fromReserve ? '-' : formatDateTime(row.return_time) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <template v-if="row._fromReserve">
            <el-button size="small" type="primary" @click="openBorrowConfirm(row)">领用</el-button>
          </template>
          <template v-else>
            <template v-if="row.approve_status === 1">
              <el-button v-if="canApplyReturn(row)" size="small" type="primary" @click="applyReturn(row)">申请归还</el-button>
              <el-button v-if="canExtend(row)" size="small" @click="extendTime(row)">延长</el-button>
              <el-button v-if="canRepair(row)" size="small" type="warning" @click="openRepair(row)">报修</el-button>
              <el-button v-if="row.return_status === 2" size="small" type="info" @click="softDelete(row)">删除</el-button>
            </template>
          </template>
        </template>
      </el-table-column>
    </el-table>
      </div>
    </el-card>

    <el-dialog v-model="repairVisible" title="提交报修" width="480px">
      <el-form :model="repairForm" label-width="100px">
        <el-form-item label="报修设备">
          <el-input :value="repairForm.equipmentName" disabled placeholder="当前领用设备" />
        </el-form-item>
        <el-form-item label="故障描述" required>
          <el-input
            v-model="repairForm.maintainContent"
            type="textarea"
            :rows="4"
            placeholder="请详细描述设备故障现象（必填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="repairVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!repairForm.maintainContent?.trim()" @click="submitRepair">提交报修</el-button>
      </template>
    </el-dialog>

    <!-- <el-dialog v-model="addVisible" title="提交领用申请" width="500px">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="设备" required>
          <el-select v-model="addForm.equipment_id" filterable placeholder="选择设备" style="width:100%">
            <el-option v-for="d in devices" :key="d.deviceId" :label="d.deviceName || ('设备#' + d.deviceId)" :value="d.deviceId" />
          </el-select>
        </el-form-item>
        <el-form-item label="领用时间" required>
          <el-date-picker v-model="addForm.use_time" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择开始使用时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="用途" required>
          <el-input v-model="addForm.purpose" type="textarea" :rows="2" placeholder="填写使用用途" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">提交</el-button>
      </template>
    </el-dialog> -->
  </div>
</template>

<script>
import axios from 'axios'
import { getUserId, getUserName, getSafeHeaderValue } from '../utils/auth'
import { formatDateTime, formatReserveTime, buildEndDatetimeForPicker } from '../utils/format'


export default {
  name: 'MyApply',
  data() {
    return {
      list: [],
      reserveList: [],
      statusFilter: '',
      highlightReserveId: null,
      showBorrowConfirmForHighlight: false,
      devices: [],
      addVisible: false,
      addForm: { equipment_id: null, reserve_id: null, equipment_name: '', use_time: '', purpose: '' },
      repairVisible: false,
      repairForm: { equipmentId: null, equipmentName: '', assetId: null, maintainContent: '' }
    }
  },
  computed: {
    mergedList() {
      const list = Array.isArray(this.reserveList) ? this.reserveList : []
      const approvedUnused = list.filter(r => {
        const status = r.status != null ? Number(r.status) : (r.reserve_status != null ? Number(r.reserve_status) : 0)
        const isUsed = r.isUsed != null ? Number(r.isUsed) : (r.is_used != null ? Number(r.is_used) : 0)
        return status === 1 && isUsed !== 1
      }).map(r => ({
        _fromReserve: true,
        reserveId: r.id ?? r.reserveId ?? r.reserve_id,
        equipment_name: r.equipmentName || r.equipment_name,
        equipment_id: r.equipmentId ?? r.equipment_id,
        reserveDate: r.reserveDate || r.reserve_date,
        startTime: r.startTime || r.start_time,
        endTime: r.endTime || r.end_time,
        reserveQuantity: r.reserveQuantity != null ? r.reserveQuantity : (r.reserve_quantity != null ? r.reserve_quantity : 1),
        purpose: r.purpose ?? ''
      }))
      const reserveListArr = list
      /** 只显示已通过审批的领用记录（领用生命周期），不显示待审批/已驳回 */
      const applies = (this.list || []).filter(a => a.approve_status === 1).map(a => {
        const row = { ...a, _fromReserve: false }
        if (a.reserve_id != null && reserveListArr.length) {
          const res = reserveListArr.find(rev => Number(rev.id ?? rev.reserveId ?? rev.reserve_id) === Number(a.reserve_id))
          if (res) {
            row.reserveDate = res.reserveDate || res.reserve_date
            row.startTime = res.startTime || res.start_time
            row.endTime = res.endTime || res.end_time
          }
        }
        return row
      })
      return [...approvedUnused, ...applies]
    },
    filteredMergedList() {
      const m = this.mergedList
      if (!this.statusFilter) return m
      if (this.statusFilter === 'pending') return m.filter(row => row._fromReserve)
      if (this.statusFilter === 'using') return m.filter(row => !row._fromReserve && row.return_status !== 2 && row.return_status !== 1)
      if (this.statusFilter === 'returning') return m.filter(row => !row._fromReserve && row.return_status === 1)
      if (this.statusFilter === 'returned') return m.filter(row => !row._fromReserve && row.return_status === 2)
      return m
    }
  },
  async created() {
    this.applyFromReserveQuery()
    await Promise.all([this.load(), this.loadReserves()])
    this.loadDevices()
  },
  methods: {
    formatDateTime,
    formatReserveTime,
    buildEndDatetimeForPicker,
    async loadDevices() {
      try {
        const r = await axios.get('/api/device/devices', { params: { borrowable: true } })
        if (r.data?.code === 200) this.devices = r.data.data || []
      } catch (e) {
        // 补充空块的注释+错误提示，解决no-empty报错，同时提升用户体验
        console.error('加载设备列表失败：', e)
        this.$message.error('设备列表加载失败，请刷新重试')
      }
    },
    async load() {
      const userId = getUserId()
      if (!userId) {
        this.$message.error('请重新登录')
        return
      }
      try {
        const r = await axios.get('/api/apply/applies/user/' + userId)
        if (r.data?.code === 200) this.list = r.data.data || []
      } catch (e) {
        console.error('加载领用列表失败：', e)
        this.$message.error('领用列表加载失败，请刷新重试')
      }
    },
    async loadReserves() {
      const userId = getUserId()
      if (!userId) {
        this.reserveList = []
        return
      }
      try {
        const r = await axios.get('/api/reserve/queryReserveByUserId', { params: { userId } })
        if (r.data?.code === 200) {
          const data = r.data.data
          this.reserveList = Array.isArray(data) ? data : []
        } else {
          this.reserveList = []
        }
      } catch (e) {
        console.error('加载预约列表失败：', e)
        this.$message.error('预约列表加载失败，待领用记录可能不完整')
        this.reserveList = []
      }
    },
    tryOpenBorrowConfirm() {
      if (!this.showBorrowConfirmForHighlight || !this.highlightReserveId) return
      const row = this.filteredMergedList.find(r => r._fromReserve && (Number(r.reserveId) === Number(this.highlightReserveId)))
      if (row) {
        this.showBorrowConfirmForHighlight = false
        this.$nextTick(() => {
          this.openBorrowConfirm(row)
        })
      }
    },
    tableRowClassName({ row }) {
      if (this.highlightReserveId && row._fromReserve && row.reserveId === this.highlightReserveId) {
        return 'row-highlight'
      }
      return ''
    },
    openBorrowConfirm(row) {
      this.$confirm('确认领用该设备吗？', '领用确认', {
        confirmButtonText: '领用',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.startBorrowFromReserve(row)
      }).catch(() => {})
    },
    async startBorrowFromReserve(row) {
      const userId = getUserId()
      if (!userId) {
        this.$message.error('请先登录')
        return
      }
      try {
        await axios.post('/api/apply/apply/startBorrow', { reserveId: row.reserveId }, {
          headers: { 'X-User-Id': userId, 'X-User-Name': getSafeHeaderValue(getUserName() || '') }
        })
        this.$message.success('已开始领用')
        this.highlightReserveId = null
        this.load()
        this.loadReserves()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '领用失败')
      }
    },
    openAdd(preset) {
      this.addForm = {
        equipment_id: preset?.equipmentId || null,
        reserve_id: preset?.reserveId || null,
        equipment_name: preset?.equipmentName || '',
        use_time: preset?.use_time || '',
        purpose: ''
      }
      this.addVisible = true
    },
    applyFromReserveQuery() {
      const q = this.$route.query
      if (q.reserveId && q.fromReserve) {
        this.statusFilter = 'pending'
        this.highlightReserveId = Number(q.reserveId)
        this.showBorrowConfirmForHighlight = true
        this.$router.replace({ path: '/my-apply', query: {} })
      }
    },
    async submitAdd() {
      if (!this.addForm.equipment_id) { 
        this.$message.warning('请选择设备')
        return 
      }
      if (!this.addForm.use_time) { 
        this.$message.warning('请选择领用时间')
        return 
      }
      if (!this.addForm.purpose?.trim()) { 
        this.$message.warning('请填写用途')
        return 
      }
      const userId = getUserId()
      if (!userId) { // 补充userId校验，避免提交时无用户ID
        this.$message.error('请重新登录')
        return
      }
      const now = new Date().toISOString()
      const payload = {
        equipment_id: Number(this.addForm.equipment_id),
        user_id: parseInt(userId, 10),
        apply_type: 0,
        apply_time: now,
        use_time: this.addForm.use_time,
        purpose: this.addForm.purpose.trim()
      }
      if (this.addForm.reserve_id) {
        payload.reserve_id = this.addForm.reserve_id
      }
      if (this.addForm.equipment_name) payload.equipment_name = this.addForm.equipment_name
      const name = getUserName()
      if (name) payload.user_name = name
      try {
        await axios.post('/api/apply/apply', payload)
        this.$message.success('提交成功')
        this.addVisible = false
        this.load()
        this.loadReserves()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '提交失败')
      }
    },
    canApplyReturn(row) {
      return row.approve_status === 1 && !row.return_time && (row.return_status === 0 || row.return_status === undefined || row.return_status === null)
    },
    /** 领用中且未归还、且有关联预约时可延长 */
    canExtend(row) {
      return this.canApplyReturn(row) && row.reserve_id
    },
    /** 领用中且未归还时可报修 */
    canRepair(row) {
      return this.canApplyReturn(row)
    },
    async extendTime(row) {
      const reserveId = row.reserve_id
      if (!reserveId) {
        this.$message.warning('该记录无关联预约，无法延长')
        return
      }
      try {
        const r = await axios.get('/api/reserve/getReserveById/' + reserveId)
        if (r.data?.code !== 200 || !r.data?.data) {
          this.$message.error('获取预约信息失败')
          return
        }
        const reserve = r.data.data
        const endStr = this.buildEndDatetimeForPicker(reserve.reserveDate || reserve.reserve_date, reserve.endTime || reserve.end_time)
        const base = endStr ? new Date(endStr.replace('T', ' ')) : new Date()
        base.setHours(base.getHours() + 1)
        const y = base.getFullYear()
        const m = String(base.getMonth() + 1).padStart(2, '0')
        const d = String(base.getDate()).padStart(2, '0')
        const h = String(base.getHours()).padStart(2, '0')
        const min = String(base.getMinutes()).padStart(2, '0')
        const sec = String(base.getSeconds()).padStart(2, '0')
        const newEndBackend = `${y}-${m}-${d} ${h}:${min}:${sec}`
        await axios.put(`/api/reserve/modifyReserve/${reserveId}`, {
          id: reserveId,
          endTime: newEndBackend
        })
        this.$message.success('已延长1小时')
        this.load()
        this.loadReserves()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '延长失败')
      }
    },
    openRepair(row) {
      this.repairForm = {
        equipmentId: row.equipment_id ?? row.equipmentId,
        equipmentName: row.equipment_name || row.equipmentName || ('设备#' + (row.equipment_id || row.equipmentId)),
        assetId: row.asset_id ?? row.assetId ?? null,
        maintainContent: ''
      }
      this.repairVisible = true
    },
    async submitRepair() {
      const userId = getUserId()
      const userName = getUserName()
      if (!userId) {
        this.$message.error('请重新登录')
        return
      }
      const content = this.repairForm.maintainContent?.trim()
      if (!content) {
        this.$message.warning('请填写故障描述')
        return
      }
      try {
        await axios.post('/api/maintain/maintain', {
          equipmentId: this.repairForm.equipmentId,
          assetId: this.repairForm.assetId || undefined,
          equipmentName: this.repairForm.equipmentName || undefined,
          maintainType: 1,
          applyUserId: Number(userId),
          applyUserName: userName || undefined,
          maintainContent: content,
          progressStatus: 0
        })
        this.$message.success('报修已提交')
        this.repairVisible = false
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '报修提交失败')
      }
    },
    async applyReturn(row) {
      const userId = getUserId()
      if (!userId) {
        this.$message.error('请先登录')
        return
      }
      try {
        await axios.post('/api/apply/apply/return/apply/' + row.id, {}, {
          headers: { 'X-User-Id': userId }
        })
        this.$message.success('已提交归还申请，请等待管理员或教师批准')
        this.load()
        this.loadReserves()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '提交归还申请失败')
      }
    },
    async cancel(row) {
      try {
        await this.$confirm('确定撤销该申请？', '提示', { type: 'warning' })
        await axios.delete('/api/apply/apply/' + row.id)
        this.$message.success('已撤销')
        this.load()
        this.loadReserves()
      } catch (e) {
        // 区分取消确认和真正的错误，避免误提示
        if (e !== 'cancel') {
          this.$message.error(e.response?.data?.msg || '撤销失败')
        }
      }
    },
    /** 已归还记录软删除：记录当前状态到 original_status，状态设为 4（已隐藏） */
    async softDelete(row) {
      try {
        await this.$confirm('确定删除？', '删除', { type: 'warning' })
        await axios.put('/api/apply/apply', { id: row.id, status: 4 })
        this.$message.success('已删除')
        this.load()
        this.loadReserves()
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error(e.response?.data?.msg || '删除失败')
        }
      }
    }
  }
}
</script>
<style scoped>
.page { padding: 0; }

.page-header { margin-bottom: 20px; }
.page-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
.page-desc { margin: 0; font-size: 13px; color: #909399; line-height: 1.5; }

/* 筛选栏：细边框+轻微阴影，更精致 */
.page-filter {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  padding: 14px 20px;
  /* 极细边框 + 浅阴影 */
  border: 1px solid #F0F2F5;
  border-radius: 8px;
  background-color: #FFFFFF;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  margin-bottom: 20px;
}

.filter-label {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.filter-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

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
.page-table :deep(.el-table__body tr:hover) { background-color: #F5F7FA; }
.page-table :deep(.el-table__body tr.row-highlight) { background-color: #ECF5FF; }

/* .table-hover-actions :deep(.el-table__body .el-table__cell:last-child .cell) {
  opacity: 0;
  transition: opacity 0.2s;
}
.table-hover-actions :deep(.el-table__body tr:hover .el-table__cell:last-child .cell) {
  opacity: 1;
} */

.list-card { margin-top: 0; }
.list-card :deep(.el-card__body) { padding: 16px; }
</style>
