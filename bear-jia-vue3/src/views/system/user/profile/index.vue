<template>
  <div class="account-settings-page">
    <a-row :gutter="[24, 24]">
      <!-- 左侧：个人信息卡片 -->
      <a-col :xs="24" :sm="24" :md="8" :lg="7" :xl="6">
        <a-card :bordered="false" class="profile-card stylish-card">
          <!-- 头像区域 -->
          <div class="avatar-wrapper">
            <a-upload
                name="avatarfile"
                class="avatar-uploader"
                :show-upload-list="false"
                :before-upload="beforeAvatarUpload"
                :customRequest="handleAvatarChange"
                :disabled="avatarUploading"
            >
              <a-avatar :size="128" :src="user.avatar" />
              <div class="upload-mask">
                <LoadingOutlined v-if="avatarUploading" style="font-size: 24px;" />
                <CameraOutlined v-else style="font-size: 24px;" />
                <span class="upload-text">更换头像</span>
              </div>
            </a-upload>
            <a-typography-title :level="4" class="username">{{ user.nickName }}</a-typography-title>
            <a-tag color="blue" class="role-tag">{{ user.roleGroup || '暂无角色' }}</a-tag>
          </div>

          <!-- 详细信息列表 -->
          <div class="info-list">
            <div class="info-row">
              <div class="info-label">
                <TeamOutlined class="info-icon" />
                <span>所属部门</span>
              </div>
              <div class="info-value">{{ user.deptName || '暂无' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <UserSwitchOutlined class="info-icon" />
                <span>所属角色</span>
              </div>
              <div class="info-value">{{ user.roleGroup || '暂无' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <ClockCircleOutlined class="info-icon" />
                <span>创建时间</span>
              </div>
              <div class="info-value">{{ user.createTime || '暂无' }}</div>
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 右侧：设置区域 -->
      <a-col :xs="24" :sm="24" :md="16" :lg="17" :xl="18">
        <a-card :bordered="false" class="settings-card stylish-card">
          <a-tabs v-model:activeKey="activeTabKey" class="settings-tabs">
            <!-- 基本资料 Tab -->
            <a-tab-pane key="basic" tab="基本资料">
              <div class="form-content">
                <a-form :model="userForm" layout="vertical" @finish="onFinish" ref="basicFormRef">
                  <a-form-item label="用户昵称" name="nickName" :rules="[{ required: true, message: '请输入用户昵称!' }]">
                    <a-input v-model:value="userForm.nickName" size="large" placeholder="请输入用户昵称" />
                  </a-form-item>
                  <a-form-item label="手机号码" name="phonenumber">
                    <a-input v-model:value="userForm.phonenumber" size="large" placeholder="请输入手机号码" />
                  </a-form-item>
                  <a-form-item label="邮箱" name="email">
                    <a-input v-model:value="userForm.email" size="large" placeholder="请输入邮箱" />
                  </a-form-item>
                  <a-form-item label="性别" name="sex" :rules="[{ required: true, message: '请选择性别!' }]">
                    <a-select v-model:value="userForm.sex" size="large" placeholder="请选择性别">
                      <a-select-option value="0">男</a-select-option>
                      <a-select-option value="1">女</a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item>
                    <a-button 
                      type="primary" 
                      size="large" 
                      html-type="submit" 
                      class="submit-button"
                      :loading="basicFormLoading"
                    >
                      保存更改
                    </a-button>
                  </a-form-item>
                </a-form>
              </div>
            </a-tab-pane>

            <!-- 修改密码 Tab -->
            <a-tab-pane key="password" tab="修改密码">
              <div class="form-content">
                <a-form layout="vertical" @finish="onChangePassword" ref="passwordFormRef">
                  <a-form-item label="旧密码" name="oldPassword" :rules="[{ required: true, message: '请输入旧密码!' }]">
                    <a-input-password v-model:value="passwordForm.oldPassword" size="large" placeholder="请输入旧密码" />
                  </a-form-item>
                  <a-form-item label="新密码" name="newPassword" :rules="[
                    { required: true, message: '请输入新密码!' },
                    { min: 6, message: '密码长度不能少于6位' }
                  ]">
                    <a-input-password v-model:value="passwordForm.newPassword" size="large" placeholder="请输入新密码" />
                  </a-form-item>
                  <a-form-item label="确认新密码" name="confirmPassword" :rules="[
                    { required: true, message: '请再次输入新密码!' },
                    { validator: validateConfirmPassword }
                  ]">
                    <a-input-password v-model:value="passwordForm.confirmPassword" size="large" placeholder="请再次输入新密码" />
                  </a-form-item>
                  <a-form-item>
                    <a-button 
                      type="primary" 
                      size="large" 
                      html-type="submit" 
                      class="submit-button"
                      :loading="passwordFormLoading"
                    >
                      更新密码
                    </a-button>
                  </a-form-item>
                </a-form>
              </div>
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { TeamOutlined, UserSwitchOutlined, ClockCircleOutlined, CameraOutlined, LoadingOutlined } from '@ant-design/icons-vue';
import { getUserProfile, updateUserProfile, updateUserPwd, uploadAvatar } from '@/api/system/user';
import { useUserStore } from '@/stores/user';

// 获取 store
const userStore = useUserStore();

// 激活的 Tab
const activeTabKey = ref('basic');
const basicFormRef = ref();
const passwordFormRef = ref();

// 加载状态
const basicFormLoading = ref(false);
const passwordFormLoading = ref(false);
const avatarUploading = ref(false);

// 用户信息
const user = reactive({
  nickName: '',
  avatar: '',
  deptName: '',
  roleGroup: '',
  createTime: ''
});

// 基本资料表单状态
const userForm = reactive({
  nickName: '',
  phonenumber: '',
  email: '',
  sex: undefined
});

// 密码表单状态
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 确认密码验证器
const validateConfirmPassword = (_, value) => {
  if (value && value !== passwordForm.newPassword) {
    return Promise.reject('两次输入的密码不一致');
  }
  return Promise.resolve();
};

// 获取用户信息
const fetchUserProfile = async () => {
  try {
    const res = await getUserProfile();
    console.log(res.data);
    // 更新用户信息
    Object.assign(user, res.data);
    user.roleGroup = res.roleGroup
    // 更新表单数据
    Object.assign(userForm, {
      nickName: res.data.nickName,
      phonenumber: res.data.phonenumber,
      email: res.data.email,
      sex: res.data.sex
    });
  } catch (err) {
    message.error('获取用户信息失败');
    console.error('获取用户信息失败:', err);
  }
};

// 头像上传前的检查
const beforeAvatarUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片！');
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB！');
  }
  return isJpgOrPng && isLt2M;
};

  // 头像上传处理
const handleAvatarChange = async (options) => {
  const { file } = options;
  if (!beforeAvatarUpload(file)) {
    return;
  }

  avatarUploading.value = true;
  const formData = new FormData();
  formData.append('avatarfile', file);

  try {
    const response = await uploadAvatar(formData);
    message.success(response.msg || '头像上传成功');
    // 更新用户头像
    user.avatar = response.imgUrl;
    // 直接更新 store 中的头像
    userStore.avatar = response.imgUrl;
  } catch (err) {
    message.error('头像上传失败');
    console.error('头像上传失败:', err);
  } finally {
    avatarUploading.value = false;
  }
};

  // 基本资料表单提交
const onFinish = async (values) => {
  basicFormLoading.value = true;
  try {
    const response = await updateUserProfile(userForm);
    message.success(response.msg || '基本资料更新成功！');
    // 更新用户信息
    Object.assign(user, userForm);
    // 直接更新 store 中的用户信息
    userStore.nickName = userForm.nickName;
  } catch (err) {
    message.error('保存失败');
    console.error('保存失败:', err);
  } finally {
    basicFormLoading.value = false;
  }
};

// 修改密码表单提交
const onChangePassword = async (values) => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.error('两次输入的密码不一致');
    return;
  }

  passwordFormLoading.value = true;
  try {
    const response = await updateUserPwd(passwordForm.oldPassword, passwordForm.newPassword);
    message.success(response.msg || '密码修改成功！');
    // 重置表单
    passwordForm.oldPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
    // 重置表单验证状态
    passwordFormRef.value.resetFields();
  } catch (err) {
    message.error('密码修改失败');
    console.error('密码修改失败:', err);
  } finally {
    passwordFormLoading.value = false;
  }
};

// 组件挂载时获取用户信息
onMounted(() => {
  fetchUserProfile();
});
</script>

<style scoped>
/* 页面全局样式 */
.account-settings-page {
  background-color: #f7f8fa; /* 更柔和的背景色 */
  padding: 24px;
  min-height: 100vh;
}

/* 通用卡片样式 */
.stylish-card {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05); /* 更柔和、更弥散的阴影 */
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.stylish-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
}
:deep(.ant-card-body) {
  padding: 32px;
}

/* 左侧 Profile 卡片 */
.profile-card {
  text-align: center;
}
.avatar-wrapper {
  margin-bottom: 24px;
  position: relative;
  display: inline-block;
}
.avatar-uploader {
  position: relative;
  display: inline-block;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.upload-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}
.avatar-uploader:hover .upload-mask {
  opacity: 1;
}
.upload-text {
  margin-top: 8px;
  font-size: 14px;
}
.username {
  margin-top: 20px;
  margin-bottom: 8px;
  font-weight: 600;
}
.role-tag {
  border-radius: 6px;
  padding: 2px 10px;
}

/* 信息列表 */
.info-list {
  text-align: left;
  margin-top: 32px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  font-size: 14px;
  border-bottom: 1px solid #f0f0f0;
}
.info-row:last-child {
  border-bottom: none;
}
.info-label {
  display: flex;
  align-items: center;
  color: #595959;
}
.info-icon {
  margin-right: 12px;
  font-size: 18px;
  color: #8c8c8c;
}
.info-value {
  color: #262626;
  font-weight: 500;
}

/* 右侧设置卡片 */
.settings-tabs {
  margin: -16px 0;
}
:deep(.ant-tabs-nav) {
  padding-left: 8px;
  margin-bottom: 24px !important; /* 增加 Tab 和表单的间距 */
}
:deep(.ant-tabs-tab) {
  font-size: 16px;
  padding: 16px 8px;
}
:deep(.ant-tabs-ink-bar) {
  height: 3px;
  border-radius: 3px;
}

.form-content {
  max-width: 500px;
}
:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #555;
}
:deep(.ant-input-lg),
:deep(.ant-select-lg .ant-select-selector) {
  border-radius: 8px !important;
}
.submit-button {
  width: 150px;
  border-radius: 8px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
  transition: all 0.3s ease;
}
.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3);
}
</style>