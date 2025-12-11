<template>
  <div>
    <ProTable
        ref="proTableRef"
        :api="tableApi"
        :columns="columns"
        :initialSearchParams="initialSearchParams"
        :searchFields="searchFields"
        rowKey="tokenId"
    >
      <!-- 1. 自定义操作按钮 -->
      <template #actions="{ selectedRowKeys, delete: deleteRows }">
        <!-- 在线用户页面没有操作按钮 -->
      </template>

      <!-- 2. 自定义列渲染 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'loginTime'">
          <!-- 格式化登录时间 -->
          <span>{{ BearJiaUtil.parseDateTime(record.loginTime) }}</span>
        </template>
        <template v-else-if="column.key === 'onlineTableOperateCol'">
          <TableActionBar
              :hasView="false"
              :hasEdit="false"
              :hasDelete="true"
              deleteText="强退"
              deleteIcon="logout-outlined"
              :record="record"
              @delete="clickForceLogout"
          />
        </template>
      </template>
    </ProTable>
  </div>
</template>

<script name="Online" setup>
import {computed, ref} from 'vue';
import {forceLogout, list} from '@/api/monitor/online';
import BearJiaUtil from '@/utils/BearJiaUtil.js';

// 引入组件
import ProTable from '@/components/BearJiaProTable/index.vue';
import TableActionBar from '@/components/TableActionBar/index.vue';

const proTableRef = ref();

// --- ProTable 配置 ---
const tableApi = {list: list}; // 在线用户没有删除功能
const initialSearchParams = {ipaddr: null, userName: null};
// 在线用户页面没有导出功能
const exportConfig = null;

const searchFields = computed(() => [
  {name: 'ipaddr', label: '登录地址', type: 'input'},
  {name: 'userName', label: '用户名称', type: 'input'},
]);

const columns = [
  {title: '会话编号', dataIndex: 'tokenId', key: 'tokenId', ellipsis: true},
  {title: '登录名称', dataIndex: 'userName', key: 'userName'},
  {title: '部门名称', dataIndex: 'deptName', key: 'deptName'},
  {title: '登录地址', dataIndex: 'ipaddr', key: 'ipaddr'},
  {title: '登录地点', dataIndex: 'loginLocation', key: 'loginLocation'},
  {title: '浏览器', dataIndex: 'browser', key: 'browser'},
  {title: '操作系统', dataIndex: 'os', key: 'os'},
  {title: '登录时间', dataIndex: 'loginTime', key: 'loginTime'},
  {title: '操作', key: 'onlineTableOperateCol', width: 120, fixed: 'right'},
];

// --- 页面特有逻辑 ---
// 点击强退
const clickForceLogout = (row) => {
  BearJiaUtil.confirmOperate('强退', () => {
    forceLogout(row.tokenId)
        .then(() => {
          BearJiaUtil.messageSuccess('强退操作成功。');
          proTableRef.value?.refresh();
        })
        .catch(() => {
        });
  });
};
</script>
<style lang="less"></style>
