// ✅ ActionButtons.vue - 操作列中的操作按钮组件（支持查看、编辑、删除、插槽）
<template>
  <span class="table-action-bar">
    <span class="action-buttons">
    <!-- 查看按钮 -->
    <slot name="view">
      <a
          v-if="viewConfig.show"
          v-hasPermi="viewConfig.permissions"
          class="action-btn view-btn"
          @click="viewConfig.handler"
      >
        <BearJiaIcon :icon="viewConfig.icon"/> {{ viewConfig.text }}
      </a>
    </slot>

    <!-- 编辑按钮 -->
    <slot name="edit">
      <a-divider v-if="editConfig.show && viewConfig.show" type="vertical"/>
      <a
          v-if="editConfig.show"
          v-hasPermi="editConfig.permissions"
          class="action-btn edit-btn"
          @click="editConfig.handler"
      >
        <BearJiaIcon :icon="editConfig.icon"/> {{ editConfig.text }}
      </a>
    </slot>

    <!-- 删除按钮 -->
    <slot name="delete">
      <a-divider v-if="deleteConfig.show && (editConfig.show || viewConfig.show)" type="vertical"/>
      <a
          v-if="deleteConfig.show"
          v-hasPermi="deleteConfig.permissions"
          class="action-btn delete-btn"
          @click="deleteConfig.handler"
      >
        <BearJiaIcon :icon="deleteConfig.icon"/> {{ deleteConfig.text }}
      </a>
    </slot>

    <!-- 自定义操作按钮插槽 -->
    <slot name="actions" :record="record">
      <!-- 默认插槽内容，用于自定义按钮 -->
    </slot>

    <!-- 默认插槽（向后兼容） -->
    <slot :record="record"></slot>
    </span>
  </span>
</template>

<script setup>
import {computed} from 'vue';
import {BearJiaIcon} from '@/utils/BearJiaIcon.js';

const props = defineProps({
  record: Object,
  // 新的配置方式: 查看按钮配置
  view: {
    type: [Object, Boolean],
    default: true
  },
  // 新的配置方式: 编辑按钮配置
  edit: {
    type: [Object, Boolean],
    default: true
  },
  // 新的配置方式: 删除按钮配置
  delete: {
    type: [Object, Boolean],
    default: false
  },
  // 向后兼容的旧参数
  hasView: {type: Boolean, default: undefined},
  hasEdit: {type: Boolean, default: undefined},
  hasDelete: {type: Boolean, default: undefined},
  icons: {type: Object, default: undefined},
  texts: {type: Object, default: undefined},
  permissions: {type: Object, default: undefined}
});

const emit = defineEmits(['view', 'edit', 'delete']);

// 默认配置
const defaultConfig = {
  view: {
    show: true,
    text: '查看',
    icon: 'eye-outlined',
    permissions: null
  },
  edit: {
    show: true,
    text: '修改',
    icon: 'edit-outlined',
    permissions: null
  },
  delete: {
    show: false,
    text: '删除',
    icon: 'delete-outlined',
    permissions: null
  }
};

// 处理权限
const getPermissions = (buttonType) => {
  // 优先使用 permissions 对象
  if (props.permissions && props.permissions[buttonType]) {
    return props.permissions[buttonType];
  }

  return null;
};

// 处理按钮配置
const processButtonConfig = (buttonType, propValue, legacyShow, legacyIcon, legacyText) => {
  const defaults = defaultConfig[buttonType];
  const legacyPermission = getPermissions(buttonType);

  // 新的对象配置方式
  if (typeof propValue === 'object' && propValue !== null) {
    return {
      show: propValue.show !== undefined ? propValue.show : true,
      text: propValue.text || defaults.text,
      icon: propValue.icon || defaults.icon,
      permissions: propValue.permissions || null,
      handler: (e) => {
        if (propValue.onClick) {
          propValue.onClick(props.record);
        } else {
          emit(buttonType, props.record);
        }
      }
    };
  }

  // 布尔值配置方式
  if (typeof propValue === 'boolean') {
    return {
      ...defaults,
      show: propValue,
      icon: legacyIcon || defaults.icon,
      text: legacyText || defaults.text,
      permissions: legacyPermission,
      handler: (e) => emit(buttonType, props.record)
    };
  }

  // 向后兼容旧参数
  if (legacyShow !== undefined) {
    return {
      ...defaults,
      show: legacyShow,
      icon: legacyIcon || defaults.icon,
      text: legacyText || defaults.text,
      permissions: legacyPermission,
      handler: (e) => emit(buttonType, props.record)
    };
  }

  // 默认配置
  return {
    ...defaults,
    permissions: legacyPermission,
    handler: (e) => emit(buttonType, props.record)
  };
};

// 计算各按钮配置
const viewConfig = computed(() => processButtonConfig(
  'view',
  props.view,
  props.hasView,
  props.icons?.view,
  props.texts?.view
));

const editConfig = computed(() => processButtonConfig(
  'edit',
  props.edit,
  props.hasEdit,
  props.icons?.edit,
  props.texts?.edit
));

const deleteConfig = computed(() => processButtonConfig(
  'delete',
  props.delete,
  props.hasDelete,
  props.icons?.delete,
  props.texts?.delete
));
</script>

<style lang="less">
/* TableActionBar 组件样式 */
.table-action-bar {
  .action-buttons {
    display: inline-flex;
    align-items: center;
    gap: 4px;
  }
}

/* 全局 action-btn 样式，供插槽使用 */
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  font-size: 13px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;

  &.view-btn {
    background-color: #e6f4ff;
    color: #1677ff;
  }

  &.edit-btn {
    background-color: #fff7e6;
    color: #fa8c16;
  }

  &.delete-btn {
    background-color: #fff1f0;
    color: #ff4d4f;
  }

  &.custom-btn {
    background-color: #f6ffed;
    color: #52c41a;
  }

  &.warning-btn {
    background-color: #fffbe6;
    color: #faad14;
  }

  &.info-btn {
    background-color: #e6fffb;
    color: #13c2c2;
  }

  &.primary-btn {
    background-color: #e6f4ff;
    color: #1677ff;
  }
}

// 暗黑主题支持
:global(.dark-theme) {
  .action-btn {
    &.view-btn {
      background-color: rgba(22, 119, 255, 0.15);
      color: var(--primary-color);
    }

    &.edit-btn {
      background-color: rgba(250, 140, 22, 0.15);
      color: #fa8c16;
    }

    &.delete-btn {
      background-color: rgba(255, 77, 79, 0.15);
      color: #ff4d4f;
    }

    &.custom-btn {
      background-color: rgba(82, 196, 26, 0.15);
      color: #52c41a;
    }

    &.warning-btn {
      background-color: rgba(250, 173, 20, 0.15);
      color: #faad14;
    }

    &.info-btn {
      background-color: rgba(19, 194, 194, 0.15);
      color: #13c2c2;
    }

    &.primary-btn {
      background-color: rgba(22, 119, 255, 0.15);
      color: var(--primary-color);
    }
  }
}
</style>
