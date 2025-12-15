<template>
  <div class="gc-statistics-page">
    <!-- 顶部统计卡片 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="24" :sm="12" :md="6">
        <a-card :bordered="false">
          <a-statistic
            title="居民总数"
            :value="statistics.totalResidents"
            :prefix="h(BearJiaIcon, { icon: 'team-outlined' })"
          >
            <template #suffix>
              <span style="font-size: 14px; color: #999">人</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card :bordered="false">
          <a-statistic
            title="问卷填写数"
            :value="statistics.totalQuestionnaires"
            :prefix="h(BearJiaIcon, { icon: 'form-outlined' })"
          >
            <template #suffix>
              <span style="font-size: 14px; color: #999">份</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card :bordered="false">
          <a-statistic
            title="筛查完成数"
            :value="statistics.totalScreenings"
            :prefix="h(BearJiaIcon, { icon: 'file-search-outlined' })"
          >
            <template #suffix>
              <span style="font-size: 14px; color: #999">人</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card :bordered="false">
          <a-statistic
            title="随访中人数"
            :value="statistics.totalFollowUps"
            :prefix="h(BearJiaIcon, { icon: 'phone-outlined' })"
          >
            <template #suffix>
              <span style="font-size: 14px; color: #999">人</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="16">
      <!-- 风险等级分布 -->
      <a-col :xs="24" :lg="12" style="margin-bottom: 16px">
        <a-card title="风险等级分布" :bordered="false">
          <div ref="riskChartRef" style="height: 300px"></div>
        </a-card>
      </a-col>

      <!-- 随访状态分布 -->
      <a-col :xs="24" :lg="12" style="margin-bottom: 16px">
        <a-card title="随访状态分布" :bordered="false">
          <div ref="followUpChartRef" style="height: 300px"></div>
        </a-card>
      </a-col>

      <!-- 月度筛查趋势 -->
      <a-col :xs="24" :lg="12" style="margin-bottom: 16px">
        <a-card title="月度筛查趋势" :bordered="false">
          <div ref="trendChartRef" style="height: 300px"></div>
        </a-card>
      </a-col>

      <!-- 年龄分布 -->
      <a-col :xs="24" :lg="12" style="margin-bottom: 16px">
        <a-card title="年龄段分布" :bordered="false">
          <div ref="ageChartRef" style="height: 300px"></div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, h } from 'vue';
import * as echarts from 'echarts';
import { message } from 'ant-design-vue';
import BearJiaIcon from '@/components/BearJiaIcon';
import {
  getResidentStatistics,
  getRiskDistribution,
  getFollowUpStatistics,
  getScreeningTrend,
  getAgeDistribution,
} from '@/api/gc';

const riskChartRef = ref(null);
const followUpChartRef = ref(null);
const trendChartRef = ref(null);
const ageChartRef = ref(null);

let riskChart = null;
let followUpChart = null;
let trendChart = null;
let ageChart = null;

// 统计数据
const statistics = reactive({
  totalResidents: 0,
  totalQuestionnaires: 0,
  totalScreenings: 0,
  totalFollowUps: 0,
});

// 初始化图表
onMounted(async () => {
  await loadStatistics();
  initCharts();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  disposeCharts();
});

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getResidentStatistics();
    Object.assign(statistics, response.data);
  } catch (error) {
    message.error('加载统计数据失败');
  }
};

// 初始化所有图表
const initCharts = async () => {
  initRiskChart();
  initFollowUpChart();
  initTrendChart();
  initAgeChart();
};

// 初始化风险等级分布图表
const initRiskChart = async () => {
  try {
    const response = await getRiskDistribution();
    const data = response.data || [];

    riskChart = echarts.init(riskChartRef.value);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)',
      },
      legend: {
        orient: 'vertical',
        left: 'left',
      },
      series: [
        {
          name: '风险等级',
          type: 'pie',
          radius: '50%',
          data: [
            { value: data.low || 0, name: '低风险', itemStyle: { color: '#52c41a' } },
            { value: data.medium || 0, name: '中风险', itemStyle: { color: '#faad14' } },
            { value: data.high || 0, name: '高风险', itemStyle: { color: '#ff4d4f' } },
            { value: data.none || 0, name: '未检测', itemStyle: { color: '#d9d9d9' } },
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    };
    riskChart.setOption(option);
  } catch (error) {
    console.error('加载风险分布数据失败', error);
  }
};

// 初始化随访状态分布图表
const initFollowUpChart = async () => {
  try {
    const response = await getFollowUpStatistics();
    const data = response.data || [];

    followUpChart = echarts.init(followUpChartRef.value);
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: ['待随访', '随访中', '已完成'],
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '人数',
          type: 'bar',
          data: [
            { value: data.pending || 0, itemStyle: { color: '#d9d9d9' } },
            { value: data.inProgress || 0, itemStyle: { color: '#1890ff' } },
            { value: data.completed || 0, itemStyle: { color: '#52c41a' } },
          ],
          barWidth: '60%',
        },
      ],
    };
    followUpChart.setOption(option);
  } catch (error) {
    console.error('加载随访统计数据失败', error);
  }
};

// 初始化月度筛查趋势图表
const initTrendChart = async () => {
  try {
    const response = await getScreeningTrend();
    const data = response.data || [];

    trendChart = echarts.init(trendChartRef.value);
    const option = {
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: ['问卷填写', '筛查完成'],
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.map((item) => item.month) || [],
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '问卷填写',
          type: 'line',
          data: data.map((item) => item.questionnaire) || [],
          smooth: true,
          itemStyle: { color: '#1890ff' },
        },
        {
          name: '筛查完成',
          type: 'line',
          data: data.map((item) => item.screening) || [],
          smooth: true,
          itemStyle: { color: '#52c41a' },
        },
      ],
    };
    trendChart.setOption(option);
  } catch (error) {
    console.error('加载筛查趋势数据失败', error);
  }
};

// 初始化年龄分布图表
const initAgeChart = async () => {
  try {
    const response = await getAgeDistribution();
    const data = response.data || [];

    ageChart = echarts.init(ageChartRef.value);
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: ['<40岁', '40-50岁', '50-60岁', '60-70岁', '≥70岁'],
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '人数',
          type: 'bar',
          data: [
            data.age40 || 0,
            data.age4050 || 0,
            data.age5060 || 0,
            data.age6070 || 0,
            data.age70 || 0,
          ],
          itemStyle: { color: '#1890ff' },
          barWidth: '60%',
        },
      ],
    };
    ageChart.setOption(option);
  } catch (error) {
    console.error('加载年龄分布数据失败', error);
  }
};

// 窗口大小变化时重新调整图表大小
const handleResize = () => {
  riskChart?.resize();
  followUpChart?.resize();
  trendChart?.resize();
  ageChart?.resize();
};

// 销毁图表
const disposeCharts = () => {
  riskChart?.dispose();
  followUpChart?.dispose();
  trendChart?.dispose();
  ageChart?.dispose();
};
</script>

<style lang="less" scoped>
.gc-statistics-page {
  height: 100%;
}
</style>
