<template>
  <a-layout-sider
      :theme="layoutSettings.darkMode ? 'dark' : 'light'"
      :collapsed="collapsed"
      :trigger="null"
      collapsible
      :class="{ 'dark-theme': layoutSettings.darkMode }"
      @collapse="$emit('update:collapsed', $event)"
  >
    <!-- Logo区域 -->
    <div class="layout-logo">
      <img src="/src/assets/images/logo.png" class="logo-img" :alt="`${SYSTEM_INFO.shortName} Logo`" />
      <span class="logo-title" v-show="!collapsed">{{ SYSTEM_INFO.name }}</span>
    </div>

    <!-- 菜单区域 -->
    <!-- 菜单区域 -->
    <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        :theme="layoutSettings.darkMode ? 'dark' : 'light'"
        mode="inline"
        class="side-menu"
        @openChange="handleOpenChange"
    >
      <!-- 工作台 (保持不变) -->
      <a-menu-item key="workbench" @click="handleMenuClick('HomePage', '/home', '主页', 'Workbench', 'workbench', '工作台')">
        <template #icon>
          <BearJiaIcon icon="HomeOutlined" />
        </template>
        工作台
      </a-menu-item>

      <!-- 动态菜单 -->
      <template v-for="(router, index) in menuData">
        <!-- 情况1: 当父菜单有多个子菜单，或者设置了 alwaysShow 且有子菜单时，渲染成可折叠的 SubMenu -->
        <a-sub-menu
            v-if="router.children && (router.children.length > 1 || (router.alwaysShow && router.children.length === 1))"
            :key="`${router.path}-${index}`"
        >
          <template #icon>
            <BearJiaIcon :icon="router.meta?.icon || 'MenuOutlined'" />
          </template>
          <template #title>
            <span>{{ router.meta?.title }}</span>
            <!-- 新增: 判断父菜单本身是否是外链 -->
            <BearJiaIcon v-if="isExternal(router.path)" icon="LinkOutlined" class="external-link-icon" />
          </template>

          <!-- 内部子菜单渲染逻辑 -->
          <template v-for="(children, childIndex) in router.children">
            <!-- 二级父菜单 -->
            <a-sub-menu
                v-if="children.children && children.children.length > 0"
                :key="`${children.path}-${childIndex}`"
            >
              <template #icon>
                <BearJiaIcon :icon="children.meta?.icon || 'AppstoreOutlined'" />
              </template>
              <template #title>
                <span>{{ children.meta?.title }}</span>
                <!-- 新增: 判断二级父菜单是否是外链 -->
                <BearJiaIcon v-if="isExternal(children.path)" icon="LinkOutlined" class="external-link-icon" />
              </template>
              <!-- 三级菜单 -->
              <a-menu-item
                  v-for="(threeLevelChildren, threeIndex) in children.children"
                  :key="`${threeLevelChildren?.path}-${threeIndex}`"
                  @click="handleThreeLevelMenuClick(router.path, children, threeLevelChildren)"
              >
                <template #icon>
                  <BearJiaIcon :icon="threeLevelChildren.meta?.icon || 'BarsOutlined'" />
                </template>
                <span>{{ threeLevelChildren.meta?.title }}</span>
                <!-- 新增: 判断三级菜单是否是外链 -->
                <BearJiaIcon v-if="isExternal(threeLevelChildren.path)" icon="LinkOutlined" class="external-link-icon" />
              </a-menu-item>
            </a-sub-menu>
            <!-- 二级菜单 -->
            <a-menu-item
                v-else
                :key="`single-${children.path}-${childIndex}`"
                @click="handleMenuClick(router.name, router.path, router.meta.title, children.name, children.path, children.meta.title, children.component)"
            >
              <template #icon>
                <BearJiaIcon :icon="children.meta?.icon || 'BarsOutlined'" />
              </template>
              <span>{{ children.meta?.title }}</span>
              <!-- 新增: 判断二级菜单是否是外链 -->
              <BearJiaIcon v-if="isExternal(children.path)" icon="LinkOutlined" class="external-link-icon" />
            </a-menu-item>
          </template>
        </a-sub-menu>

        <!-- 情况2: 当父菜单只有一个子菜单且没有设置 alwaysShow 时 (包括外链)，直接渲染成一个普通的 MenuItem -->
        <a-menu-item
            v-else-if="router.children && router.children.length === 1 && !router.alwaysShow"
            :key="`single-child-wrapper-${router.children[0].path}`"
            @click="handleMenuClick(router.name, router.path, router.meta.title, router.children[0].name, router.children[0].path, router.children[0].meta.title, router.children[0].component)"
        >
          <template #icon>
            <BearJiaIcon :icon="router.children[0].meta?.icon || router.meta?.icon || 'MenuOutlined'" />
          </template>
          <span>{{ router.children[0].meta?.title }}</span>
          <!-- 新增: 判断唯一的子菜单是否是外链 -->
          <BearJiaIcon v-if="isExternal(router.children[0].path)" icon="LinkOutlined" class="external-link-icon" />
        </a-menu-item>

        <!-- 情况3 (可选，容错): 没有子菜单的顶级菜单 -->
        <a-menu-item
            v-else-if="!router.children || router.children.length === 0"
            :key="`no-child-${router.path}-${index}`"
            @click="handleMenuClick(null, null, null, router.name, router.path, router.meta.title, router.component)"
        >
          <template #icon>
            <BearJiaIcon :icon="router.meta?.icon || 'MenuOutlined'" />
          </template>
          <span>{{ router.meta?.title }}</span>
          <!-- 新增: 判断顶级菜单本身是否是外链 -->
          <BearJiaIcon v-if="isExternal(router.path)" icon="LinkOutlined" class="external-link-icon" />
        </a-menu-item>
      </template>
    </a-menu>
  </a-layout-sider>
</template>

<script setup>
import { ref } from 'vue';
import { BearJiaIcon } from '@/utils/BearJiaIcon.js';
import { isExternal } from '@/utils/bearjia.js';
import { SYSTEM_INFO } from '@/config/system.config';

const props = defineProps({
  collapsed: Boolean,
  menuData: Array,
  layoutSettings: Object
});

const emit = defineEmits(['update:collapsed', 'menuSelect']);

const selectedKeys = ref([]);
const openKeys = ref([]);

const handleOpenChange = (keys) => {
  if (keys.length <= 1) {
    openKeys.value = keys;
    return;
  }

  const latestKey = keys[keys.length - 1];
  const isTopLevel = props.menuData.some((item, idx) => `${item.path}-${idx}` === latestKey);

  if (isTopLevel) {
    openKeys.value = [latestKey];
  } else {
    openKeys.value = keys;
  }
};

const handleMenuClick = (fatherName, fatherPath, fatherTitle, name, path, title, component) => {
  try {
    // 检查是否是外链
    if (isExternal(path)) {
      // 外链直接在新窗口打开，不触发路由跳转
      window.open(path, '_blank');
      return;
    }

    if (path === 'workbench') {
      selectedKeys.value = ['workbench'];
      openKeys.value = [];
    } else {
      const fatherIndex = props.menuData.findIndex(item => item.path === fatherPath);
      const childIndex = props.menuData[fatherIndex]?.children.findIndex(item => item.path === path);
      const menuKey = `${path}-${childIndex}`;
      const fatherKey = `${fatherPath}-${fatherIndex}`;

      selectedKeys.value = [menuKey];
      openKeys.value = [fatherKey];
    }
    emit('menuSelect', {
      fatherName, fatherPath, fatherTitle, name, path, title, component
    });
  } catch (error) {
    console.error('菜单点击错误:', error);
  }
};

const handleThreeLevelMenuClick = (greatFatherPath, father, menu) => {
  try {
    // 检查是否是外链
    if (isExternal(menu.path)) {
      // 外链直接在新窗口打开，不触发路由跳转
      window.open(menu.path, '_blank');
      return;
    }

    const greatFatherIndex = props.menuData.findIndex(item => item.path === greatFatherPath);
    const fatherIndex = props.menuData[greatFatherIndex]?.children.findIndex(item => item.path === father.path);
    const menuIndex = father?.children.findIndex(item => item.path === menu.path);

    selectedKeys.value = [`${menu.path}-${menuIndex}`];
    openKeys.value = [
      `${greatFatherPath}-${greatFatherIndex}`,
      `${father.path}-${fatherIndex}`
    ];

    emit('menuSelect', {
      greatFatherPath, father, menu
    });
  } catch (error) {
    console.error('三级菜单点击错误:', error);
  }
};

const setCurrentMenu = (fullPath) => {
  console.log('SideMenu.setCurrentMenu 被调用，路径:', fullPath);

  // 检查是否是工作台
  if (fullPath === '/home/workbench') {
    selectedKeys.value = ['workbench'];
    openKeys.value = [];
    console.log('设置工作台激活');
    return;
  }

  if (!props.menuData || props.menuData.length === 0) {
    console.log('菜单数据为空');
    return;
  }

  // 遍历菜单数据查找匹配的路径
  for (const [routerIndex, router] of props.menuData.entries()) {
    if (!router.children) continue;

    for (const [childIndex, child] of router.children.entries()) {
      // 构建完整路径进行比较
      const childFullPath = `${router.path}/${child.path}`;

      // 检查三级菜单
      if (child.children && child.children.length > 0) {
        for (const [threeIndex, threeLevel] of child.children.entries()) {
          const threeFullPath = `${router.path}/${threeLevel.path}`;

          // 同时匹配完整路径和相对路径
          if (threeLevel.path === fullPath || threeFullPath === fullPath) {
            selectedKeys.value = [`${threeLevel.path}-${threeIndex}`];
            openKeys.value = [
              `${router.path}-${routerIndex}`,
              `${child.path}-${childIndex}`
            ];
            console.log('找到三级菜单:', {
              匹配路径: threeLevel.path,
              完整路径: threeFullPath,
              selectedKeys: selectedKeys.value,
              openKeys: openKeys.value
            });
            return;
          }
        }
      } else {
        // 检查二级菜单 - 同时匹配完整路径和相对路径
        if (child.path === fullPath || childFullPath === fullPath) {
          selectedKeys.value = [`single-${child.path}-${childIndex}`];
          openKeys.value = [`${router.path}-${routerIndex}`];
          console.log('找到二级菜单:', {
            子菜单路径: child.path,
            完整路径: childFullPath,
            selectedKeys: selectedKeys.value,
            openKeys: openKeys.value
          });
          return;
        }
      }
    }

    // 检查只有一个子菜单的情况
    if (router.children.length === 1) {
      const onlyChild = router.children[0];
      const onlyChildFullPath = `${router.path}/${onlyChild.path}`;

      if (onlyChild.path === fullPath || onlyChildFullPath === fullPath) {
        selectedKeys.value = [`single-child-wrapper-${onlyChild.path}`];
        openKeys.value = [];
        console.log('找到单子菜单:', {
          子菜单路径: onlyChild.path,
          完整路径: onlyChildFullPath,
          selectedKeys: selectedKeys.value
        });
        return;
      }
    }
  }

  console.log('未找到匹配的菜单项，路径:', fullPath);
  console.log('菜单数据示例:', props.menuData.slice(0, 2).map(r => ({
    path: r.path,
    children: r.children?.slice(0, 2).map(c => ({
      path: c.path,
      hasChildren: !!c.children
    }))
  })));
};

defineExpose({ setCurrentMenu });
</script>

<style lang="less">
@import '@/style/components/menu.less';
.ant-layout-sider{
  width: 68px!important;
}
:deep(.ant-layout-sider) {
  margin: 16px 0 16px 16px;
  border-radius: 16px;
  overflow: hidden;
  background: var(--component-background) !important;
  width: 68px!important;

  &.dark-theme {
    background: var(--sidebar-background) !important;

    .layout-logo {
      background-color: var(--sidebar-background) !important;
    }

    // 强制覆盖 Ant Design 的默认暗色菜单背景
    .ant-menu {
      background: var(--sidebar-background) !important;

      &.ant-menu-dark {
        background: var(--sidebar-background) !important;
      }
    }
  }

  &:not(.dark-theme) {
    .layout-logo {
      background-color: var(--component-background);
    }
  }
}

.layout-logo {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: var(--component-background);
  .logo-img {
    height: 32px;
    margin-right: 8px;
  }
  .logo-title {
    color: var(--text-primary);
    font-size: 18px;
    font-weight: 600;
    white-space: nowrap;
    opacity: 1;
    transition: all 0.3s;
  }
}

.side-menu {
  height: calc(100% - 64px);
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.side-menu::-webkit-scrollbar {
  display: none;
}
// 在您的 style 标签内，可以放在 .side-menu 样式定义的旁边
.external-link-icon {
  margin-left: 56px;
  color: #888; // 给一个不太突兀的颜色
  vertical-align: middle; // 垂直居中对齐
}

// 暗黑模式下的适配
.ant-layout-sider.dark-theme {
  .external-link-icon {
    color: #a0a0a0; // 暗黑模式下颜色浅一点
  }
}
</style>
