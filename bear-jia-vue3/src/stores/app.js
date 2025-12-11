import { defineStore } from 'pinia';
import Cookies from 'js-cookie';
import { themeService, THEME_MODES } from '@/utils/theme';

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebar: {
      opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true,
      withoutAnimation: false
    },
    device: 'desktop',
    size: Cookies.get('size') || 'medium',
    layoutSettings: {
      primaryColor: themeService.getCurrentTheme().primaryColor || '#1677ff',
      darkMode: themeService.getCurrentTheme().mode === THEME_MODES.DARK,
      navMode: localStorage.getItem('navMode') || 'side', // side, top, mix, column, drawer
      theme: themeService.getCurrentTheme().mode || 'light',
      layout: localStorage.getItem('layout') || 'mix',
      contentWidth: localStorage.getItem('contentWidth') || 'fluid',
      fixedHeader: localStorage.getItem('fixedHeader') !== 'false',
      fixedSidebar: localStorage.getItem('fixedSidebar') !== 'false',
      splitMenus: localStorage.getItem('splitMenus') === 'true' || false,
      colorWeak: localStorage.getItem('colorWeak') === 'true' || false,
      multiTab: localStorage.getItem('multiTab') !== 'false',
      hideFooter: localStorage.getItem('hideFooter') === 'true' || false,
      tableStriped: localStorage.getItem('tableStriped') === 'true' || false,
      tableShowHeader: localStorage.getItem('tableShowHeader') !== 'false'
    },
    // 系统配置
    systemConfig: {
      title: localStorage.getItem('systemTitle') || 'BearJia Admin',
      logo: localStorage.getItem('systemLogo') || '/src/assets/images/logo.png',
      copyright: localStorage.getItem('systemCopyright') || 'Copyright © 2024 BearJia',
      version: localStorage.getItem('systemVersion') || '1.0.0',
      description: localStorage.getItem('systemDescription') || 'BearJia后台管理系统',
      keywords: localStorage.getItem('systemKeywords') || 'BearJia,Admin,Vue3,Ant Design Vue'
    }
  }),

  actions: {
    toggleSideBar() {
      this.sidebar.opened = !this.sidebar.opened;
      this.sidebar.withoutAnimation = false;
      if (this.sidebar.opened) {
        Cookies.set('sidebarStatus', 1);
      } else {
        Cookies.set('sidebarStatus', 0);
      }
    },

    closeSideBar(withoutAnimation) {
      Cookies.set('sidebarStatus', 0);
      this.sidebar.opened = false;
      this.sidebar.withoutAnimation = withoutAnimation;
    },

    toggleDevice(device) {
      this.device = device;
    },

    setSize(size) {
      this.size = size;
      Cookies.set('size', size);
    },

    updateSettings(settings) {
      this.layoutSettings = {
        ...this.layoutSettings,
        ...settings
      };

      // 持久化存储每个设置项
      Object.keys(settings).forEach(key => {
        // 主题相关的设置使用新的主题服务
        if (key === 'primaryColor') {
          themeService.setPrimaryColor(settings[key]);
        } else if (key === 'darkMode') {
          themeService.setThemeMode(settings[key] ? THEME_MODES.DARK : THEME_MODES.LIGHT);
        } else if (key === 'colorWeak') {
          themeService.setColorWeak(settings[key]);
        } else {
          localStorage.setItem(key, String(settings[key]));
        }
      });

      // 应用主题变化（使用新的主题服务）
      this.applyTheme();
    },

    // 更新系统配置
    updateSystemConfig(config) {
      this.systemConfig = { ...this.systemConfig, ...config };

      // 持久化存储系统配置
      Object.keys(config).forEach(key => {
        localStorage.setItem(`system${key.charAt(0).toUpperCase() + key.slice(1)}`, config[key]);
      });

      // 更新页面标题
      if (config.title) {
        document.title = config.title;
      }
    },

    // 应用主题（使用新的主题服务）
    applyTheme() {
      try {
        // 主题色和模式由主题服务管理
        // 使用静默模式避免初始化时显示提示消息
        themeService.setPrimaryColor(this.layoutSettings.primaryColor, true);
        themeService.setThemeMode(this.layoutSettings.darkMode ? THEME_MODES.DARK : THEME_MODES.LIGHT, true);
        themeService.setColorWeak(this.layoutSettings.colorWeak, true);

        console.log('✅ 主题应用成功:', this.layoutSettings);
      } catch (error) {
        console.error('❌ 主题应用失败:', error);
      }
    },

    // 重置所有设置
    resetSettings() {
      const defaultSettings = {
        primaryColor: '#1677ff',
        darkMode: false,
        navMode: 'side',
        theme: 'light',
        layout: 'mix',
        contentWidth: 'fluid',
        fixedHeader: true,
        fixedSidebar: true,
        splitMenus: false,
        colorWeak: false,
        multiTab: true,
        hideFooter: false,
        tableStriped: false,
        tableShowHeader: true
      };

      this.layoutSettings = defaultSettings;

      // 清除localStorage（非主题相关）
      Object.keys(defaultSettings).forEach(key => {
        if (!['primaryColor', 'darkMode', 'colorWeak'].includes(key)) {
          localStorage.removeItem(key);
        }
      });

      // 重置主题（使用主题服务）
      themeService.resetTheme();
    },

    // 导出配置
    exportSettings() {
      const config = {
        layoutSettings: this.layoutSettings,
        systemConfig: this.systemConfig,
        timestamp: new Date().toISOString()
      };

      const blob = new Blob([JSON.stringify(config, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `bearjia-config-${new Date().toISOString().split('T')[0]}.json`;
      a.click();
      URL.revokeObjectURL(url);
    },

    // 导入配置
    importSettings(config) {
      if (config.layoutSettings) {
        this.updateSettings(config.layoutSettings);
      }
      if (config.systemConfig) {
        this.updateSystemConfig(config.systemConfig);
      }
    }
  }
});
