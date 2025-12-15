<template>
  <a-modal v-model:open="visible" title="问卷记录详情" :width="800" :footer="null">
    <a-descriptions bordered>
      <a-descriptions-item label="居民姓名">{{ detailData.residentName }}</a-descriptions-item>
      <a-descriptions-item label="总分">{{ detailData.totalScore }}</a-descriptions-item>
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
