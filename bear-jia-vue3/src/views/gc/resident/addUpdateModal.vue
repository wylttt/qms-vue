<template>
  <a-modal
    v-model:open="visible"
    :title="modalTitle"
    :width="800"
    @cancel="handleCancel"
    @ok="handleOk"
  >
    <a-form
      ref="formRef"
      :label-col="{ span: 6 }"
      :model="formData"
      :rules="rules"
      :wrapper-col="{ span: 16 }"
    >
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="姓名" name="residentName">
            <a-input v-model:value="formData.residentName" placeholder="请输入姓名" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="性别" name="sex">
            <a-select v-model:value="formData.sex" placeholder="请选择性别">
              <a-select-option
                v-for="item in gcSexDict"
                :key="item.value"
                :value="item.value"
              >
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="身份证号" name="idCard">
            <a-input v-model:value="formData.idCard" placeholder="请输入身份证号" @blur="parseIdCard" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="出生日期" name="birthDate">
            <a-date-picker
              v-model:value="formData.birthDate"
              placeholder="请选择出生日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="联系电话" name="phone">
            <a-input v-model:value="formData.phone" placeholder="请输入联系电话" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="年龄" name="age">
            <a-input-number
              v-model:value="formData.age"
              :max="150"
              :min="0"
              placeholder="请输入年龄"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="24">
          <a-form-item label="居住地址" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
            <a-space direction="vertical" style="width: 100%">
              <a-cascader
                v-model:value="addressCascader"
                :options="addressOptions"
                :field-names="{ label: 'name', value: 'code', children: 'children' }"
                placeholder="请选择省/市/区/镇/村"
                style="width: 100%"
                @change="handleAddressChange"
              />
              <a-input
                v-model:value="formData.detailedAddress"
                placeholder="请输入详细地址"
              />
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="是否重点人群" name="isKeyPopulation">
            <a-radio-group v-model:value="formData.isKeyPopulation">
              <a-radio :value="1">是</a-radio>
              <a-radio :value="0">否</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="24">
          <a-form-item label="备注" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }" name="remark">
            <a-textarea
              v-model:value="formData.remark"
              :rows="3"
              placeholder="请输入备注"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { addResident, updateResident, getResident } from '@/api/gc';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';

const props = defineProps({
  gcSexDict: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(['refresh-father-page-table']);

const visible = ref(false);
const isEdit = ref(false);
const formRef = ref();

const formData = reactive({
  residentId: null,
  residentName: null,
  sex: null,
  idCard: null,
  birthDate: null,
  age: null,
  phone: null,
  province: null,
  provinName: null,
  city: null,
  cityName: null,
  district: null,
  districtName: null,
  town: null,
  townName: null,
  village: null,
  villageName: null,
  detailedAddress: null,
  isKeyPopulation: 0,
  remark: null,
});

// 地址级联选择
const addressCascader = ref([]);
const addressOptions = ref([]);

// 表单验证规则
const rules = {
  residentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    {
      pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
      message: '身份证号格式不正确',
      trigger: 'blur',
    },
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '手机号格式不正确',
      trigger: 'blur',
    },
  ],
};

const modalTitle = computed(() => (isEdit.value ? '编辑居民' : '新增居民'));

// 打开新增弹窗
const openAddModal = () => {
  resetForm();
  isEdit.value = false;
  visible.value = true;
  loadAddressOptions();
};

// 打开编辑弹窗
const openUpdateModal = async (record) => {
  resetForm();
  isEdit.value = true;
  visible.value = true;
  loadAddressOptions();
  
  try {
    const response = await getResident(record.residentId);
    Object.assign(formData, response.data);
    
    // 设置地址级联值
    const cascaderValue = [];
    if (formData.province) cascaderValue.push(formData.province);
    if (formData.city) cascaderValue.push(formData.city);
    if (formData.district) cascaderValue.push(formData.district);
    if (formData.town) cascaderValue.push(formData.town);
    if (formData.village) cascaderValue.push(formData.village);
    addressCascader.value = cascaderValue;
  } catch (error) {
    message.error('获取居民信息失败');
  }
};

// 重置表单
const resetForm = () => {
  formData.residentId = null;
  formData.residentName = null;
  formData.sex = null;
  formData.idCard = null;
  formData.birthDate = null;
  formData.age = null;
  formData.phone = null;
  formData.province = null;
  formData.provinName = null;
  formData.city = null;
  formData.cityName = null;
  formData.district = null;
  formData.districtName = null;
  formData.town = null;
  formData.townName = null;
  formData.village = null;
  formData.villageName = null;
  formData.detailedAddress = null;
  formData.isKeyPopulation = 0;
  formData.remark = null;
  addressCascader.value = [];
  formRef.value?.resetFields();
};

// 从身份证号解析信息
const parseIdCard = () => {
  const idCard = formData.idCard;
  if (!idCard || idCard.length < 15) return;

  // 解析出生日期
  let birthYear, birthMonth, birthDay;
  if (idCard.length === 18) {
    birthYear = idCard.substring(6, 10);
    birthMonth = idCard.substring(10, 12);
    birthDay = idCard.substring(12, 14);
  } else if (idCard.length === 15) {
    birthYear = '19' + idCard.substring(6, 8);
    birthMonth = idCard.substring(8, 10);
    birthDay = idCard.substring(10, 12);
  }

  if (birthYear && birthMonth && birthDay) {
    formData.birthDate = `${birthYear}-${birthMonth}-${birthDay}`;
    
    // 计算年龄
    const today = dayjs();
    const birth = dayjs(formData.birthDate);
    formData.age = today.diff(birth, 'year');
  }

  // 解析性别
  if (idCard.length === 18) {
    const sexCode = parseInt(idCard.substring(16, 17));
    formData.sex = sexCode % 2 === 0 ? '2' : '1'; // 偶数为女，奇数为男
  } else if (idCard.length === 15) {
    const sexCode = parseInt(idCard.substring(14, 15));
    formData.sex = sexCode % 2 === 0 ? '2' : '1';
  }
};

// 加载地址选项（这里需要实际的地址数据接口）
const loadAddressOptions = () => {
  // TODO: 调用接口获取五级地址数据
  // 临时使用空数组
  addressOptions.value = [];
};

// 处理地址级联变化
const handleAddressChange = (value, selectedOptions) => {
  if (selectedOptions && selectedOptions.length > 0) {
    formData.province = selectedOptions[0]?.code || null;
    formData.provinName = selectedOptions[0]?.name || null;
    formData.city = selectedOptions[1]?.code || null;
    formData.cityName = selectedOptions[1]?.name || null;
    formData.district = selectedOptions[2]?.code || null;
    formData.districtName = selectedOptions[2]?.name || null;
    formData.town = selectedOptions[3]?.code || null;
    formData.townName = selectedOptions[3]?.name || null;
    formData.village = selectedOptions[4]?.code || null;
    formData.villageName = selectedOptions[4]?.name || null;
  }
};

// 取消
const handleCancel = () => {
  visible.value = false;
  resetForm();
};

// 确定
const handleOk = async () => {
  try {
    await formRef.value.validate();
    
    if (isEdit.value) {
      await updateResident(formData);
      message.success('修改成功');
    } else {
      await addResident(formData);
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
