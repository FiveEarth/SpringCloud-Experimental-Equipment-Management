/**
 * 统一时间显示：将 ISO 或日期字符串格式化为 YYYY-MM-DD HH:mm
 */
export function formatDateTime(val) {
  if (val == null || val === '') return '-'
  const d = new Date(val)
  if (Number.isNaN(d.getTime())) return val
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day} ${h}:${min}`
}

/** 仅日期 YYYY-MM-DD */
export function formatDate(val) {
  if (val == null || val === '') return '-'
  const d = new Date(val)
  if (Number.isNaN(d.getTime())) return val
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

/**
 * 预约开始/结束时间：DB 的 TIME 会变成 1970-01-01 HH:mm:ss，用预约日期拼接成正确显示
 * @param reserveDate 预约日期 "YYYY-MM-DD"
 * @param timeVal 时间字段（可能是 "1970-01-01 09:00:00" 或 "yyyy-MM-dd HH:mm:ss"）
 */
export function formatReserveTime(reserveDate, timeVal) {
  if (timeVal == null || timeVal === '') return '-'
  const str = String(timeVal).trim()
  const d = new Date(str)
  if (Number.isNaN(d.getTime())) return str
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  const isEpoch = d.getFullYear() === 1970 && d.getMonth() === 0 && d.getDate() === 1
  const datePart = (reserveDate && String(reserveDate).slice(0, 10)) || ''
  if (isEpoch && datePart) return `${datePart} ${h}:${min}`
  return formatDateTime(timeVal)
}

/**
 * 用预约日期 + 结束时间拼出完整结束时间，用于修改预约的日期选择器（避免 1970 年）
 * 返回 "YYYY-MM-DDTHH:mm:ss" 供 el-date-picker value-format 使用
 */
export function buildEndDatetimeForPicker(reserveDate, endTimeVal) {
  if (!reserveDate || endTimeVal == null || endTimeVal === '') return ''
  const datePart = String(reserveDate).slice(0, 10)
  const d = new Date(endTimeVal)
  if (Number.isNaN(d.getTime())) return datePart + 'T18:00:00'
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  const sec = String(d.getSeconds()).padStart(2, '0')
  return `${datePart}T${h}:${min}:${sec}`
}

/**
 * 转为后端要求的 "yyyy-MM-dd HH:mm:ss" 格式
 */
export function toBackendDateTime(val) {
  if (val == null || val === '') return ''
  const s = String(val).trim()
  return s.includes('T') ? s.replace('T', ' ').slice(0, 19) : s.slice(0, 19)
}
