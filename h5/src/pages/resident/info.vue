<template>
  <view class="user-info">
    <!-- 头部信息卡片 -->
    <view class="header-card">
      <view class="avatar-section">
        <view class="avatar">
          <text class="avatar-text">{{ avatarText }}</text>
        </view>
        <view class="user-details">
          <view class="user-name">{{ userInfo.residentName || '未登录' }}</view>
          <view class="user-phone">{{ userInfo.phone || '' }}</view>
        </view>
      </view>
    </view>

    <!-- 基本信息 -->
    <view class="info-section">
      <view class="section-title">
        <text class="title-text">基本信息</text>
        <view class="edit-btn" @click="gotoEdit">
          <text>编辑</text>
        </view>
      </view>
      <view class="info-grid">
        <view class="info-item">
          <text class="info-label">身份证号</text>
          <text class="info-value">{{ formatIdCard(userInfo.idCardNo) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">性别</text>
          <text class="info-value">{{ getGenderText(userInfo.gender) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">出生日期</text>
          <text class="info-value">{{ userInfo.birthDate || '-' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">年龄</text>
          <text class="info-value">{{ calculateAge(userInfo.birthDate) }}岁</text>
        </view>
        <view class="info-item full-width">
          <text class="info-label">居住地址</text>
          <text class="info-value">{{ getFullAddress() }}</text>
        </view>
      </view>
    </view>

    <!-- 我的记录 -->
    <view class="record-section">
      <view class="section-title">
        <text class="title-text">我的记录</text>
      </view>
      <view class="record-list">
        <view class="record-item" @click="gotoQuestionnaire">
          <view class="item-left">
            <text class="item-icon">📝</text>
            <text class="item-text">问卷记录</text>
          </view>
          <view class="item-right">
            <text class="item-count">{{ questionnaireCount }}</text>
            <text class="arrow">›</text>
          </view>
        </view>
        <view class="record-item" @click="gotoAppointment">
          <view class="item-left">
            <text class="item-icon">🩸</text>
            <text class="item-text">预约记录</text>
          </view>
          <view class="item-right">
            <text class="item-count">{{ appointmentCount }}</text>
            <text class="arrow">›</text>
          </view>
        </view>
        <view class="record-item" @click="gotoResult">
          <view class="item-left">
            <text class="item-icon">🔬</text>
            <text class="item-text">筛查结果</text>
          </view>
          <view class="item-right">
            <text class="item-count">{{ resultCount }}</text>
            <text class="arrow">›</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-item" @click="showAbout">
        <view class="item-left">
          <text class="item-icon">ℹ️</text>
          <text class="item-text">关于我们</text>
        </view>
        <text class="arrow">›</text>
      </view>
      <view class="menu-item" @click="handleLogout">
        <view class="item-left">
          <text class="item-icon">🚪</text>
          <text class="item-text">退出登录</text>
        </view>
        <text class="arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getResidentInfo } from '@/api/gc.js';

export default {
  data() {
    return {
      userInfo: {
        residentId: '',
        residentName: '',
        idCardNo: '',
        phone: '',
        gender: '',
        birthDate: '',
        provinceName: '',
        cityName: '',
        districtName: '',
        streetName: '',
        communityName: '',
        detailAddress: ''
      },
      
      // 统计数据
      questionnaireCount: 0,
      appointmentCount: 0,
      resultCount: 0
    };
  },

  computed: {
    // 头像文字
    avatarText() {
      const name = this.userInfo.residentName || '';
      return name ? name.charAt(name.length - 1) : '居';
    }
  },

  onLoad() {
    this.loadUserInfo();
  },

  onShow() {
    // 每次显示页面时刷新数据
    this.loadUserInfo();
  },

  methods: {
    // 加载用户信息
    async loadUserInfo() {
      try {
        // 从缓存获取用户基本信息
        const cachedUser = uni.getStorageSync('userInfo');
        if (cachedUser && cachedUser.residentId) {
          this.userInfo = { ...this.userInfo, ...cachedUser };
          
          // 从后端获取完整信息
          const res = await getResidentInfo(cachedUser.residentId);
          if (res.code === 200 && res.data) {
            this.userInfo = { ...this.userInfo, ...res.data };
            
            // 更新缓存
            uni.setStorageSync('userInfo', this.userInfo);
            
            // 加载统计数据
            this.loadStatistics();
          }
        } else {
          // 未登录，跳转登录页
          uni.redirectTo({
            url: '/pages/login/login'
          });
        }
      } catch (error) {
        console.error('加载用户信息失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      }
    },

    // 加载统计数据
    async loadStatistics() {
      try {
        // TODO: 调用统计接口获取问卷、预约、结果数量
        // 这里先使用模拟数据
        this.questionnaireCount = 0;
        this.appointmentCount = 0;
        this.resultCount = 0;
      } catch (error) {
        console.error('加载统计数据失败:', error);
      }
    },

    // 跳转到编辑页面
    gotoEdit() {
      uni.navigateTo({
        url: '/pages/resident/edit'
      });
    },

    // 跳转到问卷记录
    gotoQuestionnaire() {
      uni.navigateTo({
        url: '/pages/questionnaire/list'
      });
    },

    // 跳转到预约记录
    gotoAppointment() {
      uni.switchTab({
        url: '/pages/appointment/list'
      });
    },

    // 跳转到筛查结果
    gotoResult() {
      uni.switchTab({
        url: '/pages/result/index'
      });
    },

    // 显示关于信息
    showAbout() {
      uni.showModal({
        title: '关于我们',
        content: '胃癌筛查管理系统\n版本：v1.0.0\n\n专注于居民健康管理，提供便捷的问卷填写、采血预约、结果查询等服务。',
        showCancel: false,
        confirmText: '知道了'
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
            uni.removeStorageSync('userInfo');
            
            uni.showToast({
              title: '已退出登录',
              icon: 'success'
            });
            
            // 跳转到登录页
            setTimeout(() => {
              uni.redirectTo({
                url: '/pages/login/login'
              });
            }, 1000);
          }
        }
      });
    },

    // 格式化身份证号（显示前6位和后4位）
    formatIdCard(idCard) {
      if (!idCard || idCard.length !== 18) return '-';
      return `${idCard.substring(0, 6)}********${idCard.substring(14)}`;
    },

    // 获取性别文本
    getGenderText(gender) {
      const genderMap = {
        '0': '男',
        '1': '女'
      };
      return genderMap[gender] || '-';
    },

    // 计算年龄
    calculateAge(birthDate) {
      if (!birthDate) return '-';
      
      const birth = new Date(birthDate);
      const now = new Date();
      let age = now.getFullYear() - birth.getFullYear();
      
      const monthDiff = now.getMonth() - birth.getMonth();
      if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < birth.getDate())) {
        age--;
      }
      
      return age;
    },

    // 获取完整地址
    getFullAddress() {
      const parts = [
        this.userInfo.provinceName,
        this.userInfo.cityName,
        this.userInfo.districtName,
        this.userInfo.streetName,
        this.userInfo.communityName,
        this.userInfo.detailAddress
      ].filter(item => item);
      
      return parts.length > 0 ? parts.join('') : '-';
    }
  }
};
</script>

<style scoped>
.user-info {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 头部信息卡片 */
.header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 30rpx 40rpx;
}

.avatar-section {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 30rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.5);
}

.avatar-text {
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 38rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.user-phone {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* 基本信息 */
.info-section {
  margin: 20rpx 30rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.title-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.edit-btn {
  padding: 8rpx 20rpx;
  background-color: #1890ff;
  color: #fff;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.info-grid {
  display: flex;
  flex-wrap: wrap;
}

.info-item {
  width: 50%;
  margin-bottom: 24rpx;
}

.info-item.full-width {
  width: 100%;
}

.info-label {
  display: block;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 8rpx;
}

.info-value {
  display: block;
  font-size: 28rpx;
  color: #333;
}

/* 我的记录 */
.record-section {
  margin: 20rpx 30rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.record-list {
  margin-top: 20rpx;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.record-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
}

.item-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.item-text {
  font-size: 28rpx;
  color: #333;
}

.item-right {
  display: flex;
  align-items: center;
}

.item-count {
  font-size: 28rpx;
  color: #1890ff;
  font-weight: bold;
  margin-right: 10rpx;
}

.arrow {
  font-size: 48rpx;
  color: #ccc;
}

/* 功能菜单 */
.menu-section {
  margin: 20rpx 30rpx;
  background-color: #fff;
  border-radius: 12rpx;
  padding: 0 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}
</style>
