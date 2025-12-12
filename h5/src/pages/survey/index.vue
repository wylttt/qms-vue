<template>
  <view class="survey-page">
    <!-- 顶部导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @click="goBack">
          <text class="back-arrow">←</text>
        </view>
        <view class="navbar-title">濂溪区胃癌筛查</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 页面内容 -->
    <view class="page-content">
      <!-- 顶部卡片 -->
      <view class="top-card">
        <view class="card-icon">
          <image src="/static/icon-medical-bag.png" mode="aspectFit"></image>
        </view>
        <view class="card-title">免费筛查 关爱健康</view>
        <view class="card-subtitle">九江市濂溪区政府民生项目</view>
      </view>

      <!-- 项目介绍 -->
      <view class="intro-section">
        <view class="section-title">项目介绍</view>
        <view class="intro-text">
          濂溪区政府为45-75岁居民提供免费胃癌筛查服务，通过科学问卷评估风险，早期发现潜在健康隐患。
        </view>

        <!-- 优势列表 -->
        <view class="advantage-list">
          <view class="advantage-item">
            <image class="check-icon" src="/static/icon-check-green.png" mode="aspectFit"></image>
            <text class="advantage-text">早发现早治疗，提高治愈率</text>
          </view>
          <view class="advantage-item">
            <image class="check-icon" src="/static/icon-check-green.png" mode="aspectFit"></image>
            <text class="advantage-text">专业医疗团队评估风险等级</text>
          </view>
          <view class="advantage-item">
            <image class="check-icon" src="/static/icon-check-green.png" mode="aspectFit"></image>
            <text class="advantage-text">政府补贴，全程免费服务</text>
          </view>
        </view>
      </view>

      <!-- 开始按钮 -->
      <view class="action-button" @click="startSurvey">
        <text>开始风险评估</text>
      </view>

      <!-- 底部版权信息 -->
      <view class="footer-info">
        <text class="footer-text">九江市濂溪区卫生健康局监制</text>
        <text class="footer-text">2025年12月更新</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      statusBarHeight: 0
    }
  },
  onLoad() {
    // 获取系统状态栏高度
    const systemInfo = uni.getSystemInfoSync();
    this.statusBarHeight = systemInfo.statusBarHeight || 0;
  },
  methods: {
    goBack() {
      uni.navigateBack();
    },
    startSurvey() {
      // TODO: 跳转到基本信息填写页面
      uni.navigateTo({
        url: '/pages/survey/basic'
      });
    }
  }
}
</script>

<style scoped>
.survey-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1E88E5 0%, #1976D2 50%, #f5f7fa 50%, #f5f7fa 100%);
}

/* 自定义导航栏 */
.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: #1E88E5;
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 30rpx;
}

.navbar-left {
  width: 80rpx;
  display: flex;
  align-items: center;
}

.back-arrow {
  font-size: 40rpx;
  color: #ffffff;
}

.navbar-title {
  flex: 1;
  text-align: center;
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
}

.navbar-right {
  width: 80rpx;
}

/* 页面内容 */
.page-content {
  padding-top: calc(88rpx + var(--status-bar-height));
  padding-bottom: 40rpx;
}

/* 顶部卡片 */
.top-card {
  margin: 30rpx;
  padding: 50rpx 30rpx;
  background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
  border-radius: 20rpx;
  text-align: center;
}

.card-icon {
  width: 160rpx;
  height: 160rpx;
  margin: 0 auto 30rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-icon image {
  width: 120rpx;
  height: 120rpx;
}

.card-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #1565C0;
  margin-bottom: 16rpx;
}

.card-subtitle {
  font-size: 26rpx;
  color: #1976D2;
}

/* 项目介绍 */
.intro-section {
  margin: 30rpx;
  padding: 40rpx 30rpx;
  background: #ffffff;
  border-radius: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
}

.intro-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.8;
  margin-bottom: 30rpx;
}

/* 优势列表 */
.advantage-list {
  margin-top: 30rpx;
}

.advantage-item {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.advantage-item:last-child {
  margin-bottom: 0;
}

.check-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 16rpx;
}

.advantage-text {
  font-size: 28rpx;
  color: #333333;
}

/* 开始按钮 */
.action-button {
  margin: 30rpx;
  padding: 30rpx;
  background: linear-gradient(135deg, #1E88E5 0%, #1565C0 100%);
  border-radius: 50rpx;
  text-align: center;
  box-shadow: 0 8rpx 20rpx rgba(30, 136, 229, 0.3);
}

.action-button text {
  font-size: 32rpx;
  font-weight: bold;
  color: #ffffff;
}

.action-button:active {
  opacity: 0.9;
  transform: scale(0.98);
}

/* 底部版权信息 */
.footer-info {
  margin-top: 60rpx;
  padding: 20rpx;
  text-align: center;
}

.footer-text {
  display: block;
  font-size: 24rpx;
  color: #999999;
  line-height: 1.8;
}
</style>
