<template>
  <div class="page-container">
    <slot name="search"></slot>
    <a-card :style="{ marginTop: hasSearchSlot ? '16px' : '0' }" class="page-card">
      <div v-if="hasActionsSlot">
        <slot name="actions"></slot>
      </div>
      <slot></slot>
    </a-card>
  </div>
</template>

<script setup>
import {computed, useSlots} from 'vue';

const slots = useSlots();
const hasSearchSlot = computed(() => !!slots.search);
const hasActionsSlot = computed(() => !!slots.actions);
</script>

<style scoped>
.page-container {
  width: 100%;
  max-width: 100%;
  height: calc(100vh - 104px); /* 计算高度：64px(HeaderBar) + 40px(HistoryNav) = 104px */
  overflow-y: auto; /* 只允许垂直滚动 */
  overflow-x: hidden; /* 隐藏水平滚动条 */
  box-sizing: border-box;
  background: var(--bg-color);
  padding: 0 16px 0 0; /* 右侧不要padding，让滚动条贴近边缘 */
  margin: 0;
  scrollbar-gutter: stable; /* 保持布局稳定性 */
}

.page-card {
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  background: var(--component-background);
  margin: 0 16px 16px 0; /* 右侧保持间距，但不影响滚动条位置 */
}

/* PageContainer级别滚动条美化 - 8px宽度 */
.page-container::-webkit-scrollbar {
  width: 8px;
}

.page-container::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
  border-radius: 4px;
  transition: background 0.3s ease;
  /* 确保滚动条贴近右边缘 */
  border: none;
  margin: 0;
}

.page-container::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.25);
}

.page-container::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 4px;
  /* 确保轨道贴近右边缘 */
  border: none;
  margin: 0;
}

.page-container::-webkit-scrollbar-corner {
  background: transparent;
}

/* 确保卡片内容区域正确处理溢出 */
:deep(.ant-card-body) {
  padding: 16px;
  width: 100%;
  box-sizing: border-box;
  background: var(--component-background);
  color: var(--text-primary);
  overflow: visible; /* 确保内容可见 */
}

/* 表格容器滚动条 - 8px宽度，与页面滚动条保持一致 */
:deep(.ant-table-wrapper) {
  overflow-x: auto;
  scrollbar-gutter: stable; /* 保持布局稳定性 */
}

:deep(.ant-table-wrapper)::-webkit-scrollbar {
  height: 8px;
  width: 8px;
}

:deep(.ant-table-wrapper)::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
  border-radius: 4px;
  transition: background 0.3s ease;
  /* 确保滚动条贴近边缘 */
  border: none;
  margin: 0;
}

:deep(.ant-table-wrapper)::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.25);
}

:deep(.ant-table-wrapper)::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 4px;
  /* 确保轨道贴近边缘 */
  border: none;
  margin: 0;
}

:deep(.ant-table-wrapper)::-webkit-scrollbar-corner {
  background: transparent;
}

/* 暗黑模式滚动条适配 */
:global(.dark-theme) .page-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
}

:global(.dark-theme) .page-container::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

:global(.dark-theme) .page-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

/* 暗黑模式表格滚动条 */
:global(.dark-theme) :deep(.ant-table-wrapper)::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
}

:global(.dark-theme) :deep(.ant-table-wrapper)::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

:global(.dark-theme) :deep(.ant-table-wrapper)::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}
</style>