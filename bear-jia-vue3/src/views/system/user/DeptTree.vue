<template>
  <div>
    <a-input-search
      style="margin-bottom: 8px"
      placeholder="Search"
      enter-button
      width="100%"
      @change="filterNode"
    />
    <a-tree
      :expanded-keys="expandedKeys"
      :auto-expand-parent="autoExpandParent"
      :tree-data="deptOptions"
      :field-names="replaceFields"
      @select="handleNodeClick"
      @expand="onExpand"
    />
  </div>
</template>

<script setup>
import {ref} from 'vue';

// 定义 Props
const props = defineProps({
  deptOptions: {
    type: Array,
    required: true,
  },
});

// 定义 Emit
const emit = defineEmits(['select']);

// 获取父节点 key 的辅助函数（无需修改）
const getParentKey = (id, tree) => {
  let parentKey;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some((item) => item.id === id)) {
        parentKey = node.id;
      } else if (getParentKey(id, node.children)) {
        parentKey = getParentKey(id, node.children);
      }
    }
  }
  return parentKey;
};

// 响应式数据
const replaceFields = ref({
  children: 'children',
  title: 'label',
  key: 'id',
  value: 'id',
});
const deptNodes = ref([]);
const expandedKeys = ref([]);
const searchValue = ref('');
const autoExpandParent = ref(true);

// 获取所有部门节点的递归函数
const getAllDeptNode = (nodes) => {
  if (!nodes || nodes.length === 0) {
    return;
  }
  nodes.forEach((node) => {
    deptNodes.value.push({ id: node.id, label: node.label });
    getAllDeptNode(node.children);
  });
};

// 过滤节点并展开相关节点
const filterNode = (e) => {
  deptNodes.value = []; // 清空之前的节点
  getAllDeptNode(props.deptOptions);
  const value = e.target.value;
  const gData = props.deptOptions;
  expandedKeys.value = deptNodes.value
      .map((item) => {
        if (item.label.indexOf(value) > -1) {
          return getParentKey(item.id, gData);
        }
        return null;
      })
      .filter((item, i, self) => item && self.indexOf(item) === i);
  searchValue.value = value;
  autoExpandParent.value = true;
};

// 处理节点点击事件
const handleNodeClick = (keys) => {
  emit('select', keys);
};

// 处理展开事件
const onExpand = (keys) => {
  expandedKeys.value = keys;
  autoExpandParent.value = false;
};
</script>

<style lang="less" scoped>
:deep(.ant-input-search) {
  display: flex;
  
  .ant-input {
    background: var(--input-bg);
    color: var(--text-primary);
    border-color: var(--border-color-base);
    border-right: none;
    
    &::placeholder {
      color: var(--text-secondary);
    }
    
    &:focus {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
  }
  
  .ant-input-search-button {
    background: var(--primary-color);
    border-color: var(--primary-color);
    border-left: none;
    
    &:hover {
      background: var(--primary-color-hover);
      border-color: var(--primary-color-hover);
    }
  }
  
  .ant-input-group-addon {
    padding: 0;
    border: none;
  }
}

:deep(.ant-tree) {
  background: var(--component-background);
  
  .ant-tree-node-content-wrapper {
    color: var(--text-primary);
    
    &:hover {
      background-color: rgba(24, 144, 255, 0.1);
    }
    
    &.ant-tree-node-selected {
      background-color: var(--primary-color);
      color: #fff;
    }
  }
  
  .ant-tree-switcher {
    color: var(--text-secondary);
  }
  
  .ant-tree-title {
    color: inherit;
  }
}
</style>
