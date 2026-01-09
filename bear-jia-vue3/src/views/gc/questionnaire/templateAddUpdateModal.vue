<template>
  <a-modal
    v-model:open="visible"
    :title="modalTitle"
    :width="900"
    @cancel="handleCancel"
    @ok="handleOk"
  >
    <a-form ref="formRef" :label-col="{ span: 4 }" :model="formData" :rules="rules">
      <a-form-item label="问卷名称" name="questionnaireName">
        <a-input v-model:value="formData.questionnaireName" placeholder="请输入问卷名称" />
      </a-form-item>
      <a-form-item label="问卷描述" name="questionnaireDescription">
        <a-textarea v-model:value="formData.questionnaireDescription" :rows="3" placeholder="请输入问卷描述" />
      </a-form-item>
      <a-form-item label="版本号" name="version">
        <a-input v-model:value="formData.version" placeholder="如：v1.0" />
      </a-form-item>
      <a-form-item label="问题配置" name="questionnaireContent">
        <a-button type="dashed" block @click="addQuestion">
          <BearJiaIcon icon="plus-outlined" />添加问题
        </a-button>
        <a-list v-if="questions.length > 0" :data-source="questions" style="margin-top: 16px">
          <template #renderItem="{ item, index }">
            <a-list-item>
              <template #actions>
                <a @click="editQuestion(index)">编辑</a>
                <a @click="deleteQuestion(index)" style="color: red">删除</a>
              </template>
              <a-list-item-meta>
                <template #title>
                  {{ index + 1 }}. {{ item.questionText }}
                  <a-tag style="margin-left: 8px">{{ item.questionType === '1' ? '单选' : '多选' }}</a-tag>
                  <a-tag v-if="item.score" color="blue">{{ item.score }}分</a-tag>
                </template>
                <template #description>
                  选项：{{ item.options?.join('、') || '无' }}
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </a-form-item>
    </a-form>

    <!-- 问题编辑弹窗 -->
    <QuestionEditModal
      ref="questionEditModalRef"
      @confirm="handleQuestionConfirm"
    />
  </a-modal>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { addQuestionnaire, updateQuestionnaire, getQuestionnaire } from '@/api/gc';
import { BearJiaIcon } from '@/utils/BearJiaIcon.js';
import QuestionEditModal from './questionEditModal.vue';
import { message } from 'ant-design-vue';

const emit = defineEmits(['refresh-father-page-table']);

const visible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const questionEditModalRef = ref();

const formData = reactive({
  questionnaireId: null,
  questionnaireName: null,
  questionnaireDescription: null,
  version: null,
  questionnaireContent: null,
});

const questions = ref([]);
const editingQuestionIndex = ref(-1);

const rules = {
  questionnaireName: [{ required: true, message: '请输入问卷名称', trigger: 'blur' }],
  version: [{ required: true, message: '请输入版本号', trigger: 'blur' }],
};

const modalTitle = computed(() => (isEdit.value ? '编辑问卷模板' : '新增问卷模板'));

const openAddModal = () => {
  resetForm();
  isEdit.value = false;
  visible.value = true;
};

const openUpdateModal = async (record) => {
  resetForm();
  isEdit.value = true;
  visible.value = true;

  try {
    const response = await getQuestionnaire(record.questionnaireId);
    Object.assign(formData, response.data);
    
    // 解析问卷内容
    if (formData.questionnaireContent) {
      try {
        questions.value = JSON.parse(formData.questionnaireContent);
      } catch (e) {
        questions.value = [];
      }
    }
  } catch (error) {
    message.error('获取问卷信息失败');
  }
};

const resetForm = () => {
  formData.questionnaireId = null;
  formData.questionnaireName = null;
  formData.questionnaireDescription = null;
  formData.version = null;
  formData.questionnaireContent = null;
  questions.value = [];
  formRef.value?.resetFields();
};

const addQuestion = () => {
  editingQuestionIndex.value = -1;
  questionEditModalRef.value.openModal();
};

const editQuestion = (index) => {
  editingQuestionIndex.value = index;
  questionEditModalRef.value.openModal(questions.value[index]);
};

const deleteQuestion = (index) => {
  questions.value.splice(index, 1);
};

const handleQuestionConfirm = (questionData) => {
  if (editingQuestionIndex.value >= 0) {
    questions.value[editingQuestionIndex.value] = questionData;
  } else {
    questions.value.push(questionData);
  }
};

const handleCancel = () => {
  visible.value = false;
  resetForm();
};

const handleOk = async () => {
  try {
    await formRef.value.validate();

    if (questions.value.length === 0) {
      message.warning('请至少添加一个问题');
      return;
    }

    formData.questionnaireContent = JSON.stringify(questions.value);

    if (isEdit.value) {
      await updateQuestionnaire(formData);
      message.success('修改成功');
    } else {
      await addQuestionnaire(formData);
      message.success('新增成功');
    }

    visible.value = false;
    emit('refresh-father-page-table');
    resetForm();
  } catch (error) {
    if (error.errorFields) {
      message.warning('请完善表单信息');
    } else {
      message.error(isEdit.value ? '修改失败' : '新增失败');
    }
  }
};

defineExpose({
  openAddModal,
  openUpdateModal,
});
</script>
