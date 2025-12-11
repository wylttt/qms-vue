import { createRouter, createWebHistory } from 'vue-router';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import { getToken } from '@/utils/auth.js';
import { useUserStore } from '@/stores/user';
import { usePermissionStore } from '@/stores/permission';
import { notification } from 'ant-design-vue';
import { constantRoutes } from './routes';

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 })
});

// 白名单
const whiteList = ['/login', '/login2', '/login3', '/auth-redirect', '/register'];

// 保存页面状态
const savePageState = (from) => {
  const currentState = {
    scrollPosition: window.scrollY
  };
  sessionStorage.setItem(`pageState_${from.fullPath}`, JSON.stringify(currentState));
};

// 恢复页面状态
const restorePageState = (to) => {
  const savedState = sessionStorage.getItem(`pageState_${to.fullPath}`);
  if (savedState) {
    const state = JSON.parse(savedState);
    window.scrollTo(0, state.scrollPosition);
  }
};

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start();
  const hasToken = getToken();

  if (hasToken) {
    const userStore = useUserStore();
    const permissionStore = usePermissionStore();
    
    if (to.path === '/login') {
      next({ path: '/home/workbench' });
      NProgress.done();
    } else {
      if (userStore.roles.length === 0) {
        try {
          await userStore.getInfo();
          const accessRoutes = await permissionStore.generateRoutes();
          accessRoutes.forEach(route => {
            router.addRoute(route);
          });
          next({ ...to, replace: true });
        } catch (error) {
          await userStore.logout();
          notification.error({
            message: '错误',
            description: '获取用户信息失败，请重新登录'
          });
          next(`/login?redirect=${to.path}`);
          NProgress.done();
        }
      } else {
        // 检查路由是否在路由表中，如果不在则可能是页面刷新后丢失，需要重新生成
        if (!router.hasRoute(to.name) && !router.getRoutes().some(route => route.path === to.path || (to.path.startsWith(route.path + '/') && route.path !== '/:pathMatch(.*)*'))) {
          try {
            const accessRoutes = await permissionStore.generateRoutes();
            accessRoutes.forEach(route => {
              router.addRoute(route);
            });
            next({ ...to, replace: true });
          } catch (error) {
            console.error('重新生成路由时出错:', error);
            await userStore.logout();
            notification.error({
              message: '错误',
              description: '重新生成路由失败，请重新登录'
            });
            next(`/login?redirect=${to.path}`);
            NProgress.done();
          }
        } else {
          next();
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next();
    } else {
      next(`/login?redirect=${to.path}`);
      NProgress.done();
    }
  }
});

// 错误处理
router.onError((error) => {
  console.error('路由错误:', error);
  notification.error({
    message: '页面加载失败',
    description: '请检查网络连接或刷新页面重试'
  });
});

// 路由后置守卫
router.afterEach((to, from) => {
  if (from.name) {
    savePageState(from);
  }
  
  if (to.name) {
    restorePageState(to);
  }
  
  document.body.classList.add('page-transition');
  setTimeout(() => {
    document.body.classList.remove('page-transition');
  }, 300);
  
  NProgress.done();
});

export default router;

