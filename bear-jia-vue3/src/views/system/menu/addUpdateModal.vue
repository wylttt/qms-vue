<template>
  <div>
    <a-modal
        v-model:visible="pageData.visible"
        width="60%"
        :title="pageData.title"
        :destroy-on-close="true"
        @ok="clickModalOk"
        @cancel="handleModalCancel"
    >
      <a-form
          ref="menuAddUpdateFormRef"
          name="menuAddUpdateForm"
          :model="menuAddUpdateForm.data"
          :label-col="{ span: 8 }"
          :wrapper-col="{ span: 14 }"
      >
        <a-row :gutter="24">
          <a-col span="12">
            <a-form-item
                name="menuName"
                label="菜单名称"
                :rules="[{ required: true, message: '菜单名称不能为空！' }]"
            >
              <a-input
                  v-model:value="menuAddUpdateForm.data.menuName"
                  :maxlength="30"
                  placeholder="请填写菜单名称"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item
                name="parentId"
                label="上级菜单"
                :rules="[{ required: true, message: '上级菜单不能为空！' }]"
            >
              <a-tree-select
                  v-model:value="menuAddUpdateForm.data.parentId"
                  placeholder="请选择上级菜单"
                  :tree-data="pageData.menusDict"
                  :field-names="{ children: 'children', label: 'menuName', key: 'menuId', value: 'menuId' }"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item
                name="orderNum"
                label="显示顺序"
                :rules="[{ required: true, message: '显示顺序不能为空！' }]"
            >
              <a-input
                  v-model:value="menuAddUpdateForm.data.orderNum"
                  :maxlength="30"
                  placeholder="请填写显示顺序"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item name="path" label="路由地址" :rules="[{}]">
              <a-input
                  v-model:value="menuAddUpdateForm.data.path"
                  :maxlength="30"
                  placeholder="请填写路由地址"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item name="component" label="组件路径" :rules="[{}]">
              <a-input
                  v-model:value="menuAddUpdateForm.data.component"
                  :maxlength="30"
                  placeholder="请填写组件路径"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item name="query" label="路由参数" :rules="[{}]">
              <a-input
                  v-model:value="menuAddUpdateForm.data.query"
                  :maxlength="30"
                  placeholder="请填写路由参数"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item
                name="isFrame"
                label="是否为外链"
                :rules="[{ required: true, message: '是否为外链不能为空！' }]"
            >
              <a-radio-group
                  v-model:value="menuAddUpdateForm.data.isFrame"
                  :options="fatherPageData.sysYesNoDict"
                  option-type="button"
                  button-style="solid"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item
                name="menuType"
                label="菜单类型"
                :rules="[{ required: true, message: '菜单类型不能为空！' }]"
            >
              <a-select
                  v-model:value="menuAddUpdateForm.data.menuType"
                  :options="fatherPageData.menuTypeDict"
                  placeholder="请选择菜单类型"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item
                name="visible"
                label="是否显示"
                :rules="[{ required: true, message: '是否显示不能为空！' }]"
            >
              <a-radio-group
                  v-model:value="menuAddUpdateForm.data.visible"
                  :options="fatherPageData.sysYesNoDict"
                  option-type="button"
                  button-style="solid"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item
                name="status"
                label="菜单状态"
                :rules="[{ required: true, message: '菜单状态不能为空！' }]"
            >
              <a-radio-group
                  v-model:value="menuAddUpdateForm.data.status"
                  :options="fatherPageData.sysNormalDisableDict"
                  option-type="button"
                  button-style="solid"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item name="perms" label="权限标识" :rules="[{}]">
              <a-input
                  v-model:value="menuAddUpdateForm.data.perms"
                  :maxlength="30"
                  placeholder="请填写权限标识"
              />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item name="icon" label="菜单图标" :rules="[{}]">
              <a-space size="large">
                <!-- 显示 Ant Design 图标 -->
                <component
                    :is="iconComponent"
                    v-if="menuAddUpdateForm.data.icon && iconComponent"
                    style="font-size: 20px;"
                />
                <!-- 显示自定义图标 -->
                <svg-icon
                    v-else-if="menuAddUpdateForm.data.icon && isCustomIcon(menuAddUpdateForm.data.icon)"
                    :icon-class="menuAddUpdateForm.data.icon"
                    :size="20"
                    color="#333"
                />
                <a-button type="dashed" @click="showIconModal">
                  选择图标
                </a-button>
              </a-space>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item name="remark" label="备注" :rules="[{}]">
              <a-textarea
                  v-model:value="menuAddUpdateForm.data.remark"
                  placeholder="请填写备注"
                  :rows="3"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <!-- 图标选择模态框 -->
    <a-modal
        v-model:visible="iconModalVisible"
        title="选择图标"
        :width="900"
        :footer="null"
        :destroy-on-close="true"
    >
      <a-input-search
          v-model:value="iconSearch"
          placeholder="搜索图标"
          style="margin-bottom: 16px;"
          clearable
          @search="filterIcons"
      />
      <a-tabs v-model:activeKey="currentIconTab" v-if="!iconSearch">
        <!-- 自定义图标标签页 -->
        <a-tab-pane key="custom" tab="自定义图标">
          <div class="icon-list">
            <a-tooltip
                v-for="icon in customIcons"
                :key="icon"
                :title="icon"
            >
              <svg-icon
                  :icon-class="icon"
                  class="icon-item custom-icon-item"
                  :class="{ 'icon-selected': menuAddUpdateForm.data.icon === icon }"
                  :size="40"
                  @click="selectIcon(icon)"
              />
            </a-tooltip>
          </div>
        </a-tab-pane>
        <!-- Ant Design 图标分类标签页 -->
        <a-tab-pane
            v-for="category in iconCategories"
            :key="category.key"
            :tab="category.title"
        >
          <div class="icon-list">
            <a-tooltip
                v-for="icon in category.icons"
                :key="icon"
                :title="icon"
            >
              <component
                  :is="allIcons[icon]"
                  class="icon-item"
                  :class="{ 'icon-selected': menuAddUpdateForm.data.icon === icon }"
                  @click="selectIcon(icon)"
              />
            </a-tooltip>
          </div>
        </a-tab-pane>
      </a-tabs>
      <div class="icon-list" v-else>
        <!-- 搜索结果：自定义图标 -->
        <a-tooltip
            v-for="icon in filteredCustomIcons"
            :key="icon"
            :title="icon + ' (自定义)'"
        >
          <svg-icon
              :icon-class="icon"
              class="icon-item custom-icon-item"
              :class="{ 'icon-selected': menuAddUpdateForm.data.icon === icon }"
              :size="48"
              @click="selectIcon(icon)"
          />
        </a-tooltip>
        <!-- 搜索结果：Ant Design 图标 -->
        <a-tooltip
            v-for="icon in filteredAntIcons"
            :key="icon"
            :title="icon"
        >
          <component
              :is="allIcons[icon]"
              class="icon-item"
              :class="{ 'icon-selected': menuAddUpdateForm.data.icon === icon }"
              @click="selectIcon(icon)"
          />
        </a-tooltip>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue';
import BearJiaUtil from '@/utils/BearJiaUtil.js';
import { listMenu as getMenuTreeselect, getMenu, addMenu, updateMenu } from '@/api/system/menu';
import { message } from 'ant-design-vue';
import { handleTree } from '@/utils/bearjia.js';
import * as allIcons from '@ant-design/icons-vue'; // 导入所有 Ant Design 图标
import iconCategoriesData from '@/components/IconSelector/icons.js'; // 导入图标分类数据
import SvgIcon from '@/components/SvgIcon/index.vue'; // 导入 SvgIcon 组件

// 父页面公用数据
const fatherPageData = defineProps({
  sysYesNoDict: Array,
  sysShowHideDict: Array,
  menuTypeDict: Array,
  sysNormalDisableDict: Array,
});

const emit = defineEmits(['refreshFatherPageTable']);

// 当前页面使用的数据
const pageData = reactive({
  title: '新增页面',
  visible: false,
  operateType: '',
  menusDict: [],
});

// 新增修改表单
const menuAddUpdateFormRef = ref();
const menuAddUpdateForm = reactive({
  data: { isFrame: '1', visible: '0', status: '0' },
});

// 重置表单
const resetMenuAddUpdateForm = () => {
  BearJiaUtil.resetFormFieldsToEmpty(menuAddUpdateForm.data);
};

// 动态计算当前图标组件
const iconComponent = computed(() => {
  const iconName = menuAddUpdateForm.data.icon;
  return iconName && allIcons[iconName] ? allIcons[iconName] : null;
});

// 图标选择相关
const iconModalVisible = ref(false);
const iconSearch = ref('');
const currentIconTab = ref('custom'); // 默认显示自定义图标

// 自定义 SVG 图标列表
const customIcons = [
  'Alipay', 'bug', 'build', 'button', 'bxAnalyse', 'cascader', 'chart',
  'checkbox', 'clipboard', 'codeNew', 'color', 'company', 'companyFill',
  'component', 'compress', 'connections', 'dashboardNew', 'date', 'dateRange',
  'dict', 'dingtalk', 'documentation', 'download', 'DragColumn', 'dragImg',
  'dragtable', 'druid', 'edit', 'expend', 'eyeOpen', 'github', 'guide',
  'input', 'job', 'log', 'loginLog', 'message', 'monitor', 'number',
  'online', 'password', 'pdf', 'peoples', 'phone', 'post', 'QRcode',
  'question', 'radio', 'rate', 'redis', 'row', 'select', 'server',
  'Sina', 'swagger', 'switch', 'system', 'textarea', 'time', 'timeRange',
  'tool', 'tree', 'treeTable', 'upload', 'users', 'validCode', 'WeChat'
];

// 转换 icons.js 中的图标名称格式（kebab-case 转 PascalCase + Outlined）
const convertIconName = (kebabName) => {
  return kebabName
    .split('-')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join('') + 'Outlined';
};

// 使用 icons.js 中的分类数据，并转换图标名称格式
const iconCategories = iconCategoriesData.map(category => ({
  ...category,
  icons: category.icons.map(icon => convertIconName(icon))
}));

const iconList = Object.keys(allIcons);
const filteredAntIcons = ref(iconList);
const filteredCustomIcons = ref(customIcons);

const showIconModal = () => {
  iconModalVisible.value = true;
  filteredAntIcons.value = iconList; // 重置为完整列表
  filteredCustomIcons.value = customIcons;
};

const filterIcons = () => {
  const searchValue = iconSearch.value.toLowerCase();
  // 过滤 Ant Design 图标
  filteredAntIcons.value = iconList.filter((icon) =>
      icon.toLowerCase().includes(searchValue)
  );
  // 过滤自定义图标
  filteredCustomIcons.value = customIcons.filter((icon) =>
      icon.toLowerCase().includes(searchValue)
  );
};

const selectIcon = (icon) => {
  menuAddUpdateForm.data.icon = icon;
  iconModalVisible.value = false;
  iconSearch.value = ''; // 清空搜索
};

// 判断是否为自定义图标
const isCustomIcon = (iconName) => {
  return customIcons.includes(iconName);
};

// 打开新增窗口
const openAddModal = () => {
  menuAddUpdateForm.data.isFrame = '1';
  menuAddUpdateForm.data.visible = '0';
  menuAddUpdateForm.data.status = '0';
  pageData.operateType = 'add';
  pageData.title = '新增菜单';
  getMenuTreeselect().then((response) => {
    pageData.menusDict = handleTree(response.data, 'menuId');
    pageData.menusDict.push({ menuId: 0, menuName: '无' });
    pageData.visible = true;
  });
};

// 打开修改窗口
const openUpdateModal = (record) => {
  pageData.operateType = 'update';
  pageData.title = '修改菜单';
  getMenu(record.menuId).then((response) => {
    menuAddUpdateForm.data = response.data;
    getMenuTreeselect().then((response) => {
      pageData.menusDict = handleTree(response.data, 'menuId');
      pageData.menusDict.push({ menuId: 0, menuName: '无' });
      pageData.visible = true;
    });
  });
};

// 点击窗口确认
const clickModalOk = () => {
  menuAddUpdateFormRef.value
      .validate()
      .then(() => {
        if (pageData.operateType === 'add') {
          addMenu(menuAddUpdateForm.data).then(() => {
            pageData.visible = false;
            resetMenuAddUpdateForm();
            emit('refreshFatherPageTable');
            BearJiaUtil.messageSuccess('新增操作成功。');
          });
        } else if (pageData.operateType === 'update') {
          updateMenu(menuAddUpdateForm.data).then(() => {
            pageData.visible = false;
            resetMenuAddUpdateForm();
            emit('refreshFatherPageTable');
            BearJiaUtil.messageSuccess('修改操作成功。');
          });
        }
      })
      .catch((info) => {
        console.log('Validate Failed:', info);
      });
};

// 点击窗口取消
const handleModalCancel = () => {
  resetMenuAddUpdateForm();
  pageData.visible = false;
};

// 暴露方法
defineExpose({
  openAddModal,
  openUpdateModal,
});
</script>

<style lang="less" scoped>
.icon-list {
  max-height: 400px;
  overflow-y: auto;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 8px;
}

.icon-item {
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
  transition: all 0.3s;
  border-radius: 4px;

  &:hover {
    color: #1890ff;
    background: #f0f0f0;
  }

  &.icon-selected {
    color: #fff;
    background: #1890ff;
  }
}

.custom-icon-item {
  width: 40px!important;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;

  &.icon-selected {
    color: #fff;
  }

  &:hover:not(.icon-selected) {
    color: #1890ff;
  }
}

:deep(.ant-tabs-content) {
  height: auto;
}
</style>
