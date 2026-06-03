<script setup lang="ts">
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="page-container">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="inner">
        <router-link to="/" class="logo">
          🔍 失物招领
        </router-link>
        <div class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link to="/publish" v-if="userStore.isLoggedIn">发布信息</router-link>
          <template v-if="userStore.isLoggedIn">
            <router-link to="/user/profile">{{ userStore.nickname || userStore.username }}</router-link>
            <a href="javascript:void(0)" @click="handleLogout">退出</a>
          </template>
          <template v-else>
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </template>
        </div>
      </div>
    </nav>

    <!-- 主内容区域 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer style="text-align:center;padding:20px;color:#909399;font-size:13px;">
      © 2024 失物招领系统 - 让遗失的美好回到身边
    </footer>
  </div>
</template>
