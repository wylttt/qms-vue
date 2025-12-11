/**
 * Vite 打包配置
 */

/**
 * 获取打包基础配置
 */
export function getBuildBase(isProd) {
  return {
    target: 'es2015',
    outDir: 'dist',
    emptyOutDir: true,
    sourcemap: isProd ? false : 'inline',
    chunkSizeWarningLimit: 1500,
    cssCodeSplit: true,
    cssMinify: isProd,
    assetsInlineLimit: 4096,
    reportCompressedSize: false
  };
}

/**
 * 获取压缩配置
 */
export function getTerserOptions(isProd) {
  if (!isProd) return undefined;
  
  return {
    compress: {
      drop_console: true,
      drop_debugger: true,
      pure_funcs: ['console.log', 'console.info', 'console.debug', 'console.trace'],
      passes: 2,
    },
    format: {
      comments: false,
    }
  };
}

/**
 * 获取静态资源文件名配置
 */
export function getAssetFileNames(assetInfo) {
  const info = assetInfo.name.split('.');
  let extType = info[info.length - 1];
  
  if (/\.(png|jpe?g|gif|svg|webp|ico)$/i.test(assetInfo.name)) {
    extType = 'img';
  } else if (/\.(woff2?|eot|ttf|otf)$/i.test(assetInfo.name)) {
    extType = 'fonts';
  } else if (/\.css$/i.test(assetInfo.name)) {
    extType = 'css';
  }
  
  return `${extType}/[name]-[hash].[ext]`;
}

/**
 * 手动代码分割策略 - 优化版
 */
export function manualChunks(id) {
  // 将 node_modules 中的代码分割为不同的 chunk
  if (id.includes('node_modules')) {
    // 获取包名
    const packageName = id.split('node_modules/')[1].split('/')[0].replace('@', '');
    
    // 核心Vue生态库 - 最高优先级，确保基础框架独立
    if (id.includes('vue/') && !id.includes('vue-router') && !id.includes('vue-request') && !id.includes('ant-design-vue') && !id.includes('@ant-design')) {
      return 'vue-core';
    }
    if (id.includes('vue-router')) {
      return 'vue-router';
    }
    if (id.includes('pinia')) {
      return 'pinia';
    }
    if (id.includes('@vueuse')) {
      return 'vueuse';
    }
    
    // Ant Design Vue 放在一个 chunk 中，避免模块之间相互引用导致运行时报错
    if (id.includes('ant-design-vue')) {
      return 'ant-design-vue';
    }
    if (id.includes('@ant-design/icons-vue')) {
      return 'antd-icons';
    }
    
    // 表单设计器
    if (id.includes('@form-create/ant-design-vue')) {
      return 'form-create-antd';
    }
    if (id.includes('@form-create/antd-designer')) {
      return 'form-designer';
    }
    if (id.includes('@form-create')) {
      return 'form-create-core';
    }
    
    // 编辑器相关
    if (id.includes('@wangeditor/editor-for-vue')) {
      return 'wangeditor-vue';
    }
    if (id.includes('@wangeditor')) {
      return 'wangeditor-core';
    }
    if (id.includes('highlight.js')) {
      return 'highlight';
    }
    if (id.includes('vue3-highlightjs')) {
      return 'vue-highlight';
    }
    
    // 图表库
    if (id.includes('echarts')) {
      if (id.includes('echarts/core')) {
        return 'echarts-core';
      }
      if (id.includes('echarts/charts')) {
        return 'echarts-charts';
      }
      if (id.includes('echarts/components')) {
        return 'echarts-components';
      }
      if (id.includes('echarts/renderers')) {
        return 'echarts-renderers';
      }
      return 'echarts-base';
    }
    
    // 工具库
    if (id.includes('axios')) {
      return 'axios';
    }
    if (id.includes('dayjs')) {
      return 'dayjs';
    }
    if (id.includes('js-cookie')) {
      return 'js-cookie';
    }
    if (id.includes('nprogress')) {
      return 'nprogress';
    }
    
    // 其他工具库
    if (id.includes('sortablejs')) {
      return 'sortable';
    }
    if (id.includes('file-saver')) {
      return 'file-saver';
    }
    if (id.includes('particles.js')) {
      return 'particles';
    }
    if (id.includes('@vueuse')) {
      return 'vueuse';
    }
    if (id.includes('vue-request')) {
      return 'vue-request';
    }
    
    // polyfills
    if (id.includes('core-js')) {
      return 'polyfills';
    }
    
    return 'vendor-misc';
  }
  
  // 项目内部代码分割 - 优化版
  if (id.includes('src/')) {
    // 布局和核心组件 - 优先级高，单独打包
    if (id.includes('src/layout/')) {
      return 'layout';
    }
    if (id.includes('src/components/layout')) {
      return 'components-layout';
    }
    if (id.includes('src/components/common')) {
      return 'components-common';
    }
    if (id.includes('src/components/')) {
      return 'components-other';
    }
    
    // 路由相关 - 按模块分割
    if (id.includes('src/router/')) {
      return 'router';
    }
    
    // 状态管理
    if (id.includes('src/stores/')) {
      return 'stores';
    }
    
    // API 按业务模块分割
    if (id.includes('src/api/system/')) {
      return 'api-system';
    }
    if (id.includes('src/api/monitor/')) {
      return 'api-monitor';
    }
    if (id.includes('src/api/tool/')) {
      return 'api-tool';
    }
    if (id.includes('src/api/')) {
      return 'api-common';
    }
    
    // 业务页面按模块分割
    if (id.includes('src/views/system/')) {
      return 'views-system';
    }
    if (id.includes('src/views/monitor/')) {
      return 'views-monitor';
    }
    if (id.includes('src/views/tool/')) {
      return 'views-tool';
    }
    if (id.includes('src/views/frontend/')) {
      return 'views-frontend';
    }
    if (id.includes('src/views/')) {
      return 'views-common';
    }
    
    // 工具和配置
    if (id.includes('src/utils/')) {
      return 'utils';
    }
    if (id.includes('src/directive/')) {
      return 'directive';
    }
    if (id.includes('src/config/')) {
      return 'config';
    }
    if (id.includes('src/plugins/')) {
      return 'plugins';
    }
    
    // 静态资源
    if (id.includes('src/assets/')) {
      return 'assets';
    }
  }
}

/**
 * 获取完整的打包配置
 */
export function getBuildConfig(isProd) {
  const baseConfig = getBuildBase(isProd);
  
  return {
    ...baseConfig,
    minify: isProd ? 'terser' : false,
    terserOptions: getTerserOptions(isProd),
    rollupOptions: {
      output: {
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: getAssetFileNames,
        manualChunks
      },
      external: isProd ? [] : [],
    },
    optimizeDeps: {
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
    }
  };
}
