<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { lostApi } from '@/api/lost'
import { getTypeText, getTypeColor, getStatusColor, truncateText, formatDate } from '@/utils'
import SidebarSection from '@/components/SidebarSection.vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const list = ref<any[]>([])
const current = ref(1)
const total = ref(0)
const pageSize = 12

const filterType = ref<number | undefined>(undefined)
const filterCategory = ref('')
const keyword = ref('')

const categories = ['证件', '电子产品', '钥匙', '钱包', '衣物', '其他']

// Banner 轮播数据
const banners = [
  {
    title: '失物招领平台',
    subtitle: '找回遗失的美好，连接你我的善意',
    bg: 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)'
  },
  {
    title: '发布失物信息',
    subtitle: '捡到物品？发布招领信息，帮助失主找回',
    bg: 'linear-gradient(135deg, #008c6e 0%, #00A884 100%)'
  },
  {
    title: '寻物启事',
    subtitle: '丢失物品？发布寻物信息，让更多人帮助寻找',
    bg: 'linear-gradient(135deg, #e67e22 0%, #f39c12 100%)'
  }
]

async function loadData() {
  loading.value = true
  try {
    const res = await lostApi.getList({
      current: current.value,
      size: pageSize,
      type: filterType.value,
      category: filterCategory.value || undefined,
      keyword: keyword.value || undefined
    })
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  current.value = 1
  loadData()
}

function goDetail(id: number) {
  router.push(`/detail/${id}`)
}

onMounted(() => loadData())
</script>

<template>
  <div class="home-page">
    <!-- Banner 轮播区 -->
    <div class="banner-section">
      <el-carousel :interval="4000" height="280px" class="home-carousel">
        <el-carousel-item v-for="(banner, i) in banners" :key="i">
          <div class="banner-slide" :style="{ background: banner.bg }">
            <div class="banner-content">
              <h2 class="banner-title">{{ banner.title }}</h2>
              <p class="banner-subtitle">{{ banner.subtitle }}</p>
              <router-link to="/publish" class="banner-btn">发布信息</router-link>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 主布局：左侧内容 + 右侧侧边栏 -->
    <div class="home-layout">
      <!-- 左侧主内容区 -->
      <div class="home-main">
        <!-- 搜索筛选栏 -->
        <div class="search-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索标题或内容..."
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <el-select v-model="filterType" placeholder="全部类型" clearable class="filter-select">
            <el-option :value="1" label="失物招领" />
            <el-option :value="2" label="寻物启事" />
          </el-select>
          <el-select v-model="filterCategory" placeholder="全部分类" clearable class="filter-select">
            <el-option v-for="c in categories" :key="c" :value="c" :label="c" />
          </el-select>
          <el-button type="primary" class="search-btn" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
        </div>

        <!-- 卡片网格 -->
        <div v-loading="loading" class="card-grid-wrap">
          <div v-if="list.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无相关信息" />
          </div>
          <div v-else class="card-grid">
            <div
              v-for="item in list"
              :key="item.id"
              class="item-card"
              @click="goDetail(item.id)"
            >
              <!-- 封面图 -->
              <div v-if="item.coverImage" class="item-card-image">
                <img :src="item.coverImage" :alt="item.title" loading="lazy" />
              </div>
              <div v-else class="item-card-image item-card-image--placeholder">
                <el-icon :size="32"><PictureFilled /></el-icon>
              </div>

              <!-- 卡片内容 -->
              <div class="item-card-body">
                <div class="item-card-tags">
                  <el-tag :color="getTypeColor(item.type)" effect="dark" size="small">
                    {{ getTypeText(item.type) }}
                  </el-tag>
                  <el-tag :color="getStatusColor(item.status)" effect="dark" size="small">
                    {{ item.status === 1 ? '已发布' : item.status === 2 ? '已认领' : item.status === 3 ? '已结束' : '待审核' }}
                  </el-tag>
                </div>
                <h3 class="item-card-title">{{ item.title }}</h3>
                <p class="item-card-desc">{{ truncateText(item.content, 60) }}</p>
                <div class="item-card-meta">
                  <span v-if="item.category">
                    <el-icon><Folder /></el-icon> {{ item.category }}
                  </span>
                  <span v-if="item.location">
                    <el-icon><Location /></el-icon> {{ item.location }}
                  </span>
                  <span class="meta-time">{{ formatDate(item.createTime, 'MM-dd') }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="total > pageSize" class="pagination-wrap">
          <el-pagination
            v-model:current-page="current"
            :total="total"
            :page-size="pageSize"
            layout="prev, pager, next"
            @current-change="loadData"
          />
        </div>
      </div>

      <!-- 右侧侧边栏 -->
      <aside class="home-sidebar">
        <SidebarSection title="关于我们">
          <p>失物招领平台致力于帮助大家找回遗失的物品。无论捡到物品还是丢失物品，都可以在这里发布信息。</p>
        </SidebarSection>

        <SidebarSection title="联系我们">
          <p>如有问题或建议，欢迎通过以下方式联系我们：</p>
          <p style="margin-top:6px;">📧 邮箱：support@lostfound.com</p>
        </SidebarSection>

        <SidebarSection title="快捷链接">
          <div class="sidebar-links">
            <template v-if="userStore.isLoggedIn">
              <router-link to="/publish">📝 发布失物信息</router-link>
              <router-link to="/user/posts">📋 我的发布</router-link>
              <router-link to="/user/claims">📄 认领记录</router-link>
            </template>
            <template v-else>
              <router-link to="/register">📝 注册账号</router-link>
              <router-link to="/login">🔑 立即登录</router-link>
            </template>
          </div>
        </SidebarSection>

        <SidebarSection title="热门分类">
          <div class="sidebar-tags">
            <el-tag
              v-for="tag in categories"
              :key="tag"
              size="small"
              class="sidebar-tag"
              @click="filterCategory = tag; handleSearch()"
            >
              {{ tag }}
            </el-tag>
          </div>
        </SidebarSection>
      </aside>
    </div>
  </div>
</template>

<style scoped>
/* ===== Banner ===== */
.banner-section {
  margin-bottom: 24px;
  border-radius: var(--radius-md, 4px);
  overflow: hidden;
}

.banner-slide {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.banner-content {
  text-align: center;
  color: #fff;
  padding: 0 20px;
}

.banner-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 2px;
}

.banner-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.banner-btn {
  display: inline-block;
  padding: 10px 36px;
  background: #fff;
  color: var(--primary, #00A884);
  border-radius: 4px;
  text-decoration: none;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.2s;
}

.banner-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-1px);
}

/* ===== 左右布局 ===== */
.home-layout {
  display: flex;
  gap: 24px;
}

.home-main {
  flex: 1;
  min-width: 0;
}

/* ===== 搜索筛选栏 ===== */
.search-bar {
  background: #edf0f4;
  border-radius: 6px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 180px;
}

.filter-select {
  width: 130px;
  flex-shrink: 0;
}

/* ===== 卡片网格 ===== */
.card-grid-wrap {
  min-height: 300px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

/* 单张卡片 */
.item-card {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

.item-card:hover {
  box-shadow: var(--shadow-hover, 0 4px 12px rgba(0, 0, 0, 0.08));
  transform: translateY(-2px);
}

/* 封面图 */
.item-card-image {
  height: 160px;
  overflow: hidden;
  background: #f0f0f0;
}

.item-card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s;
}

.item-card:hover .item-card-image img {
  transform: scale(1.05);
}

.item-card-image--placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

/* 卡片内容 */
.item-card-body {
  padding: 12px 14px 14px;
}

.item-card-tags {
  display: flex;
  gap: 4px;
  margin-bottom: 8px;
}

.item-card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary, #333);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-card-desc {
  font-size: 13px;
  color: var(--text-secondary, #999);
  margin-bottom: 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 39px;
}

.item-card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #bbb;
  flex-wrap: wrap;
}

.item-card-meta span {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.meta-time {
  margin-left: auto;
}

/* ===== 分页 ===== */
.pagination-wrap {
  text-align: center;
  margin-top: 32px;
}

/* ===== 侧边栏 ===== */
.home-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.sidebar-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sidebar-links a {
  font-size: 13px;
  color: var(--text-regular, #666);
  text-decoration: none;
  transition: color 0.2s;
  padding: 4px 0;
}

.sidebar-links a:hover {
  color: var(--primary, #00A884);
}

.sidebar-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.sidebar-tag {
  cursor: pointer;
}

/* ===== 响应式 ===== */
@media (max-width: 1100px) {
  .card-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 860px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .home-sidebar {
    display: none;
  }
  .banner-title {
    font-size: 22px;
  }
  .banner-subtitle {
    font-size: 14px;
  }
}

@media (max-width: 560px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
  .search-bar {
    padding: 12px;
    gap: 8px;
  }
  .filter-select {
    width: 100%;
  }
  .banner-section .el-carousel {
    height: 200px;
  }
  .banner-title {
    font-size: 18px;
  }
}
</style>
