import request from './request'

/**
 * 失物/寻物相关 API
 */
export const lostApi = {
  /** 分页获取列表 */
  getList(params: { current?: number; size?: number; type?: number; category?: string; keyword?: string }) {
    return request.get('/lost/list', { params })
  },

  /** 获取详情 */
  getDetail(id: number) {
    return request.get(`/lost/${id}`)
  },

  /** 发布信息 */
  publish(data: any) {
    return request.post('/lost/publish', data)
  },

  /** 更新信息 */
  update(id: number, data: any) {
    return request.put(`/lost/${id}`, data)
  },

  /** 删除信息 */
  delete(id: number) {
    return request.delete(`/lost/${id}`)
  },

  /** 获取我的发布 */
  getMyPosts(current = 1, size = 10) {
    return request.get('/lost/my', { params: { current, size } })
  }
}
