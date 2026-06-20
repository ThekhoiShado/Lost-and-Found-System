<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { lostApi } from '@/api/lost'
import { commentApi } from '@/api/comment'
import { claimApi } from '@/api/claim'
import { uploadApi } from '@/api/user'
import type { UploadFile } from 'element-plus'
import { getTypeText, getTypeColor, getStatusText, getStatusColor, formatDate } from '@/utils'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const item = ref<any>(null)
const comments = ref<any[]>([])
const loading = ref(true)

// 认领弹窗
const claimDialogVisible = ref(false)
const claimForm = ref({
  claimantName: '',
  claimantPhone: '',
  claimDetail: '',
  proofImages: '[]'
})

// 凭证图片上传（axios 手动上传）
const proofImageList = ref<string[]>([])
const uploadingProof = ref(false)

async function onProofChange(file: UploadFile) {
  const rawFile = file.raw
  if (!rawFile) return
  const mimeType = rawFile.type || ''
  const fileName = rawFile.name || file.name || ''
  const isImage = mimeType.startsWith('image/') || /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(fileName)
  if (!isImage) { ElMessage.error('只能上传图片文件'); return }
  if (rawFile.size / 1024 / 1024 > 10) { ElMessage.error('图片大小不能超过 10MB'); return }

  uploadingProof.value = true
  try {
    const res = await uploadApi.uploadImage(rawFile)
    const url = res.data?.data
    if (url) {
      proofImageList.value.push(url)
      claimForm.value.proofImages = JSON.stringify(proofImageList.value)
      ElMessage.success('凭证上传成功')
    } else {
      ElMessage.error('凭证上传失败：返回数据异常')
    }
  } catch (e: any) {
    console.error('凭证上传失败', e)
    ElMessage.error('凭证上传失败')
  }
  uploadingProof.value = false
}

// 移除凭证图片
function removeProofImage(index: number) {
  proofImageList.value.splice(index, 1)
  claimForm.value.proofImages = JSON.stringify(proofImageList.value)
}

// 打开认领弹窗时重置凭证图片
function openClaimDialog() {
  proofImageList.value = []
  claimForm.value = {
    claimantName: '',
    claimantPhone: '',
    claimDetail: '',
    proofImages: '[]'
  }
  claimDialogVisible.value = true
}

// 评论输入
const commentText = ref('')
const replyTo = ref<{ id: number; username: string } | null>(null)

const isOwner = computed(() => userStore.userId === item.value?.userId)

async function loadDetail() {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const [detailRes, commentRes] = await Promise.all([
      lostApi.getDetail(id),
      commentApi.getList(id)
    ])
    item.value = detailRes.data.data
    comments.value = commentRes.data.data || []
  } finally {
    loading.value = false
  }
}

// 提交认领
async function submitClaim() {
  if (!claimForm.value.claimantName || !claimForm.value.claimantPhone || !claimForm.value.claimDetail) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await claimApi.submit({
      lostItemId: item.value.id,
      ...claimForm.value
    })
    ElMessage.success('认领申请已提交')
    claimDialogVisible.value = false
  } catch { /* error handled by interceptor */ }
}

// 提交评论
async function submitComment() {
  if (!commentText.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    await commentApi.add({
      lostItemId: item.value.id,
      content: commentText.value,
      parentId: replyTo.value?.id,
      replyToUserId: replyTo.value?.id
    })
    ElMessage.success('评论成功')
    commentText.value = ''
    replyTo.value = null
    await loadDetail()
  } catch { /* error handled by interceptor */ }
}

// 回复评论
function replyComment(comment: any) {
  replyTo.value = { id: comment.id, username: comment.nickname || comment.username }
  commentText.value = ''
  document.getElementById('comment-input')?.scrollIntoView({ behavior: 'smooth' })
}

// 点赞
async function toggleLike(commentId: number) {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await commentApi.toggleLike(commentId)
    loadDetail()
  } catch { /* handled */ }
}

// 删除评论
async function deleteComment(commentId: number) {
  try {
    await ElMessageBox.confirm('确定删除该评论？', '提示', { type: 'warning' })
    await commentApi.delete(commentId)
    ElMessage.success('删除成功')
    loadDetail()
  } catch { /* cancelled */ }
}

// 编辑信息
function editItem() {
  router.push(`/publish/${item.value.id}`)
}

// 删除信息
async function deleteItem() {
  try {
    await ElMessageBox.confirm('确定删除该信息？', '提示', { type: 'warning' })
    await lostApi.delete(item.value.id)
    ElMessage.success('删除成功')
    router.push('/')
  } catch { /* cancelled */ }
}

onMounted(() => loadDetail())
</script>

<template>
  <div class="detail-page" v-loading="loading">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator=">" class="detail-breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item v-if="item">{{ getTypeText(item.type) }}</el-breadcrumb-item>
      <el-breadcrumb-item v-if="item">{{ item.title }}</el-breadcrumb-item>
    </el-breadcrumb>

    <div v-if="item" class="detail-container">
      <!-- 标题区 -->
      <div class="detail-header">
        <div class="detail-tags">
          <el-tag :color="getTypeColor(item.type)" effect="dark">
            {{ getTypeText(item.type) }}
          </el-tag>
          <el-tag :color="getStatusColor(item.status)" effect="dark">
            {{ getStatusText(item.status) }}
          </el-tag>
        </div>
        <h1 class="detail-title">{{ item.title }}</h1>
        <div class="detail-meta">
          <span class="meta-item">
            <el-icon><User /></el-icon> {{ item.nickname || item.username }}
          </span>
          <span class="meta-divider">|</span>
          <span class="meta-item">
            <el-icon><Clock /></el-icon> {{ formatDate(item.createTime) }}
          </span>
          <span class="meta-divider">|</span>
          <span class="meta-item">
            <el-icon><View /></el-icon> {{ (item.viewCount || 0) }} 次浏览
          </span>
          <span v-if="item.location" class="meta-divider">|</span>
          <span v-if="item.location" class="meta-item">
            <el-icon><Location /></el-icon> {{ item.location }}
          </span>
        </div>
      </div>

      <!-- 图片 -->
      <div v-if="item.coverImage" class="detail-image-wrap">
        <img :src="item.coverImage" :alt="item.title" class="detail-image" />
      </div>

      <!-- 内容区：两栏布局 -->
      <div class="detail-body">
        <!-- 左侧：富文本内容 -->
        <div class="detail-content">
          <h3 class="section-title">详细信息</h3>
          <div class="rich-text-content" v-html="item.content"></div>
        </div>

        <!-- 右侧：信息面板 -->
        <div class="detail-info-panel">
          <div class="info-card">
            <h3 class="info-card-title">
              <el-icon><InfoFilled /></el-icon> 基本信息
            </h3>
            <div class="info-row">
              <span class="info-label">分类</span>
              <span class="info-value">{{ item.category || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">地点</span>
              <span class="info-value">{{ item.location || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">日期</span>
              <span class="info-value">{{ item.lostDate || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">发布者</span>
              <span class="info-value">{{ item.nickname || item.username }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">联系方式</span>
              <span class="info-value info-value--contact">{{ item.contact }}</span>
            </div>
          </div>

          <!-- 操作按钮区 -->
          <div class="info-actions">
            <!-- 认领按钮（非发布者+已登录+已发布状态） -->
            <el-button
              v-if="userStore.isLoggedIn && !isOwner && item.status === 1"
              type="primary"
              size="large"
              class="claim-btn"
              @click="openClaimDialog"
            >
              <el-icon><ChatDotSquare /></el-icon> 提交认领申请
            </el-button>

            <!-- 发布者操作 -->
            <div v-if="isOwner" class="owner-actions">
              <el-button @click="editItem">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" @click="deleteItem">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>

            <!-- 未登录提示 -->
            <div v-if="!userStore.isLoggedIn && item.status === 1" class="login-tip">
              <router-link to="/login">登录</router-link> 后可以提交认领申请
            </div>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3 class="comment-section-title">
          💬 留言板
          <span class="comment-count">{{ comments.length }}</span>
        </h3>

        <!-- 评论输入 -->
        <div v-if="userStore.isLoggedIn" id="comment-input" class="comment-input-area">
          <div v-if="replyTo" class="reply-indicator">
            回复 @{{ replyTo.username }}
            <el-button link type="danger" size="small" @click="replyTo = null">取消</el-button>
          </div>
          <el-input
            v-model="commentText"
            type="textarea"
            :rows="3"
            placeholder="写下你的留言..."
          />
          <el-button
            type="primary"
            size="small"
            class="comment-submit-btn"
            @click="submitComment"
          >
            {{ replyTo ? '回复' : '发表评论' }}
          </el-button>
        </div>
        <div v-else class="comment-login-tip">
          <router-link to="/login">登录</router-link> 后参与评论
        </div>

        <!-- 评论列表 -->
        <div v-if="comments.length === 0" class="comment-empty">
          暂无留言，来写第一条吧
        </div>
        <div v-else class="comment-list">
          <div
            v-for="comment in comments"
            :key="comment.id"
            class="comment-item"
          >
            <div class="comment-main">
              <el-avatar :size="36" :src="comment.avatar" class="comment-avatar">
                {{ (comment.nickname || comment.username || '?')[0] }}
              </el-avatar>
              <div class="comment-body">
                <div class="comment-header">
                  <div class="comment-user-info">
                    <strong class="comment-username">{{ comment.nickname || comment.username }}</strong>
                    <span v-if="comment.top === 1" class="top-badge">⭐ 置顶</span>
                  </div>
                  <span class="comment-time">{{ formatDate(comment.createTime, 'MM-dd HH:mm') }}</span>
                </div>
                <p class="comment-content-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <span class="action-item" @click="toggleLike(comment.id)">
                    👍 {{ comment.likeCount || 0 }}
                  </span>
                  <span class="action-item" @click="replyComment(comment)">
                    💬 回复
                  </span>
                  <span
                    v-if="userStore.userId === comment.userId"
                    class="action-item action-item--danger"
                    @click="deleteComment(comment.id)"
                  >
                    删除
                  </span>
                </div>

                <!-- 子回复 -->
                <div v-if="comment.children?.length" class="comment-replies">
                  <div
                    v-for="reply in comment.children"
                    :key="reply.id"
                    class="reply-item"
                  >
                    <el-avatar :size="24" :src="reply.avatar" class="reply-avatar">
                      {{ (reply.nickname || reply.username || '?')[0] }}
                    </el-avatar>
                    <div class="reply-body">
                      <div class="reply-header">
                        <strong class="reply-username">{{ reply.nickname || reply.username }}</strong>
                        <span v-if="reply.replyToUsername" class="reply-to">
                          回复 @{{ reply.replyToUsername }}
                        </span>
                        <span class="reply-time">{{ formatDate(reply.createTime, 'MM-dd HH:mm') }}</span>
                      </div>
                      <p class="reply-content">{{ reply.content }}</p>
                      <div class="reply-actions">
                        <span class="action-item" @click="toggleLike(reply.id)">
                          👍 {{ reply.likeCount || 0 }}
                        </span>
                        <span
                          v-if="userStore.userId === reply.userId"
                          class="action-item action-item--danger"
                          @click="deleteComment(reply.id)"
                        >
                          删除
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 认领弹窗 -->
      <el-dialog v-model="claimDialogVisible" title="提交认领申请" width="500px">
        <el-form :model="claimForm" label-width="80px">
          <el-form-item label="您的姓名">
            <el-input v-model="claimForm.claimantName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="claimForm.claimantPhone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="认领说明">
            <el-input
              v-model="claimForm.claimDetail"
              type="textarea"
              :rows="4"
              placeholder="请详细描述您与该物品的关系或提供凭证信息"
            />
          </el-form-item>
          <el-form-item label="凭证图片">
            <div v-if="proofImageList.length > 0" class="proof-image-list">
              <div v-for="(url, i) in proofImageList" :key="i" class="proof-image-item">
                <img :src="url" alt="凭证图片" />
                <el-button type="danger" size="small" circle class="proof-remove-btn" @click="removeProofImage(i)">
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>
            <el-upload
              :show-file-list="false"
              :auto-upload="false"
              :on-change="onProofChange"
              accept="image/*"
            >
              <el-button size="small" :loading="uploadingProof">
                <el-icon><Plus /></el-icon> 添加凭证图片
              </el-button>
            </el-upload>
            <span class="upload-hint">上传能证明物品归属的图片，如购买记录、照片等</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="claimDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitClaim">提交申请</el-button>
        </template>
      </el-dialog>
    </div>

    <!-- 信息不存在 -->
    <div v-else-if="!loading" class="detail-empty">
      <el-empty description="信息不存在或已删除" />
    </div>
  </div>
</template>

<style scoped>
/* ===== 布局容器 ===== */
.detail-page {
  max-width: 1000px;
  margin: 0 auto;
}

.detail-breadcrumb {
  margin-bottom: 20px;
}

.detail-empty {
  text-align: center;
  padding: 60px 0;
}

/* ===== 标题区 ===== */
.detail-header {
  margin-bottom: 20px;
}

.detail-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.detail-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary, #00A884);
  margin-bottom: 10px;
  line-height: 1.4;
}

.detail-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 2px;
  font-size: 13px;
  color: var(--text-secondary, #999);
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.meta-divider {
  color: #ddd;
  margin: 0 8px;
}

/* ===== 图片 ===== */
.detail-image-wrap {
  text-align: center;
  margin-bottom: 24px;
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
  border: 1px solid var(--border-light, #f0f0f0);
}

.detail-image {
  max-width: 100%;
  max-height: 420px;
  border-radius: 4px;
  object-fit: contain;
}

/* ===== 内容两栏布局 ===== */
.detail-body {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

/* 左侧：富文本 */
.detail-content {
  flex: 1;
  min-width: 0;
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 24px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary, #00A884);
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-light, #f0f0f0);
  margin-bottom: 16px;
}

/* 右侧面板 */
.detail-info-panel {
  width: 280px;
  flex-shrink: 0;
}

.info-card {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 16px 18px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

.info-card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary, #333);
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary, #00A884);
}

.info-row {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px dashed var(--border-light, #f0f0f0);
  font-size: 13px;
  line-height: 1.6;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  color: var(--text-secondary, #999);
  width: 65px;
  flex-shrink: 0;
}

.info-value {
  color: var(--text-primary, #333);
  flex: 1;
  word-break: break-all;
}

.info-value--contact {
  color: var(--primary, #00A884);
  font-weight: 600;
}

/* 操作按钮 */
.info-actions {
  margin-top: 16px;
}

.claim-btn {
  width: 100%;
}

.owner-actions {
  display: flex;
  gap: 8px;
}

.login-tip {
  text-align: center;
  font-size: 13px;
  color: var(--text-secondary, #999);
  padding: 12px 0;
}

/* ===== 评论区 ===== */
.comment-section {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 24px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

.comment-section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--text-primary, #333);
}

.comment-count {
  font-size: 13px;
  color: var(--text-secondary, #999);
  font-weight: 400;
  margin-left: 6px;
}

/* 评论输入 */
.comment-input-area {
  margin-bottom: 24px;
}

.reply-indicator {
  font-size: 13px;
  color: var(--text-secondary, #999);
  margin-bottom: 8px;
}

.comment-submit-btn {
  margin-top: 8px;
}

.comment-login-tip {
  text-align: center;
  padding: 16px 0;
  font-size: 13px;
  color: var(--text-secondary, #999);
}

.comment-empty {
  text-align: center;
  color: var(--text-secondary, #999);
  padding: 30px 0;
  font-size: 13px;
}

/* 评论列表 */
.comment-list {
  margin-top: 8px;
}

.comment-item {
  border-bottom: 1px solid var(--border-light, #f0f0f0);
  padding: 14px 0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-main {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.comment-user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-username {
  font-size: 14px;
  color: var(--text-primary, #333);
}

.top-badge {
  font-size: 12px;
  color: #e6a23c;
}

.comment-time {
  font-size: 12px;
  color: #c0c4cc;
}

.comment-content-text {
  font-size: 14px;
  color: var(--text-regular, #666);
  margin: 6px 0;
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-secondary, #999);
}

.action-item {
  cursor: pointer;
  transition: color 0.2s;
}

.action-item:hover {
  color: var(--primary, #00A884);
}

.action-item--danger:hover {
  color: #f56c6c;
}

/* 子回复 */
.comment-replies {
  margin-top: 10px;
  padding-left: 16px;
  border-left: 2px solid var(--border-light, #f0f0f0);
}

.reply-item {
  display: flex;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px dashed #f5f5f5;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-avatar {
  flex-shrink: 0;
  margin-top: 2px;
}

.reply-body {
  flex: 1;
  min-width: 0;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.reply-username {
  font-size: 13px;
  color: var(--text-primary, #333);
}

.reply-to {
  font-size: 12px;
  color: var(--text-secondary, #999);
}

.reply-time {
  font-size: 11px;
  color: #c0c4cc;
}

.reply-content {
  font-size: 13px;
  color: var(--text-regular, #666);
  margin: 4px 0;
  line-height: 1.5;
}

.reply-actions {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary, #999);
}

/* ===== 凭证图片上传 ===== */
.proof-image-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.proof-image-item {
  position: relative;
  width: 80px;
  height: 80px;
}

.proof-image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--radius-md, 4px);
  border: 1px solid var(--border-base, #e8e8e8);
}

.proof-remove-btn {
  position: absolute;
  top: -6px;
  right: -6px;
}

.upload-hint {
  display: block;
  font-size: 12px;
  color: var(--text-secondary, #999);
  margin-top: 4px;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .detail-body {
    flex-direction: column;
  }

  .detail-info-panel {
    width: 100%;
  }

  .detail-title {
    font-size: 18px;
  }
}
</style>
