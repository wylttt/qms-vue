<template>
  <PageContainer>
    <div class="workbench-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <a-card class="welcome-card">
        <div class="welcome-content">
          <div class="welcome-info">
            <h2 class="welcome-title">
              <BearJiaIcon icon="smile-outlined" class="welcome-icon"/>
              欢迎回来，{{ userInfo.nickName || userInfo.userName }}！
            </h2>
            <p class="welcome-desc">今天是个美好的一天，开始您的工作吧~</p>
            <div class="welcome-meta">
              <span><BearJiaIcon icon="clock-circle-outlined"/> 上次登录：{{ lastLoginTime }}</span>
              <span><BearJiaIcon icon="environment-outlined"/> 登录地点：{{ loginLocation }}</span>
            </div>
          </div>
          <div class="welcome-actions">
            <a-button type="primary" @click="goToUserManagement">
              <BearJiaIcon icon="user-outlined"/>
              用户管理
            </a-button>
            <a-button @click="goToSystemSettings">
              <BearJiaIcon icon="setting-outlined"/>
              系统设置
            </a-button>
          </div>
        </div>
      </a-card>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="6">
          <a-card class="stats-card" @click="goToUserManagement">
            <a-statistic
                title="用户总数"
                :value="statistics.userCount"
                :loading="loading.stats"
                prefix-icon="user-outlined"
                class="stats-item"
            >
              <template #prefix>
                <BearJiaIcon icon="user-outlined" class="stats-icon user-icon"/>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card class="stats-card" @click="goToRoleManagement">
            <a-statistic
                title="角色数量"
                :value="statistics.roleCount"
                :loading="loading.stats"
                class="stats-item"
            >
              <template #prefix>
                <BearJiaIcon icon="team-outlined" class="stats-icon role-icon"/>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card class="stats-card" @click="goToMenuManagement">
            <a-statistic
                title="菜单数量"
                :value="statistics.menuCount"
                :loading="loading.stats"
                class="stats-item"
            >
              <template #prefix>
                <BearJiaIcon icon="menu-outlined" class="stats-icon menu-icon"/>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card class="stats-card" @click="goToOnlineUsers">
            <a-statistic
                title="在线用户"
                :value="statistics.onlineCount"
                :loading="loading.stats"
                class="stats-item"
            >
              <template #prefix>
                <BearJiaIcon icon="wifi-outlined" class="stats-icon online-icon"/>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <a-row :gutter="[16, 16]">
        <!-- 左侧内容 -->
        <a-col :xs="24" :lg="16">
          <!-- 我的网站展示 -->
          <a-card title="我的网站" class="website-card">
            <template #extra>
              <a-button type="link" size="small" @click="visitAllWebsites">
                <BearJiaIcon icon="global-outlined"/>
                访问全部
              </a-button>
            </template>
            <div class="website-grid">
              <div
                v-for="(site, index) in mySites"
                :key="index"
                class="website-item"
                @click="visitWebsite(site.url)"
              >
                <div class="website-preview">
                  <div class="website-icon" :style="{ background: site.color }">
                    <BearJiaIcon :icon="site.icon"/>
                  </div>
                  <div class="website-info">
                    <div class="website-name">{{ site.name }}</div>
                    <div class="website-desc">{{ site.description }}</div>
                    <div class="website-tech">
                      <a-tag
                        v-for="tech in site.technologies"
                        :key="tech"
                        size="small"
                        :color="getTechColor(tech)"
                      >
                        {{ tech }}
                      </a-tag>
                    </div>
                  </div>
                </div>
                <div class="website-status">
                  <a-badge
                    :status="site.status === 'online' ? 'success' : 'default'"
                    :text="site.status === 'online' ? '在线' : '维护中'"
                  />
                </div>
              </div>
            </div>
          </a-card>

          <!-- 更新日志 -->
          <a-card title="更新日志" class="update-log-card">
            <template #extra>
              <div class="update-log-actions">
                <a-button type="link" size="small" @click="toggleAllUpdates">
                  <BearJiaIcon :icon="allUpdatesExpanded ? 'up-outlined' : 'down-outlined'"/>
                  {{ allUpdatesExpanded ? '全部收起' : '全部展开' }}
                </a-button>
                <a-button type="link" size="small" @click="viewAllUpdates">
                  <BearJiaIcon icon="history-outlined"/>
                  查看全部
                </a-button>
              </div>
            </template>
            <a-timeline class="update-timeline">
              <a-timeline-item
                  v-for="(update, index) in updateLogs"
                  :key="index"
                  :color="update.type === 'major' ? '#f5222d' : update.type === 'minor' ? '#1890ff' : '#52c41a'"
              >
                <template #dot>
                  <BearJiaIcon
                    :icon="getUpdateIcon(update.type)"
                    :class="['update-icon', `update-${update.type}`]"
                  />
                </template>
                <div class="update-item">
                  <div class="update-header" @click="toggleUpdate(index)">
                    <div class="update-header-left">
                      <span class="update-version">{{ update.version }}</span>
                      <span class="update-type" :class="`type-${update.type}`">
                        {{ getUpdateTypeName(update.type) }}
                      </span>
                      <span class="update-date">{{ update.date }}</span>
                    </div>
                    <div class="update-toggle">
                      <BearJiaIcon
                        :icon="expandedUpdates[index] ? 'up-outlined' : 'down-outlined'"
                        class="toggle-icon"
                      />
                    </div>
                  </div>
                  <a-collapse-transition>
                    <div v-show="expandedUpdates[index]" class="update-content">
                      <div class="update-title">{{ update.title }}</div>
                      <ul class="update-features">
                        <li v-for="(feature, idx) in update.features" :key="idx">
                          {{ feature }}
                        </li>
                      </ul>
                    </div>
                  </a-collapse-transition>
                </div>
              </a-timeline-item>
            </a-timeline>
          </a-card>


        </a-col>

        <!-- 右侧内容 -->
        <a-col :xs="24" :lg="8">
          <!-- 最新通知 -->
          <a-card title="最新通知" class="notice-card">
            <template #extra>
              <a-button type="link" size="small" @click="goToNoticeManagement">查看全部</a-button>
            </template>
            <a-list
                :data-source="noticeList"
                :loading="loading.notice"
                size="small"
            >
              <template #renderItem="{ item }">
                <a-list-item class="notice-item" @click="viewNotice(item)">
                  <a-list-item-meta>
                    <template #title>
                      <div class="notice-title">
                        <span class="notice-type" :class="getNoticeTypeClass(item.noticeType)">
                          {{ getNoticeTypeName(item.noticeType) }}
                        </span>
                        <span class="notice-text">{{ item.noticeTitle }}</span>
                      </div>
                    </template>
                    <template #description>
                      <div class="notice-time">
                        <BearJiaIcon icon="clock-circle-outlined"/>
                        {{ formatTime(item.createTime) }}
                      </div>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
              <template #loadMore>
                <div v-if="noticeList.length === 0 && !loading.notice" class="empty-notice">
                  <a-empty description="暂无通知" :image="false"/>
                </div>
              </template>
            </a-list>
          </a-card>

          <!-- 系统信息 -->
          <a-card title="系统信息" class="system-info-card">
            <div class="system-info">
              <div class="info-item">
                <span class="info-label">系统版本：</span>
                <span class="info-value">{{ systemInfo.name }} v{{ systemInfo.version }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">前端框架：</span>
                <span class="info-value">Vue 3.x + Ant Design Vue</span>
              </div>
              <div class="info-item">
                <span class="info-label">后端框架：</span>
                <span class="info-value">Spring Boot + MyBatis</span>
              </div>
              <div class="info-item">
                <span class="info-label">数据库：</span>
                <span class="info-value">MySQL 8.0</span>
              </div>
              <div class="info-item">
                <span class="info-label">缓存：</span>
                <span class="info-value">Redis</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务器时间：</span>
                <span class="info-value">{{ currentTime }}</span>
              </div>
            </div>
          </a-card>

          <!-- 快速链接 -->
          <a-card title="快速链接" class="quick-links-card">
            <div class="quick-links">
              <a-button type="link" @click="goToServerMonitor">
                <BearJiaIcon icon="monitor-outlined"/>
                服务监控
              </a-button>
              <a-button type="link" @click="goToCacheMonitor">
                <BearJiaIcon icon="database-outlined"/>
                缓存监控
              </a-button>
              <a-button type="link" @click="goToJobManagement">
                <BearJiaIcon icon="schedule-outlined"/>
                定时任务
              </a-button>
              <a-button type="link" @click="goToSystemConfig">
                <BearJiaIcon icon="setting-outlined"/>
                系统配置
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
    </div>
  </PageContainer>
</template>

<script setup>
import {computed, getCurrentInstance, onMounted, onUnmounted, reactive, ref} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '@/stores/user';
import {BearJiaIcon} from '@/utils/BearJiaIcon.js';
import PageContainer from '@/components/PageContainer/index.vue';
import dayjs from 'dayjs';
import { SYSTEM_INFO } from '@/config/system.config';

// API imports
import {listUser} from '@/api/system/user';
import {listRole} from '@/api/system/role';
import {listMenu} from '@/api/system/menu';
import {listNotice} from '@/api/system/notice';
import {list as listOnlineUsers} from '@/api/monitor/online';

const {proxy} = getCurrentInstance();
const router = useRouter();
const userStore = useUserStore();

// 系统信息
const systemInfo = ref(SYSTEM_INFO);

// 响应式数据
const loading = reactive({
  stats: false,
  notice: false,
  server: false,
});

const statistics = reactive({
  userCount: 0,
  roleCount: 0,
  menuCount: 0,
  onlineCount: 0,
});

const noticeList = ref([]);
const currentTime = ref('');
const timeInterval = ref(null);

// 更新日志折叠状态
const expandedUpdates = ref({});
const allUpdatesExpanded = ref(false);

// 更新日志数据
const updateLogs = ref([
  {
    version: 'v1.4.0',
    type: 'patch',
    title: '修复完善',
    date: '2025-11-13',
    features: [
      '1、优化登录页面品牌展示区域',
      '2、调整开发环境配置',
      '3、移除未使用的图标组件',
      '4、优化消息中心的 UI',
      '5、修复数据监控打不开的问题',
      '6、添加角色数据权限自定义功能',
      '7、添加表单设计器页面并集成相关依赖',
      '8、重构项目配置结构',
      '9、集成主题服务并优化自动导入配置',
      '10、修复 刷新页面，左侧菜单树没有联动激活',
      '11、实现文件管理功能模块',
      '12、静默应用主题设置避免初始化提示',
      '13、集成虚拟滚动功能以优化大数据量渲染性能',
      '14、优化菜单显示逻辑，支持 alwaysShow 配置',
      '15、支持自定义SVG图标和Ant Design图标分类展示',
    ]
  },
  {
    version: 'v1.3.0',
    type: 'patch',
    title: '修复完善',
    date: '2025-09-15',
    features: [
      '1、修复 输入框和搜索图标分开的问题',
      '2、修复 抽屉模式的展示问题',
      '3、修复分栏布局下面图标不展示的问题',
      '4、修复默认头像展示的问题',
      '5、修复统一的布局高度问题',
      '6、修复多条重复滚动条的问题',
      '7、修复标签过多导致显示不全的问题',
      '8、增加表格操作列工具，修复列设置展示的问题',
      '9、修复退出登录后页面跳转错误',
      '10、优化角色权限设置功能',
      '11、增加菜单外链支持并优化菜单处理逻辑',
      '12、新增字符串处理工具并优化用户列表',
      '13、优化主题变量设置和样式覆盖',
      '14、优化缓存监控页面功能和样式',
      '15、优化个人资料页面布局和功能',
    ]
  },
  {
    version: 'v1.2.0',
    type: 'major',
    title: '重大功能更新',
    date: '2025-07-15',
    features: [
      '新增ProTable组件，统一表格布局',
      '完善工作台页面，增加统计功能',
      '优化HistoryNav组件，支持动态显示',
      '修复部门管理树形结构显示问题'
    ]
  },
  {
    version: 'v1.1.5',
    type: 'minor',
    title: '功能优化',
    date: '2025-06-10',
    features: [
      '优化字典管理可展开行功能',
      '修复导航模式选择样式问题',
      '增强TableActionBar组件扩展性',
      '改进SearchForm组件用户体验'
    ]
  },
  {
    version: 'v1.1.0',
    type: 'minor',
    title: '界面美化',
    date: '2025-06-05',
    features: [
      '重新设计登录页面',
      '优化主题色彩搭配',
      '增加暗色主题支持',
      '提升移动端适配效果'
    ]
  },
  {
    version: 'v1.0.0',
    type: 'patch',
    title: '正式发布',
    date: '2025-05-01',
    features: [
      '完成基础框架搭建',
      '实现用户权限管理',
      '集成代码生成功能',
      '建立完整的监控体系'
    ]
  }
]);

// 我的网站数据
const mySites = ref([
  {
    name: 'JavaXiaoBear博客',
    description: '技术分享与学习笔记',
    url: 'https://javaxiaobear.cn',
    icon: 'read-outlined',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    status: 'online',
    technologies: ['Vue', 'Spring Boot', 'MySQL']
  },
  {
    name: 'BearJia Vue3',
    description: '高颜值现代化后台管理',
    url: 'https://gitee.com/javaxiaobear_admin/bear-jia-vue3.git',
    icon: 'tool-outlined',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    status: 'online',
    technologies: ['Ant Design', 'TypeScript', 'Vite']
  },
  {
    name: `${systemInfo.name}后端代码`,
    description: '快速开发管理平台',
    url: 'https://gitee.com/javaxiaobear_admin/bearjia-admin-backend.git',
    icon: 'dashboard-outlined',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    status: 'online',
    technologies: ['Spring Boot', 'MyBatis', 'Redis']
  },
  {
    name: '小熊学 Java',
    description: '学习指南+面试手册',
    url: 'https://javaxiaobear.cn',
    icon: 'api-outlined',
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    status: 'online',
    technologies: ['Java', '微服务', '分布式']
  },
]);

// 计算属性
const userInfo = computed(() => userStore.userInfo || {});
const lastLoginTime = computed(() => {
  const loginTime = userInfo.value.loginDate;
  return loginTime ? dayjs(loginTime).format('YYYY-MM-DD HH:mm:ss') : '首次登录';
});
const loginLocation = computed(() => userInfo.value.loginIp || '未知');

// 获取统计数据
const getStatistics = async () => {
  loading.stats = true;
  try {
    // 并发请求所有统计数据
    const [userRes, roleRes, menuRes, onlineRes] = await Promise.allSettled([
      listUser({pageNum: 1, pageSize: 1}),
      listRole({pageNum: 1, pageSize: 1}),
      listMenu({}),
      listOnlineUsers({})
    ]);

    if (userRes.status === 'fulfilled') {
      statistics.userCount = userRes.value.total || 0;
    }
    if (roleRes.status === 'fulfilled') {
      statistics.roleCount = roleRes.value.total || 0;
    }
    if (menuRes.status === 'fulfilled') {
      statistics.menuCount = Array.isArray(menuRes.value) ? menuRes.value.length : 0;
    }
    if (onlineRes.status === 'fulfilled') {
      statistics.onlineCount = onlineRes.value.total || 0;
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  } finally {
    loading.stats = false;
  }
};

// 获取通知数据
const getNoticeData = async () => {
  loading.notice = true;
  try {
    const response = await listNotice({pageNum: 1, pageSize: 5, status: '0'});
    noticeList.value = response.rows || [];
  } catch (error) {
    console.error('获取通知数据失败:', error);
    noticeList.value = [];
  } finally {
    loading.notice = false;
  }
};

// 更新日志相关方法
const getUpdateIcon = (type) => {
  const iconMap = {
    'major': 'fire-outlined',
    'minor': 'star-outlined',
    'patch': 'bug-outlined'
  };
  return iconMap[type] || 'info-circle-outlined';
};

const getUpdateTypeName = (type) => {
  const typeMap = {
    'major': '重大更新',
    'minor': '功能更新',
    'patch': '修复更新'
  };
  return typeMap[type] || '更新';
};

const viewAllUpdates = () => {
  // 这里可以跳转到更新日志页面或打开弹窗
  console.log('查看全部更新日志');
};

// 切换单个更新日志的展开/收起状态
const toggleUpdate = (index) => {
  expandedUpdates.value[index] = !expandedUpdates.value[index];
};

// 切换全部更新日志的展开/收起状态
const toggleAllUpdates = () => {
  allUpdatesExpanded.value = !allUpdatesExpanded.value;

  // 设置所有更新日志的展开状态
  updateLogs.value.forEach((_, index) => {
    expandedUpdates.value[index] = allUpdatesExpanded.value;
  });
};

// 网站相关方法
const getTechColor = (tech) => {
  const colorMap = {
    'Vue': 'green',
    'Vue3': 'green',
    'React': 'blue',
    'Spring Boot': 'orange',
    'MySQL': 'blue',
    'Ant Design': 'blue',
    'Vite': 'purple',
    'TypeScript': 'blue',
    'Tailwind': 'cyan',
    'Swagger': 'green',
    'Spring Doc': 'orange',
    'Nginx': 'green',
    'Nuxt': 'green',
    'MongoDB': 'green',
    'Node.js': 'green',
    'Hexo': 'blue',
    'GitHub Pages': 'gray',
    'Markdown': 'blue'
  };
  return colorMap[tech] || 'default';
};

const visitWebsite = (url) => {
  window.open(url, '_blank');
};

const visitAllWebsites = () => {
  // 打开所有网站的导航页面
  window.open('https://javaxiaobear.cn', '_blank');
};

// 工具方法

  const getNoticeTypeClass = (type) => {
    const typeMap = {
      '1': 'notice-type-info',
      '2': 'notice-type-warning',
    };
    return typeMap[type] || 'notice-type-info';
  };

  const getNoticeTypeName = (type) => {
    const typeMap = {
      '1': '通知',
      '2': '公告',
    };
    return typeMap[type] || '通知';
  };

  const formatTime = (time) => {
    if (!time) return '';
    const now = dayjs();
    const target = dayjs(time);
    const diff = now.diff(target, 'minute');

    if (diff < 1) return '刚刚';
    if (diff < 60) return `${diff}分钟前`;
    if (diff < 1440) return `${Math.floor(diff / 60)}小时前`;
    return target.format('MM-DD HH:mm');
  };

  const updateCurrentTime = () => {
    currentTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss');
  };



// 导航方法
  const goToUserManagement = () => {
    router.push('/system/user');
  };

  const goToRoleManagement = () => {
    router.push('/system/role');
  };

  const goToMenuManagement = () => {
    router.push('/system/menu');
  };

  const goToOnlineUsers = () => {
    router.push('/monitor/online');
  };

  const goToNoticeManagement = () => {
    router.push('/system/notice');
  };

  const goToServerMonitor = () => {
    router.push('/monitor/server');
  };

  const goToCacheMonitor = () => {
    router.push('/monitor/cache');
  };

  const goToJobManagement = () => {
    router.push('/monitor/job');
  };

  const goToSystemConfig = () => {
    router.push('/system/config');
  };

  const goToSystemSettings = () => {
    router.push('/system/config');
  };

  const viewNotice = (notice) => {
    // 这里可以打开通知详情弹窗
    console.log('查看通知:', notice);
  };

// 生命周期
  onMounted(() => {
    // 初始化数据
    getStatistics();
    getNoticeData();

    // 初始化更新日志折叠状态（默认只展开第一个）
    updateLogs.value.forEach((_, index) => {
      expandedUpdates.value[index] = index === 0; // 只展开第一个
    });

    // 更新时间
    updateCurrentTime();
    timeInterval.value = setInterval(updateCurrentTime, 1000);
  });

  onUnmounted(() => {
    if (timeInterval.value) {
      clearInterval(timeInterval.value);
    }
  })
</script>

<style lang="less" scoped>
.workbench-container {
  padding: 16px;
  // 移除背景色和最小高度，让PageContainer控制

  .welcome-section {
    margin-bottom: 16px;

    .welcome-card {
      background: #fafafa;
      border: 1px solid #f0f0f0;
      //background: linear-gradient(135deg, #f0f7ff 0%, #e6f0ff 100%);
      //border: none;
      border-radius: 12px;
      overflow: hidden;

      :deep(.ant-card-body) {
        padding: 24px;
      }

      .welcome-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: #666666;

        .welcome-info {
          flex: 1;

          .welcome-title {
            margin: 0 0 8px 0;
            font-size: 24px;
            font-weight: 600;
            color: #1677ff;

            .welcome-icon {
              margin-right: 8px;
              color: #1677ff;
            }
          }

          .welcome-desc {
            margin: 0 0 16px 0;
            font-size: 16px;
            opacity: 0.9;
          }

          .welcome-meta {
            display: flex;
            gap: 24px;
            font-size: 14px;
            opacity: 0.8;

            span {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }

        .welcome-actions {
          display: flex;
          gap: 12px;

          .ant-btn {
            height: 40px;
            border-radius: 8px;
            font-weight: 500;
          }
        }
      }
    }
  }

  .stats-section {
    margin-bottom: 16px;

    .stats-card {
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
      border: 1px solid #e8e8e8;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        border-color: var(--primary-color);
      }

      .stats-item {
        .stats-icon {
          font-size: 24px;
          margin-right: 8px;

          &.user-icon {
            color: #1890ff;
          }

          &.role-icon {
            color: #52c41a;
          }

          &.menu-icon {
            color: #faad14;
          }

          &.online-icon {
            color: #f5222d;
          }
        }

        :deep(.ant-statistic-title) {
          font-size: 14px;
          color: #666;
          margin-bottom: 8px;
        }

        :deep(.ant-statistic-content) {
          font-size: 24px;
          font-weight: 600;
        }
      }
    }
  }

  .main-content {
    .update-log-card,
    .website-card,
    .notice-card,
    .system-info-card,
    .quick-links-card {
      margin-bottom: 16px;
      border-radius: 8px;
      border: 1px solid #e8e8e8;

      :deep(.ant-card-head) {
        border-bottom: 1px solid #f0f0f0;
      }
    }

    // 更新日志操作按钮
    .update-log-actions {
      display: flex;
      gap: 8px;
    }

    // 更新日志样式
    .update-timeline {
      margin-top: 16px;

      .update-item {
        .update-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 8px 12px;
          border-radius: 6px;
          cursor: pointer;
          transition: all 0.3s ease;
          margin-bottom: 8px;
          border: 1px solid transparent;

          &:hover {
            background: #f5f5f5;
            border-color: #d9d9d9;
          }

          .update-header-left {
            display: flex;
            align-items: center;
            gap: 8px;
            flex: 1;

            .update-version {
              font-weight: 600;
              color: #333;
              font-size: 16px;
            }

            .update-type {
              padding: 2px 8px;
              border-radius: 12px;
              font-size: 10px;
              font-weight: 500;

              &.type-major {
                background: #fff2f0;
                color: #f5222d;
              }

              &.type-minor {
                background: #e6f7ff;
                color: #1890ff;
              }

              &.type-patch {
                background: #f6ffed;
                color: #52c41a;
              }
            }

            .update-date {
              color: #999;
              font-size: 12px;
            }
          }

          .update-toggle {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 24px;
            height: 24px;
            border-radius: 4px;
            transition: all 0.3s ease;

            &:hover {
              background: #e6f7ff;
            }

            .toggle-icon {
              font-size: 12px;
              color: #666;
              transition: transform 0.3s ease;
            }
          }
        }

        .update-content {
          padding: 0 12px 12px 12px;
          animation: fadeIn 0.3s ease;

          .update-title {
            font-size: 14px;
            color: #333;
            margin-bottom: 8px;
            font-weight: 500;
          }

          .update-features {
            margin: 0;
            padding-left: 16px;

            li {
              color: #666;
              font-size: 13px;
              line-height: 1.6;
              margin-bottom: 4px;
              position: relative;

              &:last-child {
                margin-bottom: 0;
              }

              &::marker {
                color: var(--primary-color);
              }
            }
          }
        }
      }

      .update-icon {
        font-size: 16px;

        &.update-major {
          color: #f5222d;
        }

        &.update-minor {
          color: #1890ff;
        }

        &.update-patch {
          color: #52c41a;
        }
      }
    }

    // 网站展示样式
    .website-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 16px;

      .website-item {
        border: 1px solid #f0f0f0;
        border-radius: 8px;
        padding: 16px;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;

        &:hover {
          border-color: var(--primary-color);
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .website-preview {
          display: flex;
          align-items: flex-start;
          gap: 12px;

          .website-icon {
            width: 48px;
            height: 48px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: white;
            flex-shrink: 0;
          }

          .website-info {
            flex: 1;
            min-width: 0;

            .website-name {
              font-size: 16px;
              font-weight: 600;
              color: #333;
              margin-bottom: 4px;
            }

            .website-desc {
              font-size: 13px;
              color: #666;
              margin-bottom: 8px;
              line-height: 1.4;
            }

            .website-tech {
              display: flex;
              flex-wrap: wrap;
              gap: 4px;
            }
          }
        }

        .website-status {
          position: absolute;
          top: 12px;
          right: 12px;
        }
      }
    }

    .notice-item {
      cursor: pointer;
      transition: background 0.3s ease;

      &:hover {
        background: #f5f5f5;
      }

      .notice-title {
        display: flex;
        align-items: center;
        gap: 8px;

        .notice-type {
          padding: 2px 6px;
          border-radius: 4px;
          font-size: 10px;
          font-weight: 500;

          &.notice-type-info {
            background: #e6f7ff;
            color: #1890ff;
          }

          &.notice-type-warning {
            background: #fff7e6;
            color: #fa8c16;
          }
        }

        .notice-text {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .notice-time {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #999;
      }
    }

    .empty-notice {
      text-align: center;
      padding: 20px;
    }

    .system-info {
      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .info-label {
          font-size: 14px;
          color: #666;
        }

        .info-value {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }
      }
    }

    .quick-links {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 8px;

      .ant-btn {
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: flex-start;
        padding: 0 12px;
        border: 1px solid #f0f0f0;
        border-radius: 6px;
        transition: all 0.3s ease;

        &:hover {
          border-color: var(--primary-color);
          background: #f6ffed;
        }

        .anticon {
          margin-right: 8px;
        }
      }
    }
  }
}

// 动画效果
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 折叠过渡动画
.ant-collapse-transition {
  transition: all 0.3s ease;
}

// 响应式设计
@media (max-width: 768px) {
  .workbench-container {
    padding: 8px;

    .welcome-content {
      flex-direction: column;
      text-align: center;

      .welcome-actions {
        margin-top: 16px;
        justify-content: center;
      }
    }

    .quick-links {
      grid-template-columns: 1fr;
    }
  }
}
</style>
