<script setup lang="ts">
/**
 * 侧边栏区块组件
 * 统一标题样式（绿色底部边框）+ 内容插槽
 */
defineProps<{
  title: string
}>()
</script>

<template>
  <div class="sidebar-section">
    <h3 class="sidebar-section-title">{{ title }}</h3>
    <div class="sidebar-section-body">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.sidebar-section {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 16px 18px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

.sidebar-section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary, #333);
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary, #00A884);
}

.sidebar-section-body {
  font-size: 13px;
  color: var(--text-regular, #666);
  line-height: 1.8;
}

.sidebar-section-body :deep(p) {
  margin: 4px 0;
}
</style>
