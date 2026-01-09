<template>
  <a-modal
    v-model:open="visible"
    :title="isEdit ? '编辑随访对象' : '新增随访对象'"
    :width="800"
    @ok="handleSubmit"
    @cancel="handleClose"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
    >
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item
            label="居民姓名"
            name="residentId"
            :rules="[{ required: true, message: '请选择居民' }]"
          >
            <a-select
              v-model:value="formData.residentId"
              placeholder="请选择居民"
              show-search
              :filter-option="filterResidentOption"
              @change="handleResidentChange"
            >
              <a-select-option
                v-for="resident in residentList"
                :key="resident.residentId"
                :value="resident.residentId"
              >
                {{ resident.name }} - {{ resident.idCard }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="联系电话">
            <a-input v-model:value="formData.phone" disabled placeholder="自动填充" />
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item
            label="随访类型"
            name="followUpType"
            :rules="[{ required: true, message: '请选择随访类型' }]"
          >
            <a-select v-model:value="formData.followUpType" placeholder="请选择随访类型">
              <a-select-option value="1">问卷随访</a-select-option>
              <a-select-option value="2">筛查随访</a-select-option>
              <a-select-option value="3">高风险随访</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item
            label="随访原因"
            name="followUpReason"
            :rules="[{ required: true, message: '请输入随访原因' }]"
          >
            <a-input v-model:value="formData.followUpReason" placeholder="请输入随访原因" />
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item
            label="计划随访日期"
            name="planDate"
            :rules="[{ required: true, message: '请选择计划随访日期' }]"
          >
            <a-date-picker
              v-model:value="formData.planDate"
              placeholder="请选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="随访状态" name="followUpStatus">
            <a-select v-model:value="formData.followUpStatus" placeholder="请选择状态">
              <a-select-option value="0">待随访</a-select-option>
              <a-select-option value="1">随访中</a-select-option>
              <a-select-option value="2">已完成</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="随访方式" name="followUpMethod">
            <a-select v-model:value="formData.followUpMethod" placeholder="请选择随访方式">
              <a-select-option value="1">电话随访</a-select-option>
              <a-select-option value="2">上门随访</a-select-option>
              <a-select-option value="3">短信随访</a-select-option>
              <a-select-option value="4">微信随访</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>

        <a-col :span="12">
          <a-form-item label="负责人" name="followUpPerson">
            <a-input v-model:value="formData.followUpPerson" placeholder="请输入负责人" />
          </a-form-item>
        </a-col>

        <a-col :span="24">
          <a-form-item
            label="备注"
            name="remark"
            :label-col="{ span: 3 }"
            :wrapper-col="{ span: 20 }"
          >
            <a-textarea
              v-model:value="formData.remark"
              placeholder="请输入备注"
              :rows="3"
              :maxlength="200"
              show-count
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { message } from 'ant-design-vue';
import { addFollowUp, updateFollowUp } from '@/api/gc';
import { listResident } from '@/api/gc';

const emit = defineEmits(['refresh-father-page-table']);

const visible = ref(false);
const isEdit = ref(false);
const formRef = ref(null);
const residentList = ref([]);

const formData = reactive({
  followUpId: undefined,
  residentId: undefined,
  residentName: '',
  phone: '',
  followUpType: undefined,
  followUpReason: '',
  planDate: undefined,
  followUpStatus: '0',
  followUpMethod: undefined,
  followUpPerson: '',
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

  // 加载居民列表
  await loadResidentList();

  // 新增模式，设置默认值
  Object.assign(formData, {
    followUpId: undefined,
    residentId: undefined,
    residentName: '',
    phone: '',
    followUpType: undefined,
    followUpReason: '',
    planDate: undefined,
    followUpStatus: '0',
    followUpMethod: undefined,
    followUpPerson: '',
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

  // 加载居民列表
  await loadResidentList();

  // 编辑模式，填充数据
  Object.assign(formData, {
    followUpId: record.followUpId,
    residentId: record.residentId,
    residentName: record.residentName,
    phone: record.phone,
    followUpType: record.followUpType,
    followUpReason: record.followUpReason,
    planDate: record.planDate,
    followUpStatus: record.followUpStatus,
    followUpMethod: record.followUpMethod,
    followUpPerson: record.followUpPerson,
    remark: record.remark,
  });
};

// 加载居民列表
const loadResidentList = async () => {
  try {
    const response = await listResident({ pageNum: 1, pageSize: 1000 });
    residentList.value = response.rows || [];
  } catch (error) {
    message.error('加载居民列表失败');
  }
};

// 居民筛选
const filterResidentOption = (input, option) => {
  const text = option.children[0];
  return text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

// 居民选择变化
const handleResidentChange = (value) => {
  const resident = residentList.value.find((r) => r.residentId === value);
  if (resident) {
    formData.residentName = resident.name;
    formData.phone = resident.phone;
  }
};

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate();

    const data = { ...formData };

    if (isEdit.value) {
      await updateFollowUp(data);
      message.success('编辑成功');
    } else {
      await addFollowUp(data);
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
