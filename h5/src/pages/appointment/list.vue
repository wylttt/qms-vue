<template>
  <view class="appointment-list">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <text class="navbar-title">我的预约</text>
    </view>

    <!-- 标签页 -->
    <view class="tabs">
      <view 
        v-for="(tab, index) in tabs" 
        :key="index"
        class="tab-item"
        :class="{ active: currentTab === index }"
        @click="switchTab(index)"
      >
        <text class="tab-text">{{ tab.name }}</text>
        <view v-if="currentTab === index" class="tab-line"></view>
      </view>
    </view>

    <!-- 预约列表 -->
    <view class="list-container">
      <scroll-view 
        scroll-y 
        class="scroll-view"
        @scrolltolower="loadMore"
        refresher-enabled
        :refresher-triggered="refreshing"
        @refresherrefresh="onRefresh"
      >
        <!-- 预约卡片 -->
        <view 
          v-for="item in appointmentList" 
          :key="item.appointmentId"
          class="appointment-card"
        >
          <!-- 状态标签 -->
          <view class="status-badge" :class="'status-' + item.status">
            <text class="status-text">{{ getStatusText(item.status) }}</text>
          </view>

          <!-- 采血点信息 -->
          <view class="site-info">
            <view class="site-name">
              <text class="icon">📍</text>
              <text class="name">{{ item.siteName }}</text>
            </view>
            <view class="site-address">{{ item.siteAddress }}</view>
          </view>

          <!-- 预约时间 -->
          <view class="time-info">
            <view class="info-row">
              <text class="label">预约时间：</text>
              <text class="value">{{ formatTime(item.appointmentTime) }}</text>
            </view>
            <view class="info-row">
              <text class="label">联系电话：</text>
              <text class="value">{{ item.sitePhone }}</text>
            </view>
          </view>

          <!-- 操作按钮 -->
          <view class="action-bar">
            <view 
              v-if="item.status === 0"
              class="btn btn-cancel"
              @click="handleCancel(item)"
            >
              <text>取消预约</text>
            </view>
            <view 
              class="btn btn-primary"
              @click="handleCall(item.sitePhone)"
            >
              <text>联系采血点</text>
            </view>
          </view>

          <!-- 预约编号 -->
          <view class="appointment-no">
            <text class="no-text">预约编号：{{ item.appointmentId }}</text>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="appointmentList.length === 0 && !loading" class="empty-state">
          <text class="empty-icon">📋</text>
          <text class="empty-text">暂无预约记录</text>
          <view class="btn btn-primary" @click="gotoCreate">
            <text>立即预约</text>
          </view>
        </view>

        <!-- 加载状态 -->
        <view v-if="loading" class="loading-state">
          <text class="loading-text">加载中...</text>
        </view>

        <!-- 加载完成提示 -->
        <view v-if="noMore && appointmentList.length > 0" class="no-more">
          <text class="no-more-text">没有更多了</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import { getAppointmentList, cancelAppointment } from '@/api/gc.js';

export default {
  data() {
    return {
      // 标签页
      tabs: [
        { name: '全部', status: null },
        { name: '已预约', status: 0 },
        { name: '已完成', status: 1 },
        { name: '已取消', status: 2 }
      ],
      currentTab: 0,

      // 预约列表
      appointmentList: [],

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
      this.loadAppointments();
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
    // 切换标签页
    switchTab(index) {
      this.currentTab = index;
      this.pageNum = 1;
      this.appointmentList = [];
      this.noMore = false;
      this.loadAppointments();
    },

    // 加载预约列表
    async loadAppointments() {
      if (this.loading || this.noMore) return;

      this.loading = true;

      try {
        const params = {
          residentId: this.residentId,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };

        // 根据标签页状态筛选
        if (this.tabs[this.currentTab].status !== null) {
          params.status = this.tabs[this.currentTab].status;
        }

        const res = await getAppointmentList(params);

        if (res.code === 200) {
          const newList = res.rows || [];
          
          if (this.pageNum === 1) {
            this.appointmentList = newList;
          } else {
            this.appointmentList = [...this.appointmentList, ...newList];
          }

          this.total = res.total || 0;

          // 判断是否还有更多数据
          if (this.appointmentList.length >= this.total) {
            this.noMore = true;
          }
        }
      } catch (error) {
        console.error('加载预约列表失败:', error);
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
        this.loadAppointments();
      }
    },

    // 下拉刷新
    onRefresh() {
      this.refreshing = true;
      this.pageNum = 1;
      this.noMore = false;
      this.loadAppointments();
    },

    // 取消预约
    handleCancel(item) {
      // 检查是否可以取消（提前24小时）
      const appointmentTime = new Date(item.appointmentTime).getTime();
      const now = new Date().getTime();
      const diffHours = (appointmentTime - now) / (1000 * 60 * 60);

      if (diffHours < 24) {
        uni.showToast({
          title: '预约时间前24小时内不可取消',
          icon: 'none',
          duration: 2000
        });
        return;
      }

      uni.showModal({
        title: '提示',
        content: '确定要取消此预约吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              uni.showLoading({ title: '取消中...' });
              
              const result = await cancelAppointment(item.appointmentId);
              
              uni.hideLoading();

              if (result.code === 200) {
                uni.showToast({
                  title: '取消成功',
                  icon: 'success'
                });

                // 刷新列表
                this.pageNum = 1;
                this.appointmentList = [];
                this.noMore = false;
                this.loadAppointments();
              }
            } catch (error) {
              uni.hideLoading();
              console.error('取消预约失败:', error);
              uni.showToast({
                title: '取消失败，请重试',
                icon: 'none'
              });
            }
          }
        }
      });
    },

    // 拨打电话
    handleCall(phoneNumber) {
      uni.makePhoneCall({
        phoneNumber: phoneNumber
      });
    },

    // 跳转到预约创建页面
    gotoCreate() {
      uni.navigateTo({
        url: '/pages/appointment/create'
      });
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '已预约',
        1: '已完成',
        2: '已取消'
      };
      return statusMap[status] || '未知';
    },

    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return '';
      
      const date = new Date(timeStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      
      return `${year}年${month}月${day}日 ${hour}:${minute}`;
    }
  }
};
</script>

<style scoped>
.appointment-list {
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

/* 标签页 */
.tabs {
  display: flex;
  background-color: #fff;
  border-bottom: 1rpx solid #eee;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 30rpx 0;
  position: relative;
}

.tab-text {
  font-size: 28rpx;
  color: #666;
}

.tab-item.active .tab-text {
  color: #1890ff;
  font-weight: bold;
}

.tab-line {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background-color: #1890ff;
  border-radius: 2rpx;
}

/* 列表容器 */
.list-container {
  height: calc(100vh - 160rpx);
}

.scroll-view {
  height: 100%;
}

/* 预约卡片 */
.appointment-card {
  margin: 20rpx 30rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  position: relative;
}

/* 状态标签 */
.status-badge {
  position: absolute;
  top: 30rpx;
  right: 30rpx;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status-0 {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-1 {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-2 {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.status-text {
  font-size: 24rpx;
}

/* 采血点信息 */
.site-info {
  margin-bottom: 20rpx;
  padding-right: 120rpx;
}

.site-name {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.icon {
  font-size: 32rpx;
  margin-right: 8rpx;
}

.name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.site-address {
  font-size: 26rpx;
  color: #999;
  padding-left: 40rpx;
}

/* 时间信息 */
.time-info {
  margin-bottom: 20rpx;
  padding: 20rpx;
  background-color: #fafafa;
  border-radius: 8rpx;
}

.info-row {
  display: flex;
  margin-bottom: 10rpx;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  font-size: 26rpx;
  color: #666;
  width: 160rpx;
}

.value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

/* 操作按钮 */
.action-bar {
  display: flex;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.btn {
  flex: 1;
  height: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.btn-primary {
  background-color: #1890ff;
  color: #fff;
}

.btn-cancel {
  background-color: #fff;
  color: #ff4d4f;
  border: 1rpx solid #ff4d4f;
}

/* 预约编号 */
.appointment-no {
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.no-text {
  font-size: 24rpx;
  color: #999;
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
  margin-bottom: 40rpx;
}

.empty-state .btn {
  width: 300rpx;
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
