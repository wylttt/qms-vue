<template>
  <view class="basic-page">
    <!-- 顶部导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @click="goBack">
          <text class="back-arrow">←</text>
        </view>
        <view class="navbar-title">基本信息</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <!-- 页面内容 -->
    <view class="page-content">
      <view class="form-container">
        <view class="form-tip">请填写您的基本信息</view>

        <!-- 表单项 -->
        <view class="form-item">
          <view class="form-label">姓名</view>
          <input 
            class="form-input" 
            v-model="formData.name" 
            placeholder="请输入姓名"
            placeholder-class="input-placeholder"
          />
        </view>

        <view class="form-item">
          <view class="form-label">身份证号码</view>
          <input 
            class="form-input" 
            v-model="formData.idCard" 
            placeholder="请输入身份证号码"
            placeholder-class="input-placeholder"
          />
        </view>

        <view class="form-item">
          <view class="form-label">联系电话</view>
          <input 
            class="form-input" 
            v-model="formData.phone" 
            type="number"
            placeholder="请输入联系电话"
            placeholder-class="input-placeholder"
          />
        </view>

        <view class="form-item">
          <view class="form-label">年龄</view>
          <input 
            class="form-input" 
            v-model="formData.age" 
            type="number"
            placeholder="请输入年龄"
            placeholder-class="input-placeholder"
          />
          <view class="age-tip">45-75岁为胃癌高危人群</view>
        </view>

        <view class="form-item">
          <view class="form-label">性别</view>
          <radio-group @change="onGenderChange" class="radio-group">
            <label class="radio-label">
              <radio value="male" :checked="formData.gender === 'male'" color="#1E88E5" />
              <text>男性</text>
            </label>
            <label class="radio-label">
              <radio value="female" :checked="formData.gender === 'female'" color="#1E88E5" />
              <text>女性</text>
            </label>
          </radio-group>
          <view class="gender-tip">早性胃癌发病率高于女性</view>
        </view>

        <!-- 受检者承诺 -->
        <view class="checkbox-area">
          <checkbox-group @change="onCheckboxChange">
            <label class="checkbox-label">
              <checkbox :checked="formData.agreed" color="#1E88E5" />
              <text class="checkbox-text">
                受检者承诺：本人不属于以下不适合筛查人群：①孕妇及哺乳期妇女②正在进行肿瘤治疗者③近半年输血者④器官移植者
              </text>
            </label>
          </checkbox-group>
        </view>
      </view>

      <!-- 下一步按钮 -->
      <view class="action-button" @click="nextStep">
        <text>下一步：风险评估</text>
      </view>
    </view>
  </view>
</template>

<script>
import { verifyIdentity } from '@/api/identity.js';

export default {
  data() {
    return {
      statusBarHeight: 0,
      // 身份验证API开关（默认关闭，等后端API准备好后改为true）
      enableRealVerify: false,
      formData: {
        name: '',
        idCard: '',
        phone: '',
        age: '',
        gender: 'male',
        agreed: false
      }
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
    onGenderChange(e) {
      this.formData.gender = e.detail.value;
    },
    onCheckboxChange(e) {
      this.formData.agreed = e.detail.value.length > 0;
    },
    // 验证身份证号码格式
    validateIdCard(idCard) {
      // 身份证号码正则：18位，前17位为数字，最后一位可以是数字或X
      const idCardReg = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/;
      return idCardReg.test(idCard);
    },
    // 从身份证号码提取信息
    extractIdCardInfo(idCard) {
      if (!this.validateIdCard(idCard)) {
        return null;
      }
      
      // 提取出生日期
      const year = idCard.substring(6, 10);
      const month = idCard.substring(10, 12);
      const day = idCard.substring(12, 14);
      const birthDate = `${year}-${month}-${day}`;
      
      // 计算年龄
      const today = new Date();
      const birth = new Date(birthDate);
      let age = today.getFullYear() - birth.getFullYear();
      const monthDiff = today.getMonth() - birth.getMonth();
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
        age--;
      }
      
      // 提取性别（第17位，奇数为男，偶数为女）
      const genderCode = parseInt(idCard.substring(16, 17));
      const gender = genderCode % 2 === 1 ? 'male' : 'female';
      
      return { age, gender, birthDate };
    },
    // 验证姓名和身份证号的复合绑定
    async validateNameIdCardBinding(name, idCard) {
      // 1. 验证姓名格式（中文姓名，2-10个字）
      const nameReg = /^[\u4e00-\u9fa5]{2,10}$/;
      if (!nameReg.test(name)) {
        uni.showToast({ 
          title: '请输入正确的中文姓名（2-10个字）', 
          icon: 'none',
          duration: 2000
        });
        return false;
      }
      
      // 2. 验证身份证号码格式
      if (!this.validateIdCard(idCard)) {
        uni.showToast({ 
          title: '请输入正确的身份证号码', 
          icon: 'none',
          duration: 2000
        });
        return false;
      }
      
      // 3. 根据开关决定是否调用真实API验证
      if (this.enableRealVerify) {
        // 启用真实API验证
        return await this.performRealVerify(name, idCard);
      } else {
        // 仅进行前端格式验证和信息提取
        return this.performLocalVerify(idCard);
      }
    },
    // 真实API验证（后端API准备好后启用）
    async performRealVerify(name, idCard) {
      try {
        uni.showLoading({
          title: '正在验证身份信息...',
          mask: true
        });
        
        const result = await verifyIdentity(name, idCard);
        
        uni.hideLoading();
        
        if (result.code === 200 && result.data.match) {
          // 验证通过，提取身份证信息并自动填充
          const idCardInfo = this.extractIdCardInfo(idCard);
          if (idCardInfo) {
            if (!this.formData.age) {
              this.formData.age = idCardInfo.age.toString();
            }
            this.formData.gender = idCardInfo.gender;
            
            uni.showToast({
              title: `身份验证通过：${idCardInfo.age}岁 ${idCardInfo.gender === 'male' ? '男性' : '女性'}`,
              icon: 'success',
              duration: 2000
            });
          }
          return true;
        } else {
          // 验证失败
          uni.showModal({
            title: '身份验证失败',
            content: result.message || '姓名与身份证号不匹配，请检查后重新输入',
            showCancel: false
          });
          return false;
        }
      } catch (error) {
        uni.hideLoading();
        console.error('身份验证API调用失败:', error);
        
        // API调用失败时，降级为前端格式验证
        uni.showModal({
          title: '提示',
          content: '身份验证服务暂时不可用，是否仅进行格式验证后继续？',
          success: (res) => {
            if (res.confirm) {
              this.performLocalVerify(idCard);
            }
          }
        });
        return false;
      }
    },
    // 本地格式验证和信息提取
    performLocalVerify(idCard) {
      const idCardInfo = this.extractIdCardInfo(idCard);
      if (idCardInfo) {
        // 自动填充年龄
        if (!this.formData.age) {
          this.formData.age = idCardInfo.age.toString();
        }
        // 自动选择性别
        this.formData.gender = idCardInfo.gender;
        
        // 提示用户
        uni.showToast({
          title: `已识别：${idCardInfo.age}岁 ${idCardInfo.gender === 'male' ? '男性' : '女性'}`,
          icon: 'success',
          duration: 2000
        });
      }
      return true;
    },
    async nextStep() {
      // 验证表单
      if (!this.formData.name) {
        uni.showToast({ title: '请输入姓名', icon: 'none' });
        return;
      }
      if (!this.formData.idCard) {
        uni.showToast({ title: '请输入身份证号码', icon: 'none' });
        return;
      }
      
      // 验证姓名和身份证号的复合绑定
      if (!this.validateNameIdCardBinding(this.formData.name, this.formData.idCard)) {
        return;
      }
      
      if (!this.formData.phone) {
        uni.showToast({ title: '请输入联系电话', icon: 'none' });
        return;
      }
      
      // 验证手机号码格式
      const phoneReg = /^1[3-9]\d{9}$/;
      if (!phoneReg.test(this.formData.phone)) {
        uni.showToast({ title: '请输入正确的手机号码', icon: 'none' });
        return;
      }
      
      if (!this.formData.age) {
        uni.showToast({ title: '请输入年龄', icon: 'none' });
        return;
      }
      
      // 验证年龄范围
      const age = parseInt(this.formData.age);
      if (age < 45 || age > 75) {
        uni.showModal({
          title: '提示',
          content: '您的年龄不在筛查适用范围（45-75岁）内，是否继续？',
          success: (res) => {
            if (res.confirm) {
              this.navigateToRisk();
            }
          }
        });
        return;
      }
      
      if (!this.formData.agreed) {
        uni.showToast({ title: '请阅读并同意受检者承诺', icon: 'none' });
        return;
      }

      // 跳转到风险评估页面
      this.navigateToRisk();
    },
    navigateToRisk() {
      uni.navigateTo({
        url: '/pages/survey/risk'
      });
    }
  }
}
</script>

<style scoped>
.basic-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 自定义导航栏 */
.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: #ffffff;
  border-bottom: 1rpx solid #e5e5e5;
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
  color: #333333;
}

.navbar-title {
  flex: 1;
  text-align: center;
  font-size: 38rpx;
  font-weight: bold;
  color: #333333;
}

.navbar-right {
  width: 80rpx;
}

/* 页面内容 */
.page-content {
  padding-top: calc(88rpx + var(--status-bar-height));
  padding-bottom: 40rpx;
}

/* 表单容器 */
.form-container {
  margin: 30rpx;
  padding: 40rpx 30rpx;
  background: #ffffff;
  border-radius: 20rpx;
}

.form-tip {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 40rpx;
}

/* 表单项 */
.form-item {
  margin-bottom: 40rpx;
}

.form-label {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 16rpx;
}

.form-input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333333;
}

.input-placeholder {
  color: #999999;
}

.age-tip,
.gender-tip {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #FF9800;
}

/* 性别选择 */
.radio-group {
  display: flex;
  gap: 40rpx;
  margin-top: 16rpx;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 28rpx;
  color: #333333;
}

/* 单选框样式优化 */
.radio-label radio {
  /* 移除缩放，使用原生尺寸 */
}

/* 复选框区域 */
.checkbox-area {
  margin-top: 40rpx;
  padding: 24rpx;
  background: #FFF9E6;
  border-radius: 12rpx;
  border: 1rpx solid #FFE4A3;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
}

.checkbox-text {
  flex: 1;
  margin-left: 12rpx;
  font-size: 24rpx;
  color: #666666;
  line-height: 1.6;
}

/* 下一步按钮 */
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
</style>
