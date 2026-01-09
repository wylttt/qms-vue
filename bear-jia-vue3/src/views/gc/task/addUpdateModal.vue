<template>
  <a-modal v-model:open="visible" :title="modalTitle" :width="700" @ok="handleOk">
    <a-form ref="formRef" :label-col="{ span: 4 }" :model="formData">
      <a-form-item label="任务名称" name="taskName">
        <a-input v-model:value="formData.taskName" placeholder="请输入任务名称" />
      </a-form-item>
      <a-form-item label="任务描述">
        <a-textarea v-model:value="formData.taskDescription" :rows="3" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { addTask, updateTask } from '@/api/gc';
import { message } from 'ant-design-vue';

const emit = defineEmits(['refresh-father-page-table']);
const visible = ref(false);
const isEdit = ref(false);
const formData = reactive({ taskId: null, taskName: null, taskDescription: null });
const formRef = ref();

const modalTitle = computed(() => (isEdit.value ? '编辑任务' : '新增任务'));

const openAddModal = () => {
  isEdit.value = false;
  visible.value = true;
};

const openUpdateModal = (record) => {
  isEdit.value = true;
  Object.assign(formData, record);
  visible.value = true;
};

const handleOk = async () => {
  try {
    await formRef.value.validate();
    if (isEdit.value) {
      await updateTask(formData);
    } else {
      await addTask(formData);
    }
    message.success(isEdit.value ? '修改成功' : '新增成功');
    visible.value = false;
    emit('refresh-father-page-table');
  } catch (error) {
    message.error(isEdit.value ? '修改失败' : '新增失败');
  }
};

defineExpose({ openAddModal, openUpdateModal });
</script>
