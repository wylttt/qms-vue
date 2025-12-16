<template>
  <div class="gc-screening-result-page">
    <ProTable
      ref="tableRef"
      :api="api"
      :columns="columns"
      :initialSearchParams="searchParams"
      :searchFields="searchFields"
      rowKey="resultId"
    >
      <template #actions="{ selectedRowKeys, delete: deleteRows }">
        <a-button v-hasPermi="['gc:screening:add']" type="primary" @click="openAddModal">
          <BearJiaIcon icon="plus-outlined" />新增
        </a-button>
        <a-button
          v-hasPermi="['gc:screening:remove']"
          :disabled="selectedRowKeys.length <= 0"
          danger
          type="primary"
          @click="() => deleteRows()"
        >
          <BearJiaIcon icon="delete-outlined" />删除
        </a-button>
        <a-button type="default" @click="handleExport">
          <BearJiaIcon icon="export-outlined" />导出
        </a-button>
      </template>

      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'residentName'">
          <a @click="openDetailModal(record)">{{ record.residentName }}</a>
        </template>
        <template v-else-if="column.key === 'idCardNo'">
          {{ desensitizeIdCard(record.idCardNo) }}
        </template>
        <template v-else-if="column.key === 'screeningType'">
          <a-tag :color="getScreeningTypeColor(record.screeningType)">
            {{ getScreeningTypeText(record.screeningType) }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'riskLevel'">
          <a-tag :color="getRiskLevelColor(record.riskLevel)">
            {{ getRiskLevelText(record.riskLevel) }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'auditStatus'">
          <a-tag :color="getAuditStatusColor(record.auditStatus)">
            {{ getAuditStatusText(record.auditStatus) }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'needFollowUp'">
          <a-tag :color="record.needFollowUp === 1 ? 'orange' : 'default'">
            {{ record.needFollowUp === 1 ? '是' : '否' }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'operateCol'">
          <TableActionBar
            :hasDelete="true"
            :hasEdit="true"
            :hasView="true"
            :record="record"
            @delete="handleDelete"
            @edit="openUpdateModal"
            @view="openDetailModal"
          >
            <template #actions="{ record }">
              <template v-if="record.auditStatus === '0'">
                <a-divider type="vertical" />
                <a class="action-btn" @click="handleAudit(record)">
                  <BearJiaIcon icon="check-circle-outlined" />审核
                </a>
              </template>
            </template>
          </TableActionBar>
        </template>
      </template>
    </ProTable>

    <!-- 新增/编辑弹窗 -->
    <AddUpdateModal ref="addUpdateModalRef" @refresh-father-page-table="() => tableRef.refresh()" />

    <!-- 详情弹窗 -->
    <DetailModal ref="detailModalRef" />

    <!-- 审核弹窗 -->
    <a-modal
      v-model:open="auditVisible"
      title="审核筛查结果"
      :width="500"
      @cancel="handleCancelAudit"
      @ok="handleConfirmAudit"
    >
      <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="审核结果">
          <a-radio-group v-model:value="auditForm.auditStatus">
            <a-radio value="1">审核通过</a-radio>
            <a-radio value="2">审核驳回</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="审核意见">
          <a-textarea
            v-model:value="auditForm.auditRemark"
            :rows="4"
            placeholder="请输入审核意见（驳回时必填）"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { BearJiaIcon } from '@/utils/BearJiaIcon.js';
import ProTable from '@/components/BearJiaProTable/index.vue';
import TableActionBar from '@/components/TableActionBar/index.vue';
import AddUpdateModal from './addUpdateModal.vue';
import DetailModal from './detailModal.vue';
import {
  listScreeningResult,
  delScreeningResult,
  auditScreeningResult,
  exportScreeningResult,
} from '@/api/gc';

// ========== 表格配置 ==========
const tableRef = ref();
const api = { list: listScreeningResult, delete: delScreeningResult };

const searchParams = reactive({
  residentName: null,
  idCardNo: null,
  screeningType: null,
  riskLevel: null,
  auditStatus: null,
  needFollowUp: null,
  startDate: null,
  endDate: null,
});

const searchFields = computed(() => [
  { name: 'residentName', label: '居民姓名', type: 'input' },
  { name: 'idCardNo', label: '身份证号', type: 'input' },
  {
    name: 'screeningType',
    label: '筛查类型',
    type: 'select',
    options: [
      { label: '血液筛查', value: '1' },
      { label: '胃镜筛查', value: '2' },
    ],
  },
  {
    name: 'riskLevel',
    label: '风险等级',
    type: 'select',
    options: [
      { label: '未检测', value: '0' },
      { label: '低风险', value: '1' },
      { label: '中风险', value: '2' },
      { label: '高风险', value: '3' },
    ],
  },
  {
    name: 'auditStatus',
    label: '审核状态',
    type: 'select',
    options: [
      { label: '待审核', value: '0' },
      { label: '审核通过', value: '1' },
      { label: '审核驳回', value: '2' },
    ],
  },
  {
    name: 'needFollowUp',
    label: '需要随访',
    type: 'select',
    options: [
      { label: '是', value: '1' },
      { label: '否', value: '0' },
    ],
  },
  { name: 'dateRange', label: '筛查日期', type: 'dateRange' },
]);

const columns = [
  { title: '居民姓名', dataIndex: 'residentName', key: 'residentName', width: 120 },
  { title: '身份证号', dataIndex: 'idCardNo', key: 'idCardNo', width: 180 },
  { title: '筛查类型', dataIndex: 'screeningType', key: 'screeningType', width: 100 },
  { title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel', width: 100 },
  { title: '审核状态', dataIndex: 'auditStatus', key: 'auditStatus', width: 100 },
  { title: '需要随访', dataIndex: 'needFollowUp', key: 'needFollowUp', width: 100 },
  { title: '筛查日期', dataIndex: 'screeningDate', key: 'screeningDate', width: 120 },
  { title: '录入时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'operateCol', width: 280, fixed: 'right' },
];

// ========== 弹窗管理 ==========
const addUpdateModalRef = ref();
const detailModalRef = ref();

const openAddModal = () => addUpdateModalRef.value.openAddModal();
const openUpdateModal = (record) => addUpdateModalRef.value.openUpdateModal(record);
const openDetailModal = (record) => detailModalRef.value.openModal(record);

const handleDelete = async (record) => {
  try {
    await delScreeningResult(record.resultId);
    message.success('删除成功');
    tableRef.value.refresh();
  } catch (error) {
    message.error('删除失败');
  }
};

// ========== 审核功能 ==========
const auditVisible = ref(false);
const auditForm = reactive({
  resultId: null,
  auditStatus: '1',
  auditRemark: '',
});

const handleAudit = (record) => {
  auditForm.resultId = record.resultId;
  auditForm.auditStatus = '1';
  auditForm.auditRemark = '';
  auditVisible.value = true;
};

const handleCancelAudit = () => {
  auditVisible.value = false;
};

const handleConfirmAudit = async () => {
  if (auditForm.auditStatus === '2' && !auditForm.auditRemark) {
    message.warning('审核驳回时请填写审核意见');
    return;
  }

  try {
    await auditScreeningResult(auditForm.resultId, {
      auditStatus: auditForm.auditStatus,
      auditRemark: auditForm.auditRemark,
    });
    message.success('审核成功');
    auditVisible.value = false;
    tableRef.value.refresh();
  } catch (error) {
    message.error('审核失败');
  }
};

// ========== 导出功能 ==========
const handleExport = async () => {
  try {
    message.loading('正在导出...');
    const response = await exportScreeningResult(searchParams);
    
    // 创建Blob对象
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    });
    
    // 创建下载链接
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `筛查结果_${new Date().getTime()}.xlsx`;
    link.click();
    
    // 释放URL对象
    window.URL.revokeObjectURL(url);
    message.success('导出成功');
  } catch (error) {
    message.error('导出失败');
  }
};

// ========== 辅助方法 ==========

// 身份证号脱敏
const desensitizeIdCard = (idCard) => {
  if (!idCard || idCard.length !== 18) return idCard;
  return idCard.substring(0, 6) + '********' + idCard.substring(14);
};

// 筛查类型
const getScreeningTypeColor = (type) => {
  const colorMap = { '1': 'blue', '2': 'purple' };
  return colorMap[type] || 'default';
};

const getScreeningTypeText = (type) => {
  const textMap = { '1': '血液筛查', '2': '胃镜筛查' };
  return textMap[type] || '未知';
};

// 风险等级
const getRiskLevelColor = (level) => {
  const colorMap = { '0': 'default', '1': 'green', '2': 'orange', '3': 'red' };
  return colorMap[level] || 'default';
};

const getRiskLevelText = (level) => {
  const textMap = { '0': '未检测', '1': '低风险', '2': '中风险', '3': '高风险' };
  return textMap[level] || '未知';
};

// 审核状态
const getAuditStatusColor = (status) => {
  const colorMap = { '0': 'default', '1': 'success', '2': 'error' };
  return colorMap[status] || 'default';
};

const getAuditStatusText = (status) => {
  const textMap = { '0': '待审核', '1': '审核通过', '2': '审核驳回' };
  return textMap[status] || '未知';
};
</script>

<style lang="less" scoped>
.gc-screening-result-page {
  height: 100%;
}
</style>
