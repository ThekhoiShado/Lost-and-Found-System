<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const userStore = useUserStore()
const profile = ref<any>({})
const editing = ref(false)
const editForm = ref({ nickname: '', phone: '', email: '' })

// 头像上传
const avatarInputRef = ref<HTMLInputElement | null>(null)
const uploadingAvatar = ref(false)

function triggerAvatarUpload() {
  avatarInputRef.value?.click()
}

async function onAvatarChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  // 校验文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('仅支持 JPG/PNG/GIF/WebP 格式的头像')
    return
  }
  // 校验文件大小（2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('头像图片大小不能超过 2MB')
    return
  }

  uploadingAvatar.value = true
  try {
    const res = await userApi.uploadAvatar(file)
    const avatarUrl = res.data.data
    profile.value.avatar = avatarUrl
    userStore.updateProfile({ avatar: avatarUrl })
    ElMessage.success('头像更新成功')
  } catch (e: any) {
    console.error('头像上传失败', e)
    // 错误信息由响应拦截器统一处理
  } finally {
    uploadingAvatar.value = false
    // 重置 input，允许重新选择同一文件
    if (avatarInputRef.value) {
      avatarInputRef.value.value = ''
    }
  }
}

// 修改密码弹窗
const passwordDialog = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

async function loadProfile() {
  try {
    const res = await userApi.getProfile()
    profile.value = res.data.data
    editForm.value = {
      nickname: profile.value.nickname || '',
      phone: profile.value.phone || '',
      email: profile.value.email || ''
    }
  } catch { /* handled */ }
}

async function saveProfile() {
  try {
    await userApi.updateProfile(editForm.value)
    userStore.updateProfile({ nickname: editForm.value.nickname })
    ElMessage.success('更新成功')
    editing.value = false
    loadProfile()
  } catch { /* handled */ }
}

async function changePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.warning('密码至少6个字符')
    return
  }
  try {
    await userApi.changePassword(passwordForm.value.oldPassword, passwordForm.value.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.push('/login')
  } catch { /* handled */ }
}

onMounted(() => loadProfile())
</script>

<template>
  <div class="profile-page">
    <PageHeader title="个人中心" subtitle="管理您的个人信息和账号设置" />

    <!-- 用户概览卡片 -->
    <div class="profile-summary-card">
      <div class="avatar-upload-wrap" @click="triggerAvatarUpload">
        <el-avatar :size="64" :src="profile.avatar" class="profile-avatar">
          {{ (profile.nickname || profile.username || '?')[0] }}
        </el-avatar>
        <div class="avatar-overlay">
          <el-icon :size="20"><Camera /></el-icon>
          <span>更换头像</span>
        </div>
      </div>
      <input
        ref="avatarInputRef"
        type="file"
        accept="image/jpeg,image/png,image/gif,image/webp"
        style="display:none"
        @change="onAvatarChange"
      />
      <div class="profile-summary-info">
        <h3>{{ profile.nickname || profile.username }}</h3>
        <p class="profile-role">
          {{ profile.role === 'admin' ? '管理员' : '普通用户' }}
          <span class="info-dot">·</span>
          注册于 {{ profile.createTime }}
        </p>
      </div>
      <div class="profile-summary-stats">
        <div class="stat-item">
          <span class="stat-num">{{ profile.postCount || 0 }}</span>
          <span class="stat-label">发布</span>
        </div>
        <div class="stat-divider" />
        <div class="stat-item">
          <span class="stat-num">{{ profile.claimCount || 0 }}</span>
          <span class="stat-label">认领</span>
        </div>
      </div>
    </div>

    <!-- 基本信息 -->
    <div class="profile-card">
      <div class="profile-card-header">
        <h4 class="profile-card-title">基本信息</h4>
        <el-button v-if="!editing" size="small" @click="editing = true">编辑</el-button>
      </div>

      <div v-if="!editing">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ profile.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ profile.phone || '未绑定' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ profile.email || '未绑定' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            {{ profile.role === 'admin' ? '管理员' : '普通用户' }}
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ profile.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div v-else>
        <el-form label-width="80px">
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" placeholder="填写昵称" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" placeholder="填写手机号" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" placeholder="填写邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveProfile">保存</el-button>
            <el-button @click="editing = false">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="profile-card">
      <h4 class="profile-card-title">快捷入口</h4>
      <div class="quick-links">
        <button class="quick-link-item" @click="router.push('/user/posts')">
          <el-icon :size="28"><Document /></el-icon>
          <span>我的发布</span>
        </button>
        <button class="quick-link-item" @click="router.push('/user/claims')">
          <el-icon :size="28"><ChatLineSquare /></el-icon>
          <span>我的认领</span>
        </button>
        <button class="quick-link-item" @click="passwordDialog = true">
          <el-icon :size="28"><Lock /></el-icon>
          <span>修改密码</span>
        </button>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="passwordDialog" title="修改密码" width="400px">
      <el-form>
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-page {
  max-width: 700px;
  margin: 0 auto;
}

/* 用户概览 */
.profile-summary-card {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
  flex-wrap: wrap;
}

.profile-avatar {
  flex-shrink: 0;
}

/* 头像上传 */
.avatar-upload-wrap {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.25s;
  font-size: 12px;
  gap: 4px;
  border-radius: 50%;
}

.avatar-overlay:hover {
  opacity: 1;
}

.profile-summary-info {
  flex: 1;
  min-width: 150px;
}

.profile-summary-info h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary, #333);
  margin-bottom: 4px;
}

.profile-role {
  font-size: 13px;
  color: var(--text-secondary, #999);
  margin: 0;
}

.info-dot {
  margin: 0 6px;
}

/* 统计 */
.profile-summary-stats {
  display: flex;
  align-items: center;
  gap: 0;
  padding: 12px 20px;
  background: var(--bg-page, #f5f5f5);
  border-radius: var(--radius-md, 4px);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 16px;
}

.stat-num {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary, #00A884);
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary, #999);
  margin-top: 2px;
}

.stat-divider {
  width: 1px;
  height: 36px;
  background: #ddd;
}

/* 卡片 */
.profile-card {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

.profile-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.profile-card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary, #333);
  display: flex;
  align-items: center;
  gap: 8px;
}

.profile-card-title::before {
  content: '';
  display: inline-block;
  width: 3px;
  height: 14px;
  background: var(--primary, #00A884);
  border-radius: 2px;
}

/* 快捷入口 */
.quick-links {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.quick-link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 28px;
  background: var(--bg-page, #f5f5f5);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  color: var(--text-regular, #666);
  font-family: inherit;
}

.quick-link-item:hover {
  background: rgba(0, 168, 132, 0.05);
  border-color: var(--primary, #00A884);
  color: var(--primary, #00A884);
}

/* 响应式 */
@media (max-width: 768px) {
  .profile-summary-card {
    flex-direction: column;
    text-align: center;
  }

  .profile-summary-stats {
    width: 100%;
    justify-content: center;
  }

  .quick-links {
    flex-direction: column;
  }
}
</style>
