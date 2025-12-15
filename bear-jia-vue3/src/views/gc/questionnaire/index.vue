<template>
  <div class="gc-questionnaire-page">
    <a-tabs v-model:activeKey="activeTab" type="card">
      <!-- 问卷模板管理 -->
      <a-tab-pane key="template" tab="问卷模板管理">
        <ProTable
          ref="templateTableRef"
          :api="templateApi"
          :columns="templateColumns"
          :initialSearchParams="templateSearchParams"
          :searchFields="templateSearchFields"
          rowKey="questionnaireId"
        >
          <template #actions="{ selectedRowKeys, delete: deleteRows }">
            <a-button v-hasPermi="['gc:questionnaire:add']" type="primary" @click="openAddTemplateModal">
              <BearJiaIcon icon="plus-outlined" />新增模板
            </a-button>
            <a-button
              v-hasPermi="['gc:questionnaire:remove']"
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
              <a-tag :color="record.status === '1' ? 'success' : 'default'">
                {{ record.status === '1' ? '已发布' : '草稿' }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'operateCol'">
              <TableActionBar
                :hasDelete="true"
                :hasEdit="true"
                :hasView="true"
                :record="record"
                @delete="handleDeleteTemplate"
                @edit="openUpdateTemplateModal"
                @view="openDetailTemplateModal"
              >
                <template #actions="{ record }">
                  <a-divider type="vertical" />
                  <a v-if="record.status === '0'" class="action-btn" @click="publishTemplate(record)">
                    <BearJiaIcon icon="check-circle-outlined" />发布
                  </a>
                  <a v-else class="action-btn" @click="disableTemplate(record)">
                    <BearJiaIcon icon="stop-outlined" />停用
                  </a>
                </template>
              </TableActionBar>
            </template>
          </template>
        </ProTable>
      </a-tab-pane>

      <!-- 问卷记录管理 -->
      <a-tab-pane key="record" tab="问卷记录管理">
        <ProTable
          ref="recordTableRef"
          :api="recordApi"
          :columns="recordColumns"
          :initialSearchParams="recordSearchParams"
          :searchFields="recordSearchFields"
          rowKey="recordId"
        >
          <template #actions="{ selectedRowKeys, delete: deleteRows }">
            <a-button
              v-hasPermi="['gc:record:remove']"
              :disabled="selectedRowKeys.length <= 0"
              danger
              type="primary"
              @click="() => deleteRows()"
            >
              <BearJiaIcon icon="delete-outlined" />删除
            </a-button>
          </template>

          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'isKeyPopulation'">
              <a-tag :color="record.isKeyPopulation === 1 ? 'red' : 'default'">
                {{ record.isKeyPopulation === 1 ? '是' : '否' }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'operateCol'">
              <TableActionBar
                :hasDelete="true"
                :hasEdit="false"
                :hasView="true"
                :record="record"
                @delete="handleDeleteRecord"
                @view="openDetailRecordModal"
              />
            </template>
          </template>
        </ProTable>
      </a-tab-pane>
    </a-tabs>

    <!-- 问卷模板相关弹窗 -->
    <TemplateAddUpdateModal
      ref="templateAddUpdateModalRef"
      @refresh-father-page-table="() => templateTableRef.refresh()"
    />
    <TemplateDetailModal ref="templateDetailModalRef" />

    <!-- 问卷记录相关弹窗 -->
    <RecordDetailModal ref="recordDetailModalRef" />
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, reactive, ref } from 'vue';
import {
  listQuestionnaire,
  delQuestionnaire,
  publishQuestionnaire,
  disableQuestionnaire,
  listRecord,
  delRecord,
} from '@/api/gc';
import { BearJiaIcon } from '@/utils/BearJiaIcon.js';
import ProTable from '@/components/BearJiaProTable/index.vue';
import TableActionBar from '@/components/TableActionBar/index.vue';
import TemplateAddUpdateModal from './templateAddUpdateModal.vue';
import TemplateDetailModal from './templateDetailModal.vue';
import RecordDetailModal from './recordDetailModal.vue';
import { message, Modal } from 'ant-design-vue';

const { proxy } = getCurrentInstance();

const activeTab = ref('template');

// ========== 问卷模板管理 ==========
const templateTableRef = ref();
const templateApi = { list: listQuestionnaire, delete: delQuestionnaire };

const templateSearchParams = reactive({
  questionnaireName: null,
  status: null,
});

const templateSearchFields = computed(() => [
  { name: 'questionnaireName', label: '问卷名称', type: 'input' },
  {
    name: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '草稿', value: '0' },
      { label: '已发布', value: '1' },
      { label: '已停用', value: '2' },
    ],
  },
]);

const templateColumns = [
  { title: '问卷名称', dataIndex: 'questionnaireName', key: 'questionnaireName', width: 200 },
  { title: '问卷描述', dataIndex: 'questionnaireDescription', key: 'questionnaireDescription', width: 300, ellipsis: true },
  { title: '版本号', dataIndex: 'version', key: 'version', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'operateCol', width: 280, fixed: 'right' },
];

const templateAddUpdateModalRef = ref();
const templateDetailModalRef = ref();

const openAddTemplateModal = () => {
  templateAddUpdateModalRef.value.openAddModal();
};

const openUpdateTemplateModal = (record) => {
  templateAddUpdateModalRef.value.openUpdateModal(record);
};

const openDetailTemplateModal = (record) => {
  templateDetailModalRef.value.openModal(record);
};

const handleDeleteTemplate = async (record) => {
  try {
    await delQuestionnaire(record.questionnaireId);
    message.success('删除成功');
    templateTableRef.value.refresh();
  } catch (error) {
    message.error('删除失败');
  }
};

const publishTemplate = (record) => {
  Modal.confirm({
    title: '确认发布',
    content: `确定要发布问卷 "${record.questionnaireName}" 吗？发布后将可用于填写。`,
    async onOk() {
      try {
        await publishQuestionnaire(record.questionnaireId);
        message.success('发布成功');
        templateTableRef.value.refresh();
      } catch (error) {
        message.error('发布失败');
      }
    },
  });
};

const disableTemplate = (record) => {
  Modal.confirm({
    title: '确认停用',
    content: `确定要停用问卷 "${record.questionnaireName}" 吗？停用后将不可用于填写。`,
    async onOk() {
      try {
        await disableQuestionnaire(record.questionnaireId);
        message.success('停用成功');
        templateTableRef.value.refresh();
      } catch (error) {
        message.error('停用失败');
      }
    },
  });
};

// ========== 问卷记录管理 ==========
const recordTableRef = ref();
const recordApi = { list: listRecord, delete: delRecord };

const recordSearchParams = reactive({
  residentName: null,
  questionnaireName: null,
  isKeyPopulation: null,
});

const recordSearchFields = computed(() => [
  { name: 'residentName', label: '居民姓名', type: 'input' },
  { name: 'questionnaireName', label: '问卷名称', type: 'input' },
  {
    name: 'isKeyPopulation',
    label: '是否重点人群',
    type: 'select',
    options: [
      { label: '是', value: '1' },
      { label: '否', value: '0' },
    ],
  },
]);

const recordColumns = [
  { title: '居民姓名', dataIndex: 'residentName', key: 'residentName', width: 120 },
  { title: '问卷名称', dataIndex: 'questionnaireName', key: 'questionnaireName', width: 200 },
  { title: '总分', dataIndex: 'totalScore', key: 'totalScore', width: 100 },
  { title: '重点人群', dataIndex: 'isKeyPopulation', key: 'isKeyPopulation', width: 100 },
  { title: '填写时间', dataIndex: 'submitTime', key: 'submitTime', width: 180 },
  { title: '填写人', dataIndex: 'fillUserName', key: 'fillUserName', width: 120 },
  { title: '操作', key: 'operateCol', width: 200, fixed: 'right' },
];

const recordDetailModalRef = ref();

const openDetailRecordModal = (record) => {
  recordDetailModalRef.value.openModal(record);
};

const handleDeleteRecord = async (record) => {
  try {
    await delRecord(record.recordId);
    message.success('删除成功');
    recordTableRef.value.refresh();
  } catch (error) {
    message.error('删除失败');
  }
};
</script>

<style lang="less" scoped>
.gc-questionnaire-page {
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
