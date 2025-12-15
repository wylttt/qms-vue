<template>
  <a-modal
    v-model:open="visible"
    title="居民详情"
    :width="800"
    :footer="null"
    @cancel="handleCancel"
  >
    <a-descriptions bordered :column="2">
      <a-descriptions-item label="姓名">
        {{ detailData.residentName }}
      </a-descriptions-item>
      <a-descriptions-item label="性别">
        <dict-tag :options="gcSexDict" :value="detailData.sex" />
      </a-descriptions-item>
      <a-descriptions-item label="身份证号">
        {{ detailData.idCard }}
      </a-descriptions-item>
      <a-descriptions-item label="出生日期">
        {{ detailData.birthDate }}
      </a-descriptions-item>
      <a-descriptions-item label="年龄">
        {{ detailData.age }} 岁
      </a-descriptions-item>
      <a-descriptions-item label="联系电话">
        {{ detailData.phone }}
      </a-descriptions-item>
      <a-descriptions-item label="是否重点人群">
        <a-tag :color="detailData.isKeyPopulation === 1 ? 'red' : 'default'">
          {{ detailData.isKeyPopulation === 1 ? '是' : '否' }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="居住地址" :span="2">
        {{ detailData.fullAddress }}
      </a-descriptions-item>
      <a-descriptions-item label="详细地址" :span="2">
        {{ detailData.detailedAddress }}
      </a-descriptions-item>
      <a-descriptions-item label="备注" :span="2">
        {{ detailData.remark }}
      </a-descriptions-item>
      <a-descriptions-item label="创建时间">
        {{ detailData.createTime }}
      </a-descriptions-item>
      <a-descriptions-item label="更新时间">
        {{ detailData.updateTime }}
      </a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { getResident } from '@/api/gc';
import { message } from 'ant-design-vue';

const props = defineProps({
  gcSexDict: {
    type: Array,
    default: () => [],
  },
});

const visible = ref(false);
const detailData = reactive({
  residentId: null,
  residentName: null,
  sex: null,
  idCard: null,
  birthDate: null,
  age: null,
  phone: null,
  fullAddress: null,
  detailedAddress: null,
  isKeyPopulation: 0,
  remark: null,
  createTime: null,
  updateTime: null,
});

// 打开弹窗
const openModal = async (record) => {
  visible.value = true;
  
  try {
    const response = await getResident(record.residentId);
    Object.assign(detailData, response.data);
  } catch (error) {
    message.error('获取居民详情失败');
  }
};

// 关闭弹窗
const handleCancel = () => {
  visible.value = false;
};

defineExpose({
  openModal,
});
</script>
