import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userId = ref<number>(Number(localStorage.getItem('userId')) || 0)
  const username = ref<string>(localStorage.getItem('username') || '')
  const nickname = ref<string>(localStorage.getItem('nickname') || '')
  const role = ref<string>(localStorage.getItem('role') || '')
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

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
    localStorage.removeItem('role')
    localStorage.removeItem('avatar')
  }

  /**
   * 更新用户信息
   */
  function updateProfile(data: { nickname?: string; avatar?: string }) {
    if (data.nickname !== undefined) {
      nickname.value = data.nickname
      localStorage.setItem('nickname', data.nickname)
    }
    if (data.avatar !== undefined) {
      avatar.value = data.avatar
      localStorage.setItem('avatar', data.avatar)
    }
  }

  return {
    token, userId, username, nickname, role, avatar,
    isLoggedIn, login, logout, updateProfile
  }
})
