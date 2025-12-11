<template>
  <!-- 消息通知按钮和弹窗 -->
  <div class="message-center-container">
    <!-- 消息按钮（如果props.showButton为true） -->
    <div v-if="showButton" class="message-button" @click="togglePanel">
      <a-badge :count="unreadCount" :offset="[-20, 4]">
        <a-button class="header-icon-btn" type="link">
          <BellOutlined />
        </a-button>
      </a-badge>
    </div>

    <!-- 消息通知提示 -->
    <div class="notification-tooltip" v-if="showTooltip">
      消息通知
    </div>

    <!-- 消息中心弹窗 -->
    <div 
      class="message-center-popup" 
      v-if="visible"
      @click.stop
    >
      <!-- 消息中心头部 -->
      <div class="message-header">
        <div class="header-title">消息中心</div>
        <div class="header-actions">
          <span class="mark-all-read" @click="markAllAsRead">全部已读</span>
          <SettingOutlined class="settings-icon" />
        </div>
      </div>

      <!-- 消息类型切换 -->
      <div class="message-tabs">
        <div 
          v-for="tab in tabs" 
          :key="tab.key"
          class="tab-item"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="message-list">
        <div 
          v-for="item in filteredMessages"
          :key="item.id"
          class="message-item"
          @click="handleMessageClick(item)"
        >
          <div class="message-icon" :style="{ backgroundColor: item.bgColor }">
            <component :is="item.icon" :style="{ color: item.iconColor }" />
          </div>
          <div class="message-content-body">
            <div class="message-header-row">
              <div class="message-title">{{ item.title }}</div>
              <div class="message-time">
                <span>{{ item.time }}</span>
                <span v-if="!item.read" class="unread-dot"></span>
              </div>
            </div>
            <div class="message-desc">{{ item.description }}</div>
          </div>
        </div>
      </div>

      <!-- 底部链接 -->
      <div class="message-footer">
        <a href="#" class="view-all-link">查看全部消息</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import {
  BellOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  WarningOutlined,
  InfoCircleOutlined,
  SettingOutlined,
  UserAddOutlined,
  FileTextOutlined,
  SearchOutlined
} from '@ant-design/icons-vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  showTooltip: {
    type: Boolean,
    default: false
  },
  showButton: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:visible']);

// 标签数据
const tabs = ref([
  { key: 'all', label: '全部' },
  { key: 'unread', label: '未读' },
  { key: 'system', label: '系统通知' },
  { key: 'task', label: '任务提醒' }
]);

// 当前选中的标签
const activeTab = ref('all');

// 所有消息数据
const allMessages = ref([
  {
    id: 1,
    title: '系统更新通知',
    description: '系统将于今晚23:00进行维护，预计耗时30分钟。',
    time: '10分钟前',
    read: false,
    icon: BellOutlined,
    bgColor: 'rgba(22, 93, 255, 0.1)',
    iconColor: '#165DFF',
    type: 'system'
  },
  {
    id: 2,
    title: '任务即将截止',
    description: '您有一个任务"系统文档整理"将在2小时后截止。',
    time: '1小时前',
    read: false,
    icon: FileTextOutlined,
    bgColor: 'rgba(54, 211, 153, 0.1)',
    iconColor: '#36D399',
    type: 'task'
  },
  {
    id: 3,
    title: '审批通过通知',
    description: '您提交的"2025年Q4预算申请"已通过审批。',
    time: '3小时前',
    read: false,
    icon: CheckCircleOutlined,
    bgColor: 'rgba(251, 189, 35, 0.1)',
    iconColor: '#FBBD23',
    type: 'system'
  },
  {
    id: 4,
    title: '新用户注册',
    description: '用户"小李"已注册系统，角色为普通员工。',
    time: '昨天',
    read: true,
    icon: UserAddOutlined,
    bgColor: 'rgba(22, 93, 255, 0.1)',
    iconColor: '#165DFF',
    type: 'system'
  },
  {
    id: 5,
    title: '数据异常提醒',
    description: '昨日系统访问量较前日异常增长300%，请留意。',
    time: '3天前',
    read: true,
    icon: WarningOutlined,
    bgColor: 'rgba(248, 114, 114, 0.1)',
    iconColor: '#F87272',
    type: 'system'
  }
]);

// 计算未读消息数量
const unreadCount = computed(() => {
  return allMessages.value.filter(item => !item.read).length;
});

// 根据当前标签过滤消息
const filteredMessages = computed(() => {
  switch (activeTab.value) {
    case 'all':
      return allMessages.value;
    case 'unread':
      return allMessages.value.filter(item => !item.read);
    case 'system':
      return allMessages.value.filter(item => item.type === 'system');
    case 'task':
      return allMessages.value.filter(item => item.type === 'task');
    default:
      return allMessages.value;
  }
});

// 处理消息点击
const handleMessageClick = (item) => {
  item.read = true;
  console.log('点击消息:', item);
};

// 全部标记为已读
const markAllAsRead = () => {
  allMessages.value.forEach(item => item.read = true);
};

// 切换面板显示状态
const togglePanel = () => {
  emit('update:visible', !props.visible);
};

// 关闭面板
const closePanel = () => {
  emit('update:visible', false);
};

// 点击外部关闭面板
const handleClickOutside = (event) => {
  if (props.visible && !event.target.closest('.message-center-container')) {
    closePanel();
  }
};

// 监听点击事件
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
/* 消息中心容器 */
.message-center-container {
  position: relative;

  .message-button {
    position: relative;
    display: inline-block;
    
    .header-icon-btn {
      color: rgba(0, 0, 0, 0.65);
      transition: color 0.3s;
      
      &:hover {
        color: #1890ff;
      }
    }
  }
}

/* 消息通知提示 */
.notification-tooltip {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 1001;
  margin-top: 8px;
}

.notification-tooltip::before {
  content: '';
  position: absolute;
  top: -4px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-bottom: 4px solid #333;
}

.message-center-popup {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  width: 300px;
  max-height: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #e5e7eb;

  /* 添加小箭头指向消息按钮 */
  &::before {
    content: '';
    position: absolute;
    top: -8px;
    right: 20px;
    width: 0;
    height: 0;
    border-left: 8px solid transparent;
    border-right: 8px solid transparent;
    border-bottom: 8px solid #e5e7eb;
  }

  &::after {
    content: '';
    position: absolute;
    top: -7px;
    right: 20px;
    width: 0;
    height: 0;
    border-left: 8px solid transparent;
    border-right: 8px solid transparent;
    border-bottom: 8px solid #fff;
  }

  .message-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0px 16px;
    border-bottom: 1px solid #e5e7eb;
    background: #fff;

    .header-title {
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
    }

    .header-actions {
      display: flex;
      align-items: center;
      gap: 8px;

      .mark-all-read {
        color: #165DFF;
        cursor: pointer;
        font-size: 14px;
        transition: color 0.3s;
        font-weight: 400;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }

      .settings-icon {
        color: #94a3b8;
        cursor: pointer;
        font-size: 14px;
        transition: color 0.3s;

        &:hover {
          color: #165DFF;
        }
      }
    }
  }

  .message-tabs {
    display: flex;
    border-bottom: 1px solid #e5e7eb;
    background: #fff;

    .tab-item {
      flex: 1;
      cursor: pointer;
      padding: -8px 8px;
      font-size: 13px;
      color: #94a3b8;
      border-bottom: 2px solid transparent;
      transition: all 0.3s;
      text-align: center;
      font-weight: 500;

      &:hover {
        color: #165DFF;
      }

      &.active {
        color: #165DFF;
        border-bottom-color: #165DFF;
      }
    }
  }

  .message-list {
    flex: 1;
    overflow-y: auto;
    max-height: 280px;
    background: #fff;

    .message-item {
      display: flex;
      align-items: flex-start;
      padding: 8px 16px;
      cursor: pointer;
      transition: all 0.2s;
      background: #fff;
      border-bottom: 1px solid #f1f5f9;

      &:hover {
        background: #f1f5f9;
      }

      &:last-child {
        border-bottom: none;
      }

      .message-icon {
        margin-right: 12px;
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        flex-shrink: 0;
        border-radius: 50%;
      }

      .message-content-body {
        flex: 1;
        min-width: 0;

        .message-header-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 4px;
          gap: 8px;
          min-height: 16px;
        }

        .message-title {
          font-weight: 500;
          color: #1e293b;
          font-size: 13px;
          line-height: 1.3;
          flex: 1;
          min-width: 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .message-desc {
          color: #94a3b8;
          font-size: 12px;
          line-height: 1.3;
          word-wrap: break-word;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .message-time {
          color: #94a3b8;
          font-size: 11px;
          display: flex;
          align-items: center;
          gap: 4px;
          flex-shrink: 0;
          white-space: nowrap;
          height: 16px;
          line-height: 1;

          .unread-dot {
            width: 6px;
            height: 6px;
            background: #165DFF;
            border-radius: 50%;
            display: inline-block;
          }
        }
      }
    }
  }

  .message-footer {
    padding: 2px 16px;
    border-top: 1px solid #e5e7eb;
    text-align: center;
    background: #fff;

    .view-all-link {
      color: #165DFF;
      text-decoration: none;
      font-size: 13px;
      font-weight: 400;
      transition: color 0.3s;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>