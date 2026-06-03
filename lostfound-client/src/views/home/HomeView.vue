<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { lostApi } from '@/api/lost'
import { getTypeText, getTypeColor, getStatusColor, truncateText, formatDate } from '@/utils'

const router = useRouter()
const loading = ref(false)
const list = ref<any[]>([])
const current = ref(1)
const total = ref(0)
const pageSize = 12

const filterType = ref<number | undefined>(undefined)
const filterCategory = ref('')
const keyword = ref('')

const categories = ['证件', '电子产品', '钥匙', '钱包', '衣物', '其他']

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
  <div>
    <!-- 搜索栏 -->
    <div class="search-section" style="text-align:center;padding:30px 0;">
      <h2 style="margin-bottom:20px;">🔍 失物招领平台</h2>
      <p style="color:#909399;margin-bottom:20px;">找回遗失的美好，连接你我的善意</p>
      <div style="display:flex;gap:10px;justify-content:center;flex-wrap:wrap;">
        <el-input v-model="keyword" placeholder="搜索标题或内容..." style="max-width:400px;" @keyup.enter="handleSearch" clearable />
        <el-select v-model="filterType" placeholder="全部类型" clearable style="width:140px">
          <el-option :value="1" label="失物招领" />
          <el-option :value="2" label="寻物启事" />
        </el-select>
        <el-select v-model="filterCategory" placeholder="全部分类" clearable style="width:120px">
          <el-option v-for="c in categories" :key="c" :value="c" :label="c" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <!-- 列表 -->
    <div v-loading="loading" style="min-height:300px;">
      <div v-if="list.length === 0 && !loading" style="text-align:center;padding:60px;color:#909399;">
        <el-empty description="暂无相关信息" />
      </div>
      <div v-else style="display:grid;grid-template-columns:repeat(auto-fill,minmax(340px,1fr));gap:16px;">
        <div v-for="item in list" :key="item.id" class="card" style="cursor:pointer;" @click="goDetail(item.id)">
          <div v-if="item.coverImage" style="height:180px;overflow:hidden;background:#f0f0f0;">
            <img :src="item.coverImage" :alt="item.title" style="width:100%;height:100%;object-fit:cover;" />
          </div>
          <div style="padding:16px;">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
              <span :style="{color:getTypeColor(item.type),fontSize:'12px',fontWeight:'bold'}">
                {{ getTypeText(item.type) }}
              </span>
              <span :style="{background:getStatusColor(item.status),color:'#fff',padding:'2px 8px',borderRadius:'4px',fontSize:'11px'}">
                {{ item.status === 1 ? '已发布' : item.status === 2 ? '已认领' : item.status === 3 ? '已结束' : '待审核' }}
              </span>
            </div>
            <h4 style="margin-bottom:8px;font-size:16px;">{{ item.title }}</h4>
            <p style="color:#909399;font-size:13px;margin-bottom:8px;">{{ truncateText(item.content, 80) }}</p>
            <div style="display:flex;justify-content:space-between;font-size:12px;color:#c0c4cc;">
              <span v-if="item.category">📂 {{ item.category }}</span>
              <span v-if="item.location">📍 {{ item.location }}</span>
              <span>{{ formatDate(item.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" style="text-align:center;margin-top:24px;">
      <el-pagination
        v-model:current-page="current"
        :total="total"
        :page-size="pageSize"
        layout="prev, pager, next"
        @current-change="loadData"
      />
    </div>
  </div>
</template>
