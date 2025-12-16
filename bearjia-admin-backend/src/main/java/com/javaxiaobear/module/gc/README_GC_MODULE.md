# 胃癌筛查模块(GC Module)包结构说明

## 模块命名
- **模块名**: gc (Gastric Cancer - 胃癌筛查)
- **包路径**: com.javaxiaobear.module.gc

## 目录结构

```
bearjia-admin-backend/src/main/java/com/javaxiaobear/module/gc/
├── controller/          # 控制器层 - RESTful API接口
│   ├── GcRegionController.java             # 行政区划管理
│   ├── GcSamplingSiteController.java       # 采血点管理
│   ├── GcSurveyorController.java           # 问卷调查员管理
│   ├── GcResidentController.java           # 居民管理
│   ├── GcQuestionnaireController.java      # 问卷管理
│   ├── GcTaskController.java               # 任务管理
│   ├── GcBloodAppointmentController.java   # 采血预约管理
│   ├── GcScreeningResultController.java    # 筛查结果管理
│   ├── GcFollowUpController.java           # 随访管理
│   └── GcStatisticsController.java         # 统计分析
│
├── domain/              # 实体类层 - 数据库表对应的Java实体
│   ├── GcRegion.java                       # 行政区划
│   ├── GcSamplingSite.java                 # 采血点
│   ├── GcSurveyor.java                     # 问卷调查员
│   ├── GcResident.java                     # 居民信息
│   ├── GcQuestionnaireTemplate.java        # 问卷模板
│   ├── GcQuestionnaireRecord.java          # 问卷记录
│   ├── GcBloodAppointment.java             # 采血预约
│   ├── GcPushLog.java                      # 推送日志
│   ├── GcTask.java                         # 任务
│   ├── GcScreeningResult.java              # 筛查结果
│   ├── GcFollowUp.java                     # 随访记录
│   ├── GcFollowUpTrack.java                # 随访跟踪记录
│   ├── vo/                                 # 视图对象(View Object)
│   │   ├── GcResidentVO.java              # 居民详情视图(含数据权限过滤)
│   │   ├── GcTaskProgressVO.java          # 任务进度统计视图
│   │   └── GcStatisticsVO.java            # 统计数据视图
│   └── dto/                                # 数据传输对象(Data Transfer Object)
│       ├── GcResidentQueryDTO.java        # 居民查询条件
│       ├── GcTaskAssignDTO.java           # 任务分配DTO
│       └── GcBloodAppointmentDTO.java     # 采血预约DTO
│
├── mapper/              # 数据访问层 - MyBatis Mapper接口
│   ├── GcRegionMapper.java
│   ├── GcSamplingSiteMapper.java
│   ├── GcSurveyorMapper.java
│   ├── GcResidentMapper.java
│   ├── GcQuestionnaireTemplateMapper.java
│   ├── GcQuestionnaireRecordMapper.java
│   ├── GcBloodAppointmentMapper.java
│   ├── GcPushLogMapper.java
│   ├── GcTaskMapper.java
│   ├── GcScreeningResultMapper.java
│   ├── GcFollowUpMapper.java
│   └── GcFollowUpTrackMapper.java
│
└── service/             # 业务逻辑层 - Service接口和实现
    ├── IGcRegionService.java
    ├── IGcSamplingSiteService.java
    ├── IGcSurveyorService.java
    ├── IGcResidentService.java
    ├── IGcQuestionnaireService.java
    ├── IGcTaskService.java
    ├── IGcBloodAppointmentService.java
    ├── IGcScreeningResultService.java
    ├── IGcFollowUpService.java
    ├── IGcStatisticsService.java
    └── impl/            # Service实现类
        ├── GcRegionServiceImpl.java
        ├── GcSamplingSiteServiceImpl.java
        ├── GcSurveyorServiceImpl.java
        ├── GcResidentServiceImpl.java
        ├── GcQuestionnaireServiceImpl.java
        ├── GcTaskServiceImpl.java
        ├── GcBloodAppointmentServiceImpl.java
        ├── GcScreeningResultServiceImpl.java
        ├── GcFollowUpServiceImpl.java
        └── GcStatisticsServiceImpl.java

bearjia-admin-backend/src/main/resources/mybatis/gc/
├── GcRegionMapper.xml                      # 行政区划Mapper XML
├── GcSamplingSiteMapper.xml                # 采血点Mapper XML
├── GcSurveyorMapper.xml                    # 问卷调查员Mapper XML
├── GcResidentMapper.xml                    # 居民Mapper XML
├── GcQuestionnaireTemplateMapper.xml       # 问卷模板Mapper XML
├── GcQuestionnaireRecordMapper.xml         # 问卷记录Mapper XML
├── GcBloodAppointmentMapper.xml            # 采血预约Mapper XML
├── GcPushLogMapper.xml                     # 推送日志Mapper XML
├── GcTaskMapper.xml                        # 任务Mapper XML
├── GcScreeningResultMapper.xml             # 筛查结果Mapper XML
├── GcFollowUpMapper.xml                    # 随访记录Mapper XML
└── GcFollowUpTrackMapper.xml               # 随访跟踪记录Mapper XML
```

## 分层职责

### 1. Controller层 (控制器层)
**职责**: 
- 接收前端请求,参数验证
- 调用Service层业务逻辑
- 返回统一格式的响应(AjaxResult)
- 权限控制(@PreAuthorize注解)

**示例**:
```java
@RestController
@RequestMapping("/gc/resident")
public class GcResidentController {
    
    @Autowired
    private IGcResidentService residentService;
    
    @PreAuthorize("@ss.hasPermi('gc:resident:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcResident resident) {
        startPage();
        List<GcResident> list = residentService.selectResidentList(resident);
        return getDataTable(list);
    }
}
```

### 2. Service层 (业务逻辑层)
**职责**:
- 核心业务逻辑处理
- 数据权限过滤
- 事务管理
- 调用Mapper层数据访问

**示例**:
```java
@Service
public class GcResidentServiceImpl implements IGcResidentService {
    
    @Autowired
    private GcResidentMapper residentMapper;
    
    @Override
    public List<GcResident> selectResidentList(GcResident resident) {
        // 数据权限过滤
        applyDataScope(resident);
        // 身份证号权限控制
        filterIdCardNo(resident);
        return residentMapper.selectResidentList(resident);
    }
}
```

### 3. Mapper层 (数据访问层)
**职责**:
- 定义数据库操作接口
- 对应MyBatis XML SQL映射

**示例**:
```java
public interface GcResidentMapper {
    List<GcResident> selectResidentList(GcResident resident);
    GcResident selectResidentById(Long residentId);
    int insertResident(GcResident resident);
    int updateResident(GcResident resident);
    int deleteResidentById(Long residentId);
}
```

### 4. Domain层 (实体层)
**职责**:
- 数据库表对应的Java实体类
- 包含VO(视图对象)和DTO(数据传输对象)

**实体类示例**:
```java
public class GcResident extends BaseEntity {
    private Long residentId;
    private String name;
    private Integer age;
    private String gender;
    private String idCardNo;
    // ... getter/setter
}
```

## 命名规范

### 1. 类名命名
- **Controller**: `Gc{模块}Controller`
- **Service接口**: `IGc{模块}Service`
- **Service实现**: `Gc{模块}ServiceImpl`
- **Mapper接口**: `Gc{模块}Mapper`
- **Entity**: `Gc{模块}`
- **VO**: `Gc{模块}VO`
- **DTO**: `Gc{模块}DTO`

### 2. 方法命名
- **查询列表**: `selectXxxList`
- **查询单个**: `selectXxxById`
- **新增**: `insertXxx`
- **修改**: `updateXxx`
- **删除**: `deleteXxxById`
- **批量删除**: `deleteXxxByIds`

### 3. 请求路径命名
- **模块路径**: `/gc/{子模块}`
- **示例**: 
  - `/gc/resident/list` - 居民列表
  - `/gc/site/add` - 新增采血点
  - `/gc/task/assign` - 任务分配

## 核心功能模块

### 模块1: 行政区划管理
**Controller**: GcRegionController  
**功能**:
- 区划树形结构查询
- 省市区街道社区级联查询
- 区划数据导入

### 模块2: 采血点管理
**Controller**: GcSamplingSiteController  
**功能**:
- 采血点CRUD
- 采血点列表查询(含数据权限)
- 采血点下拉列表

### 模块3: 问卷调查员管理
**Controller**: GcSurveyorController  
**功能**:
- 调查员CRUD
- 调查员绩效统计

### 模块4: 居民管理
**Controller**: GcResidentController  
**功能**:
- 居民信息CRUD
- 居民列表查询(含身份证号权限过滤)
- 居民导出Excel

### 模块5: 问卷管理
**Controller**: GcQuestionnaireController  
**功能**:
- 问卷模板管理
- 问卷填写提交
- 重点人群自动判定

### 模块6: 任务管理
**Controller**: GcTaskController  
**功能**:
- 任务分配(问卷/采血)
- 任务进度查询
- 多层级进度统计

### 模块7: 采血预约管理
**Controller**: GcBloodAppointmentController  
**功能**:
- 采血预约CRUD
- 推送第三方采血系统
- 采样状态回调接口

### 模块8: 筛查结果管理
**Controller**: GcScreeningResultController  
**功能**:
- 血液筛查结果录入
- 胃镜筛查结果录入(预留)
- 筛查结果统计

### 模块9: 随访管理
**Controller**: GcFollowUpController  
**功能**:
- 随访对象管理
- 随访跟踪记录
- 随访跟踪员分配

### 模块10: 统计分析
**Controller**: GcStatisticsController  
**功能**:
- 任务完成率统计
- 筛查结果分布统计
- 各层级进度汇总

## 开发顺序建议

### 第一批: 基础数据管理(1周)
1. GcRegion - 行政区划管理
2. GcSamplingSite - 采血点管理
3. GcSurveyor - 问卷调查员管理

### 第二批: 核心业务(2周)
4. GcResident - 居民管理(含身份证号权限)
5. GcQuestionnaire - 问卷管理(含重点人群判定)
6. GcTask - 任务管理(含进度统计)

### 第三批: 采血业务(1周)
7. GcBloodAppointment - 采血预约(含第三方对接)
8. GcScreeningResult - 筛查结果管理

### 第四批: 随访与统计(1周)
9. GcFollowUp - 随访管理
10. GcStatistics - 统计分析

## 依赖的基础组件

### 1. 已有基础类
- `BaseController` - 控制器基类
- `BaseEntity` - 实体基类
- `BaseService` - 服务基类
- `AjaxResult` - 统一响应对象
- `TableDataInfo` - 分页响应对象

### 2. 工具类
- `SecurityUtils` - 安全工具类(获取当前用户、角色等)
- `StringUtils` - 字符串工具类
- `DateUtils` - 日期工具类
- `ExcelUtil` - Excel导入导出工具
- `PageUtils` - 分页工具

### 3. 注解
- `@PreAuthorize` - 权限控制
- `@Log` - 操作日志
- `@DataScope` - 数据权限

## 数据权限实现

### 1. 行级数据权限
在Service层添加数据权限过滤:

```java
// 获取当前登录用户
LoginUser loginUser = SecurityUtils.getLoginUser();

// 根据角色过滤数据
if (loginUser.hasRole("district_admin")) {
    resident.setDistrict("濂溪区");
} else if (loginUser.hasRole("street_admin")) {
    resident.setStreet(loginUser.getStreet());
}
```

### 2. 字段级权限(身份证号)
在Service层或Mapper XML中动态处理:

```java
// 采血点管理员和医生不可查看身份证号
if (loginUser.hasRole("site_admin") || loginUser.hasRole("doctor")) {
    list.forEach(r -> r.setIdCardNo(null));
}
```

## 第三方系统对接

### 采血系统推送服务
创建独立的推送服务类:

```
com.javaxiaobear.module.gc.service.third/
├── IBloodSystemPushService.java           # 推送服务接口
└── impl/
    └── BloodSystemPushServiceImpl.java    # 推送服务实现
```

功能:
- 推送采血预约信息
- 接收采样状态回调
- 推送失败重试机制
- 推送日志记录

## 下一步开发清单

- [ ] 1. 创建12个Entity实体类
- [ ] 2. 创建12个Mapper接口
- [ ] 3. 创建12个Mapper XML
- [ ] 4. 创建Service接口和实现类
- [ ] 5. 创建Controller控制器
- [ ] 6. 编写单元测试
- [ ] 7. 前端接口联调
- [ ] 8. 性能优化
