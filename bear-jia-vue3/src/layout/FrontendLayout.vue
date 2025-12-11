<template>
  <div class="frontend-layout">
    <!-- 顶部导航栏 -->
    <header class="frontend-header" :class="{ 'header-scrolled': isScrolled }">
      <div class="header-container">
        <!-- Logo -->
        <div class="logo-section">
          <router-link to="/frontend/home" class="logo-link">
            <img src="/src/assets/images/logo.png" :alt="systemInfo.name" class="logo-img" />
            <span class="logo-text">{{ systemInfo.name }}</span>
          </router-link>
        </div>

        <!-- 导航菜单 -->
        <nav class="nav-menu" :class="{ 'nav-open': mobileMenuOpen }">
          <router-link 
            v-for="item in menuItems" 
            :key="item.path"
            :to="item.path"
            class="nav-item"
            @click="closeMobileMenu"
          >
            {{ item.title }}
          </router-link>
        </nav>

        <!-- 右侧操作区 -->
        <div class="header-actions">
          <!-- 主题切换 -->
          <a-button 
            type="text" 
            :icon="h(darkMode ? SunOutlined : MoonOutlined)"
            @click="toggleTheme"
            class="theme-toggle"
          />
          
          <!-- 后台管理入口 -->
          <a-button 
            type="primary" 
            @click="goToAdmin"
            class="admin-btn"
          >
            <template #icon>
              <setting-outlined />
            </template>
            管理后台
          </a-button>

          <!-- 移动端菜单按钮 -->
          <a-button 
            type="text"
            class="mobile-menu-btn"
            @click="toggleMobileMenu"
          >
            <template #icon>
              <menu-outlined v-if="!mobileMenuOpen" />
              <close-outlined v-else />
            </template>
          </a-button>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="frontend-main">
      <router-view v-slot="{ Component, route }">
        <transition name="page-fade" mode="out-in">
          <keep-alive :include="keepAliveComponents">
            <component :is="Component" :key="route.path" />
          </keep-alive>
        </transition>
      </router-view>
    </main>

    <!-- 底部 -->
    <footer class="frontend-footer">
      <div class="footer-container">
        <div class="footer-content">
          <!-- 公司信息 -->
          <div class="footer-section">
            <h3>{{ systemInfo.companyName }}</h3>
            <p>{{ systemInfo.description }}</p>
            <div class="social-links">
              <a href="#" class="social-link">
                <wechat-outlined />
              </a>
              <a href="#" class="social-link">
                <weibo-outlined />
              </a>
              <a href="#" class="social-link">
                <github-outlined />
              </a>
            </div>
          </div>

          <!-- 快速链接 -->
          <div class="footer-section">
            <h4>快速链接</h4>
            <ul class="footer-links">
              <li><router-link to="/frontend/about">关于我们</router-link></li>
              <li><router-link to="/frontend/products">产品中心</router-link></li>
              <li><router-link to="/frontend/news">新闻资讯</router-link></li>
              <li><router-link to="/frontend/contact">联系我们</router-link></li>
            </ul>
          </div>

          <!-- 联系信息 -->
          <div class="footer-section">
            <h4>联系我们</h4>
            <div class="contact-info">
              <p><phone-outlined /> 400-123-4567</p>
              <p><mail-outlined /> contact@bearjia.com</p>
              <p><environment-outlined /> 北京市朝阳区xxx大厦</p>
            </div>
          </div>
        </div>

        <div class="footer-bottom">
          <p>{{ systemInfo.copyright }}</p>
          <p>
            <a href="#">隐私政策</a> | 
            <a href="#">服务条款</a> | 
            <a href="#">网站地图</a>
          </p>
        </div>
      </div>
    </footer>

    <!-- 回到顶部按钮 -->
    <transition name="fade">
      <a-button 
        v-show="showBackTop"
        type="primary"
        shape="circle"
        size="large"
        class="back-top-btn"
        @click="scrollToTop"
      >
        <template #icon>
          <up-outlined />
        </template>
      </a-button>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, h } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAppStore } from '@/stores/app';
import { SYSTEM_INFO } from '@/config/system.config';
import {
  SettingOutlined,
  MenuOutlined,
  CloseOutlined,
  WechatOutlined,
  WeiboOutlined,
  GithubOutlined,
  PhoneOutlined,
  MailOutlined,
  EnvironmentOutlined,
  UpOutlined
} from '@ant-design/icons-vue';

const router = useRouter();
const route = useRoute();
const appStore = useAppStore();

// 系统信息
const systemInfo = ref(SYSTEM_INFO);

// 响应式数据
const isScrolled = ref(false);
const mobileMenuOpen = ref(false);
const showBackTop = ref(false);
const darkMode = ref(false);

// 导航菜单项
const menuItems = [
  { path: '/frontend/home', title: '首页' },
  { path: '/frontend/about', title: '关于我们' },
  { path: '/frontend/products', title: '产品中心' },
  { path: '/frontend/news', title: '新闻资讯' },
  { path: '/frontend/contact', title: '联系我们' }
];

// 需要缓存的组件
const keepAliveComponents = computed(() => {
  return route.meta?.keepAlive ? [route.name] : [];
});

// 滚动监听
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  isScrolled.value = scrollTop > 50;
  showBackTop.value = scrollTop > 300;
};

// 切换移动端菜单
const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value;
};

// 关闭移动端菜单
const closeMobileMenu = () => {
  mobileMenuOpen.value = false;
};

// 切换主题
const toggleTheme = () => {
  darkMode.value = !darkMode.value;
  appStore.updateSettings({ darkMode: darkMode.value });
};

// 跳转到管理后台
const goToAdmin = () => {
  router.push('/');
};

// 回到顶部
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
};

// 生命周期
onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  darkMode.value = appStore.layoutSettings.darkMode;
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style lang="less" scoped>
.frontend-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

// 头部样式
.frontend-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;

  &.header-scrolled {
    background: rgba(255, 255, 255, 0.98);
    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  }

  .header-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 70px;
  }

  .logo-section {
    .logo-link {
      display: flex;
      align-items: center;
      text-decoration: none;
      color: inherit;

      .logo-img {
        width: 40px;
        height: 40px;
        margin-right: 12px;
      }

      .logo-text {
        font-size: 24px;
        font-weight: 700;
        color: var(--primary-color);
      }
    }
  }

  .nav-menu {
    display: flex;
    gap: 32px;

    .nav-item {
      color: #333;
      text-decoration: none;
      font-weight: 500;
      padding: 8px 0;
      position: relative;
      transition: color 0.3s ease;

      &:hover,
      &.router-link-active {
        color: var(--primary-color);
      }

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        width: 0;
        height: 2px;
        background: var(--primary-color);
        transition: width 0.3s ease;
      }

      &:hover::after,
      &.router-link-active::after {
        width: 100%;
      }
    }

    @media (max-width: 768px) {
      position: fixed;
      top: 70px;
      left: 0;
      right: 0;
      background: white;
      flex-direction: column;
      padding: 20px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      transform: translateY(-100%);
      opacity: 0;
      visibility: hidden;
      transition: all 0.3s ease;

      &.nav-open {
        transform: translateY(0);
        opacity: 1;
        visibility: visible;
      }

      .nav-item {
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }
      }
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 12px;

    .theme-toggle {
      color: #666;
    }

    .admin-btn {
      border-radius: 20px;
    }

    .mobile-menu-btn {
      display: none;

      @media (max-width: 768px) {
        display: flex;
      }
    }

    @media (max-width: 768px) {
      .nav-menu {
        display: none;
      }
    }
  }
}

// 主要内容区域
.frontend-main {
  flex: 1;
  margin-top: 70px;
}

// 页面切换动画
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.3s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

// 底部样式
.frontend-footer {
  background: #1a1a1a;
  color: #fff;
  margin-top: auto;

  .footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 60px 24px 20px;
  }

  .footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 40px;
    margin-bottom: 40px;
  }

  .footer-section {
    h3, h4 {
      margin-bottom: 20px;
      color: #fff;
    }

    h3 {
      font-size: 24px;
      font-weight: 700;
    }

    h4 {
      font-size: 18px;
      font-weight: 600;
    }

    p {
      color: #ccc;
      line-height: 1.6;
      margin-bottom: 16px;
    }

    .social-links {
      display: flex;
      gap: 12px;

      .social-link {
        width: 40px;
        height: 40px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        text-decoration: none;
        transition: all 0.3s ease;

        &:hover {
          background: var(--primary-color);
          transform: translateY(-2px);
        }
      }
    }

    .footer-links {
      list-style: none;
      padding: 0;

      li {
        margin-bottom: 12px;

        a {
          color: #ccc;
          text-decoration: none;
          transition: color 0.3s ease;

          &:hover {
            color: var(--primary-color);
          }
        }
      }
    }

    .contact-info {
      p {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;
      }
    }
  }

  .footer-bottom {
    border-top: 1px solid #333;
    padding-top: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #999;

    @media (max-width: 768px) {
      flex-direction: column;
      gap: 12px;
      text-align: center;
    }

    a {
      color: #999;
      text-decoration: none;
      margin: 0 4px;

      &:hover {
        color: var(--primary-color);
      }
    }
  }
}

// 回到顶部按钮
.back-top-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 999;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

// 淡入淡出动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 暗色主题
:global(.dark-theme) {
  .frontend-header {
    background: rgba(20, 20, 20, 0.95);
    border-bottom-color: rgba(255, 255, 255, 0.1);

    &.header-scrolled {
      background: rgba(20, 20, 20, 0.98);
    }

    .nav-item {
      color: #fff;

      &:hover,
      &.router-link-active {
        color: var(--primary-color);
      }
    }

    @media (max-width: 768px) {
      .nav-menu {
        background: #1a1a1a;
      }
    }
  }
}
</style>
