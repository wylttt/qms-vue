# 菜单CRUD操作API

<cite>
**本文档引用的文件**
- [menu.js](file://bear-jia-vue3\src\api\system\menu.js)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java)
- [SysMenuServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysMenuServiceImpl.java)
- [SysMenu.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysMenu.java)
- [SysMenuMapper.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\mapper\SysMenuMapper.java)
- [SysMenuMapper.xml](file://bearjia-admin-backend\src\main\resources\mybatis\system\SysMenuMapper.xml)
- [UserConstants.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\common\constant\UserConstants.java)
- [StringUtils.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\common\utils\StringUtils.java)
- [Constants.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\common\constant\Constants.java)
- [Log.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\framework\aspectj\lang\annotation\Log.java)
</cite>

## 目录
1. [新增菜单](#新增菜单)
2. [修改菜单](#修改菜单)
3. [删除菜单](#删除菜单)
4. [查询菜单](#查询菜单)
5. [权限控制](#权限控制)
6. [操作日志](#操作日志)
7. [前端调用最佳实践](#前端调用最佳实践)

## 新增菜单

### 接口信息
- **HTTP方法**: POST
- **URL路径**: `/system/menu`
- **认证要求**: JWT Token
- **权限要求**: `system:menu:add`

### 请求参数
#### 请求体 (JSON)
```json
{
  "menuName": "菜单名称",
  "parentId": 0,
  "orderNum": 1,
  "path": "路由地址",
  "component": "组件路径",
  "query": "路由参数",
  "isFrame": "1",
  "isCache": "0",
  "menuType": "M",
  "visible": "0",
  "status": "0",
  "perms": "权限字符串",
  "icon": "菜单图标",
  "remark": "备注"
}
```

### 响应数据结构
#### 成功响应 (HTTP 200)
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

#### 错误响应
- **菜单名称已存在**
```json
{
  "code": 500,
  "msg": "新增菜单'菜单名称'失败，菜单名称已存在"
}
```

- **外链地址格式错误**
```json
{
  "code": 500,
  "msg": "新增菜单'菜单名称'失败，地址必须以http(s)://开头"
}
```

### 校验逻辑
1. **唯一性校验**: 检查相同父菜单下是否存在同名菜单
2. **外链地址格式校验**: 当`isFrame`为"0"时，`path`必须以`http://`或`https://`开头

### 请求示例
```javascript
addMenu({
  menuName: "系统管理",
  parentId: 0,
  orderNum: 1,
  path: "/system",
  component: "Layout",
  menuType: "M",
  visible: "0",
  status: "0",
  icon: "system"
})
```

**Section sources**
- [menu.js](file://bear-jia-vue3\src\api\system\menu.js#L44-L51)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L80-L98)
- [SysMenuServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysMenuServiceImpl.java#L337-L347)
- [SysMenuMapper.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\mapper\SysMenuMapper.java#L124)
- [SysMenuMapper.xml](file://bearjia-admin-backend\src\main\resources\mybatis\system\SysMenuMapper.xml#L136-L139)
- [StringUtils.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\common\utils\StringUtils.java#L294-L298)
- [Constants.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\common\constant\Constants.java#L36-L41)

## 修改菜单

### 接口信息
- **HTTP方法**: PUT
- **URL路径**: `/system/menu`
- **认证要求**: JWT Token
- **权限要求**: `system:menu:edit`

### 请求参数
#### 请求体 (JSON)
```json
{
  "menuId": 1,
  "menuName": "菜单名称",
  "parentId": 0,
  "orderNum": 1,
  "path": "路由地址",
  "component": "组件路径",
  "query": "路由参数",
  "isFrame": "1",
  "isCache": "0",
  "menuType": "M",
  "visible": "0",
  "status": "0",
  "perms": "权限字符串",
  "icon": "菜单图标",
  "remark": "备注"
}
```

### 响应数据结构
#### 成功响应 (HTTP 200)
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

#### 错误响应
- **菜单名称已存在**
```json
{
  "code": 500,
  "msg": "修改菜单'菜单名称'失败，菜单名称已存在"
}
```

- **外链地址格式错误**
```json
{
  "code": 500,
  "msg": "修改菜单'菜单名称'失败，地址必须以http(s)://开头"
}
```

- **自引用错误**
```json
{
  "code": 500,
  "msg": "修改菜单'菜单名称'失败，上级菜单不能选择自己"
}
```

### 校验逻辑
1. **唯一性校验**: 检查相同父菜单下是否存在同名菜单（排除自身）
2. **外链地址格式校验**: 当`isFrame`为"0"时，`path`必须以`http://`或`https://`开头
3. **自引用校验**: 确保`menuId`不等于`parentId`

### 请求示例
```javascript
updateMenu({
  menuId: 1,
  menuName: "系统管理",
  perms: "system:menu:edit",
  status: "0"
})
```

**Section sources**
- [menu.js](file://bear-jia-vue3\src\api\system\menu.js#L53-L60)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L100-L122)
- [SysMenuServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysMenuServiceImpl.java#L337-L347)
- [SysMenuMapper.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\mapper\SysMenuMapper.java#L124)

## 删除菜单

### 接口信息
- **HTTP方法**: DELETE
- **URL路径**: `/system/menu/{menuId}`
- **认证要求**: JWT Token
- **权限要求**: `system:menu:remove`

### 请求参数
#### 路径参数
- `menuId`: 菜单ID (Long)

### 响应数据结构
#### 成功响应 (HTTP 200)
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

#### 错误响应
- **存在子菜单**
```json
{
  "code": 301,
  "msg": "存在子菜单,不允许删除"
}
```

- **菜单已分配给角色**
```json
{
  "code": 301,
  "msg": "菜单已分配,不允许删除"
}
```

### 校验逻辑
1. **子菜单存在性校验**: 检查是否存在子菜单
2. **角色分配校验**: 检查是否已分配给任何角色

### 请求示例
```javascript
delMenu(1)
```

**Section sources**
- [menu.js](file://bear-jia-vue3\src\api\system\menu.js#L62-L67)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L124-L141)
- [SysMenuServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysMenuServiceImpl.java#L275-L280)
- [SysMenuMapper.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\mapper\SysMenuMapper.java#L91)
- [SysMenuServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysMenuServiceImpl.java#L288-L293)
- [SysRoleMenuMapper.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\mapper\SysRoleMenuMapper.java#L65)

## 查询菜单

### 接口信息
- **HTTP方法**: GET
- **URL路径**: 
  - `/system/menu/list`: 查询菜单列表
  - `/system/menu/{menuId}`: 查询菜单详细
  - `/system/menu/treeselect`: 查询菜单下拉树结构
  - `/system/menu/roleMenuTreeselect/{roleId}`: 根据角色ID查询菜单下拉树结构
- **认证要求**: JWT Token
- **权限要求**: `system:menu:list` 或 `system:menu:query`

### 请求参数
#### 查询菜单列表
- `menuName`: 菜单名称 (可选)
- `visible`: 显示状态 (可选)
- `status`: 菜单状态 (可选)

#### 查询菜单详细
- `menuId`: 菜单ID (路径参数)

### 响应数据结构
#### 菜单列表响应
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "menuId": 1,
      "menuName": "系统管理",
      "parentId": 0,
      "orderNum": 1,
      "path": "/system",
      "component": "Layout",
      "query": null,
      "isFrame": "1",
      "isCache": "0",
      "menuType": "M",
      "visible": "0",
      "status": "0",
      "perms": null,
      "icon": "system",
      "createBy": "admin",
      "createTime": "2023-01-01 00:00:00",
      "updateBy": "admin",
      "updateTime": "2023-01-01 00:00:00",
      "remark": null,
      "children": []
    }
  ]
}
```

#### 菜单详细响应
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "menuId": 1,
    "menuName": "系统管理",
    "parentId": 0,
    "orderNum": 1,
    "path": "/system",
    "component": "Layout",
    "query": null,
    "isFrame": "1",
    "isCache": "0",
    "menuType": "M",
    "visible": "0",
    "status": "0",
    "perms": null,
    "icon": "system",
    "createBy": "admin",
    "createTime": "2023-01-01 00:00:00",
    "updateBy": "admin",
    "updateTime": "2023-01-01 00:00:00",
    "remark": null
  }
}
```

### 请求示例
```javascript
// 查询菜单列表
listMenu({ menuName: "系统" })

// 查询菜单详细
getMenu(1)

// 查询菜单下拉树结构
treeselect()

// 根据角色ID查询菜单下拉树结构
roleMenuTreeselect(1)
```

**Section sources**
- [menu.js](file://bear-jia-vue3\src\api\system\menu.js#L11-L43)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L36-L78)
- [SysMenuServiceImpl.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\service\impl\SysMenuServiceImpl.java#L53-L80)

## 权限控制

### 权限注解实现
使用`@PreAuthorize`注解实现细粒度的权限控制：

```java
// 查询菜单列表
@PreAuthorize("@ss.hasPermi('system:menu:list')")
@GetMapping("/list")
public AjaxResult list(SysMenu menu) { ... }

// 查询菜单详细
@PreAuthorize("@ss.hasPermi('system:menu:query')")
@GetMapping(value = "/{menuId}")
public AjaxResult getInfo(@PathVariable Long menuId) { ... }

// 新增菜单
@PreAuthorize("@ss.hasPermi('system:menu:add')")
@PostMapping
public AjaxResult add(@Validated @RequestBody SysMenu menu) { ... }

// 修改菜单
@PreAuthorize("@ss.hasPermi('system:menu:edit')")
@PutMapping
public AjaxResult edit(@Validated @RequestBody SysMenu menu) { ... }

// 删除菜单
@PreAuthorize("@ss.hasPermi('system:menu:remove')")
@DeleteMapping("/{menuId}")
public AjaxResult remove(@PathVariable("menuId") Long menuId) { ... }
```

### 权限标识
- `system:menu:list`: 查询菜单列表权限
- `system:menu:query`: 查询菜单详细权限
- `system:menu:add`: 新增菜单权限
- `system:menu:edit`: 修改菜单权限
- `system:menu:remove`: 删除菜单权限

**Section sources**
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L39-L40)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L50-L51)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L83-L84)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L103-L104)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L127-L128)

## 操作日志

### 日志注解实现
使用`@Log`注解记录操作日志：

```java
// 新增菜单
@Log(title = "菜单管理", businessType = BusinessType.INSERT)
@PostMapping
public AjaxResult add(@Validated @RequestBody SysMenu menu) { ... }

// 修改菜单
@Log(title = "菜单管理", businessType = BusinessType.UPDATE)
@PutMapping
public AjaxResult edit(@Validated @RequestBody SysMenu menu) { ... }

// 删除菜单
@Log(title = "菜单管理", businessType = BusinessType.DELETE)
@DeleteMapping("/{menuId}")
public AjaxResult remove(@PathVariable("menuId") Long menuId) { ... }
```

### 日志记录内容
- **标题**: 菜单管理
- **业务类型**: INSERT(新增)、UPDATE(修改)、DELETE(删除)
- **操作人**: 通过JWT Token获取
- **请求参数**: 记录请求的参数
- **响应结果**: 记录响应结果

**Section sources**
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L84)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L104)
- [SysMenuController.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\controller\SysMenuController.java#L128)
- [Log.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\base\framework\aspectj\lang\annotation\Log.java)

## 前端调用最佳实践

### 表单验证与后端校验配合
1. **前端表单验证**:
   - 使用`@NotBlank`、`@NotNull`、`@Size`等注解进行基本验证
   - 在前端表单中设置必填项和长度限制

2. **后端校验**:
   - 唯一性校验: 检查菜单名称在同级菜单中的唯一性
   - 外链地址格式校验: 验证URL格式
   - 自引用校验: 防止菜单选择自己作为上级菜单
   - 子菜单存在性校验: 删除时检查是否存在子菜单
   - 角色分配校验: 删除时检查是否已分配给角色

### 错误处理
```javascript
// 统一错误处理
function handleMenuError(error) {
  const msg = error.response?.data?.msg || '操作失败';
  ElMessage.error(msg);
}

// 使用示例
addMenu(data).catch(handleMenuError);
updateMenu(data).catch(handleMenuError);
delMenu(menuId).catch(handleMenuError);
```

### 权限控制
```javascript
// 前端权限控制
<el-button v-hasPermi="['system:menu:add']" @click="handleAdd">
  新增
</el-button>
<el-button v-hasPermi="['system:menu:edit']" @click="handleEdit">
  修改
</el-button>
<el-button v-hasPermi="['system:menu:remove']" @click="handleDelete">
  删除
</el-button>
```

**Section sources**
- [SysMenu.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysMenu.java#L79-L80)
- [SysMenu.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysMenu.java#L111-L112)
- [SysMenu.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysMenu.java#L122-L123)
- [SysMenu.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysMenu.java#L133-L134)
- [SysMenu.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysMenu.java#L174-L175)
- [SysMenu.java](file://bearjia-admin-backend\src\main\java\com\javaxiaobear\module\system\domain\SysMenu.java#L205-L206)