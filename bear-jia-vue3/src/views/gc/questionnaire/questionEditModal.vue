<template>
  <a-modal v-model:open="visible" title="编辑问题" :width="700" @ok="handleOk">
    <a-form ref="formRef" :model="formData">
      <a-form-item label="问题文本" name="questionText">
        <a-input v-model:value="formData.questionText" />
      </a-form-item>
      <a-form-item label="问题类型" name="questionType">
        <a-radio-group v-model:value="formData.questionType">
          <a-radio value="1">单选</a-radio>
          <a-radio value="2">多选</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';

const emit = defineEmits(['confirm']);
const visible = ref(false);
const formData = reactive({ questionText: '', questionType: '1', options: [], score: 0 });

const openModal = (data) => {
  visible.value = true;
  if (data) Object.assign(formData, data);
};

const handleOk = () => {
  emit('confirm', { ...formData });
  visible.value = false;
};

defineExpose({ openModal });
</script>
