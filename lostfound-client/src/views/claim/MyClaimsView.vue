<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { claimApi } from '@/api/claim'
import { getClaimStatusText, formatDate } from '@/utils'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const list = ref<any[]>([])
const current = ref(1)
const total = ref(0)
const pageSize = 10
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res = await claimApi.getMyClaims(current.value, pageSize)
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } finally { loading.value = false }
}

function getStatusTagType(status: number) {
  return status === 0 ? 'warning' : status === 1 ? 'success' : 'danger'
}

// 跳转到失物详情页
function goDetail(lostItemId: number) {
  router.push('/detail/' + lostItemId)
}

onMounted(() => loadData())
</script>

<template>
  <div class="my-claims-page">
    <PageHeader
      title="我的认领申请"
      subtitle="查看您提交的所有认领申请及审核结果"
      :show-back="true"
      @back="router.push('/user/profile')"
    />

    <div v-loading="loading" class="claims-list">
      <!-- 空状态 -->
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无认领申请" />
      </div>

      <!-- 列表 -->
      <div v-for="item in list" :key="item.id" class="claim-item" @click="goDetail(item.lostItemId)">
        <div class="claim-header">
          <el-tag :type="getStatusTagType(item.status)" size="small">
            {{ getClaimStatusText(item.status) }}
          </el-tag>
          <span class="claim-id">失物 ID：{{ item.lostItemId }}</span>
          <span class="claim-arrow"><el-icon><ArrowRight /></el-icon></span>
          <span class="claim-time">{{ formatDate(item.createTime) }}</span>
        </div>
        <div v-if="item.claimDetail" class="claim-detail">
          <strong>认领说明：</strong>{{ item.claimDetail.length > 120 ? item.claimDetail.substring(0, 120) + '...' : item.claimDetail }}
        </div>
        <div v-if="item.auditRemark" class="claim-remark">
          <strong>审核备注：</strong>{{ item.auditRemark }}
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
  </div>
</template>

<style scoped>
.my-claims-page {
  max-width: 800px;
  margin: 0 auto;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

/* 列表项 */
.claim-item {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 16px 20px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
  cursor: pointer;
  transition: all 0.2s;
}

.claim-item:hover {
  border-color: var(--primary, #00A884);
  box-shadow: var(--shadow-hover, 0 4px 12px rgba(0, 0, 0, 0.08));
}

.claim-arrow {
  color: #ccc;
  font-size: 12px;
  transition: color 0.2s;
}

.claim-item:hover .claim-arrow {
  color: var(--primary, #00A884);
}

.claim-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.claim-id {
  font-size: 13px;
  color: var(--text-regular, #666);
}

.claim-time {
  font-size: 13px;
  color: var(--text-secondary, #999);
  margin-left: auto;
}

.claim-detail {
  font-size: 14px;
  color: var(--text-regular, #666);
  line-height: 1.6;
  margin-bottom: 6px;
  padding: 10px 12px;
  background: var(--bg-page, #f5f5f5);
  border-radius: var(--radius-sm, 2px);
}

.claim-remark {
  font-size: 13px;
  color: var(--text-secondary, #999);
  line-height: 1.5;
}

/* 分页 */
.pagination-wrap {
  text-align: center;
  margin-top: 24px;
}

/* 响应式 */
@media (max-width: 560px) {
  .claim-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .claim-time {
    margin-left: 0;
  }
}
</style>
