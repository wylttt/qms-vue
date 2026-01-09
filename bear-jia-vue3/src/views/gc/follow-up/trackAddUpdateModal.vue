<template>
  <a-modal
    v-model:open="visible"
    :title="isEdit ? '编辑跟踪记录' : '新增跟踪记录'"
    :width="700"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
    >
      <a-form-item
        label="随访对象"
        name="followUpId"
        :rules="[{ required: true, message: '请选择随访对象' }]"
      >
        <a-select
          v-model:value="formData.followUpId"
          placeholder="请选择随访对象"
          show-search
          :filter-option="filterFollowUpOption"
          @change="handleFollowUpChange"
        >
          <a-select-option
            v-for="item in followUpList"
            :key="item.followUpId"
            :value="item.followUpId"
          >
            {{ item.residentName }} - {{ getFollowUpTypeText(item.followUpType) }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item
        label="跟踪日期"
        name="trackDate"
        :rules="[{ required: true, message: '请选择跟踪日期' }]"
      >
        <a-date-picker
          v-model:value="formData.trackDate"
          placeholder="请选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </a-form-item>

      <a-form-item
        label="跟踪方式"
        name="trackMethod"
        :rules="[{ required: true, message: '请选择跟踪方式' }]"
      >
        <a-select v-model:value="formData.trackMethod" placeholder="请选择跟踪方式">
          <a-select-option value="1">电话跟踪</a-select-option>
          <a-select-option value="2">上门跟踪</a-select-option>
          <a-select-option value="3">短信跟踪</a-select-option>
          <a-select-option value="4">微信跟踪</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item
        label="跟踪结果"
        name="trackResult"
        :rules="[{ required: true, message: '请选择跟踪结果' }]"
      >
        <a-select v-model:value="formData.trackResult" placeholder="请选择跟踪结果">
          <a-select-option value="1">联系成功</a-select-option>
          <a-select-option value="2">无法联系</a-select-option>
          <a-select-option value="3">拒绝随访</a-select-option>
          <a-select-option value="4">同意随访</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="跟踪人员" name="trackPerson">
        <a-input v-model:value="formData.trackPerson" placeholder="请输入跟踪人员" />
      </a-form-item>

      <a-form-item
        label="跟踪内容"
        name="trackContent"
        :rules="[{ required: true, message: '请输入跟踪内容' }]"
      >
        <a-textarea
          v-model:value="formData.trackContent"
          placeholder="请输入跟踪内容"
          :rows="4"
          :maxlength="500"
          show-count
        />
      </a-form-item>

      <a-form-item label="备注" name="remark">
        <a-textarea
          v-model:value="formData.remark"
          placeholder="请输入备注"
          :rows="3"
          :maxlength="200"
          show-count
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { addFollowUpTrack, updateFollowUpTrack, listFollowUp } from '@/api/gc';

const emit = defineEmits(['refresh-father-page-table']);

const visible = ref(false);
const isEdit = ref(false);
const formRef = ref(null);
const followUpList = ref([]);

const formData = reactive({
  trackId: undefined,
  followUpId: undefined,
  trackDate: undefined,
  trackMethod: undefined,
  trackResult: undefined,
  trackPerson: '',
  trackContent: '',
  remark: '',
});

// 打开新增弹窗
const openAddModal = async () => {
  visible.value = true;
  isEdit.value = false;

  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields();
  }

  // 加载随访对象列表
  await loadFollowUpList();

  // 新增模式，设置默认值
  Object.assign(formData, {
    trackId: undefined,
    followUpId: undefined,
    trackDate: undefined,
    trackMethod: undefined,
    trackResult: undefined,
    trackPerson: '',
    trackContent: '',
    remark: '',
  });
};

// 打开编辑弹窗
const openUpdateModal = async (record) => {
  visible.value = true;
  isEdit.value = true;

  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields();
  }

  // 加载随访对象列表
  await loadFollowUpList();

  // 编辑模式，填充数据
  Object.assign(formData, {
    trackId: record.trackId,
    followUpId: record.followUpId,
    trackDate: record.trackDate,
    trackMethod: record.trackMethod,
    trackResult: record.trackResult,
    trackPerson: record.trackPerson,
    trackContent: record.trackContent,
    remark: record.remark,
  });
};

// 加载随访对象列表
const loadFollowUpList = async () => {
  try {
    const response = await listFollowUp({ pageNum: 1, pageSize: 1000 });
    followUpList.value = response.rows || [];
  } catch (error) {
    message.error('加载随访对象列表失败');
  }
};

// 随访对象筛选
const filterFollowUpOption = (input, option) => {
  const text = option.children[0];
  return text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

// 随访对象选择变化
const handleFollowUpChange = (value) => {
  // 可以在这里做一些额外处理
};

// 随访类型文本
const getFollowUpTypeText = (type) => {
  const typeMap = {
    '1': '问卷随访',
    '2': '筛查随访',
    '3': '高风险随访',
  };
  return typeMap[type] || '';
};

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate();

    const data = { ...formData };

    if (isEdit.value) {
      await updateFollowUpTrack(data);
      message.success('编辑成功');
    } else {
      await addFollowUpTrack(data);
      message.success('新增成功');
    }

    visible.value = false;
    emit('refresh-father-page-table');
  } catch (error) {
    if (error.errorFields) {
      message.error('请完善表单信息');
    } else {
      message.error(isEdit.value ? '编辑失败' : '新增失败');
    }
  }
};

// 关闭弹窗
const handleClose = () => {
  visible.value = false;
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

defineExpose({
  openAddModal,
  openUpdateModal,
});
</script>

<style scoped lang="less">
:deep(.ant-form-item) {
  margin-bottom: 16px;
}
</style>
