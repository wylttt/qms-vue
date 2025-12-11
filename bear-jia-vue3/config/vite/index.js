/**
 * Vite 配置入口
 */
import { loadEnv } from 'vite';
import { getAlias, getServerConfig, getDefineConfig, getOptimizeDeps } from './base.js';
import { getBuildConfig } from './build.js';
import { getPlugins } from './plugins.js';

/**
 * 获取完整的 Vite 配置
 */
export function getViteConfig({ mode }) {
  const isProd = mode === 'prod';
  const cwd = process.cwd();
  const env = loadEnv(mode, cwd);
  const dirname = cwd;
  
  return {
    plugins: getPlugins(isProd),
    resolve: {
      alias: getAlias(dirname)
    },
    base: env.VITE_BASE_URL,
    server: getServerConfig(mode, cwd),
    build: getBuildConfig(isProd),
    optimizeDeps: getOptimizeDeps(),
    define: getDefineConfig(),
  };
}