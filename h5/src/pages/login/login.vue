<template>
  <view class="login-container">
    <view class="logo-section">
      <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
      <text class="title">濂溪区胃癌筛查系统</text>
      <text class="subtitle">守护您的健康</text>
    </view>

    <view class="form-section">
      <view class="input-group">
        <text class="label">手机号</text>
        <input
          class="input"
          v-model="phone"
          type="number"
          maxlength="11"
          placeholder="请输入手机号"
        />
      </view>

      <view class="input-group">
        <text class="label">验证码</text>
        <view class="code-input-wrapper">
          <input
            class="input code-input"
            v-model="code"
            type="number"
            maxlength="6"
            placeholder="请输入验证码"
          />
          <button 
            class="code-btn" 
            :disabled="codeDisabled"
            @tap="sendCode"
          >
            {{ codeText }}
          </button>
        </view>
      </view>

      <button class="login-btn" @tap="handleLogin">登录</button>

      <view class="tips">
        <text>登录即表示您已阅读并同意</text>
        <text class="link">《用户协议》</text>
        <text>和</text>
        <text class="link">《隐私政策》</text>
      </view>
    </view>

    <view class="surveyor-entry">
      <text @tap="gotoSurveyorLogin">问卷调查员登录 ></text>
    </view>
  </view>
</template>

<script>
import { saveToken, saveUserInfo } from '@/api/gc.js';

export default {
  data() {
    return {
      phone: '',
      code: '',
      codeDisabled: false,
      codeText: '获取验证码',
      countdown: 60
    };
  },
  
  methods: {
    // 发送验证码
    sendCode() {
      if (!this.phone) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return;
      }
      
      // 验证手机号格式
      if (!/^1[3-9]\d{9}$/.test(this.phone)) {
        uni.showToast({
          title: '手机号格式不正确',
          icon: 'none'
        });
        return;
      }
      
      // TODO: 调用后端发送验证码接口
      uni.showLoading({ title: '发送中...' });
      
      // 模拟发送成功
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({
          title: '验证码已发送',
          icon: 'success'
        });
        
        // 开始倒计时
        this.codeDisabled = true;
        this.countdown = 60;
        this.codeText = `${this.countdown}秒后重试`;
        
        const timer = setInterval(() => {
          this.countdown--;
          if (this.countdown > 0) {
            this.codeText = `${this.countdown}秒后重试`;
          } else {
            clearInterval(timer);
            this.codeDisabled = false;
            this.codeText = '获取验证码';
          }
        }, 1000);
      }, 1000);
    },
    
    // 登录
    handleLogin() {
      if (!this.phone) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return;
      }
      
      if (!this.code) {
        uni.showToast({
          title: '请输入验证码',
          icon: 'none'
        });
        return;
      }
      
      uni.showLoading({ title: '登录中...' });
      
      // TODO: 调用后端登录接口
      // 模拟登录成功
      setTimeout(() => {
        uni.hideLoading();
        
        // 保存token和用户信息
        saveToken('mock_token_' + Date.now());
        saveUserInfo({
          phone: this.phone,
          userType: 'resident'
        });
        
        uni.showToast({
          title: '登录成功',
          icon: 'success'
        });
        
        // 跳转到首页
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          });
        }, 1500);
      }, 1000);
    },
    
    // 跳转到调查员登录
    gotoSurveyorLogin() {
      uni.navigateTo({
        url: '/pages/surveyor/login'
      });
    }
  }
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 80rpx 60rpx;
}

.logo-section {
  text-align: center;
  margin-bottom: 100rpx;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 30rpx;
}

.title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 20rpx;
}

.subtitle {
  display: block;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.form-section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
}

.input-group {
  margin-bottom: 40rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 20rpx;
}

.input {
  width: 100%;
  height: 90rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.code-input-wrapper {
  display: flex;
  align-items: center;
}

.code-input {
  flex: 1;
  margin-right: 20rpx;
}

.code-btn {
  width: 200rpx;
  height: 90rpx;
  line-height: 90rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 10rpx;
  font-size: 26rpx;
  padding: 0;
}

.code-btn[disabled] {
  background: #cccccc;
}

.login-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 10rpx;
  font-size: 32rpx;
  margin-top: 60rpx;
}

.tips {
  text-align: center;
  font-size: 24rpx;
  color: #999999;
  margin-top: 30rpx;
}

.link {
  color: #667eea;
}

.surveyor-entry {
  text-align: center;
  margin-top: 60rpx;
}

.surveyor-entry text {
  color: #ffffff;
  font-size: 28rpx;
}
</style>
