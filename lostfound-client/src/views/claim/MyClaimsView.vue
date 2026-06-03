<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { claimApi } from '@/api/claim'
import { getClaimStatusText, formatDate } from '@/utils'

const list = ref<any[]>([])
const current = ref(1)
const total = ref(0)
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res = await claimApi.getMyClaims(current.value)
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } finally { loading.value = false }
}

function getStatusTagType(status: number) {
  return status === 0 ? 'warning' : status === 1 ? 'success' : 'danger'
}

onMounted(() => loadData())
</script>

<template>
  <div style="max-width:900px;margin:0 auto;">
    <h2 class="page-title">📝 我的认领申请</h2>

    <div v-loading="loading">
      <div v-if="list.length === 0 && !loading" style="text-align:center;padding:60px;">
        <el-empty description="暂无认领申请" />
      </div>

      <div v-for="item in list" :key="item.id" class="card" style="padding:16px;">
        <div style="display:flex;justify-content:space-between;align-items:start;">
          <div style="flex:1;">
            <div style="margin-bottom:8px;">
              <el-tag :type="getStatusTagType(item.status)" size="small">
                {{ getClaimStatusText(item.status) }}
              </el-tag>
            </div>
            <p><strong>失物ID：</strong>{{ item.lostItemId }}</p>
            <p><strong>申请时间：</strong>{{ formatDate(item.createTime) }}</p>
            <p v-if="item.claimDetail"><strong>认领说明：</strong>{{ item.claimDetail.substring(0, 100) }}</p>
            <p v-if="item.auditRemark" style="color:#909399;">
              <strong>审核备注：</strong>{{ item.auditRemark }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
