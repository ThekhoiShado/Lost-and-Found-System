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
  },

  /** 上传头像（上传 + 更新一步完成） */
  uploadAvatar(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/user/avatar', formData)
  }
}

/**
 * 文件上传 API
 */
export const uploadApi = {
  /**
   * 上传图片
   * @param file - 要上传的图片文件对象
   * @returns 返回上传请求的 Promise，包含服务器返回的图片信息
   * @example
   * const file = document.querySelector('input[type="file"]').files[0]
   * uploadApi.uploadImage(file).then(res => {
   *   console.log('图片上传成功', res.data)
   * })
   */
  uploadImage(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/image', formData)
  }
}