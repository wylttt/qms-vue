// 使用路由懒加载
import frontendRoutes from './frontend';

// 公共路由
export const constantRoutes = [
  {
    path: '/login',
    name: 'LoginPage',
    component: () => import(/* webpackChunkName: "login" */ '@/views/LoginPage.vue'),
    hidden: true
  },
  {
    path: '/login2',
    name: 'LoginPage2',
    component: () => import(/* webpackChunkName: "login" */ '@/views/LoginPage2.vue'),
    hidden: true
  },
  {
    path: '/login3',
    name: 'LoginPage3',
    component: () => import(/* webpackChunkName: "login" */ '@/views/LoginPage3.vue'),
    hidden: true
  },
  {
    path: '/register',
    name: 'RegisterPage',
    component: () => import(/* webpackChunkName: "login" */ '@/views/RegisterPage.vue'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import(/* webpackChunkName: "error" */ '@/views/404.vue'),
    hidden: true
  },
//   {
//     path: '/401',
//     component: () => import(/* webpackChunkName: "error" */ '@/views/error/401.vue'),
//     hidden: true
//   },
  {
    path: '/',
    redirect: '/home/workbench',
  },
  {
    path: '/home',
    name: 'HomePage',
    component: () => import(/* webpackChunkName: "layout" */ '@/layout/BaseLayout.vue'),
    children: [
      {
        name: 'Workbench',
        path: 'workbench',
        component: () => import(/* webpackChunkName: "workbench" */ '@/views/workbench/WorkbenchPage.vue'),
        meta: {
          title: '工作台',
          icon: 'dashboard',
          affix: true
        }
      },
      {
        path: '',
        component: () => import(/* webpackChunkName: "workbench" */ '@/views/workbench/WorkbenchPage.vue'),
        meta: {
          title: '工作台',
          icon: 'dashboard',
          affix: true
        }
      }
    ],
  },
  {
    path: '/system/user/profile',
    name: 'profile',
    component: () => import(/* webpackChunkName: "layout" */ '@/layout/BaseLayout.vue'),
    children: [
      {
        path: '',
        component: () => import(/* webpackChunkName: "profile" */ '@/views/system/user/profile/index.vue'),
      }
    ],
  },
    {
        path: '/system/role/module/authUser',
        name: 'RoleAuthUser',
        component: () => import(/* webpackChunkName: "layout" */ '@/layout/BaseLayout.vue'),
        children: [
            {
                path: '',
                component: () => import(/* webpackChunkName: "role" */ '@/views/system/role/module/authUser.vue'),
                meta: {
                    title: '角色授权用户',
                    icon: 'user'
                }
            }
        ],
    },
  // 前台路由
  ...frontendRoutes
];

export default constantRoutes;