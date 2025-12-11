<template>
  <div>
    <ProTable
        ref="proTableRef"
        :api="tableApi"
        :columns="columns"
        :initialSearchParams="initialSearchParams"
        :searchFields="searchFields"
        :isTreeTable="true"
        :rowSelection="{ type: 'radio' }"
        rowKey="deptId"
    >
      <!-- 1. 自定义操作按钮 -->
      <template #actions="{ selectedRowKeys, delete: deleteRows }">
        <a-button v-hasPermi="['system:dept:add']" type="primary" @click="openDeptAddModal">
          <BearJiaIcon icon="plus-outlined"/>新增
        </a-button>
        <a-button v-hasPermi="['system:dept:remove']" :disabled="selectedRowKeys.length <= 0" danger type="primary"
                  @click="() => deleteRows()">
          <BearJiaIcon icon="delete-outlined"/>删除
        </a-button>
      </template>

      <!-- 2. 自定义列渲染 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <!-- 使用字典标签组件 -->
          <dict-tag :options="sys_normal_disable" :value="record.status"/>
        </template>
        <template v-else-if="column.key === 'deptTableOperateCol'">
          <TableActionBar
              :hasDelete="false"
              :hasEdit="true"
              :hasView="true"
              :record="record"
              @edit="openDeptUpdateModal"
              @view="openDeptDetailModal"
          />
        </template>
      </template>
    </ProTable>

    <!-- Modals -->
    <DeptAddUpdateModal ref="deptAddUpdateModalRef"
                        @querDeptTreeData="querDeptTreeData"
                        :statusDict="sys_normal_disable"
                        :deptTreeData="pageData.deptTreeData"
                        @refreshFatherPageTable="() => proTableRef.refresh()" />
    <DeptDetailModal ref="deptDetailModalRef"
                     :statusDict="sys_normal_disable"
                     :deptTreeData="pageData.deptTreeData" />
  </div>
</template>

<script setup name="Dept">
import {computed, getCurrentInstance, reactive, ref} from 'vue';
import {listDept, delDept} from '@/api/system/dept';
import BearJiaUtil from '@/utils/BearJiaUtil.js';

// 引入组件
import ProTable from '@/components/BearJiaProTable/index.vue';
import DeptAddUpdateModal from './addUpdateModal.vue';
import DeptDetailModal from './detailModal.vue';
import TableActionBar from '@/components/TableActionBar/index.vue';
import {BearJiaIcon} from '@/utils/BearJiaIcon.js';

const {proxy} = getCurrentInstance();
const proTableRef = ref();

// 使用 useDict 获取字典
const {sys_normal_disable} = proxy.useDict("sys_normal_disable");

// 当前页面使用的数据（保留部门树数据）
const pageData = reactive({
  deptTreeData: [],
});

const querDeptTreeData = () => {
  BearJiaUtil.getDeptTreeData().then((res) => {
    pageData.deptTreeData = res.data;
  });
};
querDeptTreeData();

// --- ProTable 配置 ---
const tableApi = {
  list: listDept,
  delete: delDept,
  // 特殊处理：部门是树形结构，需要将扁平数据转换为树形结构
  processListData: (data) => {
    // 使用BearJiaUtil的handleTree方法将扁平数据转换为树形结构
    // 参数：数据源, id字段, parentId字段, children字段
    return BearJiaUtil.handleTree(data, 'deptId', 'parentId', 'children');
  }
};
const initialSearchParams = {deptName: null, status: null};
// 部门管理没有导出功能
const exportConfig = null;

const searchFields = computed(() => [
  {name: 'deptName', label: '部门名称', type: 'input'},
  {name: 'status', label: '部门状态', type: 'select', options: sys_normal_disable.value},
]);

const columns = [
  {title: '部门名称', dataIndex: 'deptName', key: 'deptName'},
  {title: '显示顺序', dataIndex: 'orderNum', key: 'orderNum'},
  {title: '负责人', dataIndex: 'leader', key: 'leader'},
  {title: '部门状态', dataIndex: 'status', key: 'status'},
  {title: '操作', key: 'deptTableOperateCol', width: 200, fixed: 'right'},
];

// --- 页面特有逻辑 ---
const deptAddUpdateModalRef = ref();
const deptDetailModalRef = ref();

const openDeptAddModal = () => deptAddUpdateModalRef.value.openAddModal();
const openDeptUpdateModal = (record) => deptAddUpdateModalRef.value.openUpdateModal(record);
const openDeptDetailModal = (record) => deptDetailModalRef.value.openModal(record);

// 删除后需要刷新部门树数据
const handleDeleteSuccess = () => {
  querDeptTreeData(); // 刷新部门树数据
  proTableRef.value?.refresh(); // 刷新表格
};


</script>
<style lang="less"></style>
