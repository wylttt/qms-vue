<template>
  <div class="gc-follow-up-page">
    <a-tabs v-model:activeKey="activeTab" type="card">
      <!-- 随访对象管理 -->
      <a-tab-pane key="follow-up" tab="随访对象管理">
        <ProTable
          ref="followUpTableRef"
          :api="followUpApi"
          :columns="followUpColumns"
          :initialSearchParams="followUpSearchParams"
          :searchFields="followUpSearchFields"
          rowKey="followUpId"
        >
          <template #actions="{ selectedRowKeys, delete: deleteRows }">
            <a-button v-hasPermi="['gc:followup:add']" type="primary" @click="openAddFollowUpModal">
              <BearJiaIcon icon="plus-outlined" />新增
            </a-button>
            <a-button
              v-hasPermi="['gc:followup:remove']"
              :disabled="selectedRowKeys.length <= 0"
              danger
              type="primary"
              @click="() => deleteRows()"
            >
              <BearJiaIcon icon="delete-outlined" />删除
            </a-button>
          </template>

          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'followUpStatus'">
              <a-tag :color="getFollowUpStatusColor(record.followUpStatus)">
                {{ getFollowUpStatusText(record.followUpStatus) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'operateCol'">
              <TableActionBar
                :hasDelete="true"
                :hasEdit="true"
                :hasView="true"
                :record="record"
                @delete="handleDeleteFollowUp"
                @edit="openUpdateFollowUpModal"
                @view="openDetailFollowUpModal"
              >
                <template #actions="{ record }">
                  <template v-if="record.followUpStatus !== '2'">
                    <a-divider type="vertical" />
                    <a class="action-btn" @click="handleComplete(record)">
                      <BearJiaIcon icon="check-outlined" />完成
                    </a>
                  </template>
                  <a-divider type="vertical" />
                  <a class="action-btn" @click="viewTracks(record)">
                    <BearJiaIcon icon="file-text-outlined" />跟踪记录
                  </a>
                </template>
              </TableActionBar>
            </template>
          </template>
        </ProTable>
      </a-tab-pane>

      <!-- 随访跟踪记录 -->
      <a-tab-pane key="track" tab="随访跟踪记录">
        <ProTable
          ref="trackTableRef"
          :api="trackApi"
          :columns="trackColumns"
          :initialSearchParams="trackSearchParams"
          :searchFields="trackSearchFields"
          rowKey="trackId"
        >
          <template #actions="{ selectedRowKeys, delete: deleteRows }">
            <a-button v-hasPermi="['gc:track:add']" type="primary" @click="openAddTrackModal">
              <BearJiaIcon icon="plus-outlined" />新增记录
            </a-button>
            <a-button
              v-hasPermi="['gc:track:remove']"
              :disabled="selectedRowKeys.length <= 0"
              danger
              type="primary"
              @click="() => deleteRows()"
            >
              <BearJiaIcon icon="delete-outlined" />删除
            </a-button>
          </template>

          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'operateCol'">
              <TableActionBar
                :hasDelete="true"
                :hasEdit="true"
                :hasView="true"
                :record="record"
                @delete="handleDeleteTrack"
                @edit="openUpdateTrackModal"
                @view="openDetailTrackModal"
              />
            </template>
          </template>
        </ProTable>
      </a-tab-pane>
    </a-tabs>

    <!-- 随访对象相关弹窗 -->
    <FollowUpAddUpdateModal
      ref="followUpAddUpdateModalRef"
      @refresh-father-page-table="() => followUpTableRef.refresh()"
    />
    <FollowUpDetailModal ref="followUpDetailModalRef" />

    <!-- 随访跟踪记录相关弹窗 -->
    <TrackAddUpdateModal
      ref="trackAddUpdateModalRef"
      @refresh-father-page-table="() => trackTableRef.refresh()"
    />
    <TrackDetailModal ref="trackDetailModalRef" />
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { listFollowUp, delFollowUp, completeFollowUp, listFollowUpTrack, delFollowUpTrack } from '@/api/gc';
import { BearJiaIcon } from '@/utils/BearJiaIcon.js';
import ProTable from '@/components/BearJiaProTable/index.vue';
import TableActionBar from '@/components/TableActionBar/index.vue';
import FollowUpAddUpdateModal from './followUpAddUpdateModal.vue';
import FollowUpDetailModal from './followUpDetailModal.vue';
import TrackAddUpdateModal from './trackAddUpdateModal.vue';
import TrackDetailModal from './trackDetailModal.vue';
import { message, Modal } from 'ant-design-vue';

const activeTab = ref('follow-up');

// ========== 随访对象管理 ==========
const followUpTableRef = ref();
const followUpApi = { list: listFollowUp, delete: delFollowUp };

const followUpSearchParams = reactive({
  residentName: null,
  followUpStatus: null,
});

const followUpSearchFields = computed(() => [
  { name: 'residentName', label: '居民姓名', type: 'input' },
  {
    name: 'followUpStatus',
    label: '随访状态',
    type: 'select',
    options: [
      { label: '待随访', value: '0' },
      { label: '随访中', value: '1' },
      { label: '已完成', value: '2' },
    ],
  },
]);

const followUpColumns = [
  { title: '居民姓名', dataIndex: 'residentName', key: 'residentName', width: 120 },
  { title: '随访原因', dataIndex: 'followUpReason', key: 'followUpReason', width: 150, ellipsis: true },
  { title: '随访状态', dataIndex: 'followUpStatus', key: 'followUpStatus', width: 100 },
  { title: '计划次数', dataIndex: 'plannedVisitCount', key: 'plannedVisitCount', width: 100 },
  { title: '实际次数', dataIndex: 'actualVisitCount', key: 'actualVisitCount', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'operateCol', width: 320, fixed: 'right' },
];

const followUpAddUpdateModalRef = ref();
const followUpDetailModalRef = ref();

const openAddFollowUpModal = () => followUpAddUpdateModalRef.value.openAddModal();
const openUpdateFollowUpModal = (record) => followUpAddUpdateModalRef.value.openUpdateModal(record);
const openDetailFollowUpModal = (record) => followUpDetailModalRef.value.openModal(record);

const handleDeleteFollowUp = async (record) => {
  try {
    await delFollowUp(record.followUpId);
    message.success('删除成功');
    followUpTableRef.value.refresh();
  } catch (error) {
    message.error('删除失败');
  }
};

const handleComplete = (record) => {
  Modal.confirm({
    title: '确认完成',
    content: `确定要完成居民 "${record.residentName}" 的随访吗？`,
    async onOk() {
      try {
        await completeFollowUp(record.followUpId, '随访已完成');
        message.success('完成成功');
        followUpTableRef.value.refresh();
      } catch (error) {
        message.error('操作失败');
      }
    },
  });
};

const viewTracks = (record) => {
  activeTab.value = 'track';
  // TODO: 筛选该随访对象的跟踪记录
  message.info(`查看随访对象 ${record.residentName} 的跟踪记录`);
};

const getFollowUpStatusColor = (status) => {
  const colorMap = { '0': 'default', '1': 'processing', '2': 'success' };
  return colorMap[status] || 'default';
};

const getFollowUpStatusText = (status) => {
  const textMap = { '0': '待随访', '1': '随访中', '2': '已完成' };
  return textMap[status] || '未知';
};

// ========== 随访跟踪记录 ==========
const trackTableRef = ref();
const trackApi = { list: listFollowUpTrack, delete: delFollowUpTrack };

const trackSearchParams = reactive({
  residentName: null,
});

const trackSearchFields = computed(() => [
  { name: 'residentName', label: '居民姓名', type: 'input' },
]);

const trackColumns = [
  { title: '居民姓名', dataIndex: 'residentName', key: 'residentName', width: 120 },
  { title: '随访方式', dataIndex: 'visitMethod', key: 'visitMethod', width: 100 },
  { title: '随访时间', dataIndex: 'visitTime', key: 'visitTime', width: 180 },
  { title: '随访人员', dataIndex: 'trackerName', key: 'trackerName', width: 120 },
  { title: '随访记录', dataIndex: 'visitRecord', key: 'visitRecord', width: 300, ellipsis: true },
  { title: '操作', key: 'operateCol', width: 200, fixed: 'right' },
];

const trackAddUpdateModalRef = ref();
const trackDetailModalRef = ref();

const openAddTrackModal = () => trackAddUpdateModalRef.value.openAddModal();
const openUpdateTrackModal = (record) => trackAddUpdateModalRef.value.openUpdateModal(record);
const openDetailTrackModal = (record) => trackDetailModalRef.value.openModal(record);

const handleDeleteTrack = async (record) => {
  try {
    await delFollowUpTrack(record.trackId);
    message.success('删除成功');
    trackTableRef.value.refresh();
  } catch (error) {
    message.error('删除失败');
  }
};
</script>

<style lang="less" scoped>
.gc-follow-up-page {
  height: 100%;

  :deep(.ant-tabs) {
    height: 100%;
    display: flex;
    flex-direction: column;

    .ant-tabs-content {
      flex: 1;
      overflow: hidden;
    }
  }
}
</style>
