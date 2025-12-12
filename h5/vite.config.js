import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    uni(),
  ],
  
  // 开发服务器配置
  server: {
    // 启用热模块替换（HMR）
    hmr: true,
    // 监听所有地址，方便真机调试
    host: '0.0.0.0',
    // 端口号
    port: 5173,
    // 自动打开浏览器
    open: false,
    // 代理配置（如果有后端接口需求）
    // proxy: {
    //   '/api': {
    //     target: 'http://localhost:8080',
    //     changeOrigin: true,
    //     rewrite: (path) => path.replace(/^\/api/, '')
    //   }
    // }
  },
  
  // 优化配置
  optimizeDeps: {
    // 排除不需要预构建的依赖
    exclude: ['vue-demi']
  },
  
  // 构建配置
  build: {
    // 小程序需要的特殊配置
    target: 'es2015',
    // 清除console
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: false, // 开发时保留console
        drop_debugger: true
      }
    }
  }
})
