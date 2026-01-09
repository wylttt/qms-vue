/**
 * 网络状态管理工具
 * 用于处理离线填写、网络监听和自动同步功能
 */

import { 
  checkNetworkStatus, 
  onNetworkStatusChange, 
  syncAllDrafts,
  getUnsyncedDrafts 
} from '@/api/gc.js';

class NetworkManager {
  constructor() {
    this.isOnline = true;
    this.networkType = 'unknown';
    this.listeners = [];
    this.syncInProgress = false;
    
    // 初始化时检查网络状态
    this.init();
  }

  /**
   * 初始化网络状态管理器
   */
  async init() {
    // 获取当前网络状态
    const status = await checkNetworkStatus();
    this.isOnline = status.isOnline;
    this.networkType = status.networkType;

    // 监听网络状态变化
    onNetworkStatusChange((status) => {
      const wasOnline = this.isOnline;
      this.isOnline = status.isOnline;
      this.networkType = status.networkType;

      console.log('网络状态变化:', {
        isOnline: this.isOnline,
        networkType: this.networkType
      });

      // 通知所有监听器
      this.notifyListeners(status);

      // 如果从离线变为在线，尝试同步草稿
      if (!wasOnline && this.isOnline) {
        this.autoSync();
      }
    });

    // 如果当前在线，检查是否有未同步的草稿
    if (this.isOnline) {
      this.checkUnsyncedDrafts();
    }
  }

  /**
   * 添加网络状态监听器
   * @param {Function} callback - 回调函数
   */
  addListener(callback) {
    if (typeof callback === 'function') {
      this.listeners.push(callback);
    }
  }

  /**
   * 移除网络状态监听器
   * @param {Function} callback - 回调函数
   */
  removeListener(callback) {
    const index = this.listeners.indexOf(callback);
    if (index > -1) {
      this.listeners.splice(index, 1);
    }
  }

  /**
   * 通知所有监听器
   * @param {Object} status - 网络状态
   */
  notifyListeners(status) {
    this.listeners.forEach(callback => {
      try {
        callback(status);
      } catch (error) {
        console.error('监听器执行错误:', error);
      }
    });
  }

  /**
   * 获取当前网络状态
   */
  getStatus() {
    return {
      isOnline: this.isOnline,
      networkType: this.networkType
    };
  }

  /**
   * 检查未同步的草稿
   */
  async checkUnsyncedDrafts() {
    const drafts = getUnsyncedDrafts();
    if (drafts.length > 0) {
      console.log(`发现 ${drafts.length} 个未同步的草稿`);
      
      // 可以在这里显示提示
      uni.showToast({
        title: `有 ${drafts.length} 个草稿待同步`,
        icon: 'none',
        duration: 2000
      });
    }
  }

  /**
   * 自动同步草稿
   */
  async autoSync() {
    if (this.syncInProgress) {
      console.log('同步正在进行中，跳过');
      return;
    }

    const drafts = getUnsyncedDrafts();
    if (drafts.length === 0) {
      console.log('没有需要同步的草稿');
      return;
    }

    console.log('检测到网络恢复，开始自动同步草稿...');
    
    this.syncInProgress = true;

    try {
      uni.showLoading({ title: '同步中...' });

      const result = await syncAllDrafts();

      uni.hideLoading();

      if (result.success) {
        uni.showToast({
          title: result.message || '同步成功',
          icon: 'success',
          duration: 2000
        });
      } else {
        uni.showToast({
          title: result.message || '同步失败',
          icon: 'none',
          duration: 2000
        });
      }
    } catch (error) {
      uni.hideLoading();
      console.error('自动同步失败:', error);
      uni.showToast({
        title: '同步失败，请稍后手动同步',
        icon: 'none',
        duration: 2000
      });
    } finally {
      this.syncInProgress = false;
    }
  }

  /**
   * 手动触发同步
   */
  async manualSync() {
    if (!this.isOnline) {
      uni.showToast({
        title: '网络未连接，无法同步',
        icon: 'none'
      });
      return;
    }

    if (this.syncInProgress) {
      uni.showToast({
        title: '同步正在进行中',
        icon: 'none'
      });
      return;
    }

    await this.autoSync();
  }
}

// 创建单例
const networkManager = new NetworkManager();

export default networkManager;
