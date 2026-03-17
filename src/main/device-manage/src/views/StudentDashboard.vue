<template>
  <div class="page student-dashboard">
    <header class="page-header">
      <h2 class="page-title">学生工作台</h2>
      <p class="page-desc">查看当前借用与历史记录，预约并使用实验室设备。</p>
    </header>
    <!-- 汇总卡片 -->
    <el-row :gutter="16" class="summary-row">
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-value">{{ currentBorrow }}</div>
          <div class="summary-label">件</div>
          <div class="summary-desc">当前借用</div>
          <div class="summary-sub">共 {{ currentBorrow }} 件工具借用中</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-value">{{ historyCount }}</div>
          <div class="summary-label">次</div>
          <div class="summary-desc">历史借用</div>
          <div class="summary-sub">总计借用次数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-value">{{ returnRate }}%</div>
          <div class="summary-desc">归还及时率</div>
          <div class="summary-sub">保持良好记录</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 可借用工具 -->
    <el-card class="tools-section list-card" shadow="hover">
      <template #header>
        <span class="section-title">可借用工具</span>
      </template>
      <div class="tools-toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索工具..."
          clearable
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <el-row :gutter="16" class="tool-grid">
        <el-col v-for="item in filteredToolGroups" :key="item.name" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="tool-card">
            <div class="tool-card-header">
              <span class="tool-name">{{ item.name }}</span>
              <el-tag v-if="item.category" size="small" type="primary" effect="plain" class="tool-tag">
                {{ item.category }}
              </el-tag>
            </div>
            <div class="tool-available">可用数量 {{ item.available }}/{{ item.total }}</div>
            <div class="tool-actions">
              <!-- <el-button
                type="primary"
                class="apply-btn"
                :disabled="item.available === 0"
                @click="openApply(item)"
              >
                申请借用
              </el-button> -->
              <el-button
                type="primary"
                class="reserve-btn"
                :disabled="item.available === 0"
                @click="openReserve(item)"
              >
                预约使用
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 我的借用记录 -->
    <el-card class="records-section list-card" shadow="hover">
      <template #header>
        <span class="section-title">我的借用记录</span>
        <el-button type="primary" text @click="$router.push('/my-apply')">查看全部</el-button>
      </template>
      <el-table :data="applyList.slice(0, 5)" stripe style="width: 100%">
        <el-table-column prop="equipment_name" label="设备名称" width="120" />
        <el-table-column label="领用时间" width="160">
          <template #default="{ row }">{{ formatTime(row.use_time) }}</template>
        </el-table-column>
        <el-table-column prop="purpose" label="用途" show-overflow-tooltip />
        <el-table-column label="归还时间" width="160">
          <template #default="{ row }">{{ formatTime(row.return_time) || '-' }}</template>
        </el-table-column>
        <el-table-column prop="approve_status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.approve_status === 0" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.approve_status === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="row.approve_status === 2" type="danger">已驳回</el-tag>
            <el-tag v-else type="info">-</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 快捷预约弹窗（工作台也可预约） -->
    <el-dialog v-model="reserveVisible" title="新建预约" width="480px">
      <el-form :model="reserveForm" label-width="100px">
        <el-form-item label="设备">
          <el-input :value="reserveForm.deviceName" disabled />
        </el-form-item>
        <el-form-item label="预约数量" required>
          <el-input-number v-model="reserveForm.reserveQuantity" :min="1" :max="reserveMaxQuantity" placeholder="1" style="width:100%" />
          <span v-if="selectedReserveItem" class="form-tip">可选 1～{{ reserveMaxQuantity }}（当前可用）</span>
        </el-form-item>
        <el-form-item label="预约日期" required>
          <el-date-picker
            v-model="reserveForm.reserveDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="开始时段" required>
          <el-time-picker
            v-model="reserveForm.startTime"
            value-format="HH:mm:ss"
            format="HH:mm"
            placeholder="开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时段" required>
          <el-time-picker
            v-model="reserveForm.endTime"
            value-format="HH:mm:ss"
            format="HH:mm"
            placeholder="结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预约用途">
          <el-input v-model="reserveForm.purpose" type="textarea" :rows="2" placeholder="选填，与领用用途含义一致" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reserveVisible = false">取消</el-button>
        <el-button type="primary" :loading="reserveLoading" @click="submitReserve">提交预约</el-button>
      </template>
    </el-dialog>

    <!-- 申请借用弹窗 -->
    <!-- <el-dialog v-model="applyVisible" title="申请借用" width="500px">
      <el-form :model="applyForm" label-width="100px">
        <el-form-item label="设备">{{ applyForm.deviceName }}</el-form-item>
        <el-form-item label="领用时间" required>
          <el-date-picker
            v-model="applyForm.use_time"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择开始使用时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="用途" required>
          <el-input v-model="applyForm.purpose" type="textarea" :rows="2" placeholder="填写使用用途" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交</el-button>
      </template>
    </el-dialog> -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { getUserId, getUserName } from '../utils/auth'
// import { el } from 'element-plus/es/locale'
const devices = ref([])
const applyList = ref([])
const searchKeyword = ref('')
// const applyVisible = ref(false)
// const applyForm = ref({ equipment_id: null, deviceName: '', use_time: '', purpose: '' })
const reserveVisible = ref(false)
const reserveLoading = ref(false)
const selectedReserveItem = ref(null)
const reserveForm = ref({ equipmentId: null, deviceName: '', reserveDate: '', startTime: '', endTime: '', purpose: '', reserveQuantity: 1 })

const currentBorrow = computed(() => applyList.value.filter(a => a.approve_status === 1 && !a.return_time).length)
const historyCount = computed(() => applyList.value.length)
const returnRate = computed(() => {
  const completed = applyList.value.filter(a => a.return_time).length
  const approved = applyList.value.filter(a => a.approve_status === 1).length
  return approved ? Math.round((completed / approved) * 100) : 100
})

const toolGroups = computed(() => {
  const map = new Map()
  for (const d of devices.value) {
    const name = d.deviceName || '未命名'
    const cnt = d.count != null ? d.count : 1
    if (!map.has(name)) {
      map.set(name, { name, available: 0, total: 0, category: categoryByModel(d.model), firstId: null })
    }
    const g = map.get(name)
    g.total += cnt
    if (d.status === 0 && cnt > 0) {
      g.available += cnt
      if (!g.firstId) g.firstId = d.deviceId
    }
  }
  return Array.from(map.values())
})

const filteredToolGroups = computed(() => {
  const k = searchKeyword.value?.toLowerCase() || ''
  if (!k) return toolGroups.value
  return toolGroups.value.filter(g => g.name.toLowerCase().includes(k))
})

const reserveMaxQuantity = computed(() => {
  const item = selectedReserveItem.value
  if (!item) return 1
  return Math.max(1, item.available ?? item.total ?? 1)
})

function categoryByModel(model) {
  if (!model) return '设备'
  if (/显微镜|镜/i.test(model)) return '观察设备'
  if (/烧杯|试管|量筒|玻璃/i.test(model)) return '玻璃器皿'
  if (/天平|称/i.test(model)) return '测量设备'
  if (/灯|加热/i.test(model)) return '加热设备'
  return '设备'
}

// function openApply(item) {
//   if (item.available === 0) return
//   applyForm.value = {
//     equipment_id: item.firstId,
//     deviceName: item.name,
//     use_time: '',
//     purpose: ''
//   }
//   applyVisible.value = true 
// }

function openReserve(item) {
  if (item.available === 0) return
  selectedReserveItem.value = item
  reserveForm.value = {
    equipmentId: item.firstId,
    deviceName: item.name,
    reserveDate: '',
    startTime: '',
    endTime: '',
    purpose: '',
    reserveQuantity: 1
  }
  reserveVisible.value = true
}

async function submitReserve() {
  const f = reserveForm.value
  if (!f.equipmentId || !f.reserveDate || !f.startTime || !f.endTime) {
    ElMessage.warning('请填写预约日期、开始时段和结束时段')
    return
  }
  const userId = getUserId()
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }
  const dateStr = typeof f.reserveDate === 'string' ? f.reserveDate : (f.reserveDate && f.reserveDate.toISOString ? f.reserveDate.toISOString().slice(0, 10) : '')
  const startStr = (typeof f.startTime === 'string' ? f.startTime : '09:00:00').slice(0, 8)
  const endStr = (typeof f.endTime === 'string' ? f.endTime : '11:00:00').slice(0, 8)
  const startTime = dateStr + ' ' + startStr
  const endTime = dateStr + ' ' + endStr
  if (endStr <= startStr) {
    ElMessage.warning('结束时段须晚于开始时段')
    return
  }
  reserveLoading.value = true
  try {
    const qty = Math.max(1, Math.min(reserveMaxQuantity.value, Number(f.reserveQuantity) || 1))
    const r = await axios.post('/api/reserve/createReserve', {
      equipmentId: Number(f.equipmentId),
      equipmentName: f.deviceName || '',
      userId: Number(userId),
      userName: getUserName() || '',
      reserveDate: dateStr,
      startTime,
      endTime,
      status: 0,
      purpose: (f.purpose && String(f.purpose).trim()) || null,
      reserveQuantity: qty
    })
    if (r.data?.code === 200) {
      reserveVisible.value = false
      loadApplyList()
      ElMessage.success('预约已提交，请等待管理员或教师审批；通过后可在「我的预约」点击领用')
    }
  } catch (e) {
    console.error(e)
  } finally {
    reserveLoading.value = false
  }
}

// async function submitApply() {
//   if (!applyForm.value.use_time || !applyForm.value.purpose?.trim()) {
//     return
//   }
//   const userId = getUserId()
//   const now = new Date().toISOString().slice(0, 19).replace('T', ' ')
//   try {
//     const payload = {
//       equipment_id: Number(applyForm.value.equipment_id),
//       user_id: parseInt(userId, 10),
//       apply_type: 0,
//       apply_time: now,
//       use_time: applyForm.value.use_time,
//       purpose: applyForm.value.purpose.trim()
//     }
//     if (applyForm.value.deviceName) payload.equipment_name = applyForm.value.deviceName
//     if (getUserName()) payload.user_name = getUserName()
//     await axios.post('/api/apply/apply', payload)
//     applyVisible.value = false
//     loadApplyList()
//     loadDevices()
//   } catch (e) {
//     // error handled by axios
//   }
// }

function formatTime(val) {
  if (!val) return ''
  const d = new Date(val)
  if (Number.isNaN(d.getTime())) return val
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0') + ' ' + String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0')
}

async function loadDevices() {
  try {
    const r = await axios.get('/api/device/devices', { params: { borrowable: true } })
    if (r.data?.code === 200) devices.value = r.data.data || []
  } catch (e) {
    console.error(e)
  }
}

async function loadApplyList() {
  const userId = getUserId()
  if (!userId) return
  try {
    const r = await axios.get('/api/apply/applies/user/' + userId)
    if (r.data?.code === 200) applyList.value = r.data.data || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadDevices()
  loadApplyList()
})
</script>

<style scoped>
.student-dashboard {
  padding: 0;
  background: #f5f7fa;
  min-height: 100%;
}
.summary-row {
  margin-bottom: 20px;
}
.summary-card {
  border-radius: 8px;
  text-align: center;
}
.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}
.summary-label {
  font-size: 14px;
  color: #909399;
  margin-left: 4px;
}
.summary-desc {
  font-size: 14px;
  color: #606266;
  margin-top: 8px;
}
.summary-sub {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.page-header { margin-bottom: 16px; }
.page-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
.page-desc { margin: 0 0 16px 0; font-size: 13px; color: #909399; }
.list-card :deep(.el-card__body) { padding: 16px; }
.tools-section,
.records-section {
  border-radius: 8px;
  margin-bottom: 20px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.tools-toolbar {
  margin-bottom: 16px;
}
.search-input {
  max-width: 320px;
}
.tool-grid {
  min-height: 120px;
}
.tool-card {
  border-radius: 8px;
  margin-bottom: 16px;
}
.tool-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}
.tool-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.tool-tag {
  flex-shrink: 0;
}
.tool-available {
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
}
.tool-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.apply-btn,
.reserve-btn {
  flex: 1;
  min-width: 0;
}
.records-section :deep(.el-card__header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
