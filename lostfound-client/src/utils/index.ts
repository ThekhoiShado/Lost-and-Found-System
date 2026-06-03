/**
 * 工具函数
 */

/**
 * 格式化日期
 */
export function formatDate(date: string | Date, fmt = 'yyyy-MM-dd HH:mm'): string {
  if (!date) return ''
  const d = typeof date === 'string' ? new Date(date) : date
  if (isNaN(d.getTime())) return ''

  const o: Record<string, number> = {
    'y+': d.getFullYear(),
    'M+': d.getMonth() + 1,
    'd+': d.getDate(),
    'H+': d.getHours(),
    'm+': d.getMinutes(),
    's+': d.getSeconds()
  }

  for (const [k, v] of Object.entries(o)) {
    if (new RegExp(`(${k})`).test(fmt)) {
      fmt = fmt.replace(RegExp.$1, String(v).padStart(RegExp.$1.length, '0'))
    }
  }
  return fmt
}

/**
 * 获取失物类型文本
 */
export function getTypeText(type: number): string {
  return type === 1 ? '失物招领' : '寻物启事'
}

/**
 * 获取失物类型标签颜色
 */
export function getTypeColor(type: number): string {
  return type === 1 ? '#409eff' : '#67c23a'
}

/**
 * 获取状态文本
 */
export function getStatusText(status: number): string {
  const map: Record<number, string> = {
    0: '待审核',
    1: '已发布',
    2: '已认领',
    3: '已结束'
  }
  return map[status] || '未知'
}

/**
 * 获取状态标签颜色
 */
export function getStatusColor(status: number): string {
  const map: Record<number, string> = {
    0: '#e6a23c',
    1: '#67c23a',
    2: '#909399',
    3: '#f56c6c'
  }
  return map[status] || '#909399'
}

/**
 * 获取认领审核状态
 */
export function getClaimStatusText(status: number): string {
  const map: Record<number, string> = {
    0: '审核中',
    1: '已通过',
    2: '已拒绝'
  }
  return map[status] || '未知'
}

/**
 * 截断文本
 */
export function truncateText(text: string, maxLength: number): string {
  if (!text) return ''
  // 去除HTML标签
  const plainText = text.replace(/<[^>]+>/g, '')
  if (plainText.length <= maxLength) return plainText
  return plainText.substring(0, maxLength) + '...'
}

/**
 * 验证手机号
 */
export function isPhone(phone: string): boolean {
  return /^1[3-9]\d{9}$/.test(phone)
}

/**
 * 验证邮箱
 */
export function isEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}
