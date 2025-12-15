<template>
  <view class="task-progress">
    <!-- 顶部统计卡片 -->
    <view class="stats-card">
      <view class="stats-header">
        <view class="surveyor-info">
          <text class="surveyor-name">{{ surveyorInfo.surveyorName || '调查员' }}</text>
          <text class="surveyor-area">{{ surveyorInfo.areaName || '' }}</text>
        </view>
        <view class="logout-btn" @click="handleLogout">
          <text>退出</text>
        </view>
      </view>

      <view class="stats-grid">
        <view class="stat-item">
          <text class="stat-value">{{ taskStats.totalTasks || 0 }}</text>
          <text class="stat-label">分配任务</text>
        </view>
        <view class="stat-item">
          <text class="stat-value completed">{{ taskStats.completedTasks || 0 }}</text>
          <text class="stat-label">已完成</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ taskStats.totalResidents || 0 }}</text>
          <text class="stat-label">录入居民</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ taskStats.completionRate || 0 }}%</text>
          <text class="stat-label">完成率</text>
        </view>
      </view>
    </view>

    <!-- 快捷操作 -->
    <view class="quick-actions">
      <view class="action-btn" @click="gotoAssist">
        <text class="action-icon">➕</text>
        <text class="action-text">协助录入</text>
      </view>
      <view class="action-btn" @click="gotoResidentList">
        <text class="action-icon">📋</text>
        <text class="action-text">居民列表</text>
      </view>
    </view>

    <!-- 任务列表 -->
    <view class="task-section">
      <view class="section-header">
        <text class="section-title">我的任务</text>
        <view class="filter-tabs">
          <view 
            v-for="(tab, index) in tabs"
            :key="index"
            class="filter-tab"
            :class="{ active: currentTab === index }"
            @click="switchTab(index)"
          >
            <text>{{ tab.name }}</text>
          </view>
        </view>
      </view>

      <scroll-view 
        scroll-y 
        class="task-list"
        @scrolltolower="loadMore"
        refresher-enabled
        :refresher-triggered="refreshing"
        @refresherrefresh="onRefresh"
      >
        <!-- 任务卡片 -->
        <view 
          v-for="task in taskList"
          :key="task.taskId"
          class="task-card"
          @click="gotoTaskDetail(task)"
        >
          <view class="task-header">
            <view class="task-title">{{ task.taskName }}</view>
            <view class="task-status" :class="'status-' + task.status">
              <text>{{ getStatusText(task.status) }}</text>
            </view>
          </view>

          <view class="task-info">
            <view class="info-row">
              <text class="info-label">目标人群：</text>
              <text class="info-value">{{ task.targetPopulation || '-' }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">目标数量：</text>
              <text class="info-value">{{ task.targetCount || 0 }} 人</text>
            </view>
            <view class="info-row">
              <text class="info-label">已完成：</text>
              <text class="info-value highlight">{{ task.completedCount || 0 }} 人</text>
            </view>
            <view class="info-row">
              <text class="info-label">截止日期：</text>
              <text class="info-value">{{ formatDate(task.endDate) }}</text>
            </view>
          </view>

          <!-- 进度条 -->
          <view class="progress-bar">
            <view class="progress-label">
              <text>完成进度</text>
              <text class="progress-percent">{{ calculateProgress(task) }}%</text>
            </view>
            <view class="progress-track">
              <view 
                class="progress-fill"
                :style="{ width: calculateProgress(task) + '%' }"
              ></view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="taskList.length === 0 && !loading" class="empty-state">
          <text class="empty-icon">📭</text>
          <text class="empty-text">暂无任务</text>
        </view>

        <!-- 加载状态 -->
        <view v-if="loading" class="loading-state">
          <text class="loading-text">加载中...</text>
        </view>

        <!-- 加载完成提示 -->
        <view v-if="noMore && taskList.length > 0" class="no-more">
          <text class="no-more-text">没有更多了</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import { getSurveyorTasks, getSurveyorStats } from '@/api/gc.js';

export default {
  data() {
    return {
      // 调查员信息
      surveyorInfo: {},

      // 任务统计
      taskStats: {
        totalTasks: 0,
        completedTasks: 0,
        totalResidents: 0,
        completionRate: 0
      },

      // 标签页
      tabs: [
        { name: '全部', status: null },
        { name: '进行中', status: 1 },
        { name: '已完成', status: 2 }
      ],
      currentTab: 0,

      // 任务列表
      taskList: [],

      // 分页参数
      pageNum: 1,
      pageSize: 10,
      total: 0,

      // 状态
      loading: false,
      refreshing: false,
      noMore: false
    };
  },

  onLoad() {
    // 获取调查员信息
    const surveyorInfo = uni.getStorageSync('surveyorInfo');
    if (surveyorInfo) {
      this.surveyorInfo = surveyorInfo;
      this.loadStats();
      this.loadTasks();
    } else {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/surveyor/login'
        });
      }, 1500);
    }
  },

  onShow() {
    // 每次显示页面时刷新数据
    if (this.surveyorInfo.surveyorId) {
      this.loadStats();
      this.onRefresh();
    }
  },

  methods: {
    // 加载统计数据
    async loadStats() {
      try {
        const res = await getSurveyorStats(this.surveyorInfo.surveyorId);
        if (res.code === 200 && res.data) {
          this.taskStats = res.data;
        }
      } catch (error) {
        console.error('加载统计数据失败:', error);
      }
    },

    // 加载任务列表
    async loadTasks() {
      if (this.loading || this.noMore) return;

      this.loading = true;

      try {
        const params = {
          surveyorId: this.surveyorInfo.surveyorId,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };

        // 根据标签页状态筛选
        if (this.tabs[this.currentTab].status !== null) {
          params.status = this.tabs[this.currentTab].status;
        }

        const res = await getSurveyorTasks(params);

        if (res.code === 200) {
          const newList = res.rows || [];
          
          if (this.pageNum === 1) {
            this.taskList = newList;
          } else {
            this.taskList = [...this.taskList, ...newList];
          }

          this.total = res.total || 0;

          // 判断是否还有更多数据
          if (this.taskList.length >= this.total) {
            this.noMore = true;
          }
        }
      } catch (error) {
        console.error('加载任务列表失败:', error);
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        });
      } finally {
        this.loading = false;
        this.refreshing = false;
      }
    },

    // 切换标签页
    switchTab(index) {
      this.currentTab = index;
      this.pageNum = 1;
      this.taskList = [];
      this.noMore = false;
      this.loadTasks();
    },

    // 加载更多
    loadMore() {
      if (!this.loading && !this.noMore) {
        this.pageNum++;
        this.loadTasks();
      }
    },

    // 下拉刷新
    onRefresh() {
      this.refreshing = true;
      this.pageNum = 1;
      this.noMore = false;
      this.loadTasks();
    },

    // 跳转到协助录入
    gotoAssist() {
      uni.navigateTo({
        url: '/pages/surveyor/assist'
      });
    },

    // 跳转到居民列表
    gotoResidentList() {
      uni.navigateTo({
        url: '/pages/surveyor/resident-list'
      });
    },

    // 跳转到任务详情
    gotoTaskDetail(task) {
      uni.navigateTo({
        url: `/pages/surveyor/task-detail?taskId=${task.taskId}`
      });
    },

    // 退出登录
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            // 清除缓存
            uni.removeStorageSync('token');
            uni.removeStorageSync('surveyorInfo');
            uni.removeStorageSync('userType');
            
            uni.showToast({
              title: '已退出登录',
              icon: 'success'
            });
            
            // 跳转到调查员登录页
            setTimeout(() => {
              uni.redirectTo({
                url: '/pages/surveyor/login'
              });
            }, 1000);
          }
        }
      });
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '未开始',
        1: '进行中',
        2: '已完成',
        3: '已关闭'
      };
      return statusMap[status] || '未知';
    },

    // 计算进度百分比
    calculateProgress(task) {
      if (!task.targetCount || task.targetCount === 0) return 0;
      const progress = Math.round((task.completedCount / task.targetCount) * 100);
      return Math.min(progress, 100);
    },

    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '-';
      
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      
      return `${year}-${month}-${day}`;
    }
  }
};
</script>

<style scoped>
.task-progress {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 统计卡片 */
.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 30rpx;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.surveyor-info {
  flex: 1;
}

.surveyor-name {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8rpx;
}

.surveyor-area {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.logout-btn {
  padding: 8rpx 20rpx;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
  color: #fff;
  font-size: 24rpx;
}

.stats-grid {
  display: flex;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 12rpx;
  padding: 30rpx 20rpx;
}

.stat-item {
  flex: 1;
  text-align: center;
  border-right: 1rpx solid rgba(255, 255, 255, 0.2);
}

.stat-item:last-child {
  border-right: none;
}

.stat-value {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8rpx;
}

.stat-value.completed {
  color: #52c41a;
}

.stat-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  gap: 20rpx;
  padding: 30rpx;
}

.action-btn {
  flex: 1;
  height: 100rpx;
  background-color: #fff;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.action-icon {
  font-size: 40rpx;
  margin-bottom: 8rpx;
}

.action-text {
  font-size: 26rpx;
  color: #333;
}

/* 任务区域 */
.task-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.section-header {
  background-color: #fff;
  padding: 20rpx 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.filter-tabs {
  display: flex;
  gap: 20rpx;
}

.filter-tab {
  padding: 10rpx 24rpx;
  background-color: #f5f5f5;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #666;
}

.filter-tab.active {
  background-color: #1890ff;
  color: #fff;
}

/* 任务列表 */
.task-list {
  flex: 1;
  padding: 20rpx 30rpx;
}

.task-card {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.task-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.task-status {
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  font-size: 24rpx;
}

.status-0 {
  background-color: #f0f0f0;
  color: #999;
}

.status-1 {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-2 {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-3 {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.task-info {
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  margin-bottom: 12rpx;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 26rpx;
  color: #999;
  width: 160rpx;
}

.info-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.info-value.highlight {
  color: #1890ff;
  font-weight: bold;
}

/* 进度条 */
.progress-bar {
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
  font-size: 24rpx;
  color: #666;
}

.progress-percent {
  color: #1890ff;
  font-weight: bold;
}

.progress-track {
  height: 12rpx;
  background-color: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1890ff 0%, #52c41a 100%);
  border-radius: 6rpx;
  transition: width 0.3s ease;
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
