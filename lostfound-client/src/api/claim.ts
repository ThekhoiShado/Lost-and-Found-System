import request from './request'

/**
 * 认领相关 API
 */
export const claimApi = {
  /** 提交认领申请 */
  submit(data: { lostItemId: number; claimantName: string; claimantPhone: string; claimDetail: string; proofImages?: string }) {
    return request.post('/claim/add', data)
  },

  /** 获取我的认领申请 */
  getMyClaims(current = 1, size = 10) {
    return request.get('/claim/my', { params: { current, size } })
  },

  /** 获取认领详情 */
  getDetail(id: number) {
    return request.get(`/claim/${id}`)
  }
}
