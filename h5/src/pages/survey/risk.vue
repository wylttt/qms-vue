<template>
  <view class="risk-page">
    <!-- 顶部导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @click="goBack">
          <text class="back-arrow">←</text>
        </view>
        <view class="navbar-title">风险评估</view>
        <view class="navbar-right">
          <text class="page-indicator">{{ currentPage }}/{{ totalPages }}</text>
        </view>
      </view>
    </view>

    <!-- 页面内容 -->
    <view class="page-content" :style="{ paddingTop: (statusBarHeight + 44) + 'px' }">
      <scroll-view 
        class="question-scroll" 
        scroll-y
        enable-back-to-top
        :scroll-top="scrollTop"
        :enhanced="true"
        :bounces="true"
        :show-scrollbar="false"
        :fast-deceleration="false"
      >
        <view class="scroll-content">
          <!-- 问题列表 -->
          <view 
            v-for="(question, index) in questions" 
            :key="index"
            class="question-item"
          >
            <view class="question-title">{{ question.title }}</view>

            <!-- 单选题 -->
            <radio-group 
              v-if="question.type === 'radio'"
              @change="(e) => onAnswerChange(index, e.detail.value)"
            >
              <label 
                v-for="(option, optIndex) in question.options" 
                :key="optIndex"
                class="option-label"
              >
                <radio 
                  :value="option.value" 
                  :checked="answers[index] === option.value"
                  color="#1E88E5"
                />
                <text>{{ option.label }}</text>
              </label>
            </radio-group>

            <!-- 复选框 -->
            <checkbox-group 
              v-if="question.type === 'checkbox'"
              @change="(e) => onCheckboxChange(index, e.detail.value)"
            >
              <label 
                v-for="(option, optIndex) in question.options" 
                :key="optIndex"
                class="option-label"
              >
                <checkbox 
                  :value="option.value" 
                  :checked="answers[index] && answers[index].includes(option.value)"
                  color="#1E88E5"
                />
                <text>{{ option.label }}</text>
              </label>
            </checkbox-group>

            <!-- 特殊提示 -->
            <view v-if="question.tip" class="question-tip">
              {{ question.tip }}
            </view>
          </view>

          <!-- 风险评估结果 -->
          <view v-if="showResult" class="result-container">
            <view class="result-title">F. 风险评估结果</view>
            <view class="result-box">
              <view class="result-label">当前风险等级</view>
              <view :class="['risk-level', riskLevel]">
                {{ riskLevel === 'low' ? '低风险' : riskLevel === 'medium' ? '中风险' : '高风险' }}
              </view>
              <view class="result-desc">
                年龄45-75岁且具备1项高危风险判定为高风险
              </view>
            </view>
          </view>

          <!-- 预约信息 -->
          <view class="appointment-section">
            <view class="appointment-label">预约血液筛查机构</view>
            <picker 
              :range="hospitalOptions"
              range-key="label"
              :value="selectedHospital"
              @change="onHospitalChange"
            >
              <view class="picker-input">
                {{ hospitalOptions[selectedHospital].label }}
              </view>
            </picker>
          </view>

          <view class="appointment-section">
            <view class="appointment-label">预约到达时间</view>
            <picker 
              mode="date"
              :value="appointmentDate"
              @change="onAppointmentDateChange"
            >
              <view class="picker-input date-picker">
                {{ appointmentDate || '年-月-日' }}
              </view>
            </picker>
            <view class="appointment-tip">预约血液筛查需要为空腹，为必须项</view>
          </view>
        </view>
      </scroll-view>

      <!-- 提交按钮 -->
      <view class="action-button" @click="submitSurvey">
        <text>提交问卷</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      statusBarHeight: 0,
      scrollTop: 0,
      currentPage: 3,
      totalPages: 3,
      answers: {},
      questions: [
        {
          title: 'A. 吸烟史',
          type: 'radio',
          options: [
            { label: '是', value: 'yes' },
            { label: '否', value: 'no' }
          ],
          tip: '选择"是"判定为高风险'
        },
        {
          title: 'A2. 饮酒史',
          type: 'radio',
          options: [
            { label: '是', value: 'yes' },
            { label: '否', value: 'no' }
          ],
          tip: '选择"是"判定为高风险'
        },
        {
          title: 'B2. 幽门螺杆菌感染史',
          type: 'radio',
          options: [
            { label: '感染过且已经治疗', value: 'treated' },
            { label: '感染过但未治疗', value: 'untreated' },
            { label: '检查过未感染', value: 'no' },
            { label: '未检查或情况不明', value: 'unknown' }
          ],
          tip: '除"未检查"外均判定高风险'
        },
        {
          title: 'C. 一级/二级亲属病史',
          type: 'radio',
          options: [
            { label: '胃癌', value: 'gastric' },
            { label: '结直肠癌', value: 'colorectal' },
            { label: '其他相关癌症', value: 'other' },
            { label: '以上皆无', value: 'none' }
          ],
          tip: '选"以上皆无"外均判定高风险'
        },
        {
          title: 'D. 是否经常食用以下食品',
          type: 'checkbox',
          options: [
            { label: '泡菜', value: 'pickle' },
            { label: '烧烤', value: 'bbq' },
            { label: '腌肉腊肠', value: 'cured' },
            { label: '咸鱼', value: 'fish' },
            { label: '咸菜', value: 'vegetable' },
            { label: '以上皆无', value: 'none' }
          ],
          tip: '选择任何食品判定为高风险'
        },
        {
          title: 'E. 一年内是否做过胃镜',
          type: 'radio',
          options: [
            { label: '是', value: 'yes' },
            { label: '否', value: 'no' }
          ],
          tip: '选择"否"判定为高风险'
        }
      ],
      showResult: false,
      riskLevel: 'low', // low, medium, high
      selectedHospital: 0,
      hospitalOptions: [
        { label: '濂溪区人民医院', value: '1' },
        { label: '濂溪区中医院', value: '2' },
        { label: '濂溪区妇幼保健院', value: '3' }
      ],
      appointmentDate: ''
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
    onAnswerChange(index, value) {
      this.answers[index] = value;
      this.$forceUpdate();
      this.calculateRisk();
    },
    onCheckboxChange(index, values) {
      this.answers[index] = values;
      this.$forceUpdate();
      this.calculateRisk();
    },
    onPickerChange(index, optionIndex) {
      this.answers[index] = optionIndex;
      this.$forceUpdate();
    },
    onDateChange(index, value) {
      this.answers[index] = value;
      this.$forceUpdate();
    },
    onHospitalChange(e) {
      this.selectedHospital = e.detail.value;
      this.$forceUpdate();
    },
    onAppointmentDateChange(e) {
      this.appointmentDate = e.detail.value;
      this.$forceUpdate();
    },
    calculateRisk() {
      // 简单的风险计算逻辑，根据实际需求调整
      let hasHighRisk = false;
      
      // 检查是否有高风险因素
      if (this.answers[0] === 'yes') hasHighRisk = true; // 吸烟
      if (this.answers[1] === 'yes') hasHighRisk = true; // 饮酒
      if (this.answers[2] && this.answers[2] !== 'unknown') hasHighRisk = true; // 幽门螺杆菌
      if (this.answers[3] && this.answers[3] !== 'none') hasHighRisk = true; // 亲属病史
      if (this.answers[4] && this.answers[4].length > 0 && !this.answers[4].includes('none')) hasHighRisk = true; // 饮食
      if (this.answers[5] === 'no') hasHighRisk = true; // 未做胃镜
      
      this.riskLevel = hasHighRisk ? 'high' : 'low';
      this.showResult = true;
    },
    submitSurvey() {
      // 验证是否所有问题都已回答
      const unanswered = this.questions.filter((q, index) => {
        const answer = this.answers[index];
        if (q.type === 'checkbox') {
          return !answer || answer.length === 0;
        }
        return !answer && answer !== 0;
      });
      
      if (unanswered.length > 0) {
        uni.showToast({
          title: '请完成所有问题',
          icon: 'none'
        });
        return;
      }

      // 验证预约信息
      if (!this.appointmentDate) {
        uni.showToast({
          title: '请选择预约到达时间',
          icon: 'none'
        });
        return;
      }

      // 提交问卷
      uni.showModal({
        title: '提交成功',
        content: '您的问卷已提交，我们将尽快为您安排筛查',
        showCancel: false,
        success: () => {
          // 返回首页
          uni.navigateBack({
            delta: 3
          });
        }
      });
    }
  }
}
</script>

<style scoped>
.risk-page {
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
  text-align: right;
}

.page-indicator {
  font-size: 28rpx;
  color: #1E88E5;
}

/* 页面内容 */
.page-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
}

/* 滚动区域 */
.question-scroll {
  flex: 1;
  height: 100%;
  box-sizing: border-box;
}

/* 滚动内容容器 */
.scroll-content {
  padding: 30rpx;
  min-height: 100%;
  box-sizing: border-box;
}

/* 问题项 */
.question-item {
  margin-bottom: 30rpx;
  padding: 30rpx;
  background: #ffffff;
  border-radius: 16rpx;
  will-change: transform;
}

.question-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 24rpx;
}

/* 选项 */
.option-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  color: #333333;
}

.option-label:last-child {
  margin-bottom: 0;
}

/* 问题提示 */
.question-tip {
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #FF9800;
}

.extra-tip {
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #666666;
}

/* 风险评估结果 */
.result-container {
  margin-bottom: 30rpx;
  padding: 30rpx;
  background: #E0F7FA;
  border-radius: 16rpx;
  will-change: transform;
}

.result-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 24rpx;
}

.result-box {
  padding: 30rpx 20rpx;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.result-label {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 16rpx;
}

.risk-level {
  font-size: 48rpx;
  font-weight: bold;
  margin-bottom: 16rpx;
  word-wrap: break-word;
  text-align: center;
}

.risk-level.low {
  color: #4CAF50;
}

.risk-level.medium {
  color: #FF9800;
}

.risk-level.high {
  color: #F44336;
}

.result-desc {
  font-size: 24rpx;
  color: #666666;
  line-height: 1.8;
  word-wrap: break-word;
  white-space: normal;
  text-align: center;
  max-width: 100%;
}

/* 选择器输入框 */
.picker-input {
  height: 88rpx;
  padding: 0 24rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333333;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.picker-input::after {
  content: '▼';
  font-size: 20rpx;
  color: #999999;
}

.date-picker::after {
  content: '📅';
  font-size: 24rpx;
}

/* 预约信息 */
.appointment-section {
  margin-bottom: 30rpx;
}

.appointment-label {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.appointment-tip {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #FF9800;
  line-height: 1.6;
}

/* 提交按钮 */
.action-button {
  margin: 0 30rpx 30rpx;
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
