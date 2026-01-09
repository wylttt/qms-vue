<template>
  <view class="surveyor-login">
    <!-- Logo区域 -->
    <view class="logo-section">
      <view class="logo">
        <text class="logo-icon">👨‍⚕️</text>
      </view>
      <view class="app-name">调查员登录</view>
      <view class="app-desc">胃癌筛查管理系统</view>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">👤</text>
          <input 
            class="input-field" 
            type="text" 
            v-model="formData.username"
            placeholder="请输入账号"
            placeholder-class="placeholder"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">🔒</text>
          <input 
            class="input-field" 
            :type="showPassword ? 'text' : 'password'"
            v-model="formData.password"
            placeholder="请输入密码"
            placeholder-class="placeholder"
          />
          <text 
            class="toggle-password" 
            @click="showPassword = !showPassword"
          >
            {{ showPassword ? '👁️' : '👁️‍🗨️' }}
          </text>
        </view>
      </view>

      <view class="form-options">
        <view class="remember-me" @click="toggleRemember">
          <view class="checkbox" :class="{ checked: rememberMe }">
            <text v-if="rememberMe" class="check-icon">✓</text>
          </view>
          <text class="option-text">记住密码</text>
        </view>
      </view>

      <view class="btn-login" @click="handleLogin">
        <text>登录</text>
      </view>
    </view>

    <!-- 底部链接 -->
    <view class="bottom-links">
      <view class="link-item" @click="gotoResidentLogin">
        <text class="link-text">返回居民登录</text>
      </view>
    </view>
  </view>
</template>

<script>
import { surveyorLogin } from '@/api/gc.js';

export default {
  data() {
    return {
      formData: {
        username: '',
        password: ''
      },
      showPassword: false,
      rememberMe: false,
      loading: false
    };
  },

  onLoad() {
    // 检查是否记住了密码
    const savedUsername = uni.getStorageSync('savedUsername');
    const savedPassword = uni.getStorageSync('savedPassword');
    
    if (savedUsername && savedPassword) {
      this.formData.username = savedUsername;
      this.formData.password = savedPassword;
      this.rememberMe = true;
    }
  },

  methods: {
    // 切换记住密码
    toggleRemember() {
      this.rememberMe = !this.rememberMe;
    },

    // 登录
    async handleLogin() {
      // 验证表单
      if (!this.formData.username) {
        uni.showToast({
          title: '请输入账号',
          icon: 'none'
        });
        return;
      }

      if (!this.formData.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        });
        return;
      }

      if (this.loading) return;
      this.loading = true;

      try {
        uni.showLoading({ title: '登录中...' });

        const res = await surveyorLogin({
          username: this.formData.username,
          password: this.formData.password
        });

        uni.hideLoading();

        if (res.code === 200) {
          // 保存token
          if (res.token) {
            uni.setStorageSync('token', res.token);
          }

          // 保存调查员信息
          if (res.data) {
            uni.setStorageSync('surveyorInfo', res.data);
            uni.setStorageSync('userType', 'surveyor'); // 标记用户类型
          }

          // 记住密码
          if (this.rememberMe) {
            uni.setStorageSync('savedUsername', this.formData.username);
            uni.setStorageSync('savedPassword', this.formData.password);
          } else {
            uni.removeStorageSync('savedUsername');
            uni.removeStorageSync('savedPassword');
          }

          uni.showToast({
            title: '登录成功',
            icon: 'success'
          });

          // 跳转到调查员工作台
          setTimeout(() => {
            uni.redirectTo({
              url: '/pages/surveyor/progress'
            });
          }, 1000);
        }
      } catch (error) {
        uni.hideLoading();
        console.error('登录失败:', error);
        
        let errorMsg = '登录失败，请重试';
        if (error && error.msg) {
          errorMsg = error.msg;
        }
        
        uni.showToast({
          title: errorMsg,
          icon: 'none',
          duration: 2000
        });
      } finally {
        this.loading = false;
      }
    },

    // 返回居民登录
    gotoResidentLogin() {
      uni.navigateBack({
        fail: () => {
          uni.redirectTo({
            url: '/pages/login/login'
          });
        }
      });
    }
  }
};
</script>

<style scoped>
.surveyor-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 100rpx 60rpx;
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 80rpx;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  margin: 0 auto 30rpx;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
}

.logo-icon {
  font-size: 80rpx;
}

.app-name {
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.app-desc {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* 登录表单 */
.login-form {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}

.form-item {
  margin-bottom: 30rpx;
}

.input-wrapper {
  display: flex;
  align-items: center;
  height: 90rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 30rpx;
}

.input-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.input-field {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.placeholder {
  color: #999;
}

.toggle-password {
  font-size: 36rpx;
  padding: 0 10rpx;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
}

.remember-me {
  display: flex;
  align-items: center;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #ddd;
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10rpx;
}

.checkbox.checked {
  background-color: #1890ff;
  border-color: #1890ff;
}

.check-icon {
  color: #fff;
  font-size: 24rpx;
  font-weight: bold;
}

.option-text {
  font-size: 26rpx;
  color: #666;
}

.btn-login {
  height: 90rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.4);
}

.btn-login:active {
  opacity: 0.8;
}

/* 底部链接 */
.bottom-links {
  margin-top: 60rpx;
  text-align: center;
}

.link-item {
  padding: 20rpx;
}

.link-text {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: underline;
}
</style>
