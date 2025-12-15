# 濂溪区胃癌筛查信息系统 - 第四阶段快速使用指南

## 一、数据初始化

### 1. 执行数据字典SQL
```bash
# 在MySQL中执行
mysql -u root -p gastric_cancer_screening < src/main/resources/sql/gc_phase4_dict_data.sql
```

或手动执行：
```sql
source /path/to/gc_phase4_dict_data.sql;
```

## 二、API接口使用示例

### 筛查结果管理

#### 1. 新增筛查结果
```http
POST /gc/screening/result
Content-Type: application/json

{
  "residentId": 1,
  "siteId": 1,
  "bloodResult": "high_risk",
  "bloodResultDetail": "PG I: 50 μg/L, PG II: 15 μg/L, PG I/II: 3.3, G-17: 25 pmol/L",
  "bloodResultDate": "2024-12-15"
}
```

**说明**: 如果`bloodResult`为`high_risk`，系统会自动创建随访对象。

#### 2. 查询筛查结果列表
```http
GET /gc/screening/result/list?pageNum=1&pageSize=10&bloodResult=high_risk
```

#### 3. 根据居民ID查询筛查结果
```http
GET /gc/screening/result/resident/1
```

#### 4. 按采血点统计筛查数据
```http
GET /gc/screening/result/statistics/site
```

响应示例：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "siteId": 1,
      "siteName": "濂溪区人民医院采血点",
      "focusGroupCount": 150,
      "nonFocusGroupCount": 50,
      "bloodParticipantCount": 180,
      "lowRiskCount": 120,
      "highRiskCount": 60,
      "gastroscopyCount": 15
    }
  ]
}
```

#### 5. 按行政区划统计筛查数据
```http
GET /gc/screening/result/statistics/district?districtId=1
```

### 随访对象管理

#### 1. 查询随访对象列表（含详细信息）
```http
GET /gc/followup/list/detail?pageNum=1&pageSize=10&followUpStatus=pending
```

响应示例：
```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [
    {
      "followUpId": 1,
      "residentId": 1,
      "resultId": 1,
      "followUpType": "blood_high_risk",
      "followUpStatus": "pending",
      "followUpLevel": "high",
      "remark": "系统自动加入：血液筛查结果为高风险",
      "resident": {
        "residentId": 1,
        "name": "张三",
        "idCard": "360403********1234",
        "phone": "13800138000",
        "gender": "1",
        "age": 55
      },
      "screeningResult": {
        "resultId": 1,
        "bloodResult": "high_risk",
        "bloodResultDetail": "PG I: 50 μg/L...",
        "bloodResultDate": "2024-12-15"
      }
    }
  ],
  "total": 1
}
```

#### 2. 分配随访任务
```http
POST /gc/followup/assign/1?assignedUserId=100&assignedUserName=李医生
```

**效果**: 
- 更新随访状态为`assigned`
- 记录分配人员和分配时间

#### 3. 完成随访
```http
PUT /gc/followup/complete/1
```

**效果**:
- 更新随访状态为`completed`
- 记录完成时间

#### 4. 根据居民ID查询随访记录
```http
GET /gc/followup/resident/1
```

### 随访跟踪管理

#### 1. 新增跟踪记录
```http
POST /gc/followup/track
Content-Type: application/json

{
  "followUpId": 1,
  "trackDate": "2024-12-15",
  "trackType": "phone",
  "trackResult": "success",
  "trackContent": "电话联系成功，居民表示愿意配合进一步检查。建议进行胃镜检查，已告知注意事项。",
  "nextTrackDate": "2024-12-22",
  "trackerUserId": 100,
  "trackerUserName": "李医生"
}
```

#### 2. 查询跟踪记录列表（含详细信息）
```http
GET /gc/followup/track/list/detail?pageNum=1&pageSize=10
```

#### 3. 根据随访对象ID查询所有跟踪记录
```http
GET /gc/followup/track/followup/1
```

响应示例：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "trackId": 1,
      "followUpId": 1,
      "trackDate": "2024-12-15",
      "trackType": "phone",
      "trackResult": "success",
      "trackContent": "电话联系成功...",
      "nextTrackDate": "2024-12-22",
      "trackerUserId": 100,
      "trackerUserName": "李医生"
    },
    {
      "trackId": 2,
      "followUpId": 1,
      "trackDate": "2024-12-22",
      "trackType": "visit",
      "trackResult": "success",
      "trackContent": "上门随访...",
      "trackerUserId": 100,
      "trackerUserName": "李医生"
    }
  ]
}
```

## 三、业务场景示例

### 场景1：高风险筛查结果录入及自动随访

**步骤1**: 录入高风险筛查结果
```http
POST /gc/screening/result
{
  "residentId": 1,
  "bloodResult": "high_risk",
  "bloodResultDetail": "PG I/II比值异常"
}
```

**步骤2**: 系统自动创建随访对象
```
✅ 自动在gc_follow_up表创建记录
- followUpType: blood_high_risk
- followUpStatus: pending
- followUpLevel: high
```

**步骤3**: 查询待随访列表
```http
GET /gc/followup/list/detail?followUpStatus=pending&followUpLevel=high
```

### 场景2：随访任务分配和跟踪

**步骤1**: 管理员分配随访任务
```http
POST /gc/followup/assign/1?assignedUserId=100&assignedUserName=李医生
```

**步骤2**: 随访人员电话跟踪
```http
POST /gc/followup/track
{
  "followUpId": 1,
  "trackType": "phone",
  "trackResult": "success",
  "trackContent": "电话沟通情况..."
}
```

**步骤3**: 随访人员上门随访
```http
POST /gc/followup/track
{
  "followUpId": 1,
  "trackType": "visit",
  "trackResult": "success",
  "trackContent": "上门随访情况..."
}
```

**步骤4**: 完成随访
```http
PUT /gc/followup/complete/1
```

### 场景3：统计分析

**统计各采血点筛查情况**
```http
GET /gc/screening/result/statistics/site
```

**统计各区域筛查情况**
```http
GET /gc/screening/result/statistics/district
```

**查看高风险人群分布**
```http
GET /gc/followup/list/detail?followUpType=blood_high_risk&followUpLevel=high
```

## 四、数据字典对照表

### 筛查结果类型
| 字典值 | 显示名称 |
|--------|----------|
| low_risk | 低风险 |
| high_risk | 高风险 |

### 随访类型
| 字典值 | 显示名称 |
|--------|----------|
| blood_high_risk | 血液筛查高风险 |
| gastroscopy_abnormal | 胃镜检查异常 |
| other | 其他原因 |

### 随访状态
| 字典值 | 显示名称 | 说明 |
|--------|----------|------|
| pending | 待随访 | 新创建的随访对象 |
| assigned | 已分配 | 已分配给随访人员 |
| in_progress | 随访中 | 正在进行随访 |
| completed | 已完成 | 随访已完成 |

### 随访优先级
| 字典值 | 显示名称 |
|--------|----------|
| high | 高 |
| medium | 中 |
| low | 低 |

### 跟踪方式
| 字典值 | 显示名称 |
|--------|----------|
| phone | 电话 |
| sms | 短信 |
| visit | 上门 |
| other | 其他 |

### 跟踪结果
| 字典值 | 显示名称 |
|--------|----------|
| success | 成功联系 |
| failed | 未联系上 |
| refused | 拒绝 |

## 五、常见问题

### Q1: 为什么录入高风险结果后没有自动创建随访对象？
**A**: 检查以下几点：
1. `bloodResult`字段是否为`high_risk`
2. 是否已存在相同residentId和resultId的随访记录（防重复）
3. 查看后台日志是否有异常

### Q2: 如何查看某个居民的完整筛查和随访历史？
**A**: 
```http
# 1. 查询筛查结果
GET /gc/screening/result/resident/{residentId}

# 2. 查询随访记录
GET /gc/followup/resident/{residentId}

# 3. 对每条随访记录，查询跟踪历史
GET /gc/followup/track/followup/{followUpId}
```

### Q3: 统计数据不准确怎么办？
**A**: 
1. 检查数据关联是否正确（residentId、resultId等）
2. 确认采血点和行政区划设置正确
3. 查看SQL日志，检查统计条件

## 六、权限配置

在`sys_menu`表中添加菜单和权限：

```sql
-- 筛查结果管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, perms) VALUES
('筛查结果管理', {父菜单ID}, 1, 'screening-result', 'gc/screening/result/index', 'gc:screening:result:list'),
('筛查结果查询', {上级菜单ID}, 1, '#', '', 'gc:screening:result:query'),
('筛查结果新增', {上级菜单ID}, 2, '#', '', 'gc:screening:result:add'),
('筛查结果修改', {上级菜单ID}, 3, '#', '', 'gc:screening:result:edit'),
('筛查结果删除', {上级菜单ID}, 4, '#', '', 'gc:screening:result:remove'),
('筛查结果统计', {上级菜单ID}, 5, '#', '', 'gc:screening:result:statistics');

-- 随访管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, perms) VALUES
('随访对象管理', {父菜单ID}, 2, 'follow-up', 'gc/followup/index', 'gc:followup:list'),
('随访对象查询', {上级菜单ID}, 1, '#', '', 'gc:followup:query'),
('随访对象新增', {上级菜单ID}, 2, '#', '', 'gc:followup:add'),
('随访对象修改', {上级菜单ID}, 3, '#', '', 'gc:followup:edit'),
('随访对象删除', {上级菜单ID}, 4, '#', '', 'gc:followup:remove'),
('随访任务分配', {上级菜单ID}, 5, '#', '', 'gc:followup:assign'),
('随访完成', {上级菜单ID}, 6, '#', '', 'gc:followup:complete');

-- 随访跟踪菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, perms) VALUES
('随访跟踪管理', {父菜单ID}, 3, 'follow-up-track', 'gc/followup/track/index', 'gc:followup:track:list'),
('跟踪记录查询', {上级菜单ID}, 1, '#', '', 'gc:followup:track:query'),
('跟踪记录新增', {上级菜单ID}, 2, '#', '', 'gc:followup:track:add'),
('跟踪记录修改', {上级菜单ID}, 3, '#', '', 'gc:followup:track:edit'),
('跟踪记录删除', {上级菜单ID}, 4, '#', '', 'gc:followup:track:remove');
```

## 七、测试建议

### 1. 单元测试
```java
@SpringBootTest
public class GcScreeningResultServiceTest {
    @Autowired
    private IGcScreeningResultService screeningResultService;
    
    @Test
    public void testAutoAddFollowUp() {
        // 测试高风险结果自动加入随访
        GcScreeningResult result = new GcScreeningResult();
        result.setResidentId(1L);
        result.setBloodResult("high_risk");
        
        int rows = screeningResultService.insertScreeningResult(result);
        
        // 验证随访对象是否创建
        List<GcFollowUp> followUps = followUpService.selectFollowUpByResidentId(1L);
        assertTrue(followUps.size() > 0);
    }
}
```

### 2. 接口测试
使用Postman或其他工具测试所有API接口。

### 3. 业务流程测试
按照业务场景示例完整走一遍流程。

---

**文档版本**: v1.0  
**更新时间**: 2024-12-15  
**联系方式**: javaxiaobear
