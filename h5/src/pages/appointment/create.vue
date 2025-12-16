<template>
  <view class="appointment-container">
    <view class="section">
      <view class="section-title">选择采血点</view>
      
      <picker 
        mode="selector" 
        :range="samplingSites" 
        range-key="siteName" 
        @change="onSiteChange"
      >
        <view class="picker-item">
          <text class="label">采血点</text>
          <view class="picker-value">
            <text>{{ selectedSite ? selectedSite.siteName : '请选择采血点' }}</text>
            <text class="arrow">›</text>
          </view>
        </view>
      </picker>

      <view v-if="selectedSite" class="site-info">
        <view class="info-row">
          <text class="info-label">地址：</text>
          <text class="info-value">{{ selectedSite.address }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">联系电话：</text>
          <text class="info-value">{{ selectedSite.siteManagerPhone }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">预约时间</view>
      
      <picker 
        mode="date" 
        :start="minDate"
        :end="maxDate"
        :value="appointmentDate" 
        @change="onDateChange"
      >
        <view class="picker-item">
          <text class="label">预约日期</text>
          <view class="picker-value">
            <text>{{ appointmentDate || '请选择日期' }}</text>
            <text class="arrow">›</text>
          </view>
        </view>
      </picker>

      <picker 
        mode="time" 
        :value="appointmentTime" 
        @change="onTimeChange"
      >
        <view class="picker-item">
          <text class="label">预约时间</text>
          <view class="picker-value">
            <text>{{ appointmentTime || '请选择时间' }}</text>
            <text class="arrow">›</text>
          </view>
        </view>
      </picker>
    </view>

    <view class="section">
      <view class="section-title">注意事项</view>
      <view class="notice">
        <view class="notice-item">1. 请提前24小时预约</view>
        <view class="notice-item">2. 采血前需空腹8-12小时</view>
        <view class="notice-item">3. 如需取消预约，请提前24小时</view>
        <view class="notice-item">4. 请携带身份证原件</view>
      </view>
    </view>

    <view class="btn-group">
      <button class="submit-btn" @tap="handleSubmit">确认预约</button>
    </view>
  </view>
</template>

<script>
import { getSamplingSiteOptions, createAppointment, checkAppointmentCapacity, getAvailableTimeSlots } from '@/api/gc.js';

export default {
  data() {
    return {
      residentId: null,
      samplingSites: [],
      selectedSite: null,
      appointmentDate: '',
      appointmentTime: '',
      minDate: '',
      maxDate: '',
      availableSlots: [], // 可用时间段
      capacityInfo: null  // 容量信息
    };
  },

  onLoad(options) {
    this.residentId = options.residentId;
    this.initDateRange();
    this.loadSamplingSites();
  },

  methods: {
    // 初始化日期范围
    initDateRange() {
      const today = new Date();
      const tomorrow = new Date(today);
      tomorrow.setDate(tomorrow.getDate() + 1);
      
      const maxDay = new Date(today);
      maxDay.setDate(maxDay.getDate() + 30);

      this.minDate = this.formatDate(tomorrow);
      this.maxDate = this.formatDate(maxDay);
    },

    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },

    // 加载采血点列表
    async loadSamplingSites() {
      try {
        uni.showLoading({ title: '加载中...' });
        // TODO: 根据居民所属区域加载采血点
        const res = await getSamplingSiteOptions();
        uni.hideLoading();

        if (res.code === 200) {
          this.samplingSites = res.data || [];
        }
      } catch (error) {
        uni.hideLoading();
        console.error('加载采血点失败', error);
      }
    },

    // 采血点改变
    async onSiteChange(e) {
      const index = e.detail.value;
      this.selectedSite = this.samplingSites[index];
      
      // 如果已选择日期,检查容量
      if (this.appointmentDate) {
        await this.checkCapacity();
      }
    },

    // 日期改变
    async onDateChange(e) {
      this.appointmentDate = e.detail.value;
      this.appointmentTime = ''; // 重置时间
      
      // 检查容量并加载可用时间段
      if (this.selectedSite) {
        await this.checkCapacity();
        await this.loadAvailableSlots();
      }
    },

    // 时间改变
    onTimeChange(e) {
      this.appointmentTime = e.detail.value;
    },
    
    // 检查采血点容量
    async checkCapacity() {
      if (!this.selectedSite || !this.appointmentDate) {
        return;
      }
      
      try {
        const res = await checkAppointmentCapacity(
          this.selectedSite.siteId,
          this.appointmentDate
        );
        
        if (res.code === 200) {
          this.capacityInfo = res.data;
          
          // 如果已满额,提示用户
          if (res.data.isFull) {
            uni.showModal({
              title: '提示',
              content: `该采血点在${this.appointmentDate}已满额(${res.data.currentCount}/${res.data.maxCapacity}),请选择其他日期或采血点`,
              showCancel: false
            });
          }
        }
      } catch (error) {
        console.error('检查容量失败', error);
      }
    },
    
    // 加载可用时间段
    async loadAvailableSlots() {
      if (!this.selectedSite || !this.appointmentDate) {
        return;
      }
      
      try {
        const res = await getAvailableTimeSlots(
          this.selectedSite.siteId,
          this.appointmentDate
        );
        
        if (res.code === 200) {
          this.availableSlots = res.data || [];
        }
      } catch (error) {
        console.error('加载可用时间段失败', error);
      }
    },

    // 表单验证
    validate() {
      if (!this.selectedSite) {
        uni.showToast({
          title: '请选择采血点',
          icon: 'none'
        });
        return false;
      }

      if (!this.appointmentDate) {
        uni.showToast({
          title: '请选择预约日期',
          icon: 'none'
        });
        return false;
      }

      if (!this.appointmentTime) {
        uni.showToast({
          title: '请选择预约时间',
          icon: 'none'
        });
        return false;
      }

      return true;
    },

    // 提交预约
    async handleSubmit() {
      if (!this.validate()) {
        return;
      }
      
      // 最终检查容量
      if (this.capacityInfo && this.capacityInfo.isFull) {
        uni.showToast({
          title: '该采血点已满额',
          icon: 'none'
        });
        return;
      }

      try {
        uni.showLoading({ title: '预约中...' });

        const appointmentData = {
          residentId: this.residentId,
          siteId: this.selectedSite.siteId,
          appointmentTime: `${this.appointmentDate} ${this.appointmentTime}:00`
        };

        const res = await createAppointment(appointmentData);
        uni.hideLoading();

        if (res.code === 200) {
          uni.showModal({
            title: '预约成功',
            content: '您的采血预约已成功,请按时前往采血点',
            showCancel: false,
            success: () => {
              uni.switchTab({
                url: '/pages/appointment/list'
              });
            }
          });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('预约失败', error);
      }
    }
  }
};
</script>

<style scoped>
.appointment-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 30rpx;
  padding-bottom: 150rpx;
}

.section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 30rpx;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 25rpx 0;
  border-bottom: 2rpx solid #f0f0f0;
}

.picker-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 28rpx;
  color: #333333;
}

.picker-value {
  display: flex;
  align-items: center;
  font-size: 28rpx;
  color: #666666;
}

.arrow {
  font-size: 40rpx;
  color: #cccccc;
  margin-left: 10rpx;
}

.site-info {
  margin-top: 30rpx;
  padding: 25rpx;
  background: #f8f8f8;
  border-radius: 10rpx;
}

.info-row {
  display: flex;
  margin-bottom: 15rpx;
  font-size: 26rpx;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  color: #999999;
  min-width: 140rpx;
}

.info-value {
  flex: 1;
  color: #333333;
}

.notice {
  padding: 20rpx;
  background: #fff3e0;
  border-radius: 10rpx;
}

.notice-item {
  font-size: 26rpx;
  color: #ff6f00;
  line-height: 2;
}

.btn-group {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 20rpx 30rpx;
  background: #ffffff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border-radius: 10rpx;
  font-size: 32rpx;
  border: none;
}
</style>
