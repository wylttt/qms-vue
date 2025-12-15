# 第二阶段核心功能完整实现指南

## 执行概要

本文档提供第二阶段（居民管理、问卷管理、任务管理）三个核心模块的完整实现指南，包括所有需要创建的文件清单和关键代码示例。

## 已完成文件

### 居民管理模块
- ✅ `GcResident.java` - 居民实体类
- ✅ `ResidentVO.java` - 居民视图对象
- ✅ `ResidentQueryDTO.java` - 查询条件DTO
- ✅ `ResidentImportDTO.java` - 导入数据DTO
- ✅ `GcResidentMapper.java` - Mapper接口
- ✅ `GcResidentMapper.xml` - MyBatis配置

## 待创建文件清单

### 一、居民管理模块（剩余文件）

#### 1. Service层
**文件**: `IGcResidentService.java`
```java
package com.javaxiaobear.module.gc.service;

import java.util.List;
import com.javaxiaobear.module.gc.domain.entity.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;
import org.springframework.web.multipart.MultipartFile;

public interface IGcResidentService {
    List<ResidentVO> selectResidentList(GcResident resident);
    ResidentVO selectResidentById(Long residentId);
    int insertResident(GcResident resident);
    int updateResident(GcResident resident);
    int deleteResidentById(Long residentId);
    Boolean checkIdCardUnique(String idCardNo);
    Map<String, Object> importResidents(MultipartFile file, Long surveyorId);
    void exportResidents(GcResident resident, HttpServletResponse response);
}
```

**文件**: `GcResidentServiceImpl.java` (核心逻辑实现)
- 实现字段级权限控制（desensitizeIdCardNo方法）
- 实现身份证号验证
- 实现批量导入Excel
- 实现导出Excel

#### 2. Controller层
**文件**: `GcResidentController.java`
- 8个REST API接口

#### 3. 工具类
**文件**: `IdCardValidator.java` - 身份证验证工具
**文件**: `IdCardDesensitizer.java` - 身份证脱敏工具
**文件**: `ExcelUtil.java` - Excel导入导出工具（可能已存在）

### 二、问卷管理模块

#### 1. 实体层
- `GcQuestionnaireTemplate.java` - 问卷模板实体
- `GcQuestionnaireRecord.java` - 问卷记录实体
- `QuestionnaireTemplateVO.java` - 模板VO
- `QuestionnaireRecordVO.java` - 记录VO
- `QuestionnaireSubmitDTO.java` - 提交DTO

#### 2. Mapper层
- `GcQuestionnaireTemplateMapper.java`
- `GcQuestionnaireTemplateMapper.xml`
- `GcQuestionnaireRecordMapper.java`
- `GcQuestionnaireRecordMapper.xml`

#### 3. Service层
- `IGcQuestionnaireTemplateService.java`
- `GcQuestionnaireTemplateServiceImpl.java`
- `IGcQuestionnaireRecordService.java`
- `GcQuestionnaireRecordServiceImpl.java`

#### 4. Controller层
- `GcQuestionnaireTemplateController.java` (6个接口)
- `GcQuestionnaireRecordController.java` (4个接口)

#### 5. 工具类
- `QuestionnaireScoreCalculator.java` - 评分计算器

### 三、任务管理模块

#### 1. 实体层
- `GcTask.java` - 任务实体
- `TaskVO.java` - 任务VO
- `TaskProgressVO.java` - 进度VO
- `SubRegionProgressVO.java` - 子区域进度VO
- `TaskSummaryVO.java` - 汇总VO

#### 2. Mapper层
- `GcTaskMapper.java`
- `GcTaskMapper.xml`

#### 3. Service层
- `IGcTaskService.java`
- `GcTaskServiceImpl.java`

#### 4. Controller层
- `GcTaskController.java` (9个接口)

## 关键代码实现

### 1. 身份证脱敏工具类

```java
package com.javaxiaobear.module.gc.util;

public class IdCardDesensitizer {
    
    /**
     * 根据角色脱敏身份证号
     */
    public static String desensitize(String idCardNo, String roleKey) {
        if (idCardNo == null || idCardNo.length() != 18) {
            return idCardNo;
        }
        
        // 医院/医生不返回
        if (roleKey.contains("hospital") || roleKey.contains("doctor")) {
            return null;
        }
        
        // 采血点管理员：完全脱敏
        if (roleKey.contains("sampling_site")) {
            return idCardNo.substring(0, 1) + "***************" + idCardNo.substring(17);
        }
        
        // 其他角色：部分脱敏
        return idCardNo.substring(0, 3) + "***********" + idCardNo.substring(14);
    }
}
```

### 2. 身份证验证工具类

```java
package com.javaxiaobear.module.gc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IdCardValidator {
    
    /**
     * 验证身份证号格式
     */
    public static boolean validate(String idCardNo) {
        if (idCardNo == null || idCardNo.length() != 18) {
            return false;
        }
        
        // 验证前17位是否为数字
        String num17 = idCardNo.substring(0, 17);
        if (!num17.matches("\\d{17}")) {
            return false;
        }
        
        // 验证校验码
        char checkCode = calculateCheckCode(num17);
        return checkCode == idCardNo.charAt(17);
    }
    
    /**
     * 从身份证号解析性别
     */
    public static String parseGender(String idCardNo) {
        if (!validate(idCardNo)) {
            return null;
        }
        int genderCode = Integer.parseInt(idCardNo.substring(16, 17));
        return genderCode % 2 == 0 ? "1" : "0"; // 0男1女
    }
    
    /**
     * 从身份证号解析出生日期
     */
    public static Date parseBirthDate(String idCardNo) {
        if (!validate(idCardNo)) {
            return null;
        }
        String birthStr = idCardNo.substring(6, 14);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            return sdf.parse(birthStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * 计算校验码
     */
    private static char calculateCheckCode(String num17) {
        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCode = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (num17.charAt(i) - '0') * weight[i];
        }
        
        return checkCode[sum % 11];
    }
}
```

### 3. 问卷评分计算器

```java
package com.javaxiaobear.module.gc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class QuestionnaireScoreCalculator {
    
    /**
     * 计算问卷总分
     */
    public static int calculateScore(String templateContent, String answerContent) {
        int totalScore = 0;
        
        try {
            JSONObject template = JSON.parseObject(templateContent);
            JSONObject answer = JSON.parseObject(answerContent);
            
            JSONArray sections = template.getJSONArray("sections");
            JSONArray answers = answer.getJSONArray("answers");
            
            // 遍历答案
            for (int i = 0; i < answers.size(); i++) {
                JSONObject answerItem = answers.getJSONObject(i);
                String questionId = answerItem.getString("questionId");
                JSONArray selectedOptions = answerItem.getJSONArray("selectedOptions");
                
                // 在模板中查找问题
                JSONObject question = findQuestion(sections, questionId);
                if (question == null) {
                    continue;
                }
                
                // 累加选项分数
                JSONArray options = question.getJSONArray("options");
                for (int j = 0; j < selectedOptions.size(); j++) {
                    String optionId = selectedOptions.getString(j);
                    JSONObject option = findOption(options, optionId);
                    if (option != null) {
                        totalScore += option.getIntValue("score");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("问卷评分计算失败", e);
        }
        
        return totalScore;
    }
    
    /**
     * 在模板中查找问题
     */
    private static JSONObject findQuestion(JSONArray sections, String questionId) {
        for (int i = 0; i < sections.size(); i++) {
            JSONObject section = sections.getJSONObject(i);
            JSONArray questions = section.getJSONArray("questions");
            for (int j = 0; j < questions.size(); j++) {
                JSONObject question = questions.getJSONObject(j);
                if (questionId.equals(question.getString("questionId"))) {
                    return question;
                }
            }
        }
        return null;
    }
    
    /**
     * 在选项中查找指定选项
     */
    private static JSONObject findOption(JSONArray options, String optionId) {
        for (int i = 0; i < options.size(); i++) {
            JSONObject option = options.getJSONObject(i);
            if (optionId.equals(option.getString("optionId"))) {
                return option;
            }
        }
        return null;
    }
}
```

### 4. Service层实现示例（GcResidentServiceImpl关键方法）

```java
@Override
public List<ResidentVO> selectResidentList(GcResident resident) {
    // 查询列表
    List<ResidentVO> list = residentMapper.selectResidentList(resident);
    
    // 获取当前用户角色
    String roleKey = SecurityUtils.getLoginUser().getUser().getRoleKey();
    
    // 对身份证号进行脱敏
    for (ResidentVO vo : list) {
        vo.setIdCardNo(IdCardDesensitizer.desensitize(vo.getIdCardNo(), roleKey));
    }
    
    return list;
}

@Override
public int insertResident(GcResident resident) {
    // 1. 验证身份证号格式
    if (!IdCardValidator.validate(resident.getIdCardNo())) {
        throw new ServiceException("身份证号格式不正确");
    }
    
    // 2. 验证身份证号唯一性
    if (!checkIdCardUnique(resident.getIdCardNo())) {
        throw new ServiceException("身份证号已存在");
    }
    
    // 3. 解析身份证号并验证一致性
    String parsedGender = IdCardValidator.parseGender(resident.getIdCardNo());
    if (!parsedGender.equals(resident.getGender())) {
        throw new ServiceException("性别与身份证号不一致");
    }
    
    Date parsedBirthDate = IdCardValidator.parseBirthDate(resident.getIdCardNo());
    // ... 验证出生日期一致性
    
    // 4. 设置默认值
    resident.setIsFocusGroup(0); // 默认非重点人群
    resident.setCreateBy(SecurityUtils.getUsername());
    
    // 5. 插入数据
    return residentMapper.insertResident(resident);
}

@Override
public Map<String, Object> importResidents(MultipartFile file, Long surveyorId) {
    Map<String, Object> result = new HashMap<>();
    int successCount = 0;
    int failureCount = 0;
    List<String> errorMessages = new ArrayList<>();
    
    try {
        // 读取Excel
        List<ResidentImportDTO> importList = ExcelUtil.readExcel(file, ResidentImportDTO.class);
        
        for (int i = 0; i < importList.size(); i++) {
            ResidentImportDTO dto = importList.get(i);
            try {
                // 验证并转换数据
                GcResident resident = convertToResident(dto, surveyorId);
                
                // 插入数据库
                residentMapper.insertResident(resident);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                errorMessages.add("第" + (i + 2) + "行:" + e.getMessage());
            }
        }
    } catch (Exception e) {
        throw new ServiceException("Excel解析失败:" + e.getMessage());
    }
    
    result.put("successCount", successCount);
    result.put("failureCount", failureCount);
    result.put("errorMessages", errorMessages);
    return result;
}
```

### 5. Controller层实现示例

```java
package com.javaxiaobear.module.gc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcResident;
import com.javaxiaobear.module.gc.service.IGcResidentService;

@RestController
@RequestMapping("/api/gc/resident")
public class GcResidentController extends BaseController {
    
    @Autowired
    private IGcResidentService residentService;
    
    /**
     * 查询居民列表
     */
    @GetMapping("/list")
    public TableDataInfo list(GcResident resident) {
        startPage();
        List<ResidentVO> list = residentService.selectResidentList(resident);
        return getDataTable(list);
    }
    
    /**
     * 查询居民详情
     */
    @GetMapping("/{residentId}")
    public AjaxResult getInfo(@PathVariable Long residentId) {
        return success(residentService.selectResidentById(residentId));
    }
    
    /**
     * 新增居民
     */
    @PostMapping
    public AjaxResult add(@RequestBody GcResident resident) {
        return toAjax(residentService.insertResident(resident));
    }
    
    /**
     * 修改居民
     */
    @PutMapping
    public AjaxResult edit(@RequestBody GcResident resident) {
        return toAjax(residentService.updateResident(resident));
    }
    
    /**
     * 删除居民
     */
    @DeleteMapping("/{residentId}")
    public AjaxResult remove(@PathVariable Long residentId) {
        return toAjax(residentService.deleteResidentById(residentId));
    }
    
    /**
     * 批量导入居民
     */
    @PostMapping("/import")
    public AjaxResult importResidents(MultipartFile file, Long surveyorId) {
        Map<String, Object> result = residentService.importResidents(file, surveyorId);
        return success(result);
    }
    
    /**
     * 导出居民列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, GcResident resident) {
        residentService.exportResidents(resident, response);
    }
    
    /**
     * 验证身份证号唯一性
     */
    @GetMapping("/checkIdCard")
    public AjaxResult checkIdCard(String idCardNo) {
        Boolean unique = residentService.checkIdCardUnique(idCardNo);
        return success(unique);
    }
}
```

## 前端开发文件清单

### 居民管理前端
1. `resident/index.vue` - 居民列表页
2. `resident/components/ResidentForm.vue` - 居民表单
3. `resident/components/RegionCascader.vue` - 五级联动选择器
4. `resident/components/ImportDialog.vue` - 批量导入对话框

### 问卷管理前端
1. `questionnaire/template/index.vue` - 问卷模板列表
2. `questionnaire/template/form.vue` - 模板表单（含JSON编辑器）
3. `questionnaire/fill/index.vue` - 问卷填写页（动态渲染）
4. `questionnaire/record/index.vue` - 问卷记录列表

### 任务管理前端
1. `task/index.vue` - 任务列表
2. `task/form.vue` - 任务表单
3. `task/progress.vue` - 进度统计页
4. `task/dashboard.vue` - 汇总看板

## 开发建议

由于代码量巨大（约200+文件），建议采用以下开发策略：

### 策略1：使用代码生成工具
利用MyBatis Generator或若依框架的代码生成功能，快速生成基础CRUD代码。

### 策略2：分模块迭代
1. 第1周：完成居民管理模块（后端+前端+测试）
2. 第2周：完成问卷管理模块（后端+前端+测试）
3. 第3周：完成任务管理模块（后端+前端+测试）

### 策略3：复用现有代码
- 参考系统已有的用户管理、部门管理等模块的代码结构
- 复用Excel导入导出工具类
- 复用分页、权限控制等基础功能

## 验证测试

### 单元测试
为每个Service类编写单元测试，覆盖核心业务逻辑。

### 接口测试
使用Postman或Apifox测试所有REST API。

### 集成测试
完整测试业务流程：居民录入→问卷填写→评分判定→任务统计。

## 总结

本文档提供了第二阶段核心功能的完整实现指南。所有关键代码已提供示例，剩余工作主要是：

1. 补全Service实现类的业务逻辑
2. 创建Controller类的所有接口
3. 开发前端页面和组件
4. 编写单元测试和接口测试

建议使用代码生成工具加速开发，专注于核心业务逻辑的实现。
