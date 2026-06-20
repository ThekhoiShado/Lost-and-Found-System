import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => { // 定义用户状态管理模块
  /** 登录凭证 */
  const token = ref<string>(localStorage.getItem('token') || '')
  /** 用户ID */
  const userId = ref<number>(Number(localStorage.getItem('userId')) || 0)
  /** 用户名 */
  const username = ref<string>(localStorage.getItem('username') || '')
  /** 昵称 */
  const nickname = ref<string>(localStorage.getItem('nickname') || '')
  /** 角色 */
  const role = ref<string>(localStorage.getItem('role') || '')
  /** 头像 */
  const avatar = ref<string>(localStorage.getItem('avatar') || '')

  /** 是否已登录 */
  const isLoggedIn = computed(() => !!token.value)

  /**
   * 登录
   */
  async function login(loginUsername: string, password: string) {
    const res = await authApi.login(loginUsername, password)
    const data = res.data.data
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    nickname.value = data.nickname
    role.value = data.role
    avatar.value = data.avatar || ''

    // 持久化存储
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('nickname', data.nickname || '')
    localStorage.setItem('role', data.role)
    localStorage.setItem('avatar', data.avatar || '')
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    nickname.value = ''
    role.value = ''
    avatar.value = ''

    localStorage.removeItem('token') // 清除持久化存储的登录凭证
    localStorage.removeItem('userId') // 清除持久化存储的用户ID
    localStorage.removeItem('username') // 清除持久化存储的用户名
    localStorage.removeItem('nickname') // 清除持久化存储的昵称 
    localStorage.removeItem('role') // 清除持久化存储的角色
    localStorage.removeItem('avatar') // 清除持久化存储的头像
  }

  /**
   * 更新用户信息
   */
  function updateProfile(data: { nickname?: string; avatar?: string }) {
    if (data.nickname !== undefined) {
      nickname.value = data.nickname // 更新昵称
      localStorage.setItem('nickname', data.nickname) // 更新持久化存储的昵称
    }
    if (data.avatar !== undefined) {
      avatar.value = data.avatar // 更新头像
      localStorage.setItem('avatar', data.avatar)  // 更新持久化存储的头像
    }
  }

  return { // 返回用户状态管理模块的状态和方法
    token, userId, username, nickname, role, avatar,
    isLoggedIn, login, logout, updateProfile // 返回用户状态管理模块的状态和方法
  }
})
