<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { lostApi } from '@/api/lost'
import { getTypeText, getTypeColor, getStatusText, getStatusColor, formatDate } from '@/utils'
import { ElMessageBox, ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const list = ref<any[]>([])
const current = ref(1)
const total = ref(0)
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res = await lostApi.getMyPosts(current.value)
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } finally { loading.value = false }
}

function goDetail(id: number) {
  router.push(`/detail/${id}`)
}

function editItem(id: number) {
  router.push(`/publish/${id}`)
}

async function deleteItem(id: number) {
  try {
    await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
    await lostApi.delete(id)
    ElMessage.success('已删除')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => loadData())
</script>

<template>
  <div class="my-posts-page">
    <PageHeader
      title="我的发布"
      subtitle="管理您发布的所有失物招领和寻物启事信息"
      :show-back="true"
      @back="router.push('/user/profile')"
    />

    <div v-loading="loading" class="posts-list">
      <!-- 空状态 -->
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无发布">
          <el-button type="primary" @click="router.push('/publish')">去发布</el-button>
        </el-empty>
      </div>

      <!-- 列表 -->
      <div
        v-for="item in list"
        :key="item.id"
        class="post-item"
        @click="goDetail(item.id)"
      >
        <div class="post-info">
          <div class="post-tags">
            <el-tag :color="getTypeColor(item.type)" effect="dark" size="small">
              {{ getTypeText(item.type) }}
            </el-tag>
            <el-tag :color="getStatusColor(item.status)" effect="dark" size="small">
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>
          <h4 class="post-title">{{ item.title }}</h4>
          <p class="post-date">{{ formatDate(item.createTime) }}</p>
        </div>
        <div class="post-actions" @click.stop>
          <el-button size="small" @click="editItem(item.id)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>
          <el-button size="small" type="danger" @click="deleteItem(item.id)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.my-posts-page {
  max-width: 800px;
  margin: 0 auto;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

/* 列表项 */
.post-item {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 16px 20px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

.post-item:hover {
  border-color: var(--primary, #00A884);
  box-shadow: var(--shadow-hover, 0 4px 12px rgba(0, 0, 0, 0.08));
}

.post-info {
  flex: 1;
  min-width: 0;
}

.post-tags {
  display: flex;
  gap: 4px;
  margin-bottom: 8px;
}

.post-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary, #333);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-date {
  font-size: 13px;
  color: var(--text-secondary, #999);
  margin: 0;
}

.post-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
  margin-left: 16px;
}

/* 响应式 */
@media (max-width: 560px) {
  .post-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .post-actions {
    margin-left: 0;
    width: 100%;
  }

  .post-actions .el-button {
    flex: 1;
  }
}
</style>
