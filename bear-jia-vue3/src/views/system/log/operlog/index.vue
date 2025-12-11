<template>
  <div>
    <a-form ref="queryFormRef" name="queryForm" :model="queryForm.data" :labelCol="{ span: 8 }" :wrapperCol="{ span: 14 }">
      <a-row :gutter="24">
        <a-col span="8">
          <a-form-item name="userName" label="用户账号">
            <a-input v-model:value="queryForm.data.userName" allowClear></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item name="status" label="所属部门">
            <a-tree-select v-model:value="queryForm.data.deptId" :tree-data="pageData.deptTreeData" :fieldNames="{ children: 'children', label: 'label', key: 'id', value: 'id' }"> </a-tree-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item name="status" label="状态">
            <a-select v-model:value="queryForm.data.status" :options="pageData.statusDict" allowClear> </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item name="beginTime" label="创建起期">
            <a-date-picker v-model:value="queryForm.data.params.beginTime" format="YYYY-MM-DD" valueFormat="YYYY-MM-DD" />
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item name="endTime" label="创建止期">
            <a-date-picker v-model:value="queryForm.data.params.endTime" format="YYYY-MM-DD" valueFormat="YYYY-MM-DD" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24" class="bearjia-button-row">
        <a-col :span="12" style="text-align: left">
          <a-button type="primary" @click="openAddModal()" v-hasPermi="['system:user:add']"><BearJiaIcon icon="plus-outlined" />新增</a-button>
          <a-button type="primary" danger @click="clickDelete()" :disabled="userTableData.selectedRowKeys.length <= 0" v-hasPermi="['system:user:remove']"><BearJiaIcon icon="delete-outlined" />删除</a-button>
          <a-button type="primary" @click="clickExport()" v-hasPermi="['system:user:export']"><BearJiaIcon icon="export-outlined" />导出</a-button>
        </a-col>
        <a-col :span="12" style="text-align: right">
          <a-button type="primary" @click="userTablePage.reload()"> <BearJiaIcon icon="SearchOutlined" />查询 </a-button>
          <a-button @click="resetQueryForm()"><BearJiaIcon icon="redo-outlined" />重置</a-button>
        </a-col>
      </a-row>
    </a-form>
    <!-- tableLayout设为 fixed 表示内容不会影响列的布局 	- | 'auto' | 'fixed' -->
    <a-table
      rowKey="userId"
      :columns="userTableData.columns"
      :data-source="userTableData.dataSource"
      :loading="userTablePage.loading.value"
      :pagination="userTablePagination"
      @change="userTableHandChangePage"
      :row-selection="{ selectedRowKeys: userTableData.selectedRowKeys, onChange: onTableSelectChange }"
    >
      <template #bodyCell="{ index, column, record }">
        <template v-if="column.key === 'pageIndex'">
          {{ index + 1 }}
        </template>
        <!-- <template v-else-if="column.key === 'userName'">
          <a @click="clickUserDetail(record)">
            {{ record.userName }}
          </a>
        </template> -->
        <template v-else-if="column.key === 'status'">
          <span>
            <a-tag v-if="record.status === '0'" color="green" style="width: 50px; text-align: center">
              {{ BearJiaUtil.getDictLabelByKey(record.status, pageData.statusDict) }}
            </a-tag>
            <a-tag v-else-if="record.status === '1'" color="red" style="width: 50px; text-align: center">
              {{ BearJiaUtil.getDictLabelByKey(record.status, pageData.statusDict) }}
            </a-tag>
          </span>
        </template>
        <template v-else-if="column.key === 'sex'">
          <span>
            {{ BearJiaUtil.getDictLabelByKey(record.sex, pageData.sexDict) }}
          </span>
        </template>
        <template v-else-if="column.key === 'operateCol'">
          <span>
            <a @click="clickUserDetail(record)"> 查看 </a>
            <a-divider type="vertical" />
            <a @click="openUpdateModal(record)" v-hasPermi="['system:user:edit']"> 修改 </a>
            <a-divider type="vertical" v-hasPermi="['system:user:edit']" />
            <a @click="openResetPasswordModal(record)"> 重置密码 </a>
          </span>
        </template>
      </template>
    </a-table>

    <UserAddUpdate ref="userAddUpdateRef" />
    <UserDetail ref="userDetailRef" />
    <UseResetPassword ref="useResetPasswordRef" />
  </div>
</template>

<script setup>
import { listUser, delUser, addUser, getUser, updateUser, resetUserPwd } from '@/api/system/user';
import { computed, reactive, ref } from 'vue';
import { BearJiaIcon } from '@/utils/BearJiaIcon.js';
import BearJiaUtil from '@/utils/BearJiaUtil.js';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import UserAddUpdate from './userAddUpdate.vue';
import UserDetail from './userDetail.vue';
import UseResetPassword from './useResetPassword.vue';
import request from '@/utils/request';

// 当前页面使用的数据
const pageData = reactive({
  sexDict: [],
  deptTreeData: [],
  statusDict: [],
  postDict: [],
  roleDict: [],
});
BearJiaUtil.getDictsByType('sys_user_sex').then((res) => {
  pageData.sexDict = res;
});
BearJiaUtil.getDeptTreeData().then((res) => {
  pageData.deptTreeData = res.data;
});
BearJiaUtil.getDictsByType('sys_normal_disable').then((res) => {
  pageData.statusDict = res;
});

// 查询Form
const queryFormRef = ref();
const queryForm = reactive({ data: { params: {} } });

// 重置查询Form
const resetQueryForm = () => {
  // 使用resetFields()方法，需要指定queryForm.data中所有字段的初始值，查询字段添加时，必须同时在queryForm.data中对应添加，比较麻烦
  // 直接循环queryForm.data中全部字段，赋值为空，不用在queryForm.data中添加每个查询字段，写法简单些。
  // 如果需要给某些字段赋初始值，可以单独进行赋值
  // queryFormRef.value.resetFields();
  BearJiaUtil.resetFormFieldsToEmpty(queryForm.data);
  // queryForm.data.userName = '1111';
  userTablePage.reload();
};

// 用户列表数据
const userTableData = reactive({
  // 列表数据
  dataSource: [],
  // 列表记录数
  total: 0,
  selectedRowKeys: [],
  // 列表列定义
  columns: [
    {
      title: '序号',
      dataIndex: 'pageIndex',
      key: 'pageIndex',
      width: 50,
      align: 'center',
    },
    {
      title: '用户账号',
      dataIndex: 'userName',
      key: 'userName',
    },
    {
      title: '用户名称',
      dataIndex: 'nickName',
      key: 'nickName',
    },
    {
      title: '手机号码',
      dataIndex: 'phonenumber',
      key: 'phonenumber',
    },
    // {
    //   title: '邮箱',
    //   dataIndex: 'email',
    //   key: 'email',
    //   width: 200,
    // },
    {
      title: '性别',
      dataIndex: 'sex',
      key: 'sex',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
    },
    {
      title: '操作',
      key: 'operateCol',
      width: 180,
    },
  ],
});

// 列表选中方法
const onTableSelectChange = (selectedRowKeys) => {
  userTableData.selectedRowKeys = selectedRowKeys;
};

// 查询列表
const queryUserList = () => {
  return listUser(queryForm.data);
};

// 用户列表翻页数据：使用vue-request中分页方法，返回参数包含{ run, reload, loading, current, pageSize }
const userTablePage = BearJiaUtil.usePagination(queryUserList, userTableData);

// 用户列表翻页工具条：必须通过计算函数每次重新生成
const userTablePagination = computed(() => BearJiaUtil.getTablePagination(userTablePage));

// 手动翻页方法
const userTableHandChangePage = (page, filters, sorter) => {
  BearJiaUtil.tableHandChangePage(userTablePage, page, filters, sorter);
};

const userAddUpdateRef = ref();
// 打开新增窗口
const openAddModal = () => {
  userAddUpdateRef.value.openAddModal(userTablePage);
};

// 打开修改窗口
const openUpdateModal = (record) => {
  userAddUpdateRef.value.openUpdateModal(record, userTablePage);
};

const userDetailRef = ref();
// 打开详细窗口
const clickUserDetail = (record) => {
  userDetailRef.value.openModal(record);
};

//重置密码
const useResetPasswordRef = ref();

// 打开重置密码窗口
const openResetPasswordModal = (record) => {
  useResetPasswordRef.value.openModal(record);
};

// 点击删除
const clickDelete = () => {
  BearJiaUtil.confirmDeleteSelectedData(() => {
    delUser(userTableData.selectedRowKeys).then((res) => {
      userTableData.selectedRowKeys = [];
      BearJiaUtil.messageSuccess('删除操作成功。');
      userTablePage.reload();
    });
  });
};
// 点击导出
const clickExport = () => {
  BearJiaUtil.download('system/user/export', queryForm.data, "操作日志_" + dayjs().format('YYYY-MM-DD_HH-mm-ss') + ".xlsx");
};
</script>

<style lang="less"></style>
