<template>
  <view class="page-container">
    <!-- 状态栏占位（安全区域适配） -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 网络状态提示 -->
    <view v-if="!isOnline" class="network-tip offline">
      <text class="tip-icon">⚠️</text>
      <text class="tip-text">网络未连接，当前为离线模式</text>
    </view>
    
    <!-- 未同步草稿提示 -->
    <view v-if="unsyncedCount > 0" class="network-tip unsynced" @click="handleSync">
      <text class="tip-icon">💾</text>
      <text class="tip-text">有 {{ unsyncedCount }} 个草稿待同步</text>
      <text class="tip-action">点击同步</text>
    </view>
    
    <!-- 顶部背景图 -->
    <view class="header-banner">
      <image class="banner-image" src="/static/header-banner-jiujiang.png" mode="widthFix"></image>
    </view>

    
    <!-- 功能菜单区域 -->
    <view class="menu-container">
      <view class="menu-grid">
        <view class="menu-item" @click="handleMenuClick('roster')">
          <view class="menu-icon">
            <image src="/static/icon-roster.png" mode="aspectFit"></image>
          </view>
          <text class="menu-text">人员名单</text>
        </view>
        
        <view class="menu-item" @click="handleMenuClick('survey')">
          <view class="menu-icon">
            <image src="/static/icon-survey.png" mode="aspectFit"></image>
          </view>
          <text class="menu-text">问卷调查表</text>
        </view>
        
        <view class="menu-item" @click="handleMenuClick('report')">
          <view class="menu-icon">
            <image src="/static/icon-report.png" mode="aspectFit"></image>
          </view>
          <text class="menu-text">报告查询</text>
        </view>
        
        <view class="menu-item" @click="handleMenuClick('service')">
          <view class="menu-icon">
            <image src="/static/icon-service.png" mode="aspectFit"></image>
          </view>
          <text class="menu-text">联系客服</text>
        </view>
      </view>

    </view>

    <!-- 说明文字区域 -->
    <view class="description-container">
      <view class="description-item">
        <text class="description-title">人员名单：</text>
        <text class="description-text">可以维护就诊人的基本信息（一个微信号允许添加多个身份证，不同微信号可以添加同一个身份证）。</text>
      </view>
      
      <view class="description-item">
        <text class="description-title">问卷调查表：</text>
        <text class="description-text">每个就诊人员抽血前，需要先填写问卷调查表，并出示问卷码给医务人员扫码录入，这是评判您适合做哪个检验项目的重要依据。</text>
      </view>
      
      <view class="description-item">
        <text class="description-title">报告查询：</text>
        <text class="description-text">等待出报告后，可在此处查询到您的报告。</text>
      </view>
      
      <view class="description-item">
        <text class="description-title">联系客服：</text>
        <text class="description-text">您有任何疑问都可咨询迪安客服。</text>
      </view>
    </view>
  </view>
</template>

<script>
import networkManager from '@/utils/networkManager.js';
import { getUnsyncedDrafts } from '@/api/gc.js';

export default {
  data() {
    return {
      statusBarHeight: 0,  // 状态栏高度
      isOnline: true,      // 网络状态
      unsyncedCount: 0     // 未同步草稿数量
    }
  },
  onLoad() {
    // 获取系统信息，适配刘海屏
    this.getSystemInfo();
    
    // 初始化网络状态
    this.initNetworkStatus();
    
    // 检查未同步草稿
    this.checkUnsyncedDrafts();
  },
  onShow() {
    // 每次显示页面时检查草稿
    this.checkUnsyncedDrafts();
  },
  onUnload() {
    // 页面卸载时移除监听器
    if (this.networkStatusListener) {
      networkManager.removeListener(this.networkStatusListener);
    }
  },
  methods: {
    // 获取系统信息（状态栏高度）
    getSystemInfo() {
      const systemInfo = uni.getSystemInfoSync();
      this.statusBarHeight = systemInfo.statusBarHeight || 0;
      console.log('状态栏高度:', this.statusBarHeight);
    },
    
    // 初始化网络状态
    initNetworkStatus() {
      // 获取当前网络状态
      const status = networkManager.getStatus();
      this.isOnline = status.isOnline;
      
      // 监听网络状态变化
      this.networkStatusListener = (status) => {
        this.isOnline = status.isOnline;
        
        // 网络恢复时，重新检查草稿
        if (status.isOnline) {
          this.checkUnsyncedDrafts();
        }
      };
      
      networkManager.addListener(this.networkStatusListener);
    },
    
    // 检查未同步草稿
    checkUnsyncedDrafts() {
      const drafts = getUnsyncedDrafts();
      this.unsyncedCount = drafts.length;
    },
    
    // 手动同步草稿
    async handleSync() {
      await networkManager.manualSync();
      // 同步后重新检查
      this.checkUnsyncedDrafts();
    },
    
    handleMenuClick(type) {
      switch(type) {
        case 'roster':
          uni.showToast({
            title: '跳转到人员名单',
            icon: 'none'
          });
          // TODO: 跳转到人员名单页面
          // uni.navigateTo({
          //   url: '/pages/roster/index'
          // });
          break;
        case 'survey':
          uni.navigateTo({
            url: '/pages/survey/index'
          });
          break;
        case 'report':
          uni.showToast({
            title: '跳转到报告查询',
            icon: 'none'
          });
          // TODO: 跳转到报告查询页面
          break;
        case 'service':
          uni.showToast({
            title: '联系客服',
            icon: 'none'
          });
          // TODO: 打开客服对话
          break;
      }
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 状态栏占位（刘海屏适配） */
.status-bar {
  width: 100%;
  background: transparent;
}

/* 网络状态提示 */
.network-tip {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  margin: 20rpx 30rpx;
  border-radius: 12rpx;
  font-size: 26rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.network-tip.offline {
  background-color: #fff7e6;
  border: 1rpx solid #ffa940;
}

.network-tip.unsynced {
  background-color: #e6f7ff;
  border: 1rpx solid #1890ff;
}

.tip-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.tip-text {
  flex: 1;
  color: #333;
}

.tip-action {
  color: #1890ff;
  font-weight: bold;
  margin-left: 20rpx;
}

/* 顶部背景图区域 */
.header-banner {
  width: 100%;
  line-height: 0;
}

.banner-image {
  width: 100%;
  display: block;
}

/* 功能菜单区域 */
.menu-container {
  position: relative;
  margin: 30rpx;
  padding: 40rpx;
  background: #ffffff;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40rpx;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15rpx;
}

.menu-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.menu-icon image {
  width: 70rpx;
  height: 70rpx;
}

.menu-item:active .menu-icon {
  transform: scale(0.95);
}

.menu-text {
  font-size: 24rpx;
  color: #333333;
  text-align: center;
  line-height: 1.2;
  white-space: nowrap;
  max-width: 150rpx;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 说明文字区域 */
.description-container {
  margin: 30rpx;
  padding: 40rpx;
  background: #ffffff;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.description-item {
  margin-bottom: 30rpx;
  line-height: 1.8;
}

.description-item:last-child {
  margin-bottom: 0;
}

.description-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
}

.description-text {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.8;
}
</style>
