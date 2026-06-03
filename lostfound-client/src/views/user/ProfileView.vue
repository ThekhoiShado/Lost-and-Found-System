<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const profile = ref<any>({})
const editing = ref(false)
const editForm = ref({ nickname: '', phone: '', email: '' })

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
  <div style="max-width:700px;margin:0 auto;">
    <h2 class="page-title">个人中心</h2>

    <div class="card" style="padding:24px;margin-bottom:20px;">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
        <h4>基本信息</h4>
        <el-button v-if="!editing" size="small" @click="editing = true">编辑</el-button>
      </div>

      <div v-if="!editing">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ profile.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ profile.phone || '未绑定' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ profile.email || '未绑定' }}</el-descriptions-item>
          <el-descriptions-item label="角色">{{ profile.role === 'admin' ? '管理员' : '普通用户' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ profile.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div v-else>
        <el-form label-width="80px">
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveProfile">保存</el-button>
            <el-button @click="editing = false">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="card" style="padding:24px;margin-bottom:20px;">
      <h4 style="margin-bottom:16px;">快捷入口</h4>
      <div style="display:flex;gap:12px;flex-wrap:wrap;">
        <el-button @click="router.push('/user/posts')">📋 我的发布</el-button>
        <el-button @click="router.push('/user/claims')">📝 我的认领申请</el-button>
        <el-button type="warning" @click="passwordDialog = true">🔑 修改密码</el-button>
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
