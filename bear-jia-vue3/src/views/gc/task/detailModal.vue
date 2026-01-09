<template>
  <a-modal v-model:open="visible" title="任务详情" :width="700" :footer="null">
    <a-descriptions bordered>
      <a-descriptions-item label="任务名称">{{ detailData.taskName }}</a-descriptions-item>
      <a-descriptions-item label="任务描述">{{ detailData.taskDescription }}</a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';

const visible = ref(false);
const detailData = reactive({});

const openModal = (record) => {
  visible.value = true;
  Object.assign(detailData, record);
};

defineExpose({ openModal });
</script>
