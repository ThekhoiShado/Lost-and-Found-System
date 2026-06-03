<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { lostApi } from '@/api/lost'
import { getTypeText, getTypeColor, getStatusText, getStatusColor, formatDate } from '@/utils'
import { ElMessageBox, ElMessage } from 'element-plus'

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
  <div style="max-width:900px;margin:0 auto;">
    <h2 class="page-title">📋 我的发布</h2>

    <div v-loading="loading">
      <div v-if="list.length === 0 && !loading" style="text-align:center;padding:60px;">
        <el-empty description="暂无发布">
          <el-button type="primary" @click="router.push('/publish')">去发布</el-button>
        </el-empty>
      </div>

      <div v-for="item in list" :key="item.id" class="card" style="padding:16px;cursor:pointer;" @click="goDetail(item.id)">
        <div style="display:flex;justify-content:space-between;align-items:start;">
          <div style="flex:1;">
            <div style="margin-bottom:8px;">
              <el-tag :color="getTypeColor(item.type)" style="color:#fff;border:none;" size="small">
                {{ getTypeText(item.type) }}
              </el-tag>
              <el-tag :color="getStatusColor(item.status)" style="color:#fff;border:none;margin-left:8px;" size="small">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
            <h4>{{ item.title }}</h4>
            <div style="color:#909399;font-size:13px;">{{ formatDate(item.createTime) }}</div>
          </div>
          <div style="display:flex;gap:8px;" @click.stop>
            <el-button size="small" @click="editItem(item.id)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteItem(item.id)">删除</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
