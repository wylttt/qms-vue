# API文档

<cite>
**本文档中引用的文件**  
- [user.js](file://bear-jia-vue3\src\api\system\user.js)
- [role.js](file://bear-jia-vue3\src\api\system\role.js)
- [menu.js](file://bear-jia-vue3\src\api\system\menu.js)
- [dept.js](file://bear-jia-vue3\src\api\system\dept.js)
- [post.js](file://bear-jia-vue3\src\api\system\post.js)
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js)
- [data.js](file://bear-jia-vue3\src\api\system\dict\data.js)
- [config.js](file://bear-jia-vue3\src\api\system\config.js)
- [notice.js](file://bear-jia-vue3\src\api\system\notice.js)
- [file.js](file://bear-jia-vue3\src\api\system\file.js)
- [server.js](file://bear-jia-vue3\src\api\monitor\server.js)
- [cache.js](file://bear-jia-vue3\src\api\monitor\cache.js)
- [job.js](file://bear-jia-vue3\src\api\monitor\job.js)
- [gen.js](file://bear-jia-vue3\src\api\tool\gen.js)
- [login.js](file://bear-jia-vue3\src\api\login.js)
- [request.js](file://bear-jia-vue3\src\utils\request.js)
- [errorCode.js](file://bear-jia-vue3\src\utils\errorCode.js)
</cite>

## 目录
1. [简介](#简介)
2. [通用请求头与响应头](#通用请求头与响应头)
3. [分页参数规范](#分页参数规范)
4. [错误码体系](#错误码体系)
5. [认证机制](#认证机制)
6. [API版本管理](#api版本管理)
7. [系统管理API](#系统管理api)
   1. [用户管理](#用户管理)
   2. [角色管理](#角色管理)
   3. [菜单管理](#菜单管理)
   4. [部门管理](#部门管理)
   5. [岗位管理](#岗位管理)
   6. [字典管理](#字典管理)
   7. [参数配置](#参数配置)
   8. [公告管理](#公告管理)
   9. [文件管理](#文件管理)
8. [系统监控API](#系统监控api)
   1. [服务器监控](#服务器监控)
   2. [缓存监控](#缓存监控)
   3. [定时任务](#定时任务)
9. [系统工具API](#系统工具api)
   1. [代码生成](#代码生成)
10. [登录认证API](#登录认证api)
11. [API调用示例](#api调用示例)

## 简介
本API文档全面记录了QMS-Vue系统提供的所有RESTful接口，按照功能模块组织，涵盖系统管理、系统监控和系统工具三大类API。文档详细说明了每个API端点的HTTP方法、URL路径、请求参数、响应格式、认证要求等信息，并提供实际调用示例。

## 通用请求头与响应头
### 通用请求头
| 请求头 | 类型 | 是否必需 | 描述 |
|--------|------|----------|------|
| Authorization | String | 是 | JWT令牌，格式为"Bearer {token}" |
| Content-Type | String | 否 | 请求体内容类型，如application/json、multipart/form-data等 |
| Accept | String | 否 | 客户端期望接收的响应内容类型 |

### 通用响应头
| 响应头 | 类型 | 描述 |
|--------|------|------|
| Content-Type | String | 响应体内容类型，通常为application/json |
| X-Response-Time | String | 服务器处理请求的时间（毫秒） |
| Cache-Control | String | 缓存控制策略 |

## 分页参数规范
所有支持分页的API接口均使用统一的分页参数约定：

| 参数名 | 类型 | 是否必需 | 默认值 | 描述 |
|--------|------|----------|--------|------|
| pageNum | Integer | 否 | 1 | 当前页码，从1开始 |
| pageSize | Integer | 否 | 10 | 每页显示记录数，最大值通常为100 |

分页响应数据结构包含以下字段：
- `total`: 总记录数
- `rows`: 当前页数据列表
- `pageNum`: 当前页码
- `pageSize`: 每页大小

**Section sources**
- [user.js](file://bear-jia-vue3\src\api\system\user.js#L5-L131)
- [role.js](file://bear-jia-vue3\src\api\system\role.js#L4-L112)

## 错误码体系
### HTTP状态码
| 状态码 | 含义 | 说明 |
|--------|------|------|
| 200 | OK | 请求成功 |
| 201 | Created | 资源创建成功 |
| 204 | No Content | 请求成功但无返回内容 |
| 400 | Bad Request | 请求参数错误 |
| 401 | Unauthorized | 未授权，需要身份验证 |
| 403 | Forbidden | 禁止访问，权限不足 |
| 404 | Not Found | 资源不存在 |
| 405 | Method Not Allowed | 请求方法不被允许 |
| 500 | Internal Server Error | 服务器内部错误 |

### 业务错误码
| 错误码 | 含义 | 说明 |
|--------|------|------|
| 500 | 系统异常 | 服务器内部错误 |
| 401 | 未授权访问 | 用户未登录或令牌失效 |
| 403 | 无权限操作 | 用户权限不足 |
| 404 | 资源不存在 | 请求的资源未找到 |
| 405 | 方法不允许 | 请求方法不支持 |
| 406 | 参数验证失败 | 请求参数不符合验证规则 |
| 501 | 业务异常 | 业务逻辑处理异常 |
| 502 | 未知错误 | 未知的系统错误 |

**Section sources**
- [errorCode.js](file://bear-jia-vue3\src\utils\errorCode.js)

## 认证机制
系统采用JWT（JSON Web Token）进行身份认证和授权。用户登录成功后，服务器返回JWT令牌，客户端在后续请求中需要在Authorization头中携带该令牌。

认证流程：
1. 用户提交用户名和密码进行登录
2. 服务器验证凭证，生成JWT令牌并返回
3. 客户端在后续请求的Authorization头中携带令牌
4. 服务器验证令牌的有效性，决定是否授权访问

**Section sources**
- [login.js](file://bear-jia-vue3\src\api\login.js#L4-L57)
- [request.js](file://bear-jia-vue3\src\utils\request.js)

## API版本管理
本系统目前采用单一版本管理策略，所有API接口均位于根路径下。未来如需支持多版本，将通过以下方式实现：
- URL路径版本控制：`/v1/system/user`、`/v2/system/user`
- 请求头版本控制：`Accept: application/vnd.qms.v1+json`
- 查询参数版本控制：`?version=1`

当前所有API接口均视为v1版本，无需在URL中显式指定版本号。

## 系统管理API

### 用户管理
提供用户信息的增删改查、密码重置、状态管理等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/user/list | 查询用户列表（分页） |
| GET | /system/user/{userId} | 查询用户详细信息 |
| POST | /system/user | 新增用户 |
| PUT | /system/user | 修改用户 |
| DELETE | /system/user/{userId} | 删除用户 |
| PUT | /system/user/resetPwd | 重置用户密码 |
| PUT | /system/user/changeStatus | 修改用户状态 |
| GET | /system/user/profile | 查询用户个人信息 |
| PUT | /system/user/profile | 修改用户个人信息 |
| PUT | /system/user/profile/updatePwd | 修改用户密码 |
| POST | /system/user/profile/avatar | 上传用户头像 |
| GET | /system/user/authRole/{userId} | 查询用户授权角色 |
| PUT | /system/user/authRole | 保存用户授权角色 |

**Section sources**
- [user.js](file://bear-jia-vue3\src\api\system\user.js#L5-L131)

### 角色管理
提供角色信息的增删改查、数据权限设置、用户授权等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/role/list | 查询角色列表（分页） |
| GET | /system/role/{roleId} | 查询角色详细信息 |
| POST | /system/role | 新增角色 |
| PUT | /system/role | 修改角色 |
| PUT | /system/role/dataScope | 设置角色数据权限 |
| PUT | /system/role/changeStatus | 修改角色状态 |
| DELETE | /system/role/{roleId} | 删除角色 |
| GET | /system/role/authUser/allocatedList | 查询角色已授权用户列表 |
| GET | /system/role/authUser/unallocatedList | 查询角色未授权用户列表 |
| PUT | /system/role/authUser/cancel | 取消用户授权 |
| PUT | /system/role/authUser/cancelAll | 批量取消用户授权 |
| PUT | /system/role/authUser/selectAll | 授权用户选择 |

**Section sources**
- [role.js](file://bear-jia-vue3\src\api\system\role.js#L4-L112)

### 菜单管理
提供菜单信息的增删改查、路由获取、树形结构查询等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /getRouters | 获取用户路由 |
| GET | /system/menu/list | 查询菜单列表 |
| GET | /system/menu/{menuId} | 查询菜单详细信息 |
| GET | /system/menu/treeselect | 查询菜单下拉树结构 |
| GET | /system/menu/roleMenuTreeselect/{roleId} | 根据角色查询菜单树结构 |
| POST | /system/menu | 新增菜单 |
| PUT | /system/menu | 修改菜单 |
| DELETE | /system/menu/{menuId} | 删除菜单 |

**Section sources**
- [menu.js](file://bear-jia-vue3\src\api\system\menu.js#L4-L68)

### 部门管理
提供部门信息的增删改查、树形结构查询等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/dept/list | 查询部门列表 |
| GET | /system/dept/list/exclude/{deptId} | 查询部门列表（排除指定节点） |
| GET | /system/dept/{deptId} | 查询部门详细信息 |
| GET | /system/user/deptTree | 查询部门下拉树结构 |
| GET | /system/dept/roleDeptTreeselect/{roleId} | 根据角色查询部门树结构 |
| POST | /system/dept | 新增部门 |
| PUT | /system/dept | 修改部门 |
| DELETE | /system/dept/{deptId} | 删除部门 |

**Section sources**
- [dept.js](file://bear-jia-vue3\src\api\system\dept.js#L4-L69)

### 岗位管理
提供岗位信息的增删改查、导出等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/post/list | 查询岗位列表（分页） |
| GET | /system/post/{postId} | 查询岗位详细信息 |
| POST | /system/post | 新增岗位 |
| PUT | /system/post | 修改岗位 |
| DELETE | /system/post/{postId} | 删除岗位 |
| GET | /system/post/export | 导出岗位 |

**Section sources**
- [post.js](file://bear-jia-vue3\src\api\system\post.js#L4-L53)

### 字典管理
提供字典类型和字典数据的增删改查、缓存刷新等功能。

#### 字典类型管理
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/dict/type/list | 查询字典类型列表 |
| GET | /system/dict/type/{dictId} | 查询字典类型详细信息 |
| POST | /system/dict/type | 新增字典类型 |
| PUT | /system/dict/type | 修改字典类型 |
| DELETE | /system/dict/type/{dictId} | 删除字典类型 |
| DELETE | /system/dict/type/refreshCache | 刷新字典缓存 |
| GET | /system/dict/type/optionselect | 获取字典选择框列表 |

#### 字典数据管理
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/dict/data/list | 查询字典数据列表 |
| GET | /system/dict/data/{dictCode} | 查询字典数据详细信息 |
| GET | /system/dict/data/type/{dictType} | 根据字典类型查询数据 |
| POST | /system/dict/data | 新增字典数据 |
| PUT | /system/dict/data | 修改字典数据 |
| DELETE | /system/dict/data/{dictCode} | 删除字典数据 |

**Section sources**
- [type.js](file://bear-jia-vue3\src\api\system\dict\type.js#L4-L61)
- [data.js](file://bear-jia-vue3\src\api\system\dict\data.js#L4-L53)

### 参数配置
提供系统参数的增删改查、缓存刷新等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/config/list | 查询参数列表 |
| GET | /system/config/{configId} | 查询参数详细信息 |
| GET | /system/config/configKey/{configKey} | 根据键名查询参数值 |
| POST | /system/config | 新增参数配置 |
| PUT | /system/config | 修改参数配置 |
| DELETE | /system/config/{configId} | 删除参数配置 |
| DELETE | /system/config/refreshCache | 刷新参数缓存 |

**Section sources**
- [config.js](file://bear-jia-vue3\src\api\system\config.js#L4-L62)

### 公告管理
提供公告信息的增删改查等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/notice/list | 查询公告列表 |
| GET | /system/notice/{noticeId} | 查询公告详细信息 |
| POST | /system/notice | 新增公告 |
| PUT | /system/notice | 修改公告 |
| DELETE | /system/notice/{noticeId} | 删除公告 |

**Section sources**
- [notice.js](file://bear-jia-vue3\src\api\system\notice.js#L4-L44)

### 文件管理
提供文件的上传、下载、预览、管理等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /system/file/list | 查询文件列表 |
| GET | /system/file/{id} | 查询文件详细信息 |
| GET | /system/file/business/{businessType} | 根据业务类型查询文件 |
| GET | /system/file/rag | 查询RAG文档列表 |
| GET | /system/file/avatar/{userId} | 查询用户头像 |
| GET | /system/file/stats | 获取文件统计信息 |
| GET | /system/file/duplicates | 查询重复文件 |
| GET | /system/file/orphans | 查询孤立文件 |
| POST | /system/file | 新增文件 |
| PUT | /system/file | 修改文件 |
| PUT | /system/file/status/{id}/{status} | 更新文件状态 |
| PUT | /system/file/progress/{id} | 更新处理进度 |
| DELETE | /system/file/{ids} | 删除文件 |
| DELETE | /system/file/physical/{ids} | 物理删除文件 |
| DELETE | /system/file/clean/{days} | 清理已删除文件 |
| POST | /system/file/upload | 上传文件 |
| POST | /system/file/upload/rag | 上传RAG文档 |
| POST | /system/file/upload/avatar | 上传用户头像 |
| POST | /system/file/upload/attachment | 上传系统附件 |
| GET | /system/file/download/{id} | 获取文件下载URL |
| GET | /system/file/preview/{id} | 获取文件预览URL |
| POST | /system/file/exists | 检查文件是否存在 |
| GET | /system/file/folderTree | 查询文件夹树结构 |
| GET | /system/file/folder/{parentId} | 查询文件夹内容 |
| POST | /system/file/createFolder | 创建文件夹 |
| POST | /system/file/move | 移动文件 |
| POST | /system/file/rename | 重命名文件 |
| GET | /system/file/path/{folderId} | 获取文件夹路径 |
| GET | /system/file/checkFolderName | 检查文件夹名称是否存在 |

**Section sources**
- [file.js](file://bear-jia-vue3\src\api\system\file.js#L4-L374)

## 系统监控API

### 服务器监控
提供服务器运行状态的监控信息。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /monitor/server | 查询服务器详细信息 |

**Section sources**
- [server.js](file://bear-jia-vue3\src\api\monitor\server.js#L4-L9)

### 缓存监控
提供缓存信息的查询和清理功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /monitor/cache | 查询缓存详细信息 |
| GET | /monitor/cache/getNames | 查询缓存名称列表 |
| GET | /monitor/cache/getKeys/{cacheName} | 查询缓存键名列表 |
| GET | /monitor/cache/getValue/{cacheName}/{cacheKey} | 查询缓存内容 |
| DELETE | /monitor/cache/clearCacheName/{cacheName} | 清理指定名称缓存 |
| DELETE | /monitor/cache/clearCacheKey/{cacheKey} | 清理指定键名缓存 |
| DELETE | /monitor/cache/clearCacheAll | 清理全部缓存 |

**Section sources**
- [cache.js](file://bear-jia-vue3\src\api\monitor\cache.js#L4-L58)

### 定时任务
提供定时任务的增删改查、执行控制等功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /monitor/job/list | 查询定时任务列表 |
| GET | /monitor/job/{jobId} | 查询定时任务详细信息 |
| POST | /monitor/job | 新增定时任务 |
| PUT | /monitor/job | 修改定时任务 |
| DELETE | /monitor/job/{jobId} | 删除定时任务 |
| GET | /monitor/job/export | 导出定时任务 |
| PUT | /monitor/job/changeStatus | 修改任务状态 |
| PUT | /monitor/job/run | 立即执行任务 |

**Section sources**
- [job.js](file://bear-jia-vue3\src\api\monitor\job.js#L4-L80)

## 系统工具API

### 代码生成
提供数据库表的代码生成功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /tool/gen/list | 查询生成表数据 |
| GET | /tool/gen/db/list | 查询数据库表列表 |
| GET | /tool/gen/{tableId} | 查询表详细信息 |
| PUT | /tool/gen | 修改代码生成信息 |
| POST | /tool/gen/importTable | 导入表 |
| GET | /tool/gen/preview/{tableId} | 预览生成代码 |
| DELETE | /tool/gen/{tableId} | 删除表数据 |
| GET | /tool/gen/genCode/{tableName} | 生成代码（自定义路径） |
| GET | /tool/gen/synchDb/{tableName} | 同步数据库 |
| GET | /tool/gen/batchGenCode | 批量生成代码 |
| POST | /tool/gen/createTable | 创建表 |

**Section sources**
- [gen.js](file://bear-jia-vue3\src\api\tool\gen.js#L4-L94)

## 登录认证API
提供用户登录、注册、退出等认证相关功能。

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /login | 用户登录 |
| POST | /register | 用户注册 |
| GET | /getInfo | 获取用户详细信息 |
| POST | /logout | 用户退出 |
| GET | /captchaImage | 获取验证码图片 |

**Section sources**
- [login.js](file://bear-jia-vue3\src\api\login.js#L4-L57)

## API调用示例

### 使用curl调用用户列表API
```bash
curl -X GET "http://localhost:8080/system/user/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json"
```

### 使用JavaScript调用新增用户API
```javascript
fetch('/system/user', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    userName: 'zhangsan',
    nickName: '张三',
    password: '123456',
    deptId: 101,
    roleId: 2
  })
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

### 使用JavaScript调用文件上传API
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('businessType', 'avatar');
formData.append('userId', '123');

fetch('/system/file/upload/avatar', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'
  },
  body: formData
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

### 使用JavaScript调用登录API
```javascript
fetch('/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin',
    password: 'admin123',
    code: '1234',
    uuid: 'a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8'
  })
})
.then(response => response.json())
.then(data => {
  // 登录成功，保存token
  localStorage.setItem('token', data.token);
  console.log('Login successful');
})
.catch(error => console.error('Login error:', error));
```