/**
 * 前台路由配置
 */

const frontendRoutes = [
  {
    path: '/frontend',
    name: 'Frontend',
    component: () => import('@/layout/FrontendLayout.vue'),
    redirect: '/frontend/home',
    children: [
      {
        path: 'home',
        name: 'FrontendHome',
        component: () => import('@/views/frontend/Home.vue'),
        meta: {
          title: '首页',
          keepAlive: true
        }
      },
      {
        path: 'about',
        name: 'FrontendAbout',
        component: () => import('@/views/frontend/About.vue'),
        meta: {
          title: '关于我们',
          keepAlive: false
        }
      },
      // {
      //   path: 'products',
      //   name: 'FrontendProducts',
      //   component: () => import('@/views/frontend/Products.vue'),
      //   meta: {
      //     title: '产品中心',
      //     keepAlive: true
      //   }
      // },
      // {
      //   path: 'products/:id',
      //   name: 'FrontendProductDetail',
      //   component: () => import('@/views/frontend/ProductDetail.vue'),
      //   meta: {
      //     title: '产品详情',
      //     keepAlive: false
      //   }
      // },
      // {
      //   path: 'news',
      //   name: 'FrontendNews',
      //   component: () => import('@/views/frontend/News.vue'),
      //   meta: {
      //     title: '新闻资讯',
      //     keepAlive: true
      //   }
      // },
      // {
      //   path: 'news/:id',
      //   name: 'FrontendNewsDetail',
      //   component: () => import('@/views/frontend/NewsDetail.vue'),
      //   meta: {
      //     title: '新闻详情',
      //     keepAlive: false
      //   }
      // },
      {
        path: 'contact',
        name: 'FrontendContact',
        component: () => import('@/views/frontend/Contact.vue'),
        meta: {
          title: '联系我们',
          keepAlive: false
        }
      }
    ]
  }
];

export default frontendRoutes;
