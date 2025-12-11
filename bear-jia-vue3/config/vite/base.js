/**
 * Vite 基础配置
 */
import path from 'path';
import { loadEnv } from 'vite';

/**
 * 获取别名配置
 */
export function getAlias(dirname) {
  return {
    '@': path.resolve(dirname, 'src'),
    '@components': path.resolve(dirname, 'src/components'),
    '@utils': path.resolve(dirname, 'src/utils'),
    '@api': path.resolve(dirname, 'src/api'),
    '@views': path.resolve(dirname, 'src/views'),
    '@stores': path.resolve(dirname, 'src/stores'),
    '@assets': path.resolve(dirname, 'src/assets'),
    '@style': path.resolve(dirname, 'src/style')
  };
}

/**
 * 获取服务器配置
 */
export function getServerConfig(mode, cwd) {
  const env = loadEnv(mode, cwd);
  
  return {
    host: '0.0.0.0',
    port: parseInt(env.VITE_PORT || 3000),
    open: true,
    proxy: {
      '/dev-api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/dev-api/, ''),
        timeout: 30000,
        ws: true,
        retry: 0,
        configure: (proxy, options) => {
          proxy.on('error', (err) => {
            console.log('代理错误', err);
          });
          proxy.on('proxyReq', (proxyReq, req) => {
            // 修改请求头，禁用缓存
            proxyReq.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate');
            proxyReq.setHeader('Pragma', 'no-cache');
            proxyReq.setHeader('Expires', '0');
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            // 修改响应头，禁用缓存
            proxyRes.headers['cache-control'] = 'no-cache, no-store, must-revalidate';
            proxyRes.headers['pragma'] = 'no-cache';
            proxyRes.headers['expires'] = '0';
          });
        }
      },
    },
  };
}

/**
 * 获取全局常量定义
 */
export function getDefineConfig() {
  return {
    __VUE_PROD_DEVTOOLS__: false,
    __VUE_OPTIONS_API__: true,
    __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false
  };
}

/**
 * 获取依赖优化配置
 */
export function getOptimizeDeps() {
  return {
    include: [
      'vue',
      'vue-router',
      'pinia',
      'dayjs',
      'axios',
      'js-cookie',
      'nprogress'
    ],
    exclude: [
      '@wangeditor/editor',
      '@wangeditor/editor-for-vue',
      '@form-create/antd-designer',
      '@form-create/ant-design-vue',
      'ant-design-vue',
      '@ant-design/icons-vue'
    ]
  };
}