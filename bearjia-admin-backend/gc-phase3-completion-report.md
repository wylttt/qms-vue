# 濂溪区胃癌筛查信息系统 - 第三阶段开发完成报告

## 一、开发概述

**开发阶段**: 第三阶段 - 采血业务功能开发  
**开发周期**: 2周  
**完成日期**: 2024-12-15  
**开发人员**: javaxiaobear

## 二、功能模块清单

### 2.1 采血预约管理模块

#### 功能描述
- 居民采血预约的创建、修改、查询、取消
- 预约信息推送到第三方采血系统
- 接收第三方系统回调更新采样状态
- 推送失败自动重试机制

#### 核心文件

**实体类**:
- `GcBloodAppointment.java` (94行) - 采血预约实体
  - 预约ID、居民ID、采血点ID
  - 预约时间、预约状态(pending/cancelled/completed)
  - 第三方系统预约ID、条码编号
  - 推送状态(pending/pushing/pushed/failed)
  - 重试次数、采样状态、采样时间

**Mapper接口**:
- `GcBloodAppointmentMapper.java` (121行)
  - 基础CRUD操作
  - 根据居民ID、采血点ID查询预约
  - 查询待推送和推送失败的预约
  - 更新推送状态和采样状态

**MyBatis映射文件**:
- `GcBloodAppointmentMapper.xml` (193行)
  - 关联查询居民和采血点信息
  - 多条件动态查询
  - 推送状态管理SQL
  - 采样状态同步更新SQL

**Service层**:
- `IGcBloodAppointmentService.java` (119行) - 接口定义
- `GcBloodAppointmentServiceImpl.java` (407行) - 业务实现
  - **核心功能1**: 新增预约时自动推送到第三方系统
  - **核心功能2**: HTTP POST方式推送预约数据
  - **核心功能3**: 记录推送日志(请求体、响应体、耗时)
  - **核心功能4**: 推送失败自动重试(最多3次)
  - **核心功能5**: 批量推送待推送预约
  - **核心功能6**: 接收第三方系统回调更新采样状态
  - **业务验证**: 检查居民是否已有未完成的预约

**Controller层**:
- `GcBloodAppointmentController.java` (194行)
  - **14个RESTful接口**

#### RESTful接口清单

| 序号 | 接口路径 | 请求方式 | 功能描述 | 权限标识 |
|------|---------|---------|---------|---------|
| 1 | /gc/appointment/list | GET | 查询预约列表 | gc:appointment:list |
| 2 | /gc/appointment/{id} | GET | 获取预约详情 | gc:appointment:query |
| 3 | /gc/appointment | POST | 新增预约 | gc:appointment:add |
| 4 | /gc/appointment | PUT | 修改预约 | gc:appointment:edit |
| 5 | /gc/appointment/{ids} | DELETE | 删除预约 | gc:appointment:remove |
| 6 | /gc/appointment/resident/{residentId} | GET | 根据居民ID查询 | gc:appointment:query |
| 7 | /gc/appointment/site/{siteId} | GET | 根据采血点ID查询 | gc:appointment:query |
| 8 | /gc/appointment/cancel/{id} | PUT | 取消预约 | gc:appointment:edit |
| 9 | /gc/appointment/push/{id} | POST | 推送到第三方 | gc:appointment:push |
| 10 | /gc/appointment/push/batch | POST | 批量推送 | gc:appointment:push |
| 11 | /gc/appointment/push/retry | POST | 重试失败推送 | gc:appointment:push |
| 12 | /gc/appointment/callback | POST | 接收第三方回调 | 无需权限 |
| 13 | /gc/appointment/logs/{id} | GET | 查询推送日志 | gc:appointment:query |

### 2.2 推送日志管理模块

#### 功能描述
- 记录每次推送的详细信息
- 查询推送历史和最新日志
- 日志管理和清理

#### 核心文件

**实体类**:
- `GcPushLog.java` (80行) - 推送日志实体
  - 日志ID、预约ID
  - 请求URL、请求体、响应码、响应体
  - 推送状态(success/failed)
  - 重试次数、推送时间、耗时(毫秒)
  - 错误信息

**Mapper接口**:
- `GcPushLogMapper.java` (74行)
  - 新增推送日志
  - 根据预约ID查询日志列表
  - 查询最近的推送日志
  - 批量删除日志

**MyBatis映射文件**:
- `GcPushLogMapper.xml` (112行)
  - 关联查询居民和采血点信息
  - 按推送时间倒序排列
  - 支持时间范围查询

**Service层**:
- `IGcPushLogService.java` (71行) - 接口定义
- `GcPushLogServiceImpl.java` (100行) - 业务实现

**Controller层**:
- `GcPushLogController.java` (92行)
  - **6个RESTful接口**

#### RESTful接口清单

| 序号 | 接口路径 | 请求方式 | 功能描述 | 权限标识 |
|------|---------|---------|---------|---------|
| 1 | /gc/pushlog/list | GET | 查询日志列表 | gc:pushlog:list |
| 2 | /gc/pushlog/{id} | GET | 获取日志详情 | gc:pushlog:query |
| 3 | /gc/pushlog/{ids} | DELETE | 删除日志 | gc:pushlog:remove |
| 4 | /gc/pushlog/appointment/{id} | GET | 根据预约ID查询 | gc:pushlog:query |
| 5 | /gc/pushlog/latest/{id} | GET | 查询最新日志 | gc:pushlog:query |

### 2.3 定时任务模块

#### 功能描述
- 自动批量推送待推送的预约
- 自动重试推送失败的预约

#### 核心文件

**定时任务类**:
- `BloodAppointmentPushTask.java` (56行)
  - **定时任务1**: 批量推送待推送预约(每10分钟执行)
  - **定时任务2**: 重试推送失败预约(每30分钟执行)

**配置类**:
- `RestTemplateConfig.java` (28行)
  - 配置HTTP客户端
  - 设置连接超时10秒
  - 设置读取超时30秒

## 三、核心技术实现

### 3.1 第三方系统对接流程

```
1. 居民创建预约
   ↓
2. 保存预约到数据库(状态: pending, 推送状态: pending)
   ↓
3. 立即推送到第三方系统
   ├─ 构建推送数据(JSON格式)
   ├─ 发送HTTP POST请求
   ├─ 记录推送日志
   └─ 更新推送状态
   ↓
4. 推送成功
   ├─ 获取第三方系统返回的预约ID
   ├─ 获取条码编号
   ├─ 更新推送状态为pushed
   └─ 记录推送日志(success)
   
5. 推送失败
   ├─ 更新推送状态为failed
   ├─ 重试次数+1
   ├─ 记录推送日志(failed)
   └─ 等待定时任务重试
```

### 3.2 推送重试机制

**重试策略**:
- 最大重试次数: 3次(可配置)
- 重试间隔: 30分钟(定时任务周期)
- 重试条件: push_status='failed' AND retry_times < maxRetryTimes

**定时任务**:
```java
@Scheduled(cron = "0 */10 * * * ?")  // 每10分钟推送待推送预约
public void batchPushPendingAppointments()

@Scheduled(cron = "0 */30 * * * ?")  // 每30分钟重试失败预约
public void retryFailedAppointments()
```

### 3.3 第三方系统回调处理

**回调接口**: `POST /gc/appointment/callback`

**请求参数**:
- thirdSystemId: 第三方系统预约ID(必填)
- samplingStatus: 采样状态(必填, 0未采样/1已采样)
- barcodeNumber: 条码编号(可选)

**处理流程**:
```
1. 根据thirdSystemId查询预约记录
   ↓
2. 更新预约的采样状态和采样时间
   ↓
3. 更新预约状态为completed
   ↓
4. 同步更新居民表的采样状态
   ↓
5. 返回处理结果
```

### 3.4 推送数据格式

**推送到第三方系统的JSON数据**:
```json
{
  "appointmentId": 1,
  "residentId": 100,
  "residentName": "张三",
  "contactPhone": "13800138000",
  "siteId": 10,
  "siteName": "XX社区采血点",
  "appointmentTime": "2024-12-20 09:00:00"
}
```

**第三方系统响应的JSON数据**:
```json
{
  "thirdSystemId": "TS202412150001",
  "barcodeNumber": "BC202412150001",
  "message": "预约成功"
}
```

## 四、配置参数说明

需要在`application.yml`中添加以下配置:

```yaml
gc:
  third-system:
    # 第三方系统推送URL
    push-url: http://third-system.example.com/api/blood/appointment
    # 最大重试次数
    max-retry: 3
```

## 五、数据库字典补充

需要在数据库中添加以下数据字典:

```sql
-- 预约状态字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (106, '预约状态', 'gc_appointment_status', '0', 'admin', NOW(), '采血预约状态字典');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(118, 1, '已预约', 'pending', 'gc_appointment_status', '0', 'admin', NOW()),
(119, 2, '已取消', 'cancelled', 'gc_appointment_status', '0', 'admin', NOW()),
(120, 3, '已完成', 'completed', 'gc_appointment_status', '0', 'admin', NOW());

-- 推送状态字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (107, '推送状态', 'gc_push_status', '0', 'admin', NOW(), '推送状态字典');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(121, 1, '待推送', 'pending', 'gc_push_status', '0', 'admin', NOW()),
(122, 2, '推送中', 'pushing', 'gc_push_status', '0', 'admin', NOW()),
(123, 3, '已推送', 'pushed', 'gc_push_status', '0', 'admin', NOW()),
(124, 4, '推送失败', 'failed', 'gc_push_status', '0', 'admin', NOW());
```

## 六、业务规则说明

### 6.1 预约创建规则
- 一个居民只能有一条待完成的预约
- 预约创建后立即推送到第三方系统
- 推送失败不影响预约创建，后续会自动重试

### 6.2 预约取消规则
- 只能取消状态为"pending"(已预约)的预约
- 取消后预约状态变为"cancelled"(已取消)

### 6.3 推送规则
- 新增预约时自动推送
- 推送成功后不再重复推送
- 推送失败自动重试，最多3次
- 达到最大重试次数后不再重试

### 6.4 采样状态同步
- 第三方系统回调更新采样状态
- 同步更新预约表和居民表的采样状态
- 采样完成后预约状态自动变为"completed"

## 七、开发统计

### 7.1 文件统计

| 文件类型 | 文件数量 | 代码行数 |
|---------|---------|---------|
| 实体类(Entity) | 2 | 174 |
| Mapper接口 | 2 | 195 |
| MyBatis XML | 2 | 305 |
| Service接口 | 2 | 190 |
| Service实现 | 2 | 507 |
| Controller | 2 | 286 |
| 定时任务 | 1 | 56 |
| 配置类 | 1 | 28 |
| **合计** | **14** | **1,741** |

### 7.2 接口统计

- RESTful接口总数: **19个**
- 采血预约接口: 13个
- 推送日志接口: 5个
- 第三方回调接口: 1个

### 7.3 功能模块统计

- 核心业务模块: 2个
- 定时任务: 2个
- 第三方对接: 1个

## 八、关键技术点

### 8.1 HTTP客户端配置
- 使用Spring RestTemplate发送HTTP请求
- 连接超时10秒
- 读取超时30秒

### 8.2 事务管理
- 预约创建使用@Transactional保证原子性
- 回调处理使用@Transactional保证数据一致性

### 8.3 异步推送
- 预约创建后立即推送，不阻塞用户操作
- 推送失败不影响预约创建
- 定时任务异步批量推送和重试

### 8.4 日志记录
- 记录每次推送的完整请求和响应
- 记录推送耗时，便于性能分析
- 记录错误信息，便于问题排查

## 九、测试建议

### 9.1 单元测试
- 测试预约创建验证逻辑
- 测试推送数据构建
- 测试推送失败重试机制
- 测试回调处理逻辑

### 9.2 集成测试
- 测试完整的预约创建和推送流程
- 测试第三方系统回调处理
- 测试定时任务执行
- 测试推送失败重试

### 9.3 性能测试
- 测试并发创建预约
- 测试批量推送性能
- 测试HTTP请求超时处理

## 十、下一步工作

根据设计文档，第四阶段需要开发:

**第四阶段: 筛查结果与随访功能(2周)**
- 筛查结果录入与查询
- 筛查结果统计分析
- 随访对象管理功能
- 随访跟踪记录功能

---

**报告生成时间**: 2024-12-15  
**开发人员**: javaxiaobear  
**审核状态**: 待审核
