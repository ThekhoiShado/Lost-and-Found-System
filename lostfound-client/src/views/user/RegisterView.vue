<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  target: '',
  code: '',
  nickname: ''
})
const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)

async function sendCode() {
  if (!form.value.target) {
    ElMessage.warning('请输入手机号或邮箱')
    return
  }
  if (countdown.value > 0) return

  sendingCode.value = true
  try {
    await authApi.sendCode(form.value.target, 'register')
    ElMessage.success('验证码已发送（查看后端日志）')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch { /* handled */ } finally {
    sendingCode.value = false
  }
}

async function handleRegister() {
  if (!form.value.username || !form.value.password || !form.value.target || !form.value.code) {
    ElMessage.warning('请填写所有必填项')
    return
  }
  if (form.value.username.length < 3) {
    ElMessage.warning('用户名至少3个字符')
    return
  }
  if (form.value.password.length < 6) {
    ElMessage.warning('密码至少6个字符')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  loading.value = true
  try {
    await authApi.register({
      username: form.value.username,
      password: form.value.password,
      target: form.value.target,
      code: form.value.code,
      nickname: form.value.nickname || undefined
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
        <h3 class="auth-title">用户注册</h3>
        <p class="auth-desc">创建账号，开始使用失物招领平台</p>
      </div>

      <!-- 表单 -->
      <el-form class="auth-form" @keyup.enter="handleRegister">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="用户名（登录用）"
            size="large"
            maxlength="50"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.nickname"
            placeholder="昵称（选填）"
            size="large"
            maxlength="100"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.target"
            placeholder="手机号 或 邮箱"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <div class="code-row">
            <el-input
              v-model="form.code"
              placeholder="验证码"
              size="large"
              maxlength="6"
              class="code-input"
            />
            <el-button
              size="large"
              :loading="sendingCode"
              :disabled="countdown > 0"
              class="code-btn"
              @click="sendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码（6位以上）"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="auth-submit-btn"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-tip">
        开发环境验证码：888888（万能验证码）
      </div>
      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 40px;
  min-height: 70vh;
}

.auth-card {
  width: 420px;
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 36px 36px 28px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

/* Logo 区 */
.auth-header {
  text-align: center;
  margin-bottom: 28px;
}

.auth-logo {
  margin-bottom: 14px;
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

/* 验证码行 */
.code-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.code-input {
  flex: 1;
}

.code-btn {
  min-width: 120px;
  flex-shrink: 0;
}

/* 提示和链接 */
.auth-tip {
  text-align: center;
  color: var(--text-secondary, #999);
  font-size: 12px;
  margin-bottom: 8px;
}

.auth-footer {
  text-align: center;
  font-size: 13px;
  color: var(--text-secondary, #999);
}

.auth-footer a {
  color: var(--primary, #00A884);
}

/* 响应式 */
@media (max-width: 480px) {
  .auth-card {
    width: 100%;
    padding: 28px 20px 20px;
    margin: 0 12px;
  }
}
</style>
