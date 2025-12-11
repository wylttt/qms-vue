<template>
  <div ref="tableContainerRef">
    <PageContainer>
      <!-- 1. 查询表单区 -->
      <template #search>
        <SearchForm
            v-if="props.searchFields && props.searchFields.length > 0"
            v-model="searchFormData"
            :fields="props.searchFields"
            @reset="handleReset"
            @search="handleSearch"
        />
      </template>

      <!-- 2. 操作按钮区 -->
      <template #actions>
        <div class="table-actions-wrapper" v-if="props.showActions" >
          <div class="actions-left">
            <slot
              name="actions"
              :selectedRowKeys="tableState.selectedRowKeys"
              :selectedRows="tableState.selectedRows || []"
              :delete="handleDelete"
              :refresh="enhancedQueryTableData"
              :export="handleExport"
              :loading="tableState.loading"
              :error="errorState"
            ></slot>
          </div>

          <div class="actions-right">
            <TableToolbar
              :columns="props.columns"
              :selected-columns="selectedColumnKeys"
              :loading="tableState.loading"
              :toolbar-config="props.toolbarConfig"
              :export-config="props.exportConfig"
              :import-config="props.importConfig"
              @refresh="enhancedQueryTableData"
              @columnSettingsChange="handleColumnSettingsChange"
              @fullscreen="handleFullscreen"
              @export="handleExport"
              @import="handleImport"
              @reset="handleTableReset"
            />
          </div>
        </div>
      </template>

      <!-- 3. 表格区 -->
      <a-table
          ref="virtualScrollState.containerRef"
          :bordered="tableConfigStore.bordered"
          :columns="displayColumns"
          :data-source="finalDataSource"
          :loading="tableState.loading"
          :pagination="pagination"
          :row-selection="finalRowSelectionConfig"
          :rowKey="props.rowKey"
          :scroll="computedScrollConfig"
          :size="tableConfigStore.size"
          :expandable="props.expandable"
          :default-expand-all-rows="props.isTreeTable"
          :indent-size="props.isTreeTable ? 20 : undefined"
          v-bind="$attrs"
          @change="handleTableChange"
      >
        <template #bodyCell="{ index, column, record }">
          <slot :column="column" :index="index" :record="record" name="bodyCell">
            <!-- 提供默认渲染，防止未定义插槽时单元格为空 -->
            <span>{{ record[column.dataIndex] }}</span>
          </slot>
        </template>

        <!-- 支持可展开行的插槽 -->
        <template v-if="props.expandable && props.expandable.expandedRowRender" #expandedRowRender="{ record }">
          <component :is="props.expandable.expandedRowRender(record)" />
        </template>

        <!-- 增强的空状态处理 -->
        <template #emptyText>
          <a-empty
            v-if="!errorState"
            description="暂无数据"
          />
          <a-result
            v-else
            status="error"
            title="加载失败"
            :sub-title="errorState"
          >
            <template #extra>
              <a-button type="primary" @click="handleRetry">点击重试</a-button>
            </template>
          </a-result>
        </template>
      </a-table>
    </PageContainer>
  </div>
</template>

<script setup>
import {computed, ref, onMounted, watch} from 'vue';
import {useTable} from '@/composables/useTable';
import {useTableConfigStore} from '@/stores/tableConfig';
import {useResizeObserver} from '@vueuse/core';
import {useVirtualScroll, getItemHeightBySize} from '@/composables/useVirtualScroll';

import PageContainer from '@/components/PageContainer/index.vue';
import SearchForm from '@/components/SearchForm/index.vue';
import TableToolbar from './TableToolbar.vue';

// 定义组件的 Props
const props = defineProps({
  // API 配置 - 应包含 list, delete 等方法
  api: {type: Object, required: true},
  // 表格列配置
  columns: {type: Array, required: true},
  // 行键
  rowKey: {type: String, required: true},
  // 搜索字段配置
  searchFields: {type: Array, default: () => []},
  // 初始搜索参数
  initialSearchParams: {type: Object, default: () => ({})},
  // 导出配置 { url: '...', fileName: '...' }
  exportConfig: {type: Object, default: null},
  // 是否显示行选择
  showSelection: {type: Boolean, default: true},
  // 是否显示操作区域
  showActions: {type: Boolean, default: true},
  // 树形表格支持
  isTreeTable: {type: Boolean, default: false},
  // 可展开行支持
  expandable: {type: Object, default: null},
  // 自定义行选择配置
  rowSelection: {type: Object, default: null},
  // 排序配置
  sortable: {type: Boolean, default: true},
  // 自动横向滚动配置
  autoHorizontalScroll: {type: Boolean, default: false},
  // 是否启用虚拟滚动
  virtualScroll: {type: Boolean, default: false},
  // 虚拟滚动配置
  virtualScrollConfig: {
    type: Object,
    default: () => ({
      threshold: 100,  // 数据量超过100条启用
      buffer: 5,       // 缓冲区行数
    })
  },
  // 是否显示列设置
  showColumnSettings: {type: Boolean, default: true},
  // 工具栏配置
  toolbarConfig: {
    type: Object,
    default: () => ({
      refresh: true,        // 刷新按钮
      density: true,        // 密度设置
      fullscreen: true,     // 全屏按钮
      columnSettings: true, // 列设置
      settings: true        // 更多设置
    })
  },
  // 导入配置
  importConfig: {
    type: Object,
    default: () => ({
      enabled: false,
      url: '',
      accept: '.xlsx,.xls,.csv'
    })
  },
});

const tableConfigStore = useTableConfigStore();

// 容器引用和宽度监听
const tableContainerRef = ref(null);
const containerWidth = ref(0);

// 错误状态
const errorState = ref(null);

// 列设置相关状态
const columnSettingsVisible = ref(false);
const selectedColumnKeys = ref([]);

// 监听容器宽度变化
onMounted(() => {
  if (tableContainerRef.value) {
    useResizeObserver(tableContainerRef.value, (entries) => {
      const entry = entries[0];
      containerWidth.value = entry.contentRect.width;
    });
  }

  // 初始化选中的列
  selectedColumnKeys.value = props.columns.map(col => col.dataIndex);
});

// 使用 useTable Hook
const {
  searchFormData,
  tableState,
  pagination,
  rowSelection,
  queryTableData,
  handleSearch,
  handleReset,
  handleTableChange,
  handleDelete,
  handleExport,
} = useTable({
  api: {
    list: props.api.list,
    delete: props.api.delete,
    exportUrl: props.exportConfig?.url,
    processListData: props.api.processListData,
  },
  columns: props.columns,
  initialSearchParams: props.initialSearchParams,
  rowKey: props.rowKey,
  exportFileName: props.exportConfig?.fileName,
  isTreeTable: props.isTreeTable,
  sortable: props.sortable,
});

// 虚拟滚动集成
const virtualScrollState = useVirtualScroll({
  dataSource: computed(() => tableState.dataSource),
  itemHeight: computed(() => getItemHeightBySize(tableConfigStore.size)),
  buffer: props.virtualScrollConfig.buffer,
  containerHeight: computed(() => tableConfigStore.tableHeight || 600),
  enabled: props.virtualScroll,
  threshold: props.virtualScrollConfig.threshold,
});

// 使用虚拟滚动时的数据源
const finalDataSource = computed(() => {
  if (props.virtualScroll && virtualScrollState.isVirtualScrollEnabled.value) {
    return virtualScrollState.visibleData.value;
  }
  return tableState.dataSource;
});

// 增强的数据查询方法，包含错误处理
const enhancedQueryTableData = async (...args) => {
  try {
    errorState.value = null;
    await queryTableData(...args);
  } catch (error) {
    console.error('表格数据加载失败:', error);
    errorState.value = error.message || '数据加载失败，请重试';
  }
};

// 重试方法
const handleRetry = () => {
  enhancedQueryTableData();
};

// 计算显示的列（支持列设置）
const displayColumns = computed(() => {
  if (!props.showColumnSettings) {
    return tableState.columns;
  }

  return tableState.columns.filter(column =>
    selectedColumnKeys.value.includes(column.dataIndex)
  );
});

// 计算滚动配置
const computedScrollConfig = computed(() => {
  const scrollConfig = {};

  // 垂直滚动配置
  if (tableConfigStore.fixHeader) {
    scrollConfig.y = tableConfigStore.tableHeight;
  }

  // 虚拟滚动配置
  if (props.virtualScroll) {
    if (scrollConfig.y) {
      scrollConfig.scrollToFirstRowOnChange = true;
    }
    // 虚拟滚动需要固定的容器高度
    if (virtualScrollState.isVirtualScrollEnabled.value) {
      scrollConfig.y = scrollConfig.y || 600;
    }
  }

  // 水平滚动配置 - 智能计算是否需要横向滚动
  if (props.autoHorizontalScroll) {
    const totalWidth = displayColumns.value.reduce((sum, col) => {
      return sum + (col.width || 120); // 默认列宽120px
    }, 0);

    // 使用实际容器宽度进行比较，如果容器宽度未获取到则使用默认值
    const compareWidth = containerWidth.value || 1200;
    if (totalWidth > compareWidth - 50) { // 留50px余量
      scrollConfig.x = totalWidth;
    }
  }

  return scrollConfig;
});

// 计算最终的 rowSelection 配置
const finalRowSelectionConfig = computed(() => {
  // 如果传入了自定义的 rowSelection 配置，优先使用
  if (props.rowSelection) {
    return {
      ...rowSelection.value,
      ...props.rowSelection,
    };
  }

  // 否则使用默认配置
  if (!props.showSelection || !tableConfigStore.rowSelection) return undefined;
  return rowSelection.value;
});

const showColumnSettings = props.showColumnSettings;

// 从 localStorage 恢复列设置
const restoreColumnSettings = () => {
  const saved = localStorage.getItem('table-column-settings');
  if (saved) {
    try {
      const savedKeys = JSON.parse(saved);
      // 确保保存的列键在当前列中存在
      const validKeys = savedKeys.filter(key =>
        props.columns.some(col => col.dataIndex === key)
      );
      if (validKeys.length > 0) {
        selectedColumnKeys.value = validKeys;
      }
    } catch (error) {
      console.warn('恢复列设置失败:', error);
    }
  }
};

// 工具栏事件处理
const handleColumnSettingsChange = (selectedKeys) => {
  selectedColumnKeys.value = selectedKeys;
  localStorage.setItem('table-column-settings', JSON.stringify(selectedKeys));
};

const handleFullscreen = (isFullscreen) => {
  // 可以在这里处理全屏逻辑
  console.log('全屏状态:', isFullscreen);
};

const handleImport = () => {
  // 处理导入逻辑
  console.log('导入数据');
};

const handleTableReset = () => {
  // 重置表格状态
  selectedColumnKeys.value = props.columns.map(col => col.dataIndex);
  localStorage.removeItem('table-column-settings');
  enhancedQueryTableData();
};

// 监听 columns 变化，重新初始化列设置
watch(() => props.columns, () => {
  selectedColumnKeys.value = props.columns.map(col => col.dataIndex);
  restoreColumnSettings();
}, { immediate: true });

// 使用增强的查询方法替换原始方法
watch(() => [props.api, props.initialSearchParams], () => {
  enhancedQueryTableData();
}, { immediate: true });

// 暴露方法给父组件
defineExpose({
  refresh: enhancedQueryTableData,
  delete: handleDelete,
  export: handleExport,
  searchFormData: searchFormData,
  tableState: tableState,
  showColumnSettings: showColumnSettings,
  retry: handleRetry,
  handleFullscreen,
  handleImport,
  handleTableReset,
  // 虚拟滚动相关方法
  virtualScroll: {
    scrollToIndex: virtualScrollState.scrollToIndex,
    scrollToTop: virtualScrollState.scrollToTop,
    scrollToBottom: virtualScrollState.scrollToBottom,
    stats: virtualScrollState.performanceStats,
  },
});
</script>

<style scoped>
.table-actions-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.actions-right {
  display: flex;
  align-items: center;
}

.column-settings {
  max-height: 400px;
  overflow-y: auto;
}

.column-list {
  width: 100%;
}

.column-item {
  display: block;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.column-item:last-child {
  border-bottom: none;
}

/* 表格容器样式 */
.ant-table-wrapper {
  position: relative;
}

/* 错误状态样式 */
.ant-result {
  padding: 24px 0;
}
</style>
