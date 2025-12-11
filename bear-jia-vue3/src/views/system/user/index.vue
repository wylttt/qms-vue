<template>
  <div>
    <a-row :gutter="16">
      <a-col span="6">
        <!-- 部门树 -->
        <a-card>
          <dept-tree
            ref="deptTreeRef"
            :dept-options="deptOptions"
            @select="clickDeptNode"
        />
        </a-card>
      </a-col>
      <a-col span="18">
        <ProTable
            ref="proTableRef"
            :api="tableApi"
            :columns="columns"
            :exportConfig="exportConfig"
            :initialSearchParams="initialSearchParams"
            :searchFields="searchFields"
            rowKey="userId"
        >
          <!-- 1. 自定义操作按钮 -->
          <template #actions="{ selectedRowKeys, delete: deleteRows }">
            <a-button v-hasPermi="['system:user:add']" type="primary" @click="openAddModal">
              <BearJiaIcon icon="plus-outlined"/>新增
            </a-button>
            <a-button v-hasPermi="['system:user:remove']" :disabled="selectedRowKeys.length <= 0" danger type="primary"
                      @click="() => deleteRows()">
              <BearJiaIcon icon="delete-outlined"/>删除
            </a-button>
            <a-button v-hasPermi="['system:user:export']" @click="() => proTableRef.export()">
              <BearJiaIcon icon="export-outlined"/>导出
            </a-button>
          </template>

          <!-- 2. 自定义列渲染 -->
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'sex'">
              <!-- 使用字典标签组件 -->
              <dict-tag :options="sys_user_sex" :value="record.sex"/>
            </template>
            <template v-else-if="column.key === 'status'">
              <span>
                <a-badge status="success"/>
                <dict-tag :options="sys_normal_disable" :value="record.status"/>
              </span>
            </template>
            <template v-else-if="column.key === 'userTableOperateCol'">
              <TableActionBar
                  :hasDelete="false"
                  :hasEdit="true"
                  :hasView="true"
                  :record="record"
                  @edit="openUpdateModal"
                  @view="openDetailModal"
              >
                <!-- 使用 actions 插槽添加重置密码按钮 -->
                <template #actions="{ record }">
                  <a-divider type="vertical"/>
                  <a
                      class="action-btn custom-btn"
                      style="margin-left: 8px;"
                      @click="openResetPasswordModal(record)"
                  >
                    <BearJiaIcon icon="key-outlined"/>
                    重置密码
                  </a>
                </template>
              </TableActionBar>
            </template>
          </template>
        </ProTable>
      </a-col>
    </a-row>

    <!-- Modals -->
    <AddUpdateModal
        ref="addUpdateModalRef"
        :sys-normal-disable-dict="sys_normal_disable"
        :sys-user-sex-dict="sys_user_sex"
        @refresh-father-page-table="() => proTableRef.refresh()"
    />
    <DetailModal
        ref="detailModalRef"
        :sys-normal-disable-dict="sys_normal_disable"
        :sys-user-sex-dict="sys_user_sex"
    />
    <UseResetPassword ref="useResetPasswordRef"/>
  </div>
</template>

<script setup>
import {computed, getCurrentInstance, reactive, ref} from 'vue';
import {delUser, listUser} from '@/api/system/user';
import BearJiaUtil from '@/utils/BearJiaUtil.js';

// 引入组件
import ProTable from '@/components/BearJiaProTable/index.vue';
import AddUpdateModal from './addUpdateModal.vue';
import DetailModal from './detailModal.vue';
import UseResetPassword from './useResetPassword.vue';
import DeptTree from './DeptTree.vue';
import TableActionBar from '@/components/TableActionBar/index.vue';
import {BearJiaIcon} from '@/utils/BearJiaIcon.js';

const {proxy} = getCurrentInstance();
const proTableRef = ref();

// 使用 useDict 获取字典
const {sys_user_sex, sys_normal_disable} = proxy.useDict("sys_user_sex", "sys_normal_disable");

// 当前页面使用的数据（保留部门树数据）
const pageDataObj = reactive({
  deptTreeData: [],
});

// 异步加载部门树数据
BearJiaUtil.getDeptTreeData().then((res) => {
  pageDataObj.deptTreeData = res.data;
});

// --- ProTable 配置 ---
const tableApi = {list: listUser, delete: delUser};
const initialSearchParams = reactive({
  userName: null,
  nickName: null,
  sex: null,
  status: null,
  deptId: null, // 部门ID，由部门树选择控制
});
const exportConfig = {url: 'system/user/export', fileName: '用户数据'};

const searchFields = computed(() => [
  {name: 'userName', label: '用户账号', type: 'input'},
  {name: 'nickName', label: '用户名称', type: 'input'},
  {name: 'sex', label: '用户性别', type: 'select', options: sys_user_sex.value},
  {name: 'status', label: '帐号状态', type: 'select', options: sys_normal_disable.value},
]);

const columns = [
  {title: '用户账号', dataIndex: 'userName', key: 'userName'},
  {title: '用户名称', dataIndex: 'nickName', key: 'nickName'},
  {title: '手机号码', dataIndex: 'phonenumber', key: 'phonenumber'},
  {title: '性别', dataIndex: 'sex', key: 'sex'},
  {title: '帐号状态', dataIndex: 'status', key: 'status'},
  {title: '操作', key: 'userTableOperateCol', width: 320, fixed: 'right'},
];

// 部门树选项（从 pageDataObj 获取）
const deptOptions = computed(() => pageDataObj.deptTreeData);

// 点击部门节点 - 更新ProTable的查询参数
const clickDeptNode = (keys) => {
  if (keys.length > 0) {
    // 直接更新 ProTable 的 searchFormData
    proTableRef.value.searchFormData.deptId = keys[0];
    // 触发ProTable刷新
    proTableRef.value?.refresh();
  } else {
    proTableRef.value.searchFormData.deptId = null;
    proTableRef.value?.refresh();
  }
}

// --- 页面特有逻辑 ---
const addUpdateModalRef = ref();
const detailModalRef = ref();
const useResetPasswordRef = ref();
const deptTreeRef = ref();

const openAddModal = () => addUpdateModalRef.value.openAddModal();
const openUpdateModal = (record) => addUpdateModalRef.value.openUpdateModal(record);
const openDetailModal = (record) => detailModalRef.value.openModal(record);
const openResetPasswordModal = (record) => useResetPasswordRef.value.openModal(record);
</script>

<style lang="less" scoped>
.ant-row {
  height: 100%;
  
  .ant-col {
    &:first-child {
      .ant-card {
        height: calc(100vh - 124px);
        
        :deep(.ant-card-body) {
          height: calc(100% - 57px);
          padding: 16px;
          overflow: hidden;
          
          > div {
            height: 100%;
            display: flex;
            flex-direction: column;
            
            .ant-input-search {
              margin-bottom: 12px;
              flex-shrink: 0;
            }
            
            .ant-tree {
              flex: 1;
              overflow-y: auto;
              
              &::-webkit-scrollbar {
                width: 6px;
              }
              
              &::-webkit-scrollbar-track {
                background: var(--bg-color);
                border-radius: 3px;
              }
              
              &::-webkit-scrollbar-thumb {
                background: var(--border-color-base);
                border-radius: 3px;
                
                &:hover {
                  background: var(--text-secondary);
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>