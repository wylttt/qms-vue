<template>
  <a-modal v-model:open="visible" title="问卷详情" :width="800" :footer="null">
    <a-descriptions bordered :column="1">
      <a-descriptions-item label="问卷名称">{{ detailData.questionnaireName }}</a-descriptions-item>
      <a-descriptions-item label="问卷描述">{{ detailData.questionnaireDescription }}</a-descriptions-item>
      <a-descriptions-item label="版本号">{{ detailData.version }}</a-descriptions-item>
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
