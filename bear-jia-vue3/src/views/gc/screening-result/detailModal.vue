<template>
  <a-modal v-model:open="visible" title="筛查结果详情" :width="800" :footer="null" @cancel="handleCancel">
    <a-descriptions bordered :column="2">
      <a-descriptions-item label="居民姓名">
        {{ detailData.residentName }}
      </a-descriptions-item>
      <a-descriptions-item label="身份证号">
        {{ detailData.idCard }}
      </a-descriptions-item>
      <a-descriptions-item label="筛查类型">
        {{ getScreeningTypeText(detailData.screeningType) }}
      </a-descriptions-item>
      <a-descriptions-item label="筛查日期">
        {{ detailData.screeningDate }}
      </a-descriptions-item>
      <a-descriptions-item label="风险等级">
        <a-tag :color="getRiskColor(detailData.riskLevel)">
          {{ getRiskText(detailData.riskLevel) }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="需要随访">
        <a-tag :color="detailData.needFollowUp === 1 ? 'red' : 'default'">
          {{ detailData.needFollowUp === 1 ? '是' : '否' }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="审核状态">
        <a-tag :color="detailData.reviewStatus === '1' ? 'success' : 'warning'">
          {{ detailData.reviewStatus === '1' ? '已审核' : '待审核' }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="审核人">
        {{ detailData.reviewUserName || '-' }}
      </a-descriptions-item>
      <a-descriptions-item label="筛查结果" :span="2">
        <div style="white-space: pre-wrap">{{ detailData.resultData }}</div>
      </a-descriptions-item>
      <a-descriptions-item label="备注" :span="2">
        {{ detailData.remark }}
      </a-descriptions-item>
      <a-descriptions-item label="录入时间">
        {{ detailData.createTime }}
      </a-descriptions-item>
      <a-descriptions-item label="审核时间">
        {{ detailData.reviewTime || '-' }}
      </a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { getScreeningResult } from '@/api/gc';
import { message } from 'ant-design-vue';

const visible = ref(false);
const detailData = reactive({
  resultId: null,
  residentName: null,
  idCard: null,
  screeningType: null,
  screeningDate: null,
  riskLevel: null,
  needFollowUp: 0,
  reviewStatus: null,
  reviewUserName: null,
  reviewTime: null,
  resultData: null,
  remark: null,
  createTime: null,
});

const openModal = async (record) => {
  visible.value = true;

  try {
    const response = await getScreeningResult(record.resultId);
    Object.assign(detailData, response.data);
  } catch (error) {
    message.error('获取筛查结果详情失败');
  }
};

const handleCancel = () => {
  visible.value = false;
};

const getScreeningTypeText = (type) => {
  const textMap = { '1': '血液筛查', '2': '胃镜筛查' };
  return textMap[type] || '未知';
};

const getRiskColor = (level) => {
  const colorMap = { '0': 'default', '1': 'green', '2': 'orange', '3': 'red' };
  return colorMap[level] || 'default';
};

const getRiskText = (level) => {
  const textMap = { '0': '未检测', '1': '低风险', '2': '中风险', '3': '高风险' };
  return textMap[level] || '未知';
};

defineExpose({
  openModal,
});
</script>
