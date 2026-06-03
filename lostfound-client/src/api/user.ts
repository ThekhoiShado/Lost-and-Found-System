import request from './request'

/**
 * 用户信息相关 API
 */
export const userApi = {
  /** 获取个人信息 */
  getProfile() {
    return request.get('/user/profile')
  },

  /** 更新个人信息 */
  updateProfile(data: any) {
    return request.put('/user/profile', data)
  },

  /** 修改密码 */
  changePassword(oldPassword: string, newPassword: string) {
    return request.put('/user/password', { oldPassword, newPassword })
  }
}

/**
 * 文件上传 API
 */
export const uploadApi = {
  /** 上传图片 */
  uploadImage(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/image', formData)
  }
}
