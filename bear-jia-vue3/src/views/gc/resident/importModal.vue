<template>
  <a-modal
    v-model:open="visible"
    title="导入居民数据"
    :width="600"
    @cancel="handleCancel"
    @ok="handleOk"
  >
    <a-upload-dragger
      v-model:file-list="fileList"
      :before-upload="beforeUpload"
      :max-count="1"
      accept=".xlsx,.xls"
      @remove="handleRemove"
    >
      <p class="ant-upload-drag-icon">
        <inbox-outlined />
      </p>
      <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
      <p class="ant-upload-hint">
        支持单个文件上传，仅支持 .xlsx、.xls 格式
      </p>
    </a-upload-dragger>

    <a-alert
      v-if="importResult"
      :message="importResult.message"
      :type="importResult.type"
      show-icon
      style="margin-top: 16px"
    >
      <template v-if="importResult.detail" #description>
        <div>成功: {{ importResult.detail.success }}</div>
        <div>失败: {{ importResult.detail.fail }}</div>
        <div v-if="importResult.detail.errors && importResult.detail.errors.length > 0">
          错误信息:
          <ul>
            <li v-for="(error, index) in importResult.detail.errors" :key="index">
              {{ error }}
            </li>
          </ul>
        </div>
      </template>
    </a-alert>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { InboxOutlined } from '@ant-design/icons-vue';
import { importResident } from '@/api/gc';
import { message } from 'ant-design-vue';

const emit = defineEmits(['refresh-father-page-table']);

const visible = ref(false);
const fileList = ref([]);
const importResult = ref(null);

// 打开弹窗
const openModal = () => {
  visible.value = true;
  fileList.value = [];
  importResult.value = null;
};

// 上传前验证
const beforeUpload = (file) => {
  const isExcel =
    file.type === 'application/vnd.ms-excel' ||
    file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
  
  if (!isExcel) {
    message.error('只能上传 Excel 文件！');
    return false;
  }
  
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    message.error('文件大小不能超过 10MB！');
    return false;
  }
  
  return false; // 阻止自动上传
};

// 移除文件
const handleRemove = () => {
  fileList.value = [];
  importResult.value = null;
};

// 取消
const handleCancel = () => {
  visible.value = false;
  fileList.value = [];
  importResult.value = null;
};

// 确定导入
const handleOk = async () => {
  if (fileList.value.length === 0) {
    message.warning('请选择要导入的文件');
    return;
  }

  const formData = new FormData();
  formData.append('file', fileList.value[0].originFileObj || fileList.value[0]);

  try {
    const response = await importResident(formData);
    
    if (response.code === 200) {
      importResult.value = {
        type: 'success',
        message: '导入成功',
        detail: response.data || response.msg,
      };
      
      setTimeout(() => {
        visible.value = false;
        emit('refresh-father-page-table');
        fileList.value = [];
        importResult.value = null;
      }, 2000);
    } else {
      importResult.value = {
        type: 'error',
        message: '导入失败',
        detail: response.msg || '未知错误',
      };
    }
  } catch (error) {
    importResult.value = {
      type: 'error',
      message: '导入失败',
      detail: error.message || '网络错误',
    };
  }
};

defineExpose({
  openModal,
});
</script>

<style scoped>
.ant-upload-hint {
  color: #999;
}
</style>
