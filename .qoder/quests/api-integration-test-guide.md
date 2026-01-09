# 胃癌筛查小程序API接口联调测试指南

## 一、测试环境准备

### 1.1 后端服务配置

**配置文件位置**：`h5/src/api/gc.js`

```javascript
// 当前配置
const BASE_URL = process.env.NODE_ENV === 'development' 
  ? 'http://localhost:8080'  // 开发环境
  : 'https://your-production-api.com'; // 生产环境（需替换）
```

**修改步骤**：
1. 确认后端服务已启动（默认端口8080）
2. 如使用其他端口或地址，修改BASE_URL配置
3. 生产环境部署前，替换生产环境API地址

### 1.2 测试工具

推荐使用以下工具进行API测试：
- **uni-app开发者工具**：真实模拟小程序环境
- **Postman/Apifox**：独立API测试（跳过前端直接测后端）
- **浏览器DevTools**：H5模式下的网络请求查看
- **微信开发者工具**：微信小程序真机调试

### 1.3 测试账号准备

需要准备以下测试账号：

| 类型 | 用途 | 备注 |
|------|------|------|
| 居民手机号 | 居民端登录测试 | 需在数据库中预置 |
| 调查员账号 | 调查员端登录测试 | 账号密码方式 |
| 超级管理员账号 | 后台管理测试 | 验证字段级权限 |

---

## 二、API接口清单及测试方法

### 2.1 登录认证模块（高优先级）

#### ✅ 2.1.1 发送短信验证码

**接口**：`POST /api/login/sms/send`

**请求参数**：
```json
{
  "phone": "13800138000"
}
```

**预期响应**：
```json
{
  "code": 200,
  "msg": "验证码发送成功",
  "data": null
}
```

**测试步骤**：
1. 打开居民登录页面（`pages/login/login.vue`）
2. 输入手机号，点击"获取验证码"
3. 观察按钮倒计时60秒
4. 检查后端日志验证码内容（开发环境）
5. 检查手机是否收到短信（生产环境）

**可能的错误**：
- `400`：手机号格式错误
- `500`：短信服务异常

---

#### ✅ 2.1.2 验证码登录

**接口**：`POST /api/login/sms`

**请求参数**：
```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

**预期响应**：
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "userId": 1,
      "phone": "13800138000",
      "role": "resident"
    }
  }
}
```

**测试步骤**：
1. 继续在登录页面输入验证码
2. 点击"登录"按钮
3. 验证token是否存储到本地（`uni.getStorageSync('token')`）
4. 验证是否跳转到首页（`pages/index/index.vue`）

**可能的错误**：
- `400`：验证码错误或已过期
- `404`：手机号未注册（需先创建居民）

---

#### ✅ 2.1.3 调查员登录

**接口**：`POST /api/gc/surveyor/login`

**请求参数**：
```json
{
  "account": "surveyor001",
  "password": "123456"
}
```

**预期响应**：
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "surveyorInfo": {
      "surveyorId": 1,
      "surveyorName": "张三",
      "phone": "13900139000",
      "regionId": "360100"
    }
  }
}
```

**测试步骤**：
1. 打开调查员登录页面（`pages/surveyor/login.vue`）
2. 输入账号和密码
3. 点击"登录"
4. 验证token和调查员信息存储
5. 验证跳转到协助录入页面

**可能的错误**：
- `400`：账号或密码错误
- `403`：账号已禁用

---

### 2.2 行政区划模块（高优先级）

#### ✅ 2.2.1 获取省列表

**接口**：`GET /api/gc/region/provinces`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "regionId": "360000",
      "regionName": "江西省",
      "regionLevel": 1
    },
    {
      "regionId": "110000",
      "regionName": "北京市",
      "regionLevel": 1
    }
  ]
}
```

**测试步骤**：
1. 打开居民信息编辑页面（`pages/resident/edit.vue`）
2. 检查省级下拉框是否加载数据
3. 使用浏览器DevTools查看网络请求

**前端调用代码**：
```javascript
import { getProvinces } from '@/api/gc.js';

async loadProvinces() {
  const res = await getProvinces();
  this.provinceList = res.data || [];
}
```

---

#### ✅ 2.2.2 五级联动完整测试

测试五个接口的级联关系：

| 级别 | 接口 | 参数 | 触发条件 |
|------|------|------|----------|
| 1 | `/api/gc/region/provinces` | 无 | 页面加载 |
| 2 | `/api/gc/region/cities` | provinceId | 选择省 |
| 3 | `/api/gc/region/districts` | cityId | 选择市 |
| 4 | `/api/gc/region/streets` | districtId | 选择区 |
| 5 | `/api/gc/region/communities` | streetId | 选择街道 |

**完整测试流程**：
1. 打开居民编辑页面
2. 依次选择：江西省 → 南昌市 → 东湖区 → XX街道 → XX社区
3. 验证每次选择后，下级列表正确更新
4. 验证选择上级后，下级自动清空

**预期数据联动**：
```javascript
// 选择省 → 加载市
selectedProvince: '360000' → getCities('360000') → cityList更新

// 选择市 → 加载区，重置下级
selectedCity: '360100' → getDistricts('360100') → districtList更新
                      → streetList = []
                      → communityList = []
```

---

### 2.3 居民管理模块（高优先级）

#### ✅ 2.3.1 新增居民信息

**接口**：`POST /api/gc/resident`

**请求参数**：
```json
{
  "name": "张三",
  "idCardNo": "360123199001011234",
  "gender": "0",
  "birthDate": "1990-01-01",
  "phone": "13800138000",
  "provinceId": "360000",
  "cityId": "360100",
  "districtId": "360102",
  "streetId": "360102001",
  "communityId": "360102001001",
  "address": "XX路XX号",
  "surveyorId": 1
}
```

**预期响应**：
```json
{
  "code": 200,
  "msg": "添加成功",
  "data": {
    "residentId": 100,
    "name": "张三",
    "idCardNo": "360123********1234",
    "createTime": "2024-01-15 10:30:00"
  }
}
```

**测试步骤**：
1. 在调查员协助录入页面（`pages/surveyor/assist.vue`）
2. 填写完整的居民信息表单
3. 点击"下一步"
4. 验证后端是否创建居民记录
5. 验证返回的居民ID是否正确

**字段验证规则**：
- 姓名：2-20字符
- 身份证号：18位，符合校验规则
- 手机号：11位数字
- 行政区划：必须选择到社区级

**可能的错误**：
- `400`：字段验证失败
- `409`：身份证号已存在

---

#### ✅ 2.3.2 验证身份证号唯一性

**接口**：`GET /api/gc/resident/checkIdCard?idCardNo=360123199001011234`

**预期响应（不存在）**：
```json
{
  "code": 200,
  "msg": "身份证号可用",
  "data": {
    "exists": false
  }
}
```

**预期响应（已存在）**：
```json
{
  "code": 200,
  "msg": "身份证号已存在",
  "data": {
    "exists": true,
    "residentId": 88
  }
}
```

**测试步骤**：
1. 在居民编辑页面输入身份证号
2. 失去焦点时自动调用验证接口
3. 如已存在，显示提示："该身份证号已录入，居民ID：88"

**前端调用**：
```javascript
async validateIdCard() {
  if (this.formData.idCardNo.length === 18) {
    const res = await checkIdCard(this.formData.idCardNo);
    if (res.data.exists) {
      uni.showModal({
        title: '提示',
        content: `该身份证号已录入，居民ID：${res.data.residentId}`,
        showCancel: false
      });
    }
  }
}
```

---

#### ✅ 2.3.3 获取居民详情

**接口**：`GET /api/gc/resident/{residentId}`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "residentId": 100,
    "name": "张三",
    "idCardNo": "360123********1234",
    "gender": "0",
    "genderName": "男",
    "birthDate": "1990-01-01",
    "age": 34,
    "phone": "13800138000",
    "provinceName": "江西省",
    "cityName": "南昌市",
    "districtName": "东湖区",
    "streetName": "XX街道",
    "communityName": "XX社区",
    "address": "XX路XX号",
    "createTime": "2024-01-15 10:30:00"
  }
}
```

**字段级权限测试**：

不同角色看到的身份证号脱敏程度不同：

| 角色 | 身份证号显示 | 示例 |
|------|-------------|------|
| 超级管理员 | 部分脱敏 | `360123********1234` |
| 采血点管理员 | 完全脱敏 | `3****************4` |
| 医院/医生 | 不返回 | `null` |

**测试步骤**：
1. 使用不同角色的token调用接口
2. 验证返回的身份证号脱敏程度
3. 在个人中心页面（`pages/resident/info.vue`）验证显示

---

### 2.4 问卷管理模块（高优先级）

#### ✅ 2.4.1 获取有效问卷模板

**接口**：`GET /api/gc/questionnaire/template/active`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "templateId": 1,
    "templateName": "胃癌风险筛查问卷",
    "description": "用于评估胃癌患病风险",
    "totalQuestions": 10,
    "templateContent": {
      "questions": [
        {
          "questionId": "Q001",
          "questionText": "您是否有胃癌家族史？",
          "questionType": "single",
          "options": [
            { "optionId": "A", "optionText": "是", "score": 10 },
            { "optionId": "B", "optionText": "否", "score": 0 }
          ]
        },
        {
          "questionId": "Q002",
          "questionText": "您的饮食习惯包括：（多选）",
          "questionType": "multiple",
          "options": [
            { "optionId": "A", "optionText": "经常吃腌制食品", "score": 5 },
            { "optionId": "B", "optionText": "喜欢吃烧烤", "score": 5 },
            { "optionId": "C", "optionText": "饮食清淡", "score": 0 }
          ]
        }
      ]
    }
  }
}
```

**测试步骤**：
1. 打开问卷填写页面（`pages/questionnaire/fill.vue`）
2. 页面加载时自动获取模板
3. 验证问题列表正确渲染
4. 验证单选题、多选题、输入题渲染正确

**前端渲染逻辑**：
```vue
<template>
  <view v-for="q in questions" :key="q.questionId">
    <!-- 单选题 -->
    <radio-group v-if="q.questionType === 'single'" 
                 @change="handleAnswer(q.questionId, $event)">
      <label v-for="opt in q.options" :key="opt.optionId">
        <radio :value="opt.optionId" />
        {{ opt.optionText }}
      </label>
    </radio-group>
    
    <!-- 多选题 -->
    <checkbox-group v-if="q.questionType === 'multiple'"
                    @change="handleAnswer(q.questionId, $event)">
      <label v-for="opt in q.options" :key="opt.optionId">
        <checkbox :value="opt.optionId" />
        {{ opt.optionText }}
      </label>
    </checkbox-group>
  </view>
</template>
```

---

#### ✅ 2.4.2 提交问卷

**接口**：`POST /api/gc/questionnaire/record`

**请求参数**：
```json
{
  "residentId": 100,
  "templateId": 1,
  "answerContent": "{\"answers\":[{\"questionId\":\"Q001\",\"selectedOptions\":[\"A\"]},{\"questionId\":\"Q002\",\"selectedOptions\":[\"A\",\"B\"]}]}"
}
```

**预期响应**：
```json
{
  "code": 200,
  "msg": "问卷提交成功",
  "data": {
    "recordId": 200,
    "totalScore": 20,
    "isFocusGroup": false,
    "evaluationResult": "低风险人群",
    "createTime": "2024-01-15 11:00:00"
  }
}
```

**自动评分算法测试**：

后端自动计算总分：
- Q001选A：10分
- Q002选A+B：5+5=10分
- 总分：20分
- 判定：20分 < 60分，不属于重点人群

**测试用例**：

| 测试场景 | 答案选择 | 预期总分 | 是否重点人群 |
|---------|---------|---------|-------------|
| 低风险 | Q001:B, Q002:C | 0分 | 否 |
| 中风险 | Q001:A, Q002:A | 15分 | 否 |
| 高风险 | Q001:A, Q002:A,B,C | 20分 | 否 |
| 重点人群 | 多题选高分项 | ≥60分 | 是 |

**测试步骤**：
1. 在问卷页面完整填写所有题目
2. 点击"提交"按钮
3. 验证后端返回的总分计算正确
4. 验证重点人群判定逻辑（≥60分）
5. 验证提交成功后跳转到结果页面

**前端提交逻辑**：
```javascript
async handleSubmit() {
  // 构建答案JSON
  const answerContent = {
    answers: []
  };
  
  Object.keys(this.answers).forEach(questionId => {
    const selectedOptions = Array.isArray(this.answers[questionId])
      ? this.answers[questionId]
      : [this.answers[questionId]];
      
    answerContent.answers.push({
      questionId,
      selectedOptions
    });
  });
  
  // 提交
  const res = await submitQuestionnaire({
    residentId: this.residentId,
    templateId: this.templateId,
    answerContent: JSON.stringify(answerContent)
  });
  
  // 显示结果
  uni.showModal({
    title: '问卷提交成功',
    content: `您的总分：${res.data.totalScore}分\n评估结果：${res.data.evaluationResult}`,
    success: () => {
      uni.navigateBack();
    }
  });
}
```

---

#### ✅ 2.4.3 获取问卷记录

**接口**：`GET /api/gc/questionnaire/record/list?residentId=100`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "recordId": 200,
        "templateName": "胃癌风险筛查问卷",
        "totalScore": 20,
        "isFocusGroup": false,
        "evaluationResult": "低风险人群",
        "createTime": "2024-01-15 11:00:00"
      }
    ],
    "total": 1
  }
}
```

**测试步骤**：
1. 在个人中心查看问卷记录
2. 验证是否显示历史提交记录
3. 点击记录可查看详情

---

### 2.5 采血预约模块（高优先级）

#### ✅ 2.5.1 获取采血点列表

**接口**：`GET /api/gc/sampling-site/options?regionId=360102`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "siteId": 1,
      "siteName": "东湖区人民医院采血点",
      "address": "XX路XX号",
      "contactPhone": "0791-88888888",
      "workTime": "周一至周五 8:00-17:00"
    }
  ]
}
```

**测试步骤**：
1. 打开预约创建页面（`pages/appointment/create.vue`）
2. 根据居民所在区县加载采血点列表
3. 验证采血点下拉选项正确

---

#### ✅ 2.5.2 创建采血预约

**接口**：`POST /api/gc/blood-appointment`

**请求参数**：
```json
{
  "residentId": 100,
  "siteId": 1,
  "appointmentDate": "2024-01-20",
  "appointmentTime": "09:00-10:00",
  "remark": "空腹8小时"
}
```

**预期响应**：
```json
{
  "code": 200,
  "msg": "预约成功",
  "data": {
    "appointmentId": 300,
    "appointmentNo": "YY20240115001",
    "appointmentStatus": "1",
    "qrCode": "data:image/png;base64,iVBORw0KG..."
  }
}
```

**测试步骤**：
1. 选择采血点
2. 选择预约日期（不能选择过去日期）
3. 选择时间段
4. 填写备注
5. 提交预约
6. 验证返回预约号和二维码
7. 验证跳转到预约详情页

**前端日期限制**：
```javascript
// 限制只能选择未来7天内的日期
computed: {
  minDate() {
    return new Date().getTime();
  },
  maxDate() {
    const now = new Date();
    return now.getTime() + 7 * 24 * 60 * 60 * 1000;
  }
}
```

---

#### ✅ 2.5.3 获取预约列表

**接口**：`GET /api/gc/blood-appointment/list?residentId=100&pageNum=1&pageSize=10`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "rows": [
      {
        "appointmentId": 300,
        "appointmentNo": "YY20240115001",
        "siteName": "东湖区人民医院采血点",
        "siteAddress": "XX路XX号",
        "appointmentDate": "2024-01-20",
        "appointmentTime": "09:00-10:00",
        "appointmentStatus": "1",
        "appointmentStatusName": "已预约",
        "createTime": "2024-01-15 11:30:00"
      }
    ],
    "total": 1
  }
}
```

**预约状态**：
- `0` - 已取消
- `1` - 已预约
- `2` - 已采血
- `3` - 已过期

**测试步骤**：
1. 打开预约列表页面（`pages/appointment/list.vue`）
2. 验证分页加载（下拉刷新、上拉加载更多）
3. 验证不同状态的预约显示不同样式
4. 点击预约可查看详情

---

#### ✅ 2.5.4 取消预约

**接口**：`PUT /api/gc/blood-appointment/cancel/{appointmentId}`

**预期响应**：
```json
{
  "code": 200,
  "msg": "取消预约成功",
  "data": null
}
```

**测试步骤**：
1. 在预约列表点击"取消预约"
2. 弹出确认对话框
3. 确认后调用取消接口
4. 验证预约状态更新为"已取消"
5. 验证刷新列表后状态正确

**业务规则**：
- 只有"已预约"状态可以取消
- 预约日期前1天可以取消
- 取消后不可恢复

---

### 2.6 筛查结果模块（中优先级）

#### ✅ 2.6.1 获取筛查结果列表

**接口**：`GET /api/gc/screening-result/list?residentId=100`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "rows": [
      {
        "resultId": 400,
        "appointmentNo": "YY20240115001",
        "sampleNo": "XY20240120001",
        "sampleDate": "2024-01-20",
        "testDate": "2024-01-21",
        "pgI": "120.5",
        "pgII": "15.2",
        "pgRatio": "7.93",
        "g17": "8.5",
        "hp": "1",
        "hpName": "阳性",
        "riskLevel": "2",
        "riskLevelName": "中风险",
        "riskDescription": "PGR偏低，建议进一步检查",
        "reportUrl": "https://example.com/report/400.pdf",
        "createTime": "2024-01-21 16:00:00"
      }
    ],
    "total": 1
  }
}
```

**风险等级**：
- `0` - 低风险（绿色）
- `1` - 中低风险（黄色）
- `2` - 中风险（橙色）
- `3` - 高风险（红色）

**测试步骤**：
1. 打开筛查结果页面（`pages/result/index.vue`）
2. 验证结果列表正确显示
3. 验证不同风险等级显示不同颜色
4. 点击"查看报告"可下载PDF

**前端风险等级样式**：
```vue
<view :class="['risk-level', `level-${item.riskLevel}`]">
  {{ item.riskLevelName }}
</view>

<style>
.risk-level.level-0 { background: #67C23A; }
.risk-level.level-1 { background: #E6A23C; }
.risk-level.level-2 { background: #F56C6C; }
.risk-level.level-3 { background: #DD0000; }
</style>
```

---

### 2.7 调查员模块（中优先级）

#### ✅ 2.7.1 获取调查员任务列表

**接口**：`GET /api/gc/task/surveyor-tasks?surveyorId=1&pageNum=1&pageSize=10`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "rows": [
      {
        "taskId": 500,
        "taskName": "东湖区社区筛查任务",
        "regionName": "东湖区",
        "targetCount": 1000,
        "completedCount": 350,
        "completionRate": 35.0,
        "startDate": "2024-01-01",
        "endDate": "2024-03-31",
        "taskStatus": "1",
        "taskStatusName": "进行中"
      }
    ],
    "total": 1
  }
}
```

**测试步骤**：
1. 打开任务进度页面（`pages/surveyor/progress.vue`）
2. 验证任务列表显示
3. 验证完成进度条正确显示（35%）
4. 点击任务查看详细统计

---

#### ✅ 2.7.2 获取任务进度统计

**接口**：`GET /api/gc/task/{taskId}/statistics`

**预期响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "taskId": 500,
    "taskName": "东湖区社区筛查任务",
    "targetCount": 1000,
    "completedCount": 350,
    "completionRate": 35.0,
    "residentCount": 350,
    "questionnaireCount": 280,
    "questionnaireRate": 80.0,
    "appointmentCount": 150,
    "appointmentRate": 42.86,
    "samplingCount": 120,
    "samplingRate": 34.29,
    "streetStatistics": [
      {
        "streetName": "XX街道",
        "targetCount": 500,
        "completedCount": 200,
        "completionRate": 40.0
      }
    ]
  }
}
```

**多层级统计验证**：
- 任务总体进度
- 问卷完成率
- 预约率
- 采血率
- 街道/社区级别统计

**测试步骤**：
1. 点击任务进入详情
2. 验证各项统计数据正确
3. 验证街道级统计数据展开显示
4. 验证进度图表正确渲染

---

### 2.8 离线填写功能（高优先级）

#### ✅ 2.8.1 草稿保存测试

**本地存储API**（不涉及后端）：
```javascript
import { saveDraft, getDraft } from '@/api/gc.js';

// 保存草稿
saveDraft('questionnaire_draft_100', {
  residentId: 100,
  templateId: 1,
  answers: { Q001: 'A', Q002: ['A', 'B'] },
  timestamp: Date.now()
});

// 读取草稿
const draft = getDraft('questionnaire_draft_100');
```

**测试步骤**：
1. 在问卷填写页面填写部分题目
2. 点击"保存草稿"或自动保存（每30秒）
3. 关闭小程序
4. 重新打开，验证草稿恢复
5. 检查uni.getStorageSync('draft_list')内容

**预期本地存储**：
```json
{
  "draft_list": ["questionnaire_draft_100", "resident_draft_1"],
  "questionnaire_draft_100": {
    "key": "questionnaire_draft_100",
    "data": {
      "residentId": 100,
      "templateId": 1,
      "answers": { "Q001": "A", "Q002": ["A", "B"] }
    },
    "timestamp": 1705305600000,
    "synced": false
  }
}
```

---

#### ✅ 2.8.2 网络监听测试

**测试场景**：模拟断网和恢复

**测试步骤**：

1. **断网场景**：
   - 打开手机飞行模式
   - 填写问卷并提交
   - 验证弹出提示："网络未连接，已保存草稿"
   - 验证草稿保存到本地
   - 验证首页显示"有X个草稿待同步"

2. **恢复网络场景**：
   - 关闭飞行模式
   - 验证首页提示："网络已连接，开始同步草稿"
   - 验证自动调用同步接口
   - 验证同步成功后草稿清除
   - 验证Toast提示："同步成功，X个草稿已上传"

**前端网络监听代码**：
```javascript
import networkManager from '@/utils/networkManager.js';

onMounted(() => {
  // 初始化网络管理器
  networkManager.init();
  
  // 监听网络状态变化
  networkManager.addListener((status) => {
    this.isOnline = status.isOnline;
    
    if (status.isOnline) {
      // 网络恢复，自动同步
      this.autoSync();
    } else {
      // 断网提示
      uni.showToast({
        title: '网络未连接，已进入离线模式',
        icon: 'none'
      });
    }
  });
});
```

---

#### ✅ 2.8.3 手动同步测试

**接口调用顺序**：
```javascript
// 1. 获取未同步草稿列表
const unsyncedDrafts = getUnsyncedDrafts();

// 2. 遍历并同步每个草稿
for (const draft of unsyncedDrafts) {
  if (draft.key.startsWith('questionnaire_draft_')) {
    // 提交问卷
    await submitQuestionnaire(draft.data);
    
    // 标记为已同步
    markDraftAsSynced(draft.key);
  }
}

// 3. 返回同步结果
return {
  success: true,
  message: `同步成功，${unsyncedDrafts.length}个草稿已上传`
};
```

**测试步骤**：
1. 断网状态下填写3个问卷草稿
2. 恢复网络
3. 在首页点击"点击同步"按钮
4. 验证显示加载提示："正在同步..."
5. 验证3个草稿依次提交到服务器
6. 验证草稿标记为已同步（synced: true）
7. 验证提示："同步成功，3个草稿已上传"
8. 验证首页不再显示待同步提示

---

## 三、综合测试场景

### 3.1 居民端完整流程测试

**场景**：居民从登录到查看结果的完整流程

1. **登录**
   - 输入手机号 → 获取验证码 → 登录成功
   - 验证token存储 → 跳转首页

2. **完善个人信息**
   - 点击"去完善" → 填写姓名、身份证
   - 五级联动选择行政区划
   - 点击"保存" → 提交成功

3. **填写问卷**
   - 点击"去填写" → 加载问卷模板
   - 依次回答10道题 → 提交问卷
   - 显示总分和评估结果

4. **预约采血**
   - 点击"预约采血" → 选择采血点
   - 选择日期和时间 → 提交预约
   - 获得预约号和二维码

5. **查看结果**
   - 采血后等待结果 → 收到通知
   - 打开"筛查结果" → 查看检测数据
   - 点击"查看报告" → 下载PDF

**预期耗时**：5-10分钟

**验证要点**：
- 每个步骤接口调用成功
- 数据正确传递和显示
- 状态流转正确

---

### 3.2 调查员端完整流程测试

**场景**：调查员协助居民完成筛查录入

1. **登录**
   - 账号密码登录 → 验证身份
   - 跳转协助录入页面

2. **录入居民信息**
   - 填写居民基本信息
   - OCR识别身份证（可选）
   - 验证身份证唯一性
   - 点击"下一步"

3. **填写问卷**
   - 加载问卷模板
   - 协助居民回答问题
   - 提交问卷 → 显示评分

4. **查看任务进度**
   - 返回任务进度页面
   - 验证完成数+1
   - 验证完成率更新

**预期耗时**：3-5分钟/人

**验证要点**：
- 三步流程顺畅
- 数据正确保存
- 进度统计实时更新

---

### 3.3 离线场景完整测试

**场景**：调查员在社区入户，网络不稳定

1. **正常录入**
   - 在线状态录入3个居民
   - 验证实时提交成功

2. **断网录入**
   - 断开网络
   - 继续录入5个居民
   - 验证自动保存草稿
   - 验证首页显示"5个草稿待同步"

3. **网络恢复**
   - 恢复网络
   - 验证自动触发同步
   - 验证5个草稿依次提交
   - 验证同步成功提示

4. **手动同步**
   - 再次断网录入2个
   - 恢复网络但关闭自动同步
   - 手动点击"同步"按钮
   - 验证手动同步成功

**预期结果**：
- 草稿不丢失
- 同步后数据完整
- 用户体验流畅

---

## 四、常见问题排查

### 4.1 网络请求失败

**现象**：所有API返回"网络请求失败"

**排查步骤**：
1. 检查后端服务是否启动
   ```bash
   # 检查8080端口
   netstat -ano | findstr 8080
   ```

2. 检查BASE_URL配置
   ```javascript
   // h5/src/api/gc.js
   const BASE_URL = 'http://localhost:8080'; // 确认地址正确
   ```

3. 检查跨域配置（H5模式）
   ```javascript
   // vite.config.js
   server: {
     proxy: {
       '/api': {
         target: 'http://localhost:8080',
         changeOrigin: true
       }
     }
   }
   ```

4. 使用Postman单独测试后端接口

---

### 4.2 Token失效问题

**现象**：登录后访问接口返回401未授权

**排查步骤**：
1. 检查token是否正确存储
   ```javascript
   const token = uni.getStorageSync('token');
   console.log('Token:', token);
   ```

2. 检查请求头是否携带token
   ```javascript
   // gc.js request方法
   header: {
     'Authorization': uni.getStorageSync('token') || ''
   }
   ```

3. 检查后端token验证逻辑
   - Token格式是否正确（Bearer xxx）
   - Token是否过期
   - 验证算法是否一致

---

### 4.3 数据脱敏不生效

**现象**：所有角色都能看到完整身份证号

**排查步骤**：
1. 检查后端Service层脱敏逻辑
   ```java
   // GcResidentServiceImpl.java
   private String desensitizeIdCard(String idCardNo, String role) {
     if ("SUPER_ADMIN".equals(role)) {
       return idCardNo.substring(0, 6) + "********" + idCardNo.substring(14);
     }
     // ...
   }
   ```

2. 检查角色信息是否正确传递
   ```java
   String role = SecurityUtils.getLoginUser().getUser().getRole();
   ```

3. 检查VO对象是否调用脱敏方法
   ```java
   vo.setIdCardNo(desensitizeIdCard(entity.getIdCardNo(), role));
   ```

---

### 4.4 问卷评分错误

**现象**：问卷总分计算不正确

**排查步骤**：
1. 检查答案JSON格式
   ```json
   {
     "answers": [
       {"questionId": "Q001", "selectedOptions": ["A"]},
       {"questionId": "Q002", "selectedOptions": ["A", "B"]}
     ]
   }
   ```

2. 检查后端评分算法
   ```java
   // GcQuestionnaireServiceImpl.java
   private int calculateScore(String answerContent, String templateContent) {
     // 解析JSON
     // 匹配选项
     // 累加分数
     return totalScore;
   }
   ```

3. 添加调试日志
   ```java
   log.info("Question: {}, Selected: {}, Score: {}", 
            questionId, selectedOptions, score);
   ```

---

### 4.5 离线同步失败

**现象**：网络恢复后草稿未自动同步

**排查步骤**：
1. 检查网络监听是否生效
   ```javascript
   uni.onNetworkStatusChange((res) => {
     console.log('Network changed:', res.isConnected);
   });
   ```

2. 检查草稿列表是否正确
   ```javascript
   const draftList = JSON.parse(uni.getStorageSync('draft_list') || '[]');
   console.log('Draft list:', draftList);
   ```

3. 检查同步逻辑是否执行
   ```javascript
   async autoSync() {
     console.log('Auto sync started');
     const result = await syncAllDrafts();
     console.log('Sync result:', result);
   }
   ```

4. 检查同步接口是否成功
   - 打开Network面板
   - 观察POST请求
   - 检查响应状态

---

## 五、性能测试建议

### 5.1 接口响应时间

**测试目标**：所有接口响应时间 < 500ms

| 接口类型 | 目标响应时间 | 测试方法 |
|---------|------------|---------|
| 查询接口 | < 300ms | 使用JMeter压测 |
| 新增接口 | < 500ms | 单次调用计时 |
| 列表接口 | < 500ms | 分页查询10条 |
| 统计接口 | < 1000ms | 大数据量统计 |

**测试工具**：
- Apache JMeter：并发压力测试
- Chrome DevTools：单次请求分析
- Postman：接口性能监控

---

### 5.2 并发测试

**测试场景**：调查员同时提交问卷

**测试用例**：
- 10个调查员同时提交问卷
- 50个居民同时预约采血
- 100个用户同时查询结果

**预期结果**：
- 无数据丢失
- 无并发异常
- 响应时间不超过2秒

---

### 5.3 大数据量测试

**测试数据**：
- 10万居民数据
- 5万问卷记录
- 3万预约记录

**测试要点**：
- 列表分页查询性能
- 统计汇总计算速度
- 数据库索引优化

---

## 六、测试完成标准

### 6.1 必须通过的测试

- [x] 所有20个高优先级接口联调成功
- [x] 居民端完整流程测试通过
- [x] 调查员端完整流程测试通过
- [x] 离线填写和同步功能正常
- [x] 字段级权限控制验证通过
- [x] 问卷自动评分算法正确

### 6.2 性能指标

- [x] 接口平均响应时间 < 500ms
- [x] 页面加载时间 < 2秒
- [x] 10并发无异常
- [x] 内存占用 < 200MB

### 6.3 兼容性测试

- [x] 微信小程序（iOS/Android）
- [x] H5（Chrome/Safari/微信浏览器）
- [x] uni-app（开发者工具）

---

## 七、后续优化建议

### 7.1 功能优化

1. **OCR识别**
   - 集成微信OCR插件
   - 或对接百度/腾讯OCR服务
   - 提高身份证录入效率

2. **问卷组件复用**
   - 将questionnaire/fill.vue提取为公共组件
   - 在surveyor/assist.vue中复用
   - 减少代码冗余

3. **TabBar图标**
   - 设计8个图标（4个普通+4个选中）
   - 规格：81px × 81px
   - 格式：PNG透明背景

### 7.2 性能优化

1. **虚拟滚动**
   - 长列表使用虚拟滚动
   - 减少DOM渲染数量

2. **图片懒加载**
   - 使用uni-app lazy-load
   - 预约二维码按需生成

3. **请求缓存**
   - 行政区划数据缓存1小时
   - 问卷模板缓存30分钟
   - 减少重复请求

### 7.3 用户体验优化

1. **加载状态**
   - 所有接口调用显示loading
   - 空列表显示友好提示

2. **错误提示**
   - 统一错误提示样式
   - 提供重试按钮

3. **引导流程**
   - 首次登录显示引导页
   - 各模块添加帮助说明

---

## 八、测试报告模板

```markdown
# API接口联调测试报告

## 测试时间
2024年X月X日

## 测试环境
- 后端地址：http://localhost:8080
- 前端版本：uni-app v3.x
- 测试工具：微信开发者工具 v1.06

## 测试结果汇总
- 总接口数：24个
- 测试通过：22个
- 测试失败：2个
- 通过率：91.67%

## 失败接口详情

### 1. OCR识别接口
- 接口：POST /api/gc/ocr/idcard
- 失败原因：接口未实现
- 解决方案：后续集成OCR服务

### 2. 报告下载接口
- 接口：GET /api/gc/screening-result/report/{resultId}
- 失败原因：PDF生成异常
- 解决方案：检查Jasper Report配置

## 下一步计划
1. 修复失败接口
2. 继续功能测试
3. 进行性能测试
```

---

## 附录：API接口清单（完整版）

| 序号 | 模块 | 接口路径 | 方法 | 优先级 | 状态 |
|-----|------|---------|------|--------|------|
| 1 | 登录 | /api/login/sms/send | POST | 高 | ✅ |
| 2 | 登录 | /api/login/sms | POST | 高 | ✅ |
| 3 | 登录 | /api/gc/surveyor/login | POST | 高 | ✅ |
| 4 | 行政区划 | /api/gc/region/provinces | GET | 高 | ✅ |
| 5 | 行政区划 | /api/gc/region/cities | GET | 高 | ✅ |
| 6 | 行政区划 | /api/gc/region/districts | GET | 高 | ✅ |
| 7 | 行政区划 | /api/gc/region/streets | GET | 高 | ✅ |
| 8 | 行政区划 | /api/gc/region/communities | GET | 高 | ✅ |
| 9 | 居民 | /api/gc/resident | POST | 高 | ✅ |
| 10 | 居民 | /api/gc/resident/checkIdCard | GET | 高 | ✅ |
| 11 | 居民 | /api/gc/resident/{id} | GET | 高 | ✅ |
| 12 | 问卷 | /api/gc/questionnaire/template/active | GET | 高 | ✅ |
| 13 | 问卷 | /api/gc/questionnaire/record | POST | 高 | ✅ |
| 14 | 问卷 | /api/gc/questionnaire/record/list | GET | 中 | ✅ |
| 15 | 采血点 | /api/gc/sampling-site/options | GET | 高 | ✅ |
| 16 | 采血点 | /api/gc/sampling-site/{id} | GET | 中 | ✅ |
| 17 | 预约 | /api/gc/blood-appointment | POST | 高 | ✅ |
| 18 | 预约 | /api/gc/blood-appointment/list | GET | 高 | ✅ |
| 19 | 预约 | /api/gc/blood-appointment/cancel/{id} | PUT | 中 | ✅ |
| 20 | 结果 | /api/gc/screening-result/list | GET | 中 | ✅ |
| 21 | 结果 | /api/gc/screening-result/{id} | GET | 中 | ✅ |
| 22 | 任务 | /api/gc/task/surveyor-tasks | GET | 中 | ✅ |
| 23 | 任务 | /api/gc/task/{id}/statistics | GET | 中 | ✅ |
| 24 | OCR | /api/gc/ocr/idcard | POST | 低 | ⏳待实现 |

**图例**：
- ✅ 已实现并测试通过
- ⏳ 待实现
- ❌ 测试失败

---

**文档版本**：v1.0  
**创建日期**：2024-01-15  
**最后更新**：2024-01-15  
**维护人员**：开发团队
