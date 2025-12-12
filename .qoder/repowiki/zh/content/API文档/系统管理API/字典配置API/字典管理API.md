# 字典管理API

<cite>
**本文档引用的文件**   
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java)
- [hasPermi.js](file://bear-jia-vue3\src\directive\permission\hasPermi.js)
- [useDict.js](file://bear-jia-vue3\src\composables\useDict.js)
- [BearJiaUtil.js](file://bear-jia-vue3\src\utils\BearJiaUtil.js)
- [SysDictType.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysDictType.java)
- [DictUtils.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\common\utils\DictUtils.java)
</cite>

## 目录
1. [字典类型管理API](#字典类型管理api)
2. [权限控制机制](#权限控制机制)
3. [前端调用与最佳实践](#前端调用与最佳实践)
4. [错误处理与响应示例](#错误处理与响应示例)
5. [缓存机制详解](#缓存机制详解)

## 字典类型管理API

字典类型管理API提供了对系统字典类型的完整CRUD操作，包括查询、新增、修改、删除、导出和刷新缓存等功能。所有API端点均位于`/system/dict/type`路径下。

### 查询字典类型列表

**HTTP方法**: `GET`  
**URL路径**: `/system/dict/type/list`  
**权限标识**: `system:dict:list`  
**请求参数**:  
- `pageNum` (整数): 当前页码，用于分页查询
- `pageSize` (整数): 每页显示数量，用于分页查询
- `dictType` (字符串): 字典类型编码，用于模糊查询
- `dictName` (字符串): 字典类型名称，用于模糊查询
- `status` (字符串): 字典状态（0正常，1停用）

**响应格式**: `TableDataInfo`分页结果  
**响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [
    {
      "dictId": 1,
      "dictName": "用户状态",
      "dictType": "sys_user_status",
      "status": "0",
      "remark": "用户状态列表",
      "createTime": "2023-01-01 10:00:00"
    }
  ],
  "total": 1
}
```

**Section sources**
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js#L3-L10)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L37-L44)

### 查询字典类型详细信息

**HTTP方法**: `GET`  
**URL路径**: `/system/dict/type/{dictId}`  
**权限标识**: `system:dict:query`  
**路径参数**:  
- `dictId` (长整型): 字典类型ID

**响应格式**: `AjaxResult`通用结果  
**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "dictId": 1,
    "dictName": "用户状态",
    "dictType": "sys_user_status",
    "status": "0",
    "remark": "用户状态列表",
    "createTime": "2023-01-01 10:00:00"
  }
}
```

**Section sources**
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js#L12-L18)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L59-L64)

### 新增字典类型

**HTTP方法**: `POST`  
**URL路径**: `/system/dict/type`  
**权限标识**: `system:dict:add`  
**请求体**: `SysDictType`对象  
**请求体结构**:
```json
{
  "dictName": "字典类型名称",
  "dictType": "字典类型编码",
  "status": "0",
  "remark": "备注信息"
}
```

**响应格式**: `AjaxResult`通用结果  
**响应示例**:
```json
{
  "code": 200,
  "msg": "新增成功"
}
```

**Section sources**
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js#L20-L27)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L69-L80)

### 修改字典类型

**HTTP方法**: `PUT`  
**URL路径**: `/system/dict/type`  
**权限标识**: `system:dict:edit`  
**请求体**: `SysDictType`对象（包含`dictId`字段）  
**请求体结构**:
```json
{
  "dictId": 1,
  "dictName": "修改后的字典类型名称",
  "dictType": "sys_user_status",
  "status": "1",
  "remark": "修改后的备注信息"
}
```

**响应格式**: `AjaxResult`通用结果  
**响应示例**:
```json
{
  "code": 200,
  "msg": "修改成功"
}
```

**Section sources**
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js#L29-L36)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L85-L96)

### 删除字典类型

**HTTP方法**: `DELETE`  
**URL路径**: `/system/dict/type/{dictId}`  
**权限标识**: `system:dict:remove`  
**路径参数**:  
- `dictId` (长整型): 字典类型ID，支持批量删除，多个ID用逗号分隔

**响应格式**: `AjaxResult`通用结果  
**响应示例**:
```json
{
  "code": 200,
  "msg": "删除成功"
}
```

**Section sources**
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js#L38-L44)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L101-L108)

### 导出字典类型

**HTTP方法**: `POST`  
**URL路径**: `/system/dict/type/export`  
**权限标识**: `system:dict:export`  
**请求参数**: 与查询列表相同的分页和查询参数

**响应格式**: Excel文件流  
**说明**: 该接口返回一个Excel文件，包含符合查询条件的字典类型数据。

**Section sources**
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L46-L54)

### 刷新字典缓存

**HTTP方法**: `DELETE`  
**URL路径**: `/system/dict/type/refreshCache`  
**权限标识**: `system:dict:remove`  
**说明**: 该接口用于刷新系统字典的Redis缓存，使最新的字典数据生效。

**响应格式**: `AjaxResult`通用结果  
**响应示例**:
```json
{
  "code": 200,
  "msg": "刷新成功"
}
```

**Section sources**
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js#L46-L52)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L113-L120)

### 获取字典选择框列表

**HTTP方法**: `GET`  
**URL路径**: `/system/dict/type/optionselect`  
**说明**: 该接口用于获取所有字典类型的下拉框选项列表，通常用于前端组件的初始化。

**响应格式**: `AjaxResult`通用结果  
**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "dictId": 1,
      "dictName": "用户状态",
      "dictType": "sys_user_status"
    },
    {
      "dictId": 2,
      "dictName": "性别",
      "dictType": "sys_user_sex"
    }
  ]
}
```

**Section sources**
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js#L54-L60)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L125-L130)

## 权限控制机制

系统通过`@ss.hasPermi`指令实现细粒度的权限控制，确保只有具备相应权限的用户才能执行特定操作。

### 权限标识与操作对应关系

| 操作 | 权限标识 | 说明 |
|------|----------|------|
| 查询列表 | `system:dict:list` | 允许查看字典类型列表 |
| 查询详情 | `system:dict:query` | 允许查看字典类型详细信息 |
| 新增 | `system:dict:add` | 允许创建新的字典类型 |
| 修改 | `system:dict:edit` | 允许修改现有字典类型 |
| 删除 | `system:dict:remove` | 允许删除字典类型 |
| 导出 | `system:dict:export` | 允许导出字典类型数据 |

### 前端权限指令使用

前端通过`v-hasPermi`指令控制UI元素的显示，确保用户只能看到其有权限操作的按钮。

```vue
<a-button v-hasPermi="['system:dict:add']" @click="openAddModal">
  新增
</a-button>
<a-button v-hasPermi="['system:dict:edit']" @click="openUpdateModal(record)">
  修改
</a-button>
<a-button v-hasPermi="['system:dict:remove']" @click="handleDelete(record)">
  删除
</a-button>
```

**Section sources**
- [hasPermi.js](file://bear-jia-vue3\src\directive\permission\hasPermi.js)
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java)

## 前端调用与最佳实践

### API调用示例

#### 查询字典类型列表
```javascript
import { listType } from '@/api/system/dict/type';

// 查询第一页，每页10条数据
const queryParams = {
  pageNum: 1,
  pageSize: 10,
  dictType: 'sys_user_status'
};

listType(queryParams).then(response => {
  console.log('查询结果:', response.rows);
  console.log('总记录数:', response.total);
});
```

#### 新增字典类型
```javascript
import { addType } from '@/api/system/dict/type';

const newDictType = {
  dictName: '新的字典类型',
  dictType: 'new_dict_type',
  status: '0',
  remark: '这是一个新的字典类型'
};

addType(newDictType).then(response => {
  if (response.code === 200) {
    console.log('新增成功');
  }
});
```

### 使用useDict组合式函数

`useDict`是Vue 3的组合式函数，用于简化字典数据的获取和使用。

```javascript
import { useDict } from '@/composables/useDict';

// 在组件中使用
const { sys_user_status, sys_normal_disable } = useDict('sys_user_status', 'sys_normal_disable');

// 获取字典标签
const statusLabel = getDictLabel('0', sys_user_status.value);
```

**Section sources**
- [useDict.js](file://bear-jia-vue3\src\composables\useDict.js)
- [BearJiaUtil.js](file://bear-jia-vue3\src\utils\BearJiaUtil.js)

## 错误处理与响应示例

### 唯一性校验失败

当尝试新增或修改字典类型时，如果字典类型编码已存在，系统会返回错误信息。

**请求示例**:
```json
{
  "dictName": "重复的字典类型",
  "dictType": "sys_user_status",
  "status": "0"
}
```

**失败响应示例**:
```json
{
  "code": 500,
  "msg": "新增字典'重复的字典类型'失败，字典类型已存在"
}
```

### 前端调用最佳实践

1. **页面初始化时预加载字典数据**:
```javascript
onMounted(() => {
  // 预加载常用字典，提升页面性能
  useDict('sys_user_status', 'sys_normal_disable', 'sys_user_sex');
});
```

2. **错误处理**:
```javascript
addType(newDictType).then(response => {
  if (response.code === 200) {
    message.success('操作成功');
  } else {
    message.error(response.msg || '操作失败');
  }
}).catch(error => {
  message.error('网络请求失败');
});
```

**Section sources**
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L74-L77)
- [addUpdateModal.vue](file://bear-jia-vue3\src\views\system\dict\addUpdateModal.vue)

## 缓存机制详解

### 缓存刷新流程

`refreshCache`接口通过以下步骤实现字典数据的热更新：

1. 调用`resetDictCache()`方法清空Redis中的字典缓存
2. 下次查询字典数据时，自动从数据库重新加载并缓存

```java
@DeleteMapping("/refreshCache")
public AjaxResult refreshCache()
{
    dictTypeService.resetDictCache();
    return success();
}
```

### Redis缓存键结构

系统使用统一的缓存键前缀`sys_dict:`来管理字典缓存。

```java
public static String getCacheKey(String configKey)
{
    return CacheConstants.SYS_DICT_KEY + configKey;
}
```

### 缓存初始化

项目启动时，系统会自动初始化字典缓存，确保应用启动后字典数据即可用。

```java
@PostConstruct
public void init()
{
    loadingDictCache();
}
```

**Section sources**
- [SysDictTypeController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysDictTypeController.java#L113-L120)
- [DictUtils.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\common\utils\DictUtils.java#L170-L174)
- [SysDictTypeServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysDictTypeServiceImpl.java#L38-L41)