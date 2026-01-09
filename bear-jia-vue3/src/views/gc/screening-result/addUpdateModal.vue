<template>
  <a-modal
    v-model:open="visible"
    :title="modalTitle"
    :width="800"
    @cancel="handleCancel"
    @ok="handleOk"
  >
    <a-form ref="formRef" :label-col="{ span: 6 }" :model="formData" :rules="rules" :wrapper-col="{ span: 16 }">
      <a-row :gutter="16">
        <a-col :span="24">
          <a-form-item label="居民" name="residentId">
            <a-select
              v-model:value="formData.residentId"
              placeholder="请选择居民"
              show-search
              :filter-option="filterOption"
            >
              <a-select-option v-for="item in residentOptions" :key="item.residentId" :value="item.residentId">
                {{ item.residentName }} - {{ item.idCard }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="筛查类型" name="screeningType">
            <a-select v-model:value="formData.screeningType" placeholder="请选择筛查类型">
              <a-select-option value="1">血液筛查</a-select-option>
              <a-select-option value="2">胃镜筛查</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="筛查日期" name="screeningDate">
            <a-date-picker
              v-model:value="formData.screeningDate"
              placeholder="请选择筛查日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="风险等级" name="riskLevel">
            <a-select v-model:value="formData.riskLevel" placeholder="请选择风险等级">
              <a-select-option value="0">未检测</a-select-option>
              <a-select-option value="1">低风险</a-select-option>
              <a-select-option value="2">中风险</a-select-option>
              <a-select-option value="3">高风险</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="需要随访" name="needFollowUp">
            <a-radio-group v-model:value="formData.needFollowUp">
              <a-radio :value="1">是</a-radio>
              <a-radio :value="0">否</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="24">
          <a-form-item label="筛查结果" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }" name="resultData">
            <a-textarea v-model:value="formData.resultData" :rows="4" placeholder="请输入筛查结果详情" />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="24">
          <a-form-item label="备注" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
            <a-textarea v-model:value="formData.remark" :rows="3" placeholder="请输入备注" />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { addScreeningResult, updateScreeningResult, getScreeningResult } from '@/api/gc';
import { listResident } from '@/api/gc';
import { message } from 'ant-design-vue';

const emit = defineEmits(['refresh-father-page-table']);

const visible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const residentOptions = ref([]);

const formData = reactive({
  resultId: null,
  residentId: null,
  screeningType: null,
  screeningDate: null,
  riskLevel: null,
  needFollowUp: 0,
  resultData: null,
  remark: null,
});

const rules = {
  residentId: [{ required: true, message: '请选择居民', trigger: 'change' }],
  screeningType: [{ required: true, message: '请选择筛查类型', trigger: 'change' }],
  screeningDate: [{ required: true, message: '请选择筛查日期', trigger: 'change' }],
  riskLevel: [{ required: true, message: '请选择风险等级', trigger: 'change' }],
  needFollowUp: [{ required: true, message: '请选择是否需要随访', trigger: 'change' }],
};

const modalTitle = computed(() => (isEdit.value ? '编辑筛查结果' : '新增筛查结果'));

const openAddModal = () => {
  resetForm();
  isEdit.value = false;
  visible.value = true;
  loadResidentOptions();
};

const openUpdateModal = async (record) => {
  resetForm();
  isEdit.value = true;
  visible.value = true;
  loadResidentOptions();

  try {
    const response = await getScreeningResult(record.resultId);
    Object.assign(formData, response.data);
  } catch (error) {
    message.error('获取筛查结果信息失败');
  }
};

const resetForm = () => {
  formData.resultId = null;
  formData.residentId = null;
  formData.screeningType = null;
  formData.screeningDate = null;
  formData.riskLevel = null;
  formData.needFollowUp = 0;
  formData.resultData = null;
  formData.remark = null;
  formRef.value?.resetFields();
};

const loadResidentOptions = async () => {
  try {
    const response = await listResident({ pageNum: 1, pageSize: 1000 });
    residentOptions.value = response.rows || [];
  } catch (error) {
    console.error('加载居民列表失败', error);
  }
};

const filterOption = (input, option) => {
  return option.children[0].toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const handleCancel = () => {
  visible.value = false;
  resetForm();
};

const handleOk = async () => {
  try {
    await formRef.value.validate();

    if (isEdit.value) {
      await updateScreeningResult(formData);
      message.success('修改成功');
    } else {
      await addScreeningResult(formData);
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
