<template>
  <div :class="['login-container', { 'dark-theme': isDarkMode }]">
    <div class="login-box">
      <div class="login-content">
        <div class="brand-section">
          <div class="logo-container">
            <img src="/src/assets/images/logo.png" class="logo" alt="BearJia Logo" />
          </div>
          <div class="brand-text">
            <h1 class="brand-name">BearJia</h1>
            <p class="brand-subtitle">Admin System</p>
          </div>
        </div>
        <h2 class="welcome-text">欢迎回来</h2>
        <p class="sub-title">登录您的账户以继续</p>

        <a-form id="loginForm" ref="loginFormRef" :model="loginFormModel" :rules="loginFormRules" :scrollToFirstError="true">
          <a-form-item name="username">
            <a-input v-model:value="loginFormModel.username" size="large" placeholder="请输入用户名">
              <template #prefix>
                <UserOutlined class="site-form-item-icon" />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item name="password">
            <a-input-password v-model:value="loginFormModel.password" size="large" placeholder="请输入密码">
              <template #prefix>
                <LockOutlined class="site-form-item-icon" />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item name="code">
            <div class="verification-code">
              <a-input v-model:value="loginFormModel.code" size="large" placeholder="验证码">
                <template #prefix>
                  <SecurityScanOutlined class="site-form-item-icon" />
                </template>
              </a-input>
              <div class="code-image" @click="getVerifyCode">
                <img :src="loginFormModel.codeUrl" alt="验证码" />
              </div>
            </div>
          </a-form-item>

          <div class="form-footer">
            <a-checkbox>记住我</a-checkbox>
            <a class="forgot-password">忘记密码？</a>
          </div>

          <a-button type="primary" block size="large" :loading="loginFormModel.loginButtonLoading" @click="submitForm">
            {{ loginFormModel.loginButtonName }}
          </a-button>

          <div class="other-actions">
            <span>还没有账号？</span>
            <a-button type="link" @click="goToRegister">立即注册</a-button>
          </div>

          <div class="switch-style">
            <a-button type="link" @click="switchLoginStyle">切换登录风格</a-button>
          </div>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getVerifyCodeImg } from '@/api/login.js';
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useAppStore } from '@/stores/app';
import { UserOutlined, LockOutlined, SecurityScanOutlined } from '@ant-design/icons-vue';
import { usePermissionStore } from '@/stores/permission';

const vueRouter = useRouter();
const vueStore = useUserStore();
const appStore = useAppStore();
const loginFormRef = ref();

// 计算暗黑模式状态
const isDarkMode = computed(() => appStore.layoutSettings.darkMode);

const loginFormModel = reactive({
  username: 'admin',
  password: 'admin123',
  code: '',
  uuid: '',
  codeUrl: '',
  loginButtonDisabled: false,
  loginButtonLoading: false,
  loginButtonName: '登录',
});

const loginFormRules = reactive({
  username: [{ required: true, message: '请输入用户名！', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码！', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码！', trigger: 'blur' }],
});

const getVerifyCode = () => {
  getVerifyCodeImg().then((res) => {
    if (res.captchaEnabled){
      if (res.img.startsWith('data:image')) {
        loginFormModel.codeUrl = res.img;
      } else {
        //适配若依的验证码
        loginFormModel.codeUrl = "data:image/gif;base64," + res.img
      }
    }
    loginFormModel.uuid = res.uuid;
  });
};

const submitForm = async () => {
  try {
    loginFormModel.loginButtonDisabled = true;
    loginFormModel.loginButtonLoading = true;
    loginFormModel.loginButtonName = '登录中...';

    await loginFormRef.value.validate();
    await vueStore.login(loginFormModel);
    await vueStore.getInfo();

    const permissionStore = usePermissionStore();
    const accessRoutes = await permissionStore.generateRoutes();
    accessRoutes.forEach(route => {
      vueRouter.addRoute(route);
    });

    await vueRouter.push({ path: '/home/workbench' });
  } catch (error) {
    console.error('登录失败:', error);
    loginFormModel.loginButtonDisabled = false;
    loginFormModel.loginButtonLoading = false;
    loginFormModel.loginButtonName = '登录';
    getVerifyCode();
  }
};

const goToRegister = () => {
  vueRouter.push('/register');
};

const switchLoginStyle = () => {
  vueRouter.push('/login3');
};

getVerifyCode();
</script>

<style lang="less" scoped>
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
    opacity: 0.3;
  }

  &.dark-theme {
    background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
    
    .login-box {
      background: var(--component-background);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
      
      .login-content {
        .brand-section {
          .brand-text {
            .brand-name {
              color: var(--text-primary);
            }
            
            .brand-subtitle {
              color: var(--text-secondary);
            }
          }
        }

        .welcome-text {
          color: var(--text-primary);
        }

        .sub-title {
          color: var(--text-secondary);
        }
        
        :deep(.ant-input),
        :deep(.ant-input-password) {
          background: var(--input-bg);
          border-color: var(--border-color-base);
          color: var(--text-primary);
          
          &::placeholder {
            color: var(--text-secondary);
          }
        }
        
        :deep(.ant-checkbox-wrapper) {
          color: var(--text-primary);
        }
        
        .site-form-item-icon {
          color: var(--text-secondary) !important;
        }
        
        .form-footer {
          .forgot-password {
            color: var(--primary-color);
            
            &:hover {
              color: var(--primary-color-hover);
            }
          }
        }
        
        .other-actions {
          color: var(--text-secondary);
        }
      }
    }
  }

  .login-box {
    width: 420px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    padding: 40px;
    position: relative;
    z-index: 1;

    .login-content {
      .brand-section {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 32px;
        
        .logo-container {
          margin-right: 16px;
          
          .logo {
            width: 48px;
            height: 48px;
            object-fit: contain;
            border-radius: 8px;
          }
        }
        
        .brand-text {
          .brand-name {
            font-size: 24px;
            font-weight: 700;
            color: #1a1a1a;
            margin: 0;
            line-height: 1;
          }
          
          .brand-subtitle {
            font-size: 12px;
            color: #666;
            margin: 2px 0 0 0;
            text-transform: uppercase;
            letter-spacing: 1px;
          }
        }
      }

      .welcome-text {
        font-size: 28px;
        font-weight: 600;
        color: #1a1a1a;
        margin-bottom: 8px;
        text-align: center;
      }

      .sub-title {
        font-size: 16px;
        color: #666;
        margin-bottom: 32px;
        text-align: center;
      }

      .verification-code {
        display: flex;
        gap: 16px;

        .ant-input-affix-wrapper {
          flex: 1;
        }

        .code-image {
          width: 100px;
          height: 40px;
          cursor: pointer;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
      }

      .form-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin: 16px 0;

        .forgot-password {
          color: #1890ff;
          cursor: pointer;

          &:hover {
            color: #40a9ff;
          }
        }
      }

      .site-form-item-icon {
        color: #bfbfbf;
      }

      .other-actions {
        margin-top: 24px;
        text-align: center;
        color: #666;
      }

      .switch-style {
        margin-top: 16px;
        text-align: center;
      }
    }
  }
}
</style>