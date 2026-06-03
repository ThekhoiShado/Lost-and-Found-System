import request from './request'

/**
 * 评论相关 API
 */
export const commentApi = {
  /** 发表评论/回复 */
  add(data: { lostItemId: number; content: string; parentId?: number; replyToUserId?: number }) {
    return request.post('/comment/add', data)
  },

  /** 获取评论树 */
  getList(lostItemId: number) {
    return request.get(`/comment/list/${lostItemId}`)
  },

  /** 删除评论 */
  delete(id: number) {
    return request.delete(`/comment/${id}`)
  },

  /** 点赞/取消点赞 */
  toggleLike(commentId: number) {
    return request.post(`/comment/like/${commentId}`)
  }
}
