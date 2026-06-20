<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'
import logo from '@/assets/header-logo.png'

const userStore = useUserStore()
const router = useRouter()

// 移动端菜单开关
const mobileMenuVisible = ref(false)

function handleLogout() {
  userStore.logout()
  router.push('/')
}

function toggleMobileMenu() {
  mobileMenuVisible.value = !mobileMenuVisible.value
}

function closeMobileMenu() {
  mobileMenuVisible.value = false
}
</script>

<template>
  <div class="page-container">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="navbar-inner">
        <!-- Logo -->
        <router-link to="/" class="navbar-logo">
          <img :src="logo" alt="失物招领" class="logo-image" />
          <span class="logo-text">失物招领</span>
        </router-link>

        <!-- 桌面端导航链接 -->
        <div class="navbar-links desktop-nav">
          <router-link to="/" class="nav-link" exact-active-class="nav-link--active">首页</router-link>
          <router-link v-if="userStore.isLoggedIn" to="/publish" class="nav-link" active-class="nav-link--active">
            发布信息
          </router-link>

          <!-- 已登录：用户下拉菜单 -->
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="hover" class="user-dropdown" @command="(cmd: string) => router.push(cmd)">
              <span class="user-dropdown-trigger">
                <el-avatar :size="28" :src="userStore.avatar || undefined">
                  {{ (userStore.nickname || userStore.username || '?')[0] }}
                </el-avatar>
                <span class="username-text">{{ userStore.nickname || userStore.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="/user/profile">
                    <el-icon><User /></el-icon> 个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="/user/posts">
                    <el-icon><Document /></el-icon> 我的发布
                  </el-dropdown-item>
                  <el-dropdown-item command="/user/claims">
                    <el-icon><ChatLineSquare /></el-icon> 我的认领
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <!-- 未登录 -->
          <template v-else>
            <router-link to="/login" class="nav-link nav-btn-outline">登录</router-link>
            <router-link to="/register" class="nav-link nav-btn-fill">注册</router-link>
          </template>
        </div>

        <!-- 移动端汉堡菜单按钮 -->
        <button class="mobile-menu-toggle" @click="toggleMobileMenu" aria-label="菜单">
          <el-icon :size="22"><Expand v-if="!mobileMenuVisible" /><Fold v-else /></el-icon>
        </button>
      </div>

      <!-- 移动端下拉菜单 -->
      <div v-show="mobileMenuVisible" class="mobile-menu" @click="closeMobileMenu">
        <router-link to="/" class="mobile-link">首页</router-link>
        <router-link v-if="userStore.isLoggedIn" to="/publish" class="mobile-link">发布信息</router-link>
        <template v-if="userStore.isLoggedIn">
          <router-link to="/user/profile" class="mobile-link">个人中心</router-link>
          <router-link to="/user/posts" class="mobile-link">我的发布</router-link>
          <router-link to="/user/claims" class="mobile-link">我的认领</router-link>
          <a href="javascript:void(0)" class="mobile-link mobile-link--logout" @click="handleLogout">退出登录</a>
        </template>
        <template v-else>
          <router-link to="/login" class="mobile-link">登录</router-link>
          <router-link to="/register" class="mobile-link">注册</router-link>
        </template>
      </div>
    </nav>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-links">
          <router-link to="/">首页</router-link>
          <span class="footer-divider">|</span>
          <router-link v-if="userStore.isLoggedIn" to="/publish">发布信息</router-link>
          <template v-if="userStore.isLoggedIn">
            <span class="footer-divider">|</span>
            <router-link to="/user/profile">个人中心</router-link>
          </template>
          <template v-else>
            <span class="footer-divider">|</span>
            <router-link to="/login">登录</router-link>
            <span class="footer-divider">|</span>
            <router-link to="/register">注册</router-link>
          </template>
        </div>
        <p class="footer-text">&copy; 2024 失物招领系统 - 让遗失的美好回到身边</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
/* ===== 导航栏 ===== */
.navbar {
  background: var(--nav-bg, #2c3e50);
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 64px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.navbar-inner {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

/* Logo */
.navbar-logo {
  display: flex;
  align-items: center;
  gap: 14px;
  text-decoration: none;
  flex-shrink: 0;
}

.logo-image {
  width: 44px;
  height: 44px;
  border-radius: 6px;
  display: block;
  object-fit: contain;
  object-position: center;
  padding: 2px;
  background: transparent;
}

.logo-text {
  margin-left: 2px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 1px;
}

/* 桌面导航 */
.navbar-links {
  display: flex;
  align-items: center;
  gap: 4px;
}

.desktop-nav {
  display: flex;
}

.nav-link {
  color: var(--nav-text-dim, rgba(255, 255, 255, 0.85));
  text-decoration: none;
  font-size: 15px;
  padding: 8px 14px;
  border-radius: 4px;
  transition: all 0.2s ease;
  position: relative;
}

.nav-link:hover {
  color: #ffffff;
  background: var(--nav-hover-bg, rgba(255, 255, 255, 0.1));
}

.nav-link--active {
  color: #ffffff;
}

.nav-link--active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 14px;
  right: 14px;
  height: 3px;
  background: var(--nav-active, #1ab394);
  border-radius: 3px 3px 0 0;
}

/* 按钮样式的导航链接 */
.nav-btn-outline {
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 6px 18px;
}

.nav-btn-outline:hover {
  border-color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
}

.nav-btn-fill {
  background: var(--primary, #00A884) !important;
  border: 1px solid var(--primary, #00A884);
  color: #fff !important;
  padding: 6px 18px;
}

.nav-btn-fill:hover {
  background: var(--primary-light, #1ab394) !important;
  border-color: var(--primary-light, #1ab394);
}

/* 用户下拉菜单 */
.user-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 4px;
  transition: background 0.2s;
  color: var(--nav-text-dim, rgba(255, 255, 255, 0.85));
}

.user-dropdown-trigger:hover {
  background: var(--nav-hover-bg, rgba(255, 255, 255, 0.1));
  color: #ffffff;
}

.username-text {
  font-size: 14px;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 移动端菜单按钮 */
.mobile-menu-toggle {
  display: none;
  background: none;
  border: none;
  color: #fff;
  cursor: pointer;
  padding: 4px;
}

.mobile-menu {
  display: none;
  position: absolute;
  top: 64px;
  left: 0;
  right: 0;
  background: var(--nav-bg, #2c3e50);
  padding: 8px 20px 16px;
  flex-direction: column;
  gap: 4px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 999;
}

.mobile-link {
  color: var(--nav-text-dim, rgba(255, 255, 255, 0.85));
  text-decoration: none;
  font-size: 15px;
  padding: 10px 8px;
  display: block;
  border-radius: 4px;
  transition: background 0.2s;
}

.mobile-link:hover {
  background: var(--nav-hover-bg, rgba(255, 255, 255, 0.1));
  color: #ffffff;
}

.mobile-link--logout {
  color: #f56c6c;
}

/* ===== 主内容区 ===== */

/* ===== 页脚 ===== */
.footer {
  background: var(--nav-bg-dark, #1a252f);
  padding: 32px 20px 24px;
  margin-top: 40px;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.footer-links {
  margin-bottom: 12px;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.55);
  font-size: 13px;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: #ffffff;
}

.footer-divider {
  color: rgba(255, 255, 255, 0.2);
  margin: 0 10px;
  font-size: 12px;
}

.footer-text {
  color: rgba(255, 255, 255, 0.35);
  font-size: 12px;
  margin: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }

  .mobile-menu-toggle {
    display: block;
  }

  .mobile-menu {
    display: flex;
  }

  .navbar {
    height: 56px;
  }

  .mobile-menu {
    top: 56px;
  }

  .logo-text {
    font-size: 17px;
  }
  .logo-image {
    width: 32px;
    height: 32px;
  }
}
</style>
