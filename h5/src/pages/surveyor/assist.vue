<template>
  <view class="assist-entry">
    <!-- 顶部导航 -->
    <view class="navbar">
      <view class="nav-back" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="navbar-title">协助录入</text>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 步骤指示器 -->
    <view class="step-indicator">
      <view class="step-item" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
        <view class="step-number">1</view>
        <text class="step-text">居民信息</text>
      </view>
      <view class="step-line" :class="{ active: currentStep > 1 }"></view>
      <view class="step-item" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
        <view class="step-number">2</view>
        <text class="step-text">问卷填写</text>
      </view>
      <view class="step-line" :class="{ active: currentStep > 2 }"></view>
      <view class="step-item" :class="{ active: currentStep >= 3 }">
        <view class="step-number">3</view>
        <text class="step-text">完成</text>
      </view>
    </view>

    <!-- 步骤1: 居民信息录入 -->
    <view v-if="currentStep === 1" class="step-content">
      <view class="form-section">
        <view class="section-title">
          <text>基本信息</text>
          <view class="ocr-btn" @click="ocrIdCard">
            <text>📷 扫描身份证</text>
          </view>
        </view>

        <view class="form-item">
          <text class="label required">姓名</text>
          <input 
            class="input-field" 
            v-model="residentForm.residentName"
            placeholder="请输入姓名"
          />
        </view>

        <view class="form-item">
          <text class="label required">身份证号</text>
          <input 
            class="input-field" 
            v-model="residentForm.idCardNo"
            placeholder="请输入身份证号"
            maxlength="18"
            @blur="parseIdCard"
          />
        </view>

        <view class="form-item">
          <text class="label required">性别</text>
          <view class="radio-group">
            <view 
              class="radio-item"
              :class="{ checked: residentForm.gender === '0' }"
              @click="residentForm.gender = '0'"
            >
              <view class="radio-icon">
                <view v-if="residentForm.gender === '0'" class="radio-dot"></view>
              </view>
              <text>男</text>
            </view>
            <view 
              class="radio-item"
              :class="{ checked: residentForm.gender === '1' }"
              @click="residentForm.gender = '1'"
            >
              <view class="radio-icon">
                <view v-if="residentForm.gender === '1'" class="radio-dot"></view>
              </view>
              <text>女</text>
            </view>
          </view>
        </view>

        <view class="form-item">
          <text class="label required">出生日期</text>
          <picker 
            mode="date" 
            :value="residentForm.birthDate"
            @change="residentForm.birthDate = $event.detail.value"
            :end="maxBirthDate"
          >
            <view class="picker-value">
              {{ residentForm.birthDate || '请选择出生日期' }}
            </view>
          </picker>
        </view>

        <view class="form-item">
          <text class="label required">手机号</text>
          <input 
            class="input-field" 
            type="number"
            v-model="residentForm.phone"
            placeholder="请输入手机号"
            maxlength="11"
          />
        </view>
      </view>

      <!-- 地址信息（简化版，使用级联选择） -->
      <view class="form-section">
        <view class="section-title">
          <text>居住地址</text>
        </view>

        <view class="form-item">
          <text class="label required">详细地址</text>
          <textarea 
            class="textarea-field" 
            v-model="residentForm.detailAddress"
            placeholder="请输入详细地址（街道、门牌号等）"
            maxlength="200"
          />
        </view>
      </view>

      <view class="btn-group">
        <view class="btn btn-primary" @click="saveResident">
          <text>下一步：填写问卷</text>
        </view>
      </view>
    </view>

    <!-- 步骤2: 问卷填写 -->
    <view v-if="currentStep === 2" class="step-content">
      <view class="questionnaire-info">
        <text class="info-label">居民：</text>
        <text class="info-value">{{ residentForm.residentName }}</text>
      </view>

      <!-- 选择问卷模板 -->
      <view v-if="!selectedTemplate" class="template-select">
        <view class="section-title">
          <text>选择问卷模板</text>
        </view>
        <view 
          v-for="template in templateList"
          :key="template.templateId"
          class="template-item"
          @click="selectTemplate(template)"
        >
          <view class="template-name">{{ template.templateName }}</view>
          <view class="template-desc">{{ template.description }}</view>
          <text class="arrow">›</text>
        </view>
      </view>

      <!-- 问卷填写（使用QuestionnaireForm组件） -->
      <view v-else class="questionnaire-fill">
        <view class="questionnaire-header">
          <text class="template-name">{{ selectedTemplate.templateName }}</text>
          <view class="change-btn" @click="changeTemplate">
            <text>更换问卷</text>
          </view>
        </view>

        <!-- 集成QuestionnaireForm组件 -->
        <QuestionnaireForm
          v-if="newResidentId"
          :residentId="newResidentId"
          :surveyorId="surveyorInfo.surveyorId"
          :templateData="selectedTemplate"
          @submit="handleQuestionnaireSubmit"
        />
      </view>
    </view>

    <!-- 步骤3: 完成 -->
    <view v-if="currentStep === 3" class="step-content">
      <view class="success-state">
        <text class="success-icon">✓</text>
        <text class="success-title">录入成功</text>
        <text class="success-desc">已为 {{ residentForm.residentName }} 完成信息录入</text>

        <view class="btn-group">
          <view class="btn btn-secondary" @click="continueEntry">
            <text>继续录入</text>
          </view>
          <view class="btn btn-primary" @click="backToProgress">
            <text>返回任务</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { addResident, getQuestionnaireTemplates } from '@/api/gc.js';
import { smartOCRIdCard, validateIdCard } from '@/utils/ocrHelper.js';
import QuestionnaireForm from '@/components/QuestionnaireForm.vue';

export default {
  components: {
    QuestionnaireForm
  },
  
  data() {
    return {
      currentStep: 1,
      
      // 居民表单
      residentForm: {
        residentName: '',
        idCardNo: '',
        gender: '',
        birthDate: '',
        phone: '',
        detailAddress: ''
      },
      
      // 问卷相关
      templateList: [],
      selectedTemplate: null,
      
      // 调查员信息
      surveyorInfo: {},
      
      // 新创建的居民ID
      newResidentId: '',
      
      maxBirthDate: ''
    };
  },

  onLoad() {
    // 获取调查员信息
    const surveyorInfo = uni.getStorageSync('surveyorInfo');
    if (surveyorInfo) {
      this.surveyorInfo = surveyorInfo;
    } else {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/surveyor/login'
        });
      }, 1500);
      return;
    }

    // 设置最大出生日期（今天）
    const today = new Date();
    this.maxBirthDate = this.formatDate(today);

    // 加载问卷模板列表
    this.loadTemplates();
  },

  methods: {
    // 加载问卷模板
    async loadTemplates() {
      try {
        const res = await getQuestionnaireTemplates({ status: 1 });
        if (res.code === 200) {
          this.templateList = res.rows || [];
        }
      } catch (error) {
        console.error('加载问卷模板失败:', error);
      }
    },

    // OCR识别身份证
    async ocrIdCard() {
      try {
        uni.showLoading({ title: '识别中...' });
        
        // 调用智能OCR识别（自动选择最优方案）
        const result = await smartOCRIdCard();
        
        uni.hideLoading();
        
        if (result.success) {
          // 填充识别结果
          if (result.data.name) {
            this.residentForm.residentName = result.data.name;
          }
          
          if (result.data.idCard) {
            this.residentForm.idCardNo = result.data.idCard;
            // 验证身份证号
            if (!validateIdCard(result.data.idCard)) {
              uni.showModal({
                title: '提示',
                content: '识别的身份证号校验失败，请手动核对',
                showCancel: false
              });
            } else {
              // 自动解析身份证信息
              this.parseIdCard();
            }
          }
          
          // 如果有地址信息，自动填充
          if (result.data.address) {
            this.residentForm.detailAddress = result.data.address;
          }
          
          uni.showToast({
            title: `识别成功（${result.method}）`,
            icon: 'success',
            duration: 2000
          });
        } else {
          uni.showToast({
            title: result.message || '识别失败，请手动输入',
            icon: 'none',
            duration: 2000
          });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('OCR识别失败', error);
        uni.showToast({
          title: '识别失败，请手动输入',
          icon: 'none'
        });
      }
    },

    // 解析身份证号
    parseIdCard() {
      const idCard = this.residentForm.idCardNo;
      if (!idCard || idCard.length !== 18) return;
      
      // 解析性别
      const genderCode = parseInt(idCard.charAt(16));
      this.residentForm.gender = (genderCode % 2 === 0) ? '1' : '0';
      
      // 解析出生日期
      const year = idCard.substring(6, 10);
      const month = idCard.substring(10, 12);
      const day = idCard.substring(12, 14);
      this.residentForm.birthDate = `${year}-${month}-${day}`;
    },

    // 保存居民信息
    async saveResident() {
      // 验证表单
      if (!this.validateResidentForm()) {
        return;
      }

      try {
        uni.showLoading({ title: '保存中...' });

        const res = await addResident({
          ...this.residentForm,
          surveyorId: this.surveyorInfo.surveyorId
        });

        uni.hideLoading();

        if (res.code === 200) {
          this.newResidentId = res.data.residentId || res.data;
          
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          });

          // 进入下一步
          this.currentStep = 2;
        }
      } catch (error) {
        uni.hideLoading();
        console.error('保存居民信息失败:', error);
        uni.showToast({
          title: error.msg || '保存失败',
          icon: 'none'
        });
      }
    },

    // 验证居民表单
    validateResidentForm() {
      const { residentName, idCardNo, gender, birthDate, phone } = this.residentForm;

      if (!residentName) {
        uni.showToast({ title: '请输入姓名', icon: 'none' });
        return false;
      }

      if (!idCardNo || idCardNo.length !== 18) {
        uni.showToast({ title: '请输入正确的身份证号', icon: 'none' });
        return false;
      }

      // 使用增强的身份证号验证（包含校验码）
      if (!validateIdCard(idCardNo)) {
        uni.showToast({ title: '身份证号校验失败', icon: 'none' });
        return false;
      }

      if (!gender) {
        uni.showToast({ title: '请选择性别', icon: 'none' });
        return false;
      }

      if (!birthDate) {
        uni.showToast({ title: '请选择出生日期', icon: 'none' });
        return false;
      }

      if (!phone || phone.length !== 11) {
        uni.showToast({ title: '请输入正确的手机号', icon: 'none' });
        return false;
      }

      return true;
    },

    // 选择问卷模板
    selectTemplate(template) {
      this.selectedTemplate = template;
    },

    // 更换问卷
    changeTemplate() {
      this.selectedTemplate = null;
    },

    // 处理问卷提交成功
    handleQuestionnaireSubmit(result) {
      const { totalScore, isFocusGroup } = result;
      
      uni.showToast({
        title: '提交成功',
        icon: 'success'
      });

      // 进入完成步骤
      setTimeout(() => {
        this.currentStep = 3;
      }, 1500);
    },

    // 继续录入
    continueEntry() {
      // 重置表单
      this.currentStep = 1;
      this.residentForm = {
        residentName: '',
        idCardNo: '',
        gender: '',
        birthDate: '',
        phone: '',
        detailAddress: ''
      };
      this.selectedTemplate = null;
      this.newResidentId = '';
    },

    // 返回任务进度
    backToProgress() {
      uni.navigateBack({
        fail: () => {
          uni.redirectTo({
            url: '/pages/surveyor/progress'
          });
        }
      });
    },

    // 返回
    goBack() {
      if (this.currentStep > 1) {
        this.currentStep--;
      } else {
        uni.navigateBack();
      }
    },

    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  }
};
</script>

<style scoped>
.assist-entry {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 导航栏 */
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.nav-back {
  width: 80rpx;
}

.back-icon {
  font-size: 48rpx;
  color: #333;
}

.navbar-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.nav-placeholder {
  width: 80rpx;
}

/* 步骤指示器 */
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40rpx 60rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.step-number {
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
  background-color: #e0e0e0;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.step-item.active .step-number {
  background-color: #1890ff;
  color: #fff;
}

.step-item.completed .step-number {
  background-color: #52c41a;
  color: #fff;
}

.step-text {
  font-size: 24rpx;
  color: #999;
}

.step-item.active .step-text {
  color: #333;
  font-weight: bold;
}

.step-line {
  flex: 1;
  height: 2rpx;
  background-color: #e0e0e0;
  margin: 0 20rpx;
}

.step-line.active {
  background-color: #52c41a;
}

/* 步骤内容 */
.step-content {
  padding: 30rpx;
}

/* 表单区域 */
.form-section {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.ocr-btn {
  padding: 10rpx 20rpx;
  background-color: #1890ff;
  color: #fff;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: normal;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-item:last-child {
  margin-bottom: 0;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 15rpx;
}

.label.required::before {
  content: '* ';
  color: #ff4d4f;
}

.input-field,
.picker-value {
  width: 100%;
  height: 80rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333;
  line-height: 80rpx;
}

.textarea-field {
  width: 100%;
  min-height: 150rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #333;
}

/* 单选组 */
.radio-group {
  display: flex;
  gap: 40rpx;
}

.radio-item {
  display: flex;
  align-items: center;
}

.radio-icon {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #ddd;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10rpx;
}

.radio-item.checked .radio-icon {
  border-color: #1890ff;
}

.radio-dot {
  width: 20rpx;
  height: 20rpx;
  background-color: #1890ff;
  border-radius: 10rpx;
}

/* 问卷信息 */
.questionnaire-info {
  background-color: #e6f7ff;
  padding: 20rpx 30rpx;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
}

.info-label {
  font-size: 26rpx;
  color: #666;
}

.info-value {
  font-size: 28rpx;
  color: #1890ff;
  font-weight: bold;
}

/* 模板选择 */
.template-select {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
}

.template-item {
  position: relative;
  padding: 30rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
}

.template-item:last-child {
  margin-bottom: 0;
}

.template-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.template-desc {
  font-size: 26rpx;
  color: #999;
}

.template-item .arrow {
  position: absolute;
  right: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 48rpx;
  color: #ccc;
}

/* 问卷填写 */
.questionnaire-fill {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
}

.questionnaire-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.change-btn {
  padding: 8rpx 20rpx;
  background-color: #fff;
  color: #1890ff;
  border: 1rpx solid #1890ff;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.questionnaire-placeholder {
  padding: 100rpx 0;
  text-align: center;
}

.placeholder-text {
  display: block;
  font-size: 32rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.placeholder-desc {
  font-size: 24rpx;
  color: #ccc;
}

/* 成功状态 */
.success-state {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 100rpx 60rpx;
  text-align: center;
}

.success-icon {
  display: block;
  width: 120rpx;
  height: 120rpx;
  line-height: 120rpx;
  background-color: #52c41a;
  color: #fff;
  font-size: 80rpx;
  border-radius: 60rpx;
  margin: 0 auto 30rpx;
}

.success-title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 15rpx;
}

.success-desc {
  display: block;
  font-size: 28rpx;
  color: #999;
  margin-bottom: 60rpx;
}

/* 按钮组 */
.btn-group {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}

.btn {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8rpx;
  font-size: 30rpx;
}

.btn-primary {
  background-color: #1890ff;
  color: #fff;
}

.btn-secondary {
  background-color: #fff;
  color: #1890ff;
  border: 1rpx solid #1890ff;
}
</style>
