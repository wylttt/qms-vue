<template>
  <div id="userLayout" :class="['user-layout-wrapper', { 'dark-theme': isDarkMode }]">
    <div class="container">
      <div class="loginbox">
        <div class="top">
          <div class="header">
            <a href="/" class="brand-link">
              <div class="logo-container">
                <img src="/src/assets/images/logo.png" class="logo" alt="BearJia Logo" />
              </div>
              <div class="title-container">
                <span class="title">BearJia 管理系统</span>
                <span class="subtitle">精美现代化</span>
              </div>
            </a>
          </div>
        </div>
        <div class="main">
          <a-form id="loginForm" ref="loginFormRef" class="user-layout-login" :model="loginFormModel" :rules="loginFormRules" :scrollToFirstError="true">
            <a-page-header title="系统登录" />
            <!-- Form校验时，需要给a-form-item添加name属性 -->
            <a-form-item name="username">
              <!-- 双向绑定，必须使用v-model:value -->
              <a-input v-model:value="loginFormModel.username" size="large" placeholder="用户名">
                <template #prefix>
                  <UserOutlined style="padding-right: 5px; color: var(--primary-color)" />
                </template>
              </a-input>
            </a-form-item>
            <a-form-item name="password">
              <a-input-password v-model:value="loginFormModel.password" size="large" placeholder="密码">
                <template #prefix>
                  <LockOutlined style="padding-right: 5px; color: var(--primary-color)" />
                </template>
              </a-input-password>
            </a-form-item>
            <a-form-item name="code">
              <a-row :gutter="16">
                <a-col class="gutter-row" :span="16">
                  <a-input v-model:value="loginFormModel.code" size="large" type="text" autocomplete="off" placeholder="验证码">
                    <template #prefix>
                      <SecurityScanOutlined style="padding-right: 5px; color: var(--primary-color)" />
                    </template>
                  </a-input>
                </a-col>
                <a-col class="gutter-row" :span="8">
                  <img class="getCaptcha" :src="loginFormModel.codeUrl" @click="getVerifyCode" />
                </a-col>
              </a-row>
            </a-form-item>
            <a-form-item>
              <a-button size="large" type="primary" htmlType="submit" class="login-button" :disabled="loginFormModel.loginButtonDisabled" :loading="loginFormModel.loginButtonLoading" @click="submitForm()">{{
                loginFormModel.loginButtonName
              }}</a-button>
            </a-form-item>
            <a-form-item>
              <div style="float: left; line-height: 30px">
                还没有账号？
                <a-button type="link" @click="goToRegister">立即注册</a-button>
                <a-button type="link">忘记密码</a-button>
              </div>
              <div style="float: right; line-height: 30px">
                <a-button type="link" @click="switchLoginStyle">切换登录风格</a-button>
              </div>
            </a-form-item>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getVerifyCodeImg } from '@/api/login.js';
import { ref, reactive, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useAppStore } from '@/stores/app';
import { UserOutlined, LockOutlined, SecurityScanOutlined } from '@ant-design/icons-vue';
import { usePermissionStore } from '@/stores/permission';

// 获取router变量
const vueRouter = useRouter();
// 获取store变量
const vueStore = useUserStore();
const appStore = useAppStore();

// 计算暗黑模式状态
const isDarkMode = computed(() => appStore.layoutSettings.darkMode);

const loginFormRef = ref();
// 定义表单对象
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
// 定义表单校验规则
const loginFormRules = reactive({
  username: [{ required: true, message: '请输入用户名！', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码！', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码！', trigger: 'blur' }],
});

// 获取验证码
const getVerifyCode = () => {
  getVerifyCodeImg().then((res) => {
    if (res.captchaEnabled) {
      if (res.img.startsWith('data:image')) {
        loginFormModel.codeUrl = res.img;
      } else {
        //适配若依的验证码
        loginFormModel.codeUrl = 'data:image/gif;base64,' + res.img;
      }
    }
    loginFormModel.uuid = res.uuid;
  });
};

// 提交表单方法
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
    accessRoutes.forEach((route) => {
      vueRouter.addRoute(route);
    });

    // 登录成功，跳转到工作台
    await vueRouter.push({ path: '/home/workbench' });
  } catch (error) {
    console.error('登录失败:', error);
    loginFormModel.loginButtonDisabled = false;
    loginFormModel.loginButtonLoading = false;
    loginFormModel.loginButtonName = '登录';
    getVerifyCode();
  }
};
// 跳转到注册页面
const goToRegister = () => {
  vueRouter.push('/register');
};

const switchLoginStyle = () => {
  vueRouter.push('/login2');
};

// 重置表单方法
const resetForm = () => {
  loginFormRef.value.resetFields();
};

// 默认调用获取验证码方法
getVerifyCode();
</script>

<style lang="less" scoped>
#userLayout.user-layout-wrapper {
  height: 100%;
  .container {
    width: 100%;
    min-height: 100%;
    background: #e2effc url(/src/assets/images/pages/login/loginPageBackground.jpg) no-repeat center top;
    background-size: 100%;
    vertical-align: middle;
    display: flex;

    a {
      text-decoration: none;
    }
    .loginbox {
      width: 800px;
      height: 500px;
      margin: auto;
      background: var(--component-background);
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
      border-radius: 8px;
    }
    .top {
      text-align: center;
      width: 355px;
      height: 500px;
      background: url(/src/assets/images/pages/login/loginPageLogoBackground.png) no-repeat center top;
      float: left;

      .header {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;

        .brand-link {
          display: flex;
          flex-direction: column;
          align-items: center;
          text-decoration: none;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
          }

          .logo-container {
            margin-bottom: 20px;

            .logo {
              height: 80px;
              width: 80px;
              object-fit: contain;
              border-radius: 12px;
              box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2);
              transition: all 0.3s ease;

              &:hover {
                box-shadow: 0 6px 20px rgba(255, 255, 255, 0.3);
              }
            }
          }

          .title-container {
            text-align: center;

            .title {
              display: block;
              font-size: 32px;
              color: #ffffff;
              font-family: 'Helvetica Neue', Arial, sans-serif;
              font-weight: 700;
              letter-spacing: 1px;
              margin-bottom: 4px;
              text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
            }

            .subtitle {
              display: block;
              font-size: 16px;
              color: rgba(255, 255, 255, 0.9);
              font-family: 'Helvetica Neue', Arial, sans-serif;
              font-weight: 400;
              letter-spacing: 2px;
              text-transform: uppercase;
              text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
            }
          }
        }
      }
    }

    .main {
      width: 445px;
      height: 500px;
      margin: 0 auto;
      float: left;
      display: block;
      padding: 0 30px;
      position: relative;
      background: var(--component-background);
    }
    .user-layout-login {
      .ant-page-header {
        padding: 60px 0 45px 0;

        .ant-page-header-heading-title {
          color: var(--text-primary);
        }
      }
      .getCaptcha {
        display: block;
        width: 100%;
        height: 40px;
      }

      button.login-button {
        padding: 0 15px;
        font-size: 16px;
        height: 40px;
        width: 100%;
        border-radius: 4px;
      }

      // 输入框样式
      .ant-input,
      .ant-input-password {
        background: var(--input-bg);
        color: var(--text-primary);
        border-color: var(--border-color-base);

        &::placeholder {
          color: var(--text-secondary);
        }
      }

      // 按钮样式
      .ant-btn {
        color: var(--text-primary);
        border-color: var(--border-color-base);

        &.ant-btn-primary {
          background: var(--primary-color);
          border-color: var(--primary-color);
          color: #fff;
        }

        &.ant-btn-link {
          color: var(--primary-color);
        }
      }
    }
  }

  // 暗黑模式下的特殊样式
  &.dark-theme {
    .container {
      background: #1a1a1a url(/src/assets/images/pages/login/loginPageBackground.jpg) no-repeat center top;
      background-size: 100%;

      .loginbox {
        background: var(--component-background);
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
      }

      .main {
        background: var(--component-background);
      }
    }
  }
}
</style>
