/**
 * 工具函数
 */

/**
 * 格式化日期
 * @param date - 日期字符串或Date对象
 * @param fmt - 格式化模板，默认为 'yyyy-MM-dd HH:mm'
 *              支持的占位符：
 *              - y+: 年份
 *              - M+: 月份
 *              - d+: 日期
 *              - H+: 小时
 *              - m+: 分钟
 *              - s+: 秒
 * @returns 格式化后的日期字符串，如果日期无效则返回空字符串
 * @example
 * formatDate(new Date(), 'yyyy-MM-dd') // '2024-01-01'
 * formatDate('2024-01-01', 'yyyy年MM月dd日') // '2024年01月01日'
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

  for (const [k, v] of Object.entries(o)) { // 遍历日期对象的属性
    if (new RegExp(`(${k})`).test(fmt)) { // 如果格式化模板中包含当前属性的占位符
      fmt = fmt.replace(RegExp.$1, String(v).padStart(RegExp.$1.length, '0')) // 替换占位符为实际值，确保长度与占位符相同
    }
  }
  return fmt // 返回格式化后的日期字符串
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