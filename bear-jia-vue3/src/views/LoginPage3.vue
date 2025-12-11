<template>
  <div :class="['login-container', { 'dark-theme': isDarkMode }]">
    <div id="particles-js"></div>
    <div class="login-box">
      <div class="login-card">
        <div class="card-header">
          <div class="logo-container">
            <img src="/src/assets/images/logo.png" class="logo" alt="BearJia Logo" />
            <div class="logo-text">BEAR JIA</div>
          </div>
          <h1 class="title">欢迎回来</h1>
          <p class="subtitle">登录以继续您的工作</p>
        </div>

        <a-form class="login-form" :model="loginFormModel" :rules="loginFormRules" ref="loginFormRef">
        <a-form-item name="username">
          <a-input
            v-model:value="loginFormModel.username"
            placeholder="请输入用户名"
            size="large"
            :maxLength="20"
          >
            <template #prefix>
              <UserOutlined class="form-icon" />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="password">
          <a-input-password
            v-model:value="loginFormModel.password"
            placeholder="请输入密码"
            size="large"
            :maxLength="20"
          >
            <template #prefix>
              <LockOutlined class="form-icon" />
            </template>
          </a-input-password>
        </a-form-item>

        <div class="verify-code-container">
          <a-form-item name="code" class="verify-code-input">
            <a-input
              v-model:value="loginFormModel.code"
              placeholder="验证码"
              size="large"
              :maxLength="4"
            >
              <template #prefix>
                <SafetyOutlined class="form-icon" />
              </template>
            </a-input>
          </a-form-item>
          <div class="verify-code-img" @click="getVerifyCode">
            <img :src="loginFormModel.codeUrl" alt="验证码" />
          </div>
        </div>

        <div class="form-options">
          <a-checkbox>记住密码</a-checkbox>
          <a class="forgot-password">忘记密码？</a>
        </div>

        <a-button
          type="primary"
          class="submit-btn"
          size="large"
          :loading="loginFormModel.loginButtonLoading"
          @click="submitForm"
        >
          {{ loginFormModel.loginButtonName }}
        </a-button>

        <div class="form-footer">
          <span>还没有账号？</span>
          <a-button type="link" @click="goToRegister">立即注册</a-button>
          <a-divider type="vertical" />
          <a-button type="link" @click="switchLoginStyle">切换登录风格</a-button>
        </div>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getVerifyCodeImg } from '@/api/login.js';
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useAppStore } from '@/stores/app';
import { UserOutlined, LockOutlined, SafetyOutlined } from '@ant-design/icons-vue';
import { usePermissionStore } from '@/stores/permission';
import 'particles.js';

const vueRouter = useRouter();
const vueStore = useUserStore();
const appStore = useAppStore();
const loginFormRef = ref();

// 计算暗黑模式状态
const isDarkMode = computed(() => appStore.layoutSettings.darkMode);

onMounted(() => {
  window.particlesJS('particles-js', {
    particles: {
      number: { value: 80, density: { enable: true, value_area: 800 } },
      color: { value: '#ffffff' },
      shape: { type: 'circle' },
      opacity: { value: 0.5, random: false },
      size: { value: 3, random: true },
      line_linked: {
        enable: true,
        distance: 150,
        color: '#ffffff',
        opacity: 0.4,
        width: 1
      },
      move: {
        enable: true,
        speed: 2,
        direction: 'none',
        random: false,
        straight: false,
        out_mode: 'out',
        bounce: false
      }
    },
    interactivity: {
      detect_on: 'canvas',
      events: {
        onhover: { enable: true, mode: 'repulse' },
        onclick: { enable: true, mode: 'push' },
        resize: true
      }
    },
    retina_detect: true
  });
});


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
  vueRouter.push('/login');
  // 切换回第一个登录页面，形成循环
};

getVerifyCode();
</script>

<style lang="less" scoped>
@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0px); }
}

@keyframes gradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  
  &.dark-theme {
    background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
    
    .login-card {
      background: rgba(31, 41, 55, 0.3);
      border: 1px solid rgba(255, 255, 255, 0.1);
      
      .card-header {
        .logo-container {
          .logo-text {
            background: linear-gradient(45deg, #00dbde, #fc00ff);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
          }
        }
        
        .title {
          color: var(--text-primary);
        }
        
        .subtitle {
          color: var(--text-secondary);
        }
      }
      
      .login-form {
        :deep(.ant-input),
        :deep(.ant-input-password) {
          background: rgba(255, 255, 255, 0.05);
          border: 1px solid rgba(255, 255, 255, 0.15);
          color: var(--text-primary);
          
          &::placeholder {
            color: rgba(255, 255, 255, 0.4);
          }
          
          &:hover,
          &:focus {
            border-color: rgba(255, 255, 255, 0.3);
            background: rgba(255, 255, 255, 0.1);
          }
        }
        
        .form-icon {
          color: rgba(255, 255, 255, 0.6) !important;
        }
        
        :deep(.ant-checkbox-wrapper) {
          color: var(--text-secondary);
        }
        
        :deep(.ant-checkbox-inner) {
          background-color: rgba(255, 255, 255, 0.05);
          border-color: rgba(255, 255, 255, 0.2);
        }
        
        .form-footer {
          color: var(--text-secondary);
          
          :deep(.ant-btn-link) {
            color: var(--text-secondary);
            
            &:hover {
              color: var(--text-primary);
            }
          }
          
          :deep(.ant-divider) {
            border-color: rgba(255, 255, 255, 0.1);
          }
        }
      }
    }
  }
}

#particles-js {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.login-box {
  position: relative;
  z-index: 2;
  perspective: 1000px;
  transform-style: preserve-3d;
}

.login-card {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(8px);
  transform: translateY(0);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px rgba(31, 38, 135, 0.25);
  }

  .logo-text {
    font-size: 24px;
    font-weight: bold;
    background: linear-gradient(45deg, #00dbde, #fc00ff);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    text-align: center;
  }

  .card-header {
    text-align: center;
    margin-bottom: 40px;

    .logo-container {
      margin-bottom: 24px;
      display: flex;
      flex-direction: column;
      align-items: center;

      .logo {
        width: 64px;
        height: 64px;
        object-fit: contain;
        border-radius: 12px;
        margin-bottom: 12px;
        box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2);
      }
    }

    .title {
      font-size: 28px;
      color: #1a1a1a;
      margin-bottom: 8px;
    }

    .subtitle {
      font-size: 16px;
      color: #666;
    }
  }
}

.login-form {
  .form-icon {
    color: rgba(255, 255, 255, 0.8);
  }

  :deep(.ant-input),
  :deep(.ant-input-password) {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: white;

    &::placeholder {
      color: rgba(255, 255, 255, 0.6);
    }

    &:hover,
    &:focus {
      border-color: rgba(255, 255, 255, 0.5);
      background: rgba(255, 255, 255, 0.15);
    }
  }

  :deep(.ant-input-password-icon) {
    color: rgba(255, 255, 255, 0.8);
    &:hover {
      color: white;
    }
  }

  .verify-code-container {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;

    .verify-code-input {
      flex: 1;
      margin-bottom: 0;
    }

    .verify-code-img {
      width: 100px;
      height: 40px;
      cursor: pointer;
      border-radius: 8px;
      overflow: hidden;
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.05);
      }

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    color: rgba(255, 255, 255, 0.8);

    :deep(.ant-checkbox-wrapper) {
      color: rgba(255, 255, 255, 0.8);
    }

    :deep(.ant-checkbox-inner) {
      background-color: rgba(255, 255, 255, 0.1);
      border-color: rgba(255, 255, 255, 0.3);
    }

    :deep(.ant-checkbox-checked .ant-checkbox-inner) {
      background-color: #1890ff;
      border-color: #1890ff;
    }

    .forgot-password {
      color: rgba(255, 255, 255, 0.8);
      cursor: pointer;
      transition: color 0.3s ease;

      &:hover {
        color: white;
        text-decoration: underline;
      }
    }
  }

  .submit-btn {
    width: 100%;
    margin-bottom: 24px;
    height: 48px;
    font-size: 16px;
    background: linear-gradient(45deg, #00dbde, #fc00ff);
    border: none;
    transition: transform 0.3s ease, box-shadow 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(252, 0, 255, 0.3);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .form-footer {
    text-align: center;
    color: rgba(255, 255, 255, 0.6);

    :deep(.ant-btn-link) {
      color: rgba(255, 255, 255, 0.8);
      transition: color 0.3s ease;

      &:hover {
        color: white;
      }
    }

    :deep(.ant-divider) {
      border-color: rgba(255, 255, 255, 0.2);
    }
  }
}

  .form-footer {
    text-align: center;
    color: #8c8c8c;

    .ant-divider {
      margin: 0 8px;
    }
  }
</style>