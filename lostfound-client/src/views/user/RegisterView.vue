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
    // 倒计时
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
  <div style="max-width:440px;margin:40px auto;">
    <div class="card" style="padding:40px;">
      <h3 style="text-align:center;margin-bottom:30px;">用户注册</h3>
      <el-form>
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名（登录用）" size="large" maxlength="50" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.nickname" placeholder="昵称（选填）" size="large" maxlength="100" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.target" placeholder="手机号 或 邮箱" size="large" />
        </el-form-item>
        <el-form-item>
          <div style="display:flex;gap:8px;width:100%;">
            <el-input v-model="form.code" placeholder="验证码" size="large" style="flex:1;" maxlength="6" />
            <el-button size="large" :loading="sendingCode" :disabled="countdown > 0" @click="sendCode" style="min-width:120px;">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码（6位以上）" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large"
                    show-password @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" style="width:100%;" @click="handleRegister">
            注 册
          </el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center;color:#909399;font-size:12px;">
        开发环境验证码：888888（万能验证码）
      </div>
      <div style="text-align:center;margin-top:8px;">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>
