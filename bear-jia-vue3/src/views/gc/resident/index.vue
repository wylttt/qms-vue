<template>
  <div class="gc-resident-page">
    <ProTable
      ref="proTableRef"
      :api="tableApi"
      :columns="columns"
      :exportConfig="exportConfig"
      :initialSearchParams="initialSearchParams"
      :searchFields="searchFields"
      rowKey="residentId"
    >
      <!-- 自定义操作按钮 -->
      <template #actions="{ selectedRowKeys, delete: deleteRows }">
        <a-button v-hasPermi="['gc:resident:add']" type="primary" @click="openAddModal">
          <BearJiaIcon icon="plus-outlined" />新增
        </a-button>
        <a-button
          v-hasPermi="['gc:resident:remove']"
          :disabled="selectedRowKeys.length <= 0"
          danger
          type="primary"
          @click="() => deleteRows()"
        >
          <BearJiaIcon icon="delete-outlined" />删除
        </a-button>
        <a-button v-hasPermi="['gc:resident:import']" @click="openImportModal">
          <BearJiaIcon icon="upload-outlined" />导入
        </a-button>
        <a-button v-hasPermi="['gc:resident:export']" @click="() => proTableRef.export()">
          <BearJiaIcon icon="export-outlined" />导出
        </a-button>
        <a-button @click="downloadTemplate">
          <BearJiaIcon icon="download-outlined" />模板下载
        </a-button>
      </template>

      <!-- 自定义列渲染 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'sex'">
          <dict-tag :options="gc_sex" :value="record.sex" />
        </template>
        <template v-else-if="column.key === 'isKeyPopulation'">
          <a-tag :color="record.isKeyPopulation === 1 ? 'red' : 'default'">
            {{ record.isKeyPopulation === 1 ? '是' : '否' }}
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
              <a class="action-btn" @click="viewHistory(record)">
                <BearJiaIcon icon="history-outlined" />
                筛查记录
              </a>
            </template>
          </TableActionBar>
        </template>
      </template>
    </ProTable>

    <!-- 新增/编辑弹窗 -->
    <AddUpdateModal
      ref="addUpdateModalRef"
      :gc-sex-dict="gc_sex"
      @refresh-father-page-table="() => proTableRef.refresh()"
    />

    <!-- 详情弹窗 -->
    <DetailModal
      ref="detailModalRef"
      :gc-sex-dict="gc_sex"
    />

    <!-- 导入弹窗 -->
    <ImportModal
      ref="importModalRef"
      @refresh-father-page-table="() => proTableRef.refresh()"
    />
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, reactive, ref } from 'vue';
import { listResident, delResident, downloadTemplate as downloadTemplateApi } from '@/api/gc';
import { BearJiaIcon } from '@/utils/BearJiaIcon.js';
import ProTable from '@/components/BearJiaProTable/index.vue';
import TableActionBar from '@/components/TableActionBar/index.vue';
import AddUpdateModal from './addUpdateModal.vue';
import DetailModal from './detailModal.vue';
import ImportModal from './importModal.vue';
import { message } from 'ant-design-vue';

const { proxy } = getCurrentInstance();
const proTableRef = ref();

// 获取字典
const { gc_sex } = proxy.useDict('gc_sex');

// ProTable 配置
const tableApi = { list: listResident, delete: delResident };

const initialSearchParams = reactive({
  residentName: null,
  idCard: null,
  phone: null,
  sex: null,
  isKeyPopulation: null,
  province: null,
  city: null,
  district: null,
  town: null,
  village: null,
});

const exportConfig = { 
  url: '/api/gc/resident/export', 
  fileName: '居民数据' 
};

const searchFields = computed(() => [
  { name: 'residentName', label: '姓名', type: 'input' },
  { name: 'idCard', label: '身份证号', type: 'input' },
  { name: 'phone', label: '联系电话', type: 'input' },
  { name: 'sex', label: '性别', type: 'select', options: gc_sex.value },
  {
    name: 'isKeyPopulation',
    label: '重点人群',
    type: 'select',
    options: [
      { label: '是', value: '1' },
      { label: '否', value: '0' },
    ],
  },
]);

const columns = [
  { title: '姓名', dataIndex: 'residentName', key: 'residentName', width: 100 },
  { title: '性别', dataIndex: 'sex', key: 'sex', width: 80 },
  { title: '年龄', dataIndex: 'age', key: 'age', width: 80 },
  { title: '身份证号', dataIndex: 'idCard', key: 'idCard', width: 180 },
  { title: '联系电话', dataIndex: 'phone', key: 'phone', width: 120 },
  { title: '居住地址', dataIndex: 'fullAddress', key: 'fullAddress', width: 300, ellipsis: true },
  { title: '重点人群', dataIndex: 'isKeyPopulation', key: 'isKeyPopulation', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'operateCol', width: 280, fixed: 'right' },
];

// Modal 引用
const addUpdateModalRef = ref();
const detailModalRef = ref();
const importModalRef = ref();

// 打开新增弹窗
const openAddModal = () => {
  addUpdateModalRef.value.openAddModal();
};

// 打开编辑弹窗
const openUpdateModal = (record) => {
  addUpdateModalRef.value.openUpdateModal(record);
};

// 打开详情弹窗
const openDetailModal = (record) => {
  detailModalRef.value.openModal(record);
};

// 打开导入弹窗
const openImportModal = () => {
  importModalRef.value.openModal();
};

// 删除单条记录
const handleDelete = async (record) => {
  try {
    await delResident(record.residentId);
    message.success('删除成功');
    proTableRef.value.refresh();
  } catch (error) {
    message.error('删除失败');
  }
};

// 查看筛查记录
const viewHistory = (record) => {
  // TODO: 跳转到筛查记录页面或打开筛查记录弹窗
  message.info(`查看居民 ${record.residentName} 的筛查记录`);
};

// 下载模板
const downloadTemplate = async () => {
  try {
    const response = await downloadTemplateApi();
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '居民信息导入模板.xlsx';
    link.click();
    window.URL.revokeObjectURL(url);
    message.success('模板下载成功');
  } catch (error) {
    message.error('模板下载失败');
  }
};
</script>

<style lang="less" scoped>
.gc-resident-page {
  height: 100%;
}
</style>
