<template>
  <a-modal
    v-model:open="visible"
    title="随访对象详情"
    :width="800"
    :footer="null"
    @cancel="handleClose"
  >
    <a-descriptions bordered :column="2">
      <a-descriptions-item label="居民姓名">
        {{ detailData.residentName }}
      </a-descriptions-item>
      <a-descriptions-item label="联系电话">
        {{ detailData.phone }}
      </a-descriptions-item>
      <a-descriptions-item label="随访类型">
        <a-tag :color="getFollowUpTypeColor(detailData.followUpType)">
          {{ getFollowUpTypeText(detailData.followUpType) }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="随访状态">
        <a-tag :color="getFollowUpStatusColor(detailData.followUpStatus)">
          {{ getFollowUpStatusText(detailData.followUpStatus) }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="随访原因" :span="2">
        {{ detailData.followUpReason }}
      </a-descriptions-item>
      <a-descriptions-item label="计划随访日期">
        {{ detailData.planDate }}
      </a-descriptions-item>
      <a-descriptions-item label="实际随访日期">
        {{ detailData.actualDate || '-' }}
      </a-descriptions-item>
      <a-descriptions-item label="随访方式">
        {{ getFollowUpMethodText(detailData.followUpMethod) }}
      </a-descriptions-item>
      <a-descriptions-item label="负责人">
        {{ detailData.followUpPerson || '-' }}
      </a-descriptions-item>
      <a-descriptions-item label="创建时间">
        {{ detailData.createTime }}
      </a-descriptions-item>
      <a-descriptions-item label="更新时间">
        {{ detailData.updateTime }}
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
  followUpId: undefined,
  residentName: '',
  phone: '',
  followUpType: '',
  followUpStatus: '',
  followUpReason: '',
  planDate: '',
  actualDate: '',
  followUpMethod: '',
  followUpPerson: '',
  createTime: '',
  updateTime: '',
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

// 随访状态文本
const getFollowUpStatusText = (status) => {
  const statusMap = {
    '0': '待随访',
    '1': '随访中',
    '2': '已完成',
  };
  return statusMap[status] || '-';
};

// 随访状态颜色
const getFollowUpStatusColor = (status) => {
  const colorMap = {
    '0': 'default',
    '1': 'processing',
    '2': 'success',
  };
  return colorMap[status] || 'default';
};

// 随访方式文本
const getFollowUpMethodText = (method) => {
  const methodMap = {
    '1': '电话随访',
    '2': '上门随访',
    '3': '短信随访',
    '4': '微信随访',
  };
  return methodMap[method] || '-';
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
