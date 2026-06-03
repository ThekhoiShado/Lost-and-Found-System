<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { lostApi } from '@/api/lost'
import { commentApi } from '@/api/comment'
import { claimApi } from '@/api/claim'
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
  claimDetail: ''
})

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
  replyTo.value = { id: comment.id, username: comment.username || comment.nickname }
  commentText.value = ''
  // 滚动到评论框
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

// 编辑
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
  <div v-loading="loading">
    <div v-if="item">
      <!-- 标题和操作 -->
      <div style="display:flex;justify-content:space-between;align-items:start;margin-bottom:16px;flex-wrap:wrap;">
        <div>
          <el-tag :color="getTypeColor(item.type)" style="color:#fff;border:none;" size="small">
            {{ getTypeText(item.type) }}
          </el-tag>
          <el-tag :color="getStatusColor(item.status)" style="color:#fff;border:none;margin-left:8px;" size="small">
            {{ getStatusText(item.status) }}
          </el-tag>
          <h2 style="margin:12px 0 8px;">{{ item.title }}</h2>
          <div style="color:#909399;font-size:13px;">
            <span>{{ item.nickname || item.username }}</span>
            <span style="margin:0 8px;">|</span>
            <span>{{ formatDate(item.createTime) }}</span>
            <span v-if="item.location" style="margin:0 8px;">|</span>
            <span v-if="item.location">📍 {{ item.location }}</span>
            <span style="margin:0 8px;">|</span>
            <span>👁 {{ (item.viewCount || 0) + 1 }}</span>
          </div>
        </div>
        <div v-if="isOwner" style="display:flex;gap:8px;">
          <el-button size="small" @click="editItem">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteItem">删除</el-button>
        </div>
      </div>

      <!-- 图片 -->
      <div v-if="item.coverImage" style="margin-bottom:16px;">
        <img :src="item.coverImage" style="max-width:100%;max-height:400px;border-radius:8px;" />
      </div>

      <!-- 富文本内容 -->
      <div class="card" style="padding:24px;margin-bottom:16px;">
        <div class="rich-text-content" v-html="item.content"></div>
      </div>

      <!-- 联系信息 -->
      <div class="card" style="padding:16px;margin-bottom:16px;background:#f0f9ff;">
        <p><strong>📞 联系方式：</strong>{{ item.contact }}</p>
        <p v-if="item.lostDate"><strong>📅 日期：</strong>{{ item.lostDate }}</p>
        <p v-if="item.category"><strong>📂 分类：</strong>{{ item.category }}</p>
      </div>

      <!-- 操作按钮 -->
      <div v-if="userStore.isLoggedIn && !isOwner && item.status === 1" style="margin-bottom:24px;">
        <el-button type="primary" @click="claimDialogVisible = true">📝 提交认领申请</el-button>
      </div>

      <!-- 评论区域 -->
      <div class="card" style="padding:20px;margin-top:24px;">
        <h3 style="margin-bottom:16px;">💬 留言板 ({{ comments.length }})</h3>

        <!-- 评论输入 -->
        <div v-if="userStore.isLoggedIn" style="margin-bottom:20px;" id="comment-input">
          <div v-if="replyTo" style="margin-bottom:8px;color:#909399;font-size:13px;">
            回复 @{{ replyTo.username }}
            <el-button link type="danger" size="small" @click="replyTo = null">取消</el-button>
          </div>
          <el-input v-model="commentText" type="textarea" :rows="3" placeholder="写下你的留言..." />
          <el-button type="primary" size="small" style="margin-top:8px;" @click="submitComment">
            {{ replyTo ? '回复' : '发表评论' }}
          </el-button>
        </div>
        <div v-else style="margin-bottom:20px;text-align:center;">
          <router-link to="/login">登录</router-link> 后参与评论
        </div>

        <!-- 评论列表 -->
        <div v-if="comments.length === 0" style="text-align:center;color:#909399;padding:20px;">
          暂无留言，来写第一条吧
        </div>
        <div v-else>
          <div v-for="comment in comments" :key="comment.id" style="border-bottom:1px solid #f0f0f0;padding:12px 0;">
            <div style="display:flex;gap:10px;">
              <el-avatar :size="36" :src="comment.avatar">
                {{ (comment.nickname || comment.username || '?')[0] }}
              </el-avatar>
              <div style="flex:1;">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                  <div>
                    <strong>{{ comment.nickname || comment.username }}</strong>
                    <span v-if="comment.top === 1" style="color:#e6a23c;margin-left:8px;">⭐ 置顶</span>
                  </div>
                  <span style="font-size:12px;color:#c0c4cc;">{{ formatDate(comment.createTime) }}</span>
                </div>
                <p style="margin:6px 0;">{{ comment.content }}</p>
                <div style="display:flex;gap:16px;font-size:13px;color:#909399;">
                  <span style="cursor:pointer;" @click="toggleLike(comment.id)">
                    👍 {{ comment.likeCount || 0 }}
                  </span>
                  <span style="cursor:pointer;" @click="replyComment(comment)">💬 回复</span>
                  <span v-if="userStore.userId === comment.userId"
                        style="cursor:pointer;color:#f56c6c;" @click="deleteComment(comment.id)">删除</span>
                </div>

                <!-- 子回复 -->
                <div v-if="comment.children?.length" style="margin-top:8px;padding-left:16px;border-left:2px solid #f0f0f0;">
                  <div v-for="reply in comment.children" :key="reply.id" style="padding:8px 0;border-bottom:1px dashed #f5f5f5;">
                    <strong>{{ reply.nickname || reply.username }}</strong>
                    <span v-if="reply.replyToUsername" style="color:#909399;"> 回复 @{{ reply.replyToUsername }}</span>
                    <span style="font-size:12px;color:#c0c4cc;margin-left:8px;">{{ formatDate(reply.createTime) }}</span>
                    <p style="margin:4px 0;">{{ reply.content }}</p>
                    <div style="font-size:12px;color:#909399;">
                      <span style="cursor:pointer;" @click="toggleLike(reply.id)">👍 {{ reply.likeCount || 0 }}</span>
                      <span v-if="userStore.userId === reply.userId"
                            style="cursor:pointer;color:#f56c6c;margin-left:12px;" @click="deleteComment(reply.id)">删除</span>
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
            <el-input v-model="claimForm.claimDetail" type="textarea" :rows="4"
                      placeholder="请详细描述您与该物品的关系或提供凭证信息" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="claimDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitClaim">提交申请</el-button>
        </template>
      </el-dialog>
    </div>

    <div v-else-if="!loading" style="text-align:center;padding:60px;">
      <el-empty description="信息不存在或已删除" />
    </div>
  </div>
</template>
