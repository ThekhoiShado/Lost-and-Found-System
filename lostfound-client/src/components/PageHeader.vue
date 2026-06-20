<script setup lang="ts">
/**
 * 页面标题组件
 * 统一的页面标题样式（左侧绿色竖条 + 可选副标题 + 可选返回按钮）
 */
defineProps<{
  title: string
  subtitle?: string
  showBack?: boolean
}>()

const emit = defineEmits<{
  back: []
}>()
</script>

<template>
  <div class="page-header">
    <div v-if="showBack" class="page-header-back" @click="emit('back')">
      <el-icon><ArrowLeft /></el-icon> 返回
    </div>
    <h2 class="page-header-title">{{ title }}</h2>
    <p v-if="subtitle" class="page-header-subtitle">{{ subtitle }}</p>
  </div>
</template>

<style scoped>
.page-header {
  margin-bottom: 24px;
}

.page-header-back {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--text-secondary, #999);
  cursor: pointer;
  font-size: 13px;
  margin-bottom: 8px;
  transition: color 0.2s;
  user-select: none;
}

.page-header-back:hover {
  color: var(--primary, #00A884);
}

.page-header-title {
  font-size: var(--font-xl, 24px);
  font-weight: 600;
  color: var(--text-primary, #333);
  position: relative;
  padding-left: 14px;
  line-height: 1.4;
}

.page-header-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 4px;
  bottom: 4px;
  width: 4px;
  background: var(--primary, #00A884);
  border-radius: 2px;
}

.page-header-subtitle {
  font-size: 14px;
  color: var(--text-secondary, #999);
  margin-top: 8px;
  padding-left: 14px;
}
</style>
