<template>
  <view class="questionnaire-container">
    <!-- 进度条 -->
    <view class="progress-bar">
      <view class="progress" :style="{ width: progressPercent + '%' }"></view>
    </view>
    <view class="progress-text">{{ currentSectionIndex + 1 }}/{{ sections.length }}</view>

    <!-- 问卷内容 -->
    <view v-if="!loading && sections.length > 0" class="content">
      <view class="section">
        <view class="section-title">{{ currentSection.sectionTitle }}</view>

        <view 
          v-for="(question, qIndex) in currentSection.questions" 
          :key="question.questionId"
          class="question-item"
        >
          <view class="question-text">
            <text class="question-num">{{ qIndex + 1 }}.</text>
            <text>{{ question.questionText }}</text>
            <text v-if="question.required" class="required">*</text>
          </view>

          <!-- 单选题 -->
          <radio-group 
            v-if="question.questionType === 'radio'" 
            @change="onRadioChange($event, question.questionId)"
          >
            <label 
              v-for="option in question.options" 
              :key="option.optionId"
              class="option-item"
            >
              <radio 
                :value="option.optionId" 
                :checked="isOptionSelected(question.questionId, option.optionId)"
                color="#667eea"
              />
              <text>{{ option.optionText }}</text>
            </label>
          </radio-group>

          <!-- 多选题 -->
          <checkbox-group 
            v-if="question.questionType === 'checkbox'" 
            @change="onCheckboxChange($event, question.questionId)"
          >
            <label 
              v-for="option in question.options" 
              :key="option.optionId"
              class="option-item"
            >
              <checkbox 
                :value="option.optionId" 
                :checked="isOptionSelected(question.questionId, option.optionId)"
                color="#667eea"
              />
              <text>{{ option.optionText }}</text>
            </label>
          </checkbox-group>

          <!-- 输入题 -->
          <textarea 
            v-if="question.questionType === 'text'"
            class="text-input"
            v-model="answers[question.questionId]"
            :placeholder="question.placeholder || '请输入您的答案'"
            maxlength="500"
          ></textarea>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && sections.length === 0" class="empty">
      <text>暂无可用问卷</text>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>

    <!-- 底部按钮 -->
    <view class="footer">
      <button 
        v-if="currentSectionIndex > 0" 
        class="btn btn-secondary" 
        @tap="prevSection"
      >
        上一步
      </button>
      <button 
        v-if="currentSectionIndex < sections.length - 1" 
        class="btn btn-primary" 
        @tap="nextSection"
      >
        下一步
      </button>
      <button 
        v-if="currentSectionIndex === sections.length - 1" 
        class="btn btn-submit" 
        @tap="handleSubmit"
      >
        提交问卷
      </button>
    </view>
  </view>
</template>

<script>
import { getActiveTemplate, submitQuestionnaire } from '@/api/gc.js';

export default {
  data() {
    return {
      loading: true,
      residentId: null,
      templateId: null,
      templateData: null,
      sections: [],
      currentSectionIndex: 0,
      answers: {}, // { questionId: selectedOptions[] or text }
      surveyorId: null // 调查员ID（协助填写时）
    };
  },

  computed: {
    currentSection() {
      return this.sections[this.currentSectionIndex] || {};
    },
    progressPercent() {
      return ((this.currentSectionIndex + 1) / this.sections.length) * 100;
    }
  },

  onLoad(options) {
    this.residentId = options.residentId;
    this.surveyorId = options.surveyorId || null;
    this.loadTemplate();
  },

  methods: {
    // 加载问卷模板
    async loadTemplate() {
      try {
        this.loading = true;
        const res = await getActiveTemplate();
        
        if (res.code === 200 && res.data) {
          this.templateId = res.data.templateId;
          this.templateData = res.data;
          
          // 解析问卷内容
          const content = typeof res.data.templateContent === 'string' 
            ? JSON.parse(res.data.templateContent) 
            : res.data.templateContent;
          
          this.sections = content.sections || [];
          
          // 初始化答案对象
          this.initAnswers();
        } else {
          uni.showToast({
            title: '暂无可用问卷',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('加载问卷失败', error);
        uni.showToast({
          title: '加载问卷失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 初始化答案对象
    initAnswers() {
      this.sections.forEach(section => {
        section.questions.forEach(question => {
          if (question.questionType === 'checkbox') {
            this.answers[question.questionId] = [];
          } else if (question.questionType === 'radio') {
            this.answers[question.questionId] = '';
          } else {
            this.answers[question.questionId] = '';
          }
        });
      });
    },

    // 单选改变
    onRadioChange(e, questionId) {
      this.answers[questionId] = e.detail.value;
    },

    // 多选改变
    onCheckboxChange(e, questionId) {
      this.answers[questionId] = e.detail.value;
    },

    // 判断选项是否被选中
    isOptionSelected(questionId, optionId) {
      const answer = this.answers[questionId];
      if (Array.isArray(answer)) {
        return answer.includes(optionId);
      }
      return answer === optionId;
    },

    // 验证当前分节
    validateCurrentSection() {
      const questions = this.currentSection.questions || [];
      
      for (let question of questions) {
        if (question.required) {
          const answer = this.answers[question.questionId];
          
          if (!answer || (Array.isArray(answer) && answer.length === 0)) {
            uni.showToast({
              title: `请回答：${question.questionText}`,
              icon: 'none',
              duration: 2000
            });
            return false;
          }
        }
      }
      
      return true;
    },

    // 上一步
    prevSection() {
      if (this.currentSectionIndex > 0) {
        this.currentSectionIndex--;
        // 滚动到顶部
        uni.pageScrollTo({
          scrollTop: 0,
          duration: 300
        });
      }
    },

    // 下一步
    nextSection() {
      if (!this.validateCurrentSection()) {
        return;
      }

      if (this.currentSectionIndex < this.sections.length - 1) {
        this.currentSectionIndex++;
        // 滚动到顶部
        uni.pageScrollTo({
          scrollTop: 0,
          duration: 300
        });
      }
    },

    // 提交问卷
    async handleSubmit() {
      // 验证最后一个分节
      if (!this.validateCurrentSection()) {
        return;
      }

      // 构建答案JSON
      const answerContent = {
        answers: []
      };

      Object.keys(this.answers).forEach(questionId => {
        const answer = this.answers[questionId];
        let selectedOptions = [];

        if (Array.isArray(answer)) {
          selectedOptions = answer;
        } else if (answer) {
          selectedOptions = [answer];
        }

        if (selectedOptions.length > 0) {
          answerContent.answers.push({
            questionId: questionId,
            selectedOptions: selectedOptions
          });
        }
      });

      try {
        uni.showLoading({ title: '提交中...' });

        const submitData = {
          residentId: this.residentId,
          templateId: this.templateId,
          answerContent: JSON.stringify(answerContent),
          surveyorId: this.surveyorId
        };

        const res = await submitQuestionnaire(submitData);
        uni.hideLoading();

        if (res.code === 200) {
          // 显示评分结果
          uni.showModal({
            title: '提交成功',
            content: `您的评分：${res.data.totalScore}分\n${res.data.isFocusGroup ? '您已被标记为重点人群，请及时预约采血' : '感谢您的配合'}`,
            showCancel: false,
            success: () => {
              // 跳转到采血预约页面
              if (res.data.isFocusGroup) {
                uni.redirectTo({
                  url: `/pages/appointment/create?residentId=${this.residentId}`
                });
              } else {
                uni.switchTab({
                  url: '/pages/index/index'
                });
              }
            }
          });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('提交问卷失败', error);
      }
    }
  }
};
</script>

<style scoped>
.questionnaire-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 150rpx;
}

.progress-bar {
  width: 100%;
  height: 8rpx;
  background: #e0e0e0;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
}

.progress {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s;
}

.progress-text {
  text-align: center;
  padding: 20rpx 0;
  font-size: 24rpx;
  color: #999999;
  background: #ffffff;
}

.content {
  padding: 30rpx;
}

.section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 40rpx;
}

.section-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 40rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.question-item {
  margin-bottom: 50rpx;
}

.question-text {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 25rpx;
  line-height: 1.6;
}

.question-num {
  font-weight: bold;
  margin-right: 10rpx;
}

.required {
  color: #ff4d4f;
  margin-left: 5rpx;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  margin-bottom: 15rpx;
  background: #f8f8f8;
  border-radius: 10rpx;
  font-size: 26rpx;
  color: #666666;
}

.option-item radio,
.option-item checkbox {
  margin-right: 15rpx;
}

.text-input {
  width: 100%;
  min-height: 150rpx;
  padding: 20rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
  font-size: 26rpx;
  background: #f8f8f8;
}

.empty, .loading {
  text-align: center;
  padding: 200rpx 0;
  color: #999999;
  font-size: 28rpx;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 20rpx 30rpx;
  background: #ffffff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  display: flex;
  gap: 20rpx;
}

.btn {
  flex: 1;
  height: 90rpx;
  line-height: 90rpx;
  border-radius: 10rpx;
  font-size: 30rpx;
  border: none;
}

.btn-secondary {
  background: #f0f0f0;
  color: #666666;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
}

.btn-submit {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #ffffff;
}
</style>
