<template>
  <a-modal
    v-model:open="visible"
    title="跟踪记录详情"
    :width="700"
    :footer="null"
    @cancel="handleClose"
  >
    <a-descriptions bordered :column="2">
      <a-descriptions-item label="随访对象" :span="2">
        {{ detailData.residentName }}
      </a-descriptions-item>
      <a-descriptions-item label="随访类型">
        <a-tag :color="getFollowUpTypeColor(detailData.followUpType)">
          {{ getFollowUpTypeText(detailData.followUpType) }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="跟踪日期">
        {{ detailData.trackDate }}
      </a-descriptions-item>
      <a-descriptions-item label="跟踪方式">
        {{ getTrackMethodText(detailData.trackMethod) }}
      </a-descriptions-item>
      <a-descriptions-item label="跟踪结果">
        <a-tag :color="getTrackResultColor(detailData.trackResult)">
          {{ getTrackResultText(detailData.trackResult) }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="跟踪人员">
        {{ detailData.trackPerson || '-' }}
      </a-descriptions-item>
      <a-descriptions-item label="创建时间">
        {{ detailData.createTime }}
      </a-descriptions-item>
      <a-descriptions-item label="跟踪内容" :span="2">
        {{ detailData.trackContent }}
      </a-descriptions-item>
      <a-descriptions-item label="备注" :span="2">
        {{ detailData.remark || '-' }}
      </a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';

const visible = ref(false);

const detailData = reactive({
  trackId: undefined,
  residentName: '',
  followUpType: '',
  trackDate: '',
  trackMethod: '',
  trackResult: '',
  trackPerson: '',
  trackContent: '',
  createTime: '',
  remark: '',
});

// 打开弹窗
const openModal = (record) => {
  visible.value = true;
  Object.assign(detailData, record);
};

// 关闭弹窗
const handleClose = () => {
  visible.value = false;
};

// 随访类型文本
const getFollowUpTypeText = (type) => {
  const typeMap = {
    '1': '问卷随访',
    '2': '筛查随访',
    '3': '高风险随访',
  };
  return typeMap[type] || '-';
};

// 随访类型颜色
const getFollowUpTypeColor = (type) => {
  const colorMap = {
    '1': 'blue',
    '2': 'green',
    '3': 'red',
  };
  return colorMap[type] || 'default';
};

// 跟踪方式文本
const getTrackMethodText = (method) => {
  const methodMap = {
    '1': '电话跟踪',
    '2': '上门跟踪',
    '3': '短信跟踪',
    '4': '微信跟踪',
  };
  return methodMap[method] || '-';
};

// 跟踪结果文本
const getTrackResultText = (result) => {
  const resultMap = {
    '1': '联系成功',
    '2': '无法联系',
    '3': '拒绝随访',
    '4': '同意随访',
  };
  return resultMap[result] || '-';
};

// 跟踪结果颜色
const getTrackResultColor = (result) => {
  const colorMap = {
    '1': 'success',
    '2': 'warning',
    '3': 'error',
    '4': 'processing',
  };
  return colorMap[result] || 'default';
};

defineExpose({
  openModal,
});
</script>

<style scoped lang="less">
:deep(.ant-descriptions-item-label) {
  width: 120px;
  font-weight: 500;
}
</style>
