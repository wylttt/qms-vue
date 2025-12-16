<template>
  <view class="page-container">
    <QuestionnaireForm
      :residentId="residentId"
      :surveyorId="surveyorId"
      @submit="handleSubmitSuccess"
    />
  </view>
</template>

<script>
import QuestionnaireForm from '@/components/QuestionnaireForm.vue';

export default {
  components: {
    QuestionnaireForm
  },
  
  data() {
    return {
      residentId: null,
      surveyorId: null
    };
  },

  onLoad(options) {
    this.residentId = options.residentId;
    this.surveyorId = options.surveyorId || null;
  },

  methods: {
    // 处理提交成功
    handleSubmitSuccess(result) {
      const { totalScore, isFocusGroup } = result;
      
      // 显示评分结果
      uni.showModal({
        title: '提交成功',
        content: `您的评分：${totalScore}分\n${isFocusGroup ? '您已被标记为重点人群，请及时预约采血' : '感谢您的配合'}`,
        showCancel: false,
        success: () => {
          // 跳转到采血预约页面
          if (isFocusGroup) {
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
  }
};
</script>

<style scoped>
.page-container {
  width: 100%;
  height: 100vh;
}
</style>
