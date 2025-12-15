<template>
  <view class="screening-result">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <text class="navbar-title">筛查结果</text>
    </view>

    <!-- 结果列表 -->
    <scroll-view 
      scroll-y 
      class="scroll-view"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 结果卡片 -->
      <view 
        v-for="item in resultList" 
        :key="item.resultId"
        class="result-card"
        @click="gotoDetail(item)"
      >
        <!-- 头部：日期和类型 -->
        <view class="card-header">
          <view class="result-type">
            <text class="type-icon">🔬</text>
            <text class="type-text">{{ getResultType(item.resultType) }}</text>
          </view>
          <view class="result-date">
            <text class="date-text">{{ formatDate(item.screeningDate) }}</text>
          </view>
        </view>

        <!-- 血液筛查结果 -->
        <view v-if="item.resultType === 0" class="result-content">
          <!-- 风险等级 -->
          <view class="risk-level" :class="'risk-' + item.riskLevel">
            <view class="risk-badge">
              <text class="badge-text">{{ getRiskLevelText(item.riskLevel) }}</text>
            </view>
          </view>

          <!-- 指标列表 -->
          <view class="indicators">
            <view class="indicator-item">
              <text class="indicator-label">PG I (胃蛋白酶原I)：</text>
              <text class="indicator-value" :class="{ abnormal: item.pg1Abnormal }">
                {{ item.pg1Value || '-' }} {{ item.pg1Unit || 'ng/mL' }}
              </text>
            </view>
            <view class="indicator-item">
              <text class="indicator-label">PG II (胃蛋白酶原II)：</text>
              <text class="indicator-value" :class="{ abnormal: item.pg2Abnormal }">
                {{ item.pg2Value || '-' }} {{ item.pg2Unit || 'ng/mL' }}
              </text>
            </view>
            <view class="indicator-item">
              <text class="indicator-label">PG I/II 比值：</text>
              <text class="indicator-value" :class="{ abnormal: item.pgRatioAbnormal }">
                {{ item.pgRatio || '-' }}
              </text>
            </view>
            <view class="indicator-item">
              <text class="indicator-label">G-17 (胃泌素-17)：</text>
              <text class="indicator-value" :class="{ abnormal: item.g17Abnormal }">
                {{ item.g17Value || '-' }} {{ item.g17Unit || 'pmol/L' }}
              </text>
            </view>
            <view class="indicator-item">
              <text class="indicator-label">HP (幽门螺杆菌)：</text>
              <text class="indicator-value" :class="{ abnormal: item.hpPositive }">
                {{ item.hpPositive ? '阳性 (+)' : '阴性 (-)' }}
              </text>
            </view>
          </view>

          <!-- 建议 -->
          <view v-if="item.suggestion" class="suggestion">
            <view class="suggestion-title">
              <text class="title-icon">💡</text>
              <text class="title-text">医生建议</text>
            </view>
            <view class="suggestion-content">
              <text class="content-text">{{ item.suggestion }}</text>
            </view>
          </view>
        </view>

        <!-- 胃镜检查结果 -->
        <view v-if="item.resultType === 1" class="result-content">
          <view class="gastroscopy-info">
            <view class="info-item">
              <text class="info-label">检查医院：</text>
              <text class="info-value">{{ item.hospital || '-' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">检查医生：</text>
              <text class="info-value">{{ item.doctor || '-' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">检查结果：</text>
              <text class="info-value">{{ item.diagnosis || '-' }}</text>
            </view>
          </view>

          <!-- 查看报告按钮 -->
          <view v-if="item.reportUrl" class="report-btn" @click.stop="viewReport(item.reportUrl)">
            <text>查看完整报告</text>
          </view>
        </view>

        <!-- 箭头图标 -->
        <view class="arrow-icon">
          <text>›</text>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="resultList.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无筛查结果</text>
        <text class="empty-desc">完成采血或检查后，结果将显示在这里</text>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading-state">
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 加载完成提示 -->
      <view v-if="noMore && resultList.length > 0" class="no-more">
        <text class="no-more-text">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getScreeningResults } from '@/api/gc.js';

export default {
  data() {
    return {
      // 结果列表
      resultList: [],

      // 分页参数
      pageNum: 1,
      pageSize: 10,
      total: 0,

      // 状态
      loading: false,
      refreshing: false,
      noMore: false,

      // 用户信息
      residentId: ''
    };
  },

  onLoad() {
    // 获取登录用户ID
    const userInfo = uni.getStorageSync('userInfo');
    if (userInfo && userInfo.residentId) {
      this.residentId = userInfo.residentId;
      this.loadResults();
    } else {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/login/login'
        });
      }, 1500);
    }
  },

  methods: {
    // 加载筛查结果
    async loadResults() {
      if (this.loading || this.noMore) return;

      this.loading = true;

      try {
        const params = {
          residentId: this.residentId,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };

        const res = await getScreeningResults(params);

        if (res.code === 200) {
          const newList = res.rows || [];
          
          if (this.pageNum === 1) {
            this.resultList = newList;
          } else {
            this.resultList = [...this.resultList, ...newList];
          }

          this.total = res.total || 0;

          // 判断是否还有更多数据
          if (this.resultList.length >= this.total) {
            this.noMore = true;
          }
        }
      } catch (error) {
        console.error('加载筛查结果失败:', error);
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        });
      } finally {
        this.loading = false;
        this.refreshing = false;
      }
    },

    // 加载更多
    loadMore() {
      if (!this.loading && !this.noMore) {
        this.pageNum++;
        this.loadResults();
      }
    },

    // 下拉刷新
    onRefresh() {
      this.refreshing = true;
      this.pageNum = 1;
      this.noMore = false;
      this.loadResults();
    },

    // 跳转到详情页面
    gotoDetail(item) {
      uni.navigateTo({
        url: `/pages/result/detail?resultId=${item.resultId}`
      });
    },

    // 查看报告
    viewReport(reportUrl) {
      if (!reportUrl) return;
      
      // 预览PDF或图片
      if (reportUrl.endsWith('.pdf')) {
        uni.downloadFile({
          url: reportUrl,
          success: (res) => {
            const filePath = res.tempFilePath;
            uni.openDocument({
              filePath: filePath,
              fileType: 'pdf'
            });
          }
        });
      } else {
        uni.previewImage({
          urls: [reportUrl],
          current: reportUrl
        });
      }
    },

    // 获取结果类型文本
    getResultType(type) {
      const typeMap = {
        0: '血液筛查',
        1: '胃镜检查'
      };
      return typeMap[type] || '未知';
    },

    // 获取风险等级文本
    getRiskLevelText(level) {
      const levelMap = {
        0: '低风险',
        1: '中风险',
        2: '高风险'
      };
      return levelMap[level] || '未评估';
    },

    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      
      return `${year}年${month}月${day}日`;
    }
  }
};
</script>

<style scoped>
.screening-result {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 导航栏 */
.navbar {
  background-color: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.navbar-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

/* 滚动视图 */
.scroll-view {
  height: calc(100vh - 100rpx);
  padding: 20rpx 0;
}

/* 结果卡片 */
.result-card {
  margin: 0 30rpx 20rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  position: relative;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.result-type {
  display: flex;
  align-items: center;
}

.type-icon {
  font-size: 32rpx;
  margin-right: 8rpx;
}

.type-text {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.result-date {
  font-size: 26rpx;
  color: #999;
}

/* 结果内容 */
.result-content {
  margin-bottom: 20rpx;
}

/* 风险等级 */
.risk-level {
  margin-bottom: 20rpx;
  text-align: center;
}

.risk-badge {
  display: inline-block;
  padding: 12rpx 32rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: bold;
}

.risk-0 .risk-badge {
  background-color: #f6ffed;
  color: #52c41a;
}

.risk-1 .risk-badge {
  background-color: #fff7e6;
  color: #fa8c16;
}

.risk-2 .risk-badge {
  background-color: #fff1f0;
  color: #ff4d4f;
}

/* 指标列表 */
.indicators {
  background-color: #fafafa;
  border-radius: 8rpx;
  padding: 20rpx;
}

.indicator-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.indicator-item:last-child {
  margin-bottom: 0;
}

.indicator-label {
  font-size: 26rpx;
  color: #666;
}

.indicator-value {
  font-size: 26rpx;
  color: #333;
  font-weight: bold;
}

.indicator-value.abnormal {
  color: #ff4d4f;
}

/* 建议 */
.suggestion {
  margin-top: 20rpx;
  padding: 20rpx;
  background-color: #e6f7ff;
  border-radius: 8rpx;
}

.suggestion-title {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.title-icon {
  font-size: 28rpx;
  margin-right: 8rpx;
}

.title-text {
  font-size: 28rpx;
  font-weight: bold;
  color: #1890ff;
}

.suggestion-content {
  padding-left: 36rpx;
}

.content-text {
  font-size: 26rpx;
  color: #666;
  line-height: 40rpx;
}

/* 胃镜信息 */
.gastroscopy-info {
  background-color: #fafafa;
  border-radius: 8rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.info-item {
  display: flex;
  margin-bottom: 12rpx;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 26rpx;
  color: #666;
  width: 160rpx;
}

.info-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

/* 报告按钮 */
.report-btn {
  height: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #1890ff;
  color: #fff;
  border-radius: 8rpx;
  font-size: 28rpx;
}

/* 箭头图标 */
.arrow-icon {
  position: absolute;
  right: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 48rpx;
  color: #ccc;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: #ccc;
}

/* 加载状态 */
.loading-state {
  padding: 40rpx 0;
  text-align: center;
}

.loading-text {
  font-size: 26rpx;
  color: #999;
}

/* 加载完成提示 */
.no-more {
  padding: 40rpx 0;
  text-align: center;
}

.no-more-text {
  font-size: 26rpx;
  color: #ccc;
}
</style>
