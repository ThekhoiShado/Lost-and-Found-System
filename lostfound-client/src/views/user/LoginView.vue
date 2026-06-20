<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = ref({ username: '', password: '' })
const loading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch { /* handled */ } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- Logo 区 -->
      <div class="auth-header">
        <div class="auth-logo">
          <span class="auth-logo-icon">S</span>
        </div>
        <h3 class="auth-title">用户登录</h3>
        <p class="auth-desc">欢迎回到失物招领平台</p>
      </div>

      <!-- 表单 -->
      <el-form class="auth-form" @keyup.enter="handleLogin">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="auth-submit-btn"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 60px;
  min-height: 70vh;
}

.auth-card {
  width: 400px;
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 40px 36px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

/* Logo 区 */
.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  margin-bottom: 16px;
}

.auth-logo-icon {
  display: inline-flex;
  width: 56px;
  height: 56px;
  background: var(--primary, #00A884);
  border-radius: 12px;
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  align-items: center;
  justify-content: center;
}

.auth-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary, #333);
  margin-bottom: 6px;
}

.auth-desc {
  font-size: 14px;
  color: var(--text-secondary, #999);
  margin: 0;
}

/* 表单 */
.auth-submit-btn {
  width: 100%;
}

/* 底部链接 */
.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: var(--text-secondary, #999);
}

.auth-footer a {
  color: var(--primary, #00A884);
}
</style>
