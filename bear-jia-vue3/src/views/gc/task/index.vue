<template>
  <div class="gc-task-page">
    <ProTable
      ref="proTableRef"
      :api="tableApi"
      :columns="columns"
      :initialSearchParams="initialSearchParams"
      :searchFields="searchFields"
      rowKey="taskId"
    >
      <template #actions="{ selectedRowKeys, delete: deleteRows }">
        <a-button v-hasPermi="['gc:task:add']" type="primary" @click="openAddModal">
          <BearJiaIcon icon="plus-outlined" />新增任务
        </a-button>
        <a-button
          v-hasPermi="['gc:task:remove']"
          :disabled="selectedRowKeys.length <= 0"
          danger
          type="primary"
          @click="() => deleteRows()"
        >
          <BearJiaIcon icon="delete-outlined" />删除
        </a-button>
      </template>

      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
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
              <a-divider type="vertical" />
              <a class="action-btn" @click="viewProgress(record)">
                <BearJiaIcon icon="bar-chart-outlined" />进度
              </a>
            </template>
          </TableActionBar>
        </template>
      </template>
    </ProTable>

    <AddUpdateModal
      ref="addUpdateModalRef"
      @refresh-father-page-table="() => proTableRef.refresh()"
    />
    <DetailModal ref="detailModalRef" />
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { listTask, delTask } from '@/api/gc';
import { BearJiaIcon } from '@/utils/BearJiaIcon.js';
import ProTable from '@/components/BearJiaProTable/index.vue';
import TableActionBar from '@/components/TableActionBar/index.vue';
import AddUpdateModal from './addUpdateModal.vue';
import DetailModal from './detailModal.vue';
import { message } from 'ant-design-vue';

const proTableRef = ref();
const tableApi = { list: listTask, delete: delTask };

const initialSearchParams = reactive({
  taskName: null,
  status: null,
});

const searchFields = computed(() => [
  { name: 'taskName', label: '任务名称', type: 'input' },
  {
    name: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '未开始', value: '0' },
      { label: '进行中', value: '1' },
      { label: '已完成', value: '2' },
    ],
  },
]);

const columns = [
  { title: '任务名称', dataIndex: 'taskName', key: 'taskName', width: 200 },
  { title: '任务描述', dataIndex: 'taskDescription', key: 'taskDescription', width: 300, ellipsis: true },
  { title: '开始时间', dataIndex: 'startTime', key: 'startTime', width: 180 },
  { title: '结束时间', dataIndex: 'endTime', key: 'endTime', width: 180 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', key: 'operateCol', width: 280, fixed: 'right' },
];

const addUpdateModalRef = ref();
const detailModalRef = ref();

const openAddModal = () => addUpdateModalRef.value.openAddModal();
const openUpdateModal = (record) => addUpdateModalRef.value.openUpdateModal(record);
const openDetailModal = (record) => detailModalRef.value.openModal(record);

const handleDelete = async (record) => {
  try {
    await delTask(record.taskId);
    message.success('删除成功');
    proTableRef.value.refresh();
  } catch (error) {
    message.error('删除失败');
  }
};

const viewProgress = (record) => {
  message.info(`查看任务 ${record.taskName} 的进度`);
};

const getStatusColor = (status) => {
  const colorMap = { '0': 'default', '1': 'processing', '2': 'success' };
  return colorMap[status] || 'default';
};

const getStatusText = (status) => {
  const textMap = { '0': '未开始', '1': '进行中', '2': '已完成' };
  return textMap[status] || '未知';
};
</script>

<style lang="less" scoped>
.gc-task-page {
  height: 100%;
}
</style>
