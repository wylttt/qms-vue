# 参数管理API

<cite>
**本文档引用文件**  
- [config.js](file://bear-jia-vue3\src\api\system\config.js)
- [index.vue](file://bear-jia-vue3\src\views\system\config\index.vue)
- [addUpdateModal.vue](file://bear-jia-vue3\src\views\system\config\addUpdateModal.vue)
- [detailModal.vue](file://bear-jia-vue3\src\views\system\config\detailModal.vue)
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java)
- [SysConfig.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysConfig.java)
- [ISysConfigService.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\ISysConfigService.java)
- [SysConfigServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysConfigServiceImpl.java)
</cite>

## 目录
1. [简介](#简介)
2. [API端点概览](#api端点概览)
3. [请求与响应结构](#请求与响应结构)
4. [核心API详细说明](#核心api详细说明)
5. [权限与安全](#权限与安全)
6. [错误处理策略](#错误处理策略)
7. [实际调用示例](#实际调用示例)
8. [缓存机制说明](#缓存机制说明)
9. [总结](#总结)

## 简介

参数管理模块提供了对系统配置参数的完整CRUD（创建、读取、更新、删除）操作支持。该模块允许管理员通过统一的接口管理所有系统级别的配置项，包括参数的查询、新增、修改、删除以及缓存刷新等功能。前端通过Vue3框架实现，后端基于Spring Boot构建RESTful API，采用Redis作为配置缓存存储，确保配置读取的高性能。

本API文档详细描述了所有可用的API端点，包括其HTTP方法、URL路径、请求参数、请求体结构、响应格式以及权限要求，旨在为开发者提供清晰、完整的集成指导。

**本文档引用文件**  
- [config.js](file://bear-jia-vue3\src\api\system\config.js)
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java)

## API端点概览

下表列出了参数管理模块提供的所有API端点及其基本用途：

| 功能 | HTTP方法 | URL路径 | 权限要求 |
|------|----------|--------|---------|
| 查询参数列表 | GET | `/system/config/list` | `system:config:list` |
| 获取参数详情 | GET | `/system/config/{configId}` | `system:config:query` |
| 根据键名查询值 | GET | `/system/config/configKey/{configKey}` | 无 |
| 新增参数配置 | POST | `/system/config` | `system:config:add` |
| 修改参数配置 | PUT | `/system/config` | `system:config:edit` |
| 删除参数配置 | DELETE | `/system/config/{configIds}` | `system:config:remove` |
| 刷新配置缓存 | DELETE | `/system/config/refreshCache` | `system:config:remove` |

## 请求与响应结构

### 请求参数说明

- **路径参数**：在URL中以`{}`包围的变量，如`{configId}`、`{configKey}`。
- **查询参数**：在GET请求中附加在URL后的键值对，用于列表查询的过滤条件。
- **请求体（Request Body）**：在POST和PUT请求中，包含`SysConfig`对象的JSON数据。

### 响应数据结构

#### 分页响应（TableDataInfo）

用于列表查询接口的响应，包含分页信息和数据列表。

```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [
    {
      "configId": 1,
      "configName": "系统名称",
      "configKey": "sys.core.name",
      "configValue": "QMS系统",
      "configType": "Y"
    }
  ],
  "total": 1
}
```

#### 单条记录响应（AjaxResult）

用于获取单条记录、新增、修改、删除等操作的响应。

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { /* 可选的返回数据 */ }
}
```

**本文档引用文件**  
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java)
- [SysConfig.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysConfig.java)

## 核心API详细说明

### 查询参数列表

**功能**：根据可选的过滤条件查询系统参数列表，支持分页。

**HTTP方法**：`GET`  
**URL路径**：`/system/config/list`  
**权限要求**：`system:config:list`

**请求参数（查询参数）**：
- `configName`：参数名称（模糊匹配）
- `configKey`：参数键名（模糊匹配）
- `configValue`：参数键值（模糊匹配）
- `configType`：系统内置（Y/N）

**响应**：`TableDataInfo` 结构，包含分页的参数列表。

**本文档引用文件**  
- [config.js](file://bear-jia-vue3\src\api\system\config.js#L4-L9)
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java#L40-L47)

### 获取参数详情

**功能**：根据参数ID获取单个参数的详细信息。

**HTTP方法**：`GET`  
**URL路径**：`/system/config/{configId}`  
**权限要求**：`system:config:query`

**路径参数**：
- `configId`：参数的唯一标识符（Long类型）

**响应**：`AjaxResult` 结构，`data`字段包含完整的`SysConfig`对象。

**本文档引用文件**  
- [config.js](file://bear-jia-vue3\src\api\system\config.js#L13-L17)
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java#L62-L67)

### 根据键名查询值

**功能**：这是参数管理中最便捷的接口，允许客户端直接通过`configKey`获取对应的`configValue`。特别适用于前端在应用启动时批量加载配置，或在运行时动态获取配置值的场景。

**HTTP方法**：`GET`  
**URL路径**：`/system/config/configKey/{configKey}`  
**权限要求**：无

**路径参数**：
- `configKey`：参数的键名（String类型）

**响应**：`AjaxResult` 结构，`data`字段直接返回`String`类型的配置值。

**本文档引用文件**  
- [config.js](file://bear-jia-vue3\src\api\system\config.js#L21-L25)
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java#L72-L76)

### 新增参数配置

**功能**：创建一个新的系统参数。

**HTTP方法**：`POST`  
**URL路径**：`/system/config`  
**权限要求**：`system:config:add`

**请求体（JSON）**：
```json
{
  "configName": "新参数名称",
  "configKey": "new.config.key",
  "configValue": "新参数值",
  "configType": "N",
  "remark": "这是一个新参数"
}
```

**校验逻辑**：调用`checkConfigKeyUnique`服务方法，检查`configKey`是否已存在。如果键名重复，返回错误信息。

**响应**：`AjaxResult` 结构，成功时`code`为200。

**本文档引用文件**  
- [addUpdateModal.vue](file://bear-jia-vue3\src\views\system\config\addUpdateModal.vue#L88-L94)
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java#L81-L92)

### 修改参数配置

**功能**：更新现有参数的配置信息。

**HTTP方法**：`PUT`  
**URL路径**：`/system/config`  
**权限要求**：`system:config:edit`

**请求体（JSON）**：
```json
{
  "configId": 1,
  "configName": "已修改的名称",
  "configKey": "modified.key",
  "configValue": "已修改的值",
  "configType": "Y",
  "remark": "已修改"
}
```

**校验逻辑**：同样调用`checkConfigKeyUnique`方法，确保修改后的`configKey`不会与数据库中其他参数的键名冲突（排除自身ID）。

**响应**：`AjaxResult` 结构，成功时`code`为200。

**本文档引用文件**  
- [addUpdateModal.vue](file://bear-jia-vue3\src\views\system\config\addUpdateModal.vue#L96-L102)
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java#L97-L108)

### 删除参数配置

**功能**：根据一个或多个`configId`删除参数。

**HTTP方法**：`DELETE`  
**URL路径**：`/system/config/{configIds}`  
**权限要求**：`system:config:remove`

**路径参数**：
- `configIds`：要删除的参数ID数组，以逗号分隔（例如：`1,2,3`）

**业务逻辑**：服务层会检查被删除的参数是否为“系统内置”（`configType = 'Y'`），如果是，则抛出`ServiceException`阻止删除。

**响应**：`AjaxResult` 结构，成功时`code`为200。

**本文档引用文件**  
- [index.vue](file://bear-jia-vue3\src\views\system\config\index.vue#L57)
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java#L113-L120)

### 刷新缓存

**功能**：重置后端的配置缓存。当参数被修改后，虽然服务层会自动更新Redis缓存，但此接口提供了一种强制刷新所有配置缓存的手段，确保所有节点的缓存状态一致。

**HTTP方法**：`DELETE`  
**URL路径**：`/system/config/refreshCache`  
**权限要求**：`system:config:remove`

**执行流程**：
1.  调用`clearConfigCache()`：清空Redis中所有以`sys_config:`为前缀的缓存项。
2.  调用`loadingConfigCache()`：从数据库重新加载所有配置项到Redis缓存中。

**响应**：`AjaxResult` 结构，成功时`code`为200。

**本文档引用文件**  
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java#L125-L132)
- [SysConfigServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysConfigServiceImpl.java#L194-L199)

## 权限与安全

所有API端点都集成了基于Spring Security的权限控制。权限检查通过`@PreAuthorize("@ss.hasPermi('permission')")`注解实现。

- **查询类操作**（列表、详情）：需要`list`和`query`权限。
- **修改类操作**（新增、修改、删除、刷新缓存）：需要`add`、`edit`或`remove`权限。
- **`getConfigKey`接口**：无需权限，因为它只返回配置值，不暴露敏感的元数据（如ID、创建时间等），且通常用于客户端初始化。

**本文档引用文件**  
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java)

## 错误处理策略

系统采用统一的异常处理机制，返回标准化的错误响应。

**常见错误码与消息**：
- `500`：内部服务器错误，通常伴随具体的错误描述。
- `403`：权限不足，用户没有执行该操作的权限。
- **业务错误**：例如，新增或修改时`configKey`重复，会返回`code`为500的`AjaxResult`，`msg`字段包含具体的错误信息，如“新增参数'xxx'失败，参数键名已存在”。

**本文档引用文件**  
- [SysConfigController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysConfigController.java#L87-L88)
- [SysConfigServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysConfigServiceImpl.java#L161-L162)

## 实际调用示例

### 前端应用启动时加载配置

```javascript
// 在应用初始化时，批量获取关键配置
const configKeys = ['sys.core.name', 'sys.account.captchaEnabled', 'app.version'];
const configPromises = configKeys.map(key => getConfigKey(key));

Promise.all(configPromises).then(results => {
  // results 是一个包含所有配置值的数组
  console.log('系统名称:', results[0]);
  console.log('验证码开关:', results[1]);
});
```

### 管理界面中的CRUD操作

在管理界面中，用户可以通过表格进行操作：
- **新增**：点击“新增”按钮，弹出表单，填写`configName`、`configKey`等信息后提交。
- **修改**：在表格操作列点击“编辑”，加载当前数据到表单，修改后提交。
- **删除**：勾选行后点击“删除”按钮，确认后执行删除。
- **查看**：点击“详情”查看完整的参数信息。

**本文档引用文件**  
- [index.vue](file://bear-jia-vue3\src\views\system\config\index.vue)
- [addUpdateModal.vue](file://bear-jia-vue3\src\views\system\config\addUpdateModal.vue)
- [detailModal.vue](file://bear-jia-vue3\src\views\system\config\detailModal.vue)

## 缓存机制说明

系统采用Redis作为配置缓存，以提高读取性能。

- **缓存键**：格式为`sys_config:{configKey}`（由`getCacheKey`方法生成）。
- **缓存读取**：`getConfigKey`接口优先从Redis读取，若不存在则查询数据库并回填缓存。
- **缓存更新**：在`insertConfig`和`updateConfig`操作成功后，自动更新Redis中的对应缓存。
- **缓存删除**：在`deleteConfigByIds`操作中，会同步删除Redis中的缓存。
- **缓存刷新**：`refreshCache`接口提供了一键清空并重新加载所有缓存的能力。

**本文档引用文件**  
- [SysConfigServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysConfigServiceImpl.java)

## 总结

参数管理API提供了一套完整、安全、高效的系统配置管理方案。通过清晰的RESTful设计和完善的权限控制，开发者可以轻松地集成和使用这些接口。`getConfigKey`接口的无权限设计极大地便利了客户端的配置获取，而基于Redis的缓存机制则保证了系统的高性能。开发者在使用时，应特别注意`checkConfigKeyUnique`的校验逻辑和`refreshCache`接口的使用场景。