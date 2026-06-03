import request from './request'

/**
 * 认证相关 API
 */
export const authApi = {
  /** 用户登录 */
  login(username: string, password: string) {
    return request.post('/auth/login', { username, password })
  },

  /** 用户注册 */
  register(data: { username: string; password: string; target: string; code: string; nickname?: string }) {
    return request.post('/auth/register', data)
  },

  /** 发送验证码 */
  sendCode(target: string, type: string) {
    return request.post('/auth/send-code', { target, type })
  }
}
