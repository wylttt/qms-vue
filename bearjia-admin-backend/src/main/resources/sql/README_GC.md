# 胃癌筛查系统数据库初始化说明

## 文件列表

### 1. gc_gastric_cancer_screening.sql
**核心表结构文件**

包含以下12个核心业务表:
- `gc_region` - 行政区划表(支持五级:省市区街道社区)
- `gc_sampling_site` - 采血点表
- `gc_surveyor` - 问卷调查员表
- `gc_resident` - 居民信息表
- `gc_questionnaire_template` - 问卷模板表
- `gc_questionnaire_record` - 问卷记录表
- `gc_blood_appointment` - 采血预约表
- `gc_push_log` - 推送日志表
- `gc_task` - 任务表
- `gc_screening_result` - 筛查结果表
- `gc_follow_up` - 随访记录表
- `gc_follow_up_track` - 随访跟踪记录表

同时包含数据字典初始化:
- gc_region_level - 区域层级字典
- gc_task_type - 任务类型字典
- gc_task_status - 任务状态字典
- gc_sampling_status - 采样状态字典
- gc_blood_result - 血液筛查结果字典
- gc_follow_up_status - 随访状态字典

### 2. gc_region_data_sample.sql
**行政区划示例数据**

包含江西省九江市濂溪区及下属街道/乡镇、社区/村的示例数据:
- 江西省 (省级)
- 九江市 (市级)
- 濂溪区 (区级)
- 6个街道/乡镇 (街道级)
- 部分社区/村 (社区级)

**注意**: 完整的全国行政区划数据需要从以下来源获取:
- 国家统计局: http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/
- 民政部行政区划代码: https://www.mca.gov.cn/
- 开源数据库: https://github.com/modood/Administrative-divisions-of-China

### 3. gc_questionnaire_template_sample.sql
**问卷模板示例数据**

包含濂溪区胃癌筛查调查问卷模板V1.0:
- 5个问卷分节(基本信息、既往病史、家族史、生活习惯、症状评估)
- 12个调查问题
- 风险评分规则(重点人群阈值60分)
- JSON格式存储,支持动态渲染

## 初始化步骤

### 步骤1: 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS bear_jia DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE bear_jia;
```

### 步骤2: 执行核心表结构
```bash
mysql -u root -p bear_jia < gc_gastric_cancer_screening.sql
```

或在MySQL客户端中执行:
```sql
SOURCE /path/to/gc_gastric_cancer_screening.sql;
```

### 步骤3: 导入行政区划数据
```bash
mysql -u root -p bear_jia < gc_region_data_sample.sql
```

**注意**: 如果需要全国行政区划数据,请先下载完整数据并转换为SQL格式后导入。

### 步骤4: 导入问卷模板示例
```bash
mysql -u root -p bear_jia < gc_questionnaire_template_sample.sql
```

### 步骤5: 验证数据
```sql
-- 检查表是否创建成功
SHOW TABLES LIKE 'gc_%';

-- 检查行政区划数据
SELECT COUNT(*) FROM gc_region;

-- 检查问卷模板数据
SELECT template_name, version, status FROM gc_questionnaire_template;

-- 检查数据字典
SELECT dict_type, dict_label, dict_value 
FROM sys_dict_data 
WHERE dict_type LIKE 'gc_%'
ORDER BY dict_type, dict_sort;
```

## 数据库设计要点

### 1. 表命名规范
- 前缀 `gc_` 表示 Gastric Cancer (胃癌筛查)
- 使用下划线分隔单词
- 表名使用单数形式

### 2. 字段命名规范
- 主键: `{table_name}_id`
- 状态字段: `status` (0正常/1停用)
- 时间字段: `create_time`, `update_time`
- 创建人: `create_by`, `update_by`

### 3. 索引设计
- 主键索引: 所有表的主键字段
- 唯一索引: 业务唯一字段(如身份证号、区划代码等)
- 普通索引: 高频查询字段、外键字段

### 4. 数据类型
- ID字段: `BIGINT` (支持大数据量)
- 状态字段: `CHAR(1)` (节省空间)
- 编码字段: `VARCHAR(20-100)`
- JSON字段: `JSON` 或 `TEXT`
- 时间字段: `DATETIME` (包含日期和时间)
- 日期字段: `DATE` (仅日期)

### 5. 字符集
- 统一使用 `utf8mb4` 字符集
- 排序规则: `utf8mb4_0900_ai_ci`

## 数据权限设计

### 角色数据权限过滤规则

| 角色 | 数据过滤条件 |
|-----|------------|
| 超级管理员 | 无过滤,查看所有数据 |
| 区级管理员 | WHERE district = '濂溪区' |
| 街道/乡镇管理员 | WHERE street = '当前用户所属街道' |
| 采血点管理员 | WHERE appointment_site_id = '当前采血点ID' |

### 字段级权限控制

**身份证号字段 (id_card_no)**:
- 超级管理员: 可查看完整
- 区级管理员: 可查看完整
- 街道/乡镇管理员: 可查看完整
- 采血点管理员: **不可查看**(返回null或脱敏)
- 医院/医生: **不可查看**(返回null或脱敏)

实现方式: 在Service层或Mapper XML中根据用户角色动态过滤字段。

## 扩展性说明

### 1. 支持全国推广
系统设计支持全国五级行政区划,当前仅使用濂溪区数据。未来扩展到其他区县或城市时:
- 导入新的行政区划数据
- 创建新的管理员账号并关联区域
- 调整数据权限过滤条件

### 2. 胃镜筛查功能预留
当前预留了胃镜筛查相关字段,未来正式启用时需:
- 完善胃镜结果数据字段
- 开发医院端管理后台或对接接口
- 实现胃镜结果统计分析

### 3. 多问卷模板支持
支持问卷模板版本管理,可扩展:
- 不同地区使用不同问卷模板
- 不同年龄段使用不同问卷模板
- 问卷模板A/B测试

## 第三方系统对接

### 采血系统对接说明

**推送采血预约信息**:
- 接口: POST /api/external/blood/appointment
- 触发时机: 居民完成采血预约时
- 推送内容: 预约ID、居民信息、采血点、预约时间
- 响应数据: 第三方系统预约ID、条码编号

**接收采样状态回调**:
- 接口: POST /api/gc/blood/callback/sampling-status
- 触发时机: 第三方系统完成采样后
- 推送内容: 预约ID、条码编号、采样状态、采样时间
- 响应数据: 成功/失败标识

**推送重试机制**:
- 第1次失败: 等待30秒后重试
- 第2次失败: 等待60秒后重试
- 第3次失败: 等待120秒后重试
- 3次均失败: 标记为推送失败,通知管理员手动处理

## 常见问题

### Q1: 如何更新行政区划数据?
A: 下载最新的行政区划数据,转换为SQL格式,使用 `ON DUPLICATE KEY UPDATE` 语句更新。建议每年更新一次。

### Q2: 如何修改重点人群判定阈值?
A: 修改 `gc_questionnaire_template` 表中的 `focus_threshold` 字段值,或在问卷模板JSON中调整评分规则。

### Q3: 如何处理身份证号重复?
A: `gc_resident` 表的 `id_card_no` 字段设置了唯一索引,插入重复数据会报错。建议在应用层先查询是否存在,存在则提示用户。

### Q4: 如何备份数据?
A: 使用mysqldump命令:
```bash
mysqldump -u root -p bear_jia gc_region gc_resident gc_questionnaire_record > backup.sql
```

### Q5: 如何查看推送失败的记录?
A: 查询推送日志表:
```sql
SELECT * FROM gc_push_log 
WHERE push_status = 'failed' 
ORDER BY push_time DESC;
```

## 联系方式

如有问题,请联系开发团队。
