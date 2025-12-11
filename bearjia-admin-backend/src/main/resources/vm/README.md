# 代码生成模板说明

本目录包含两套前端代码生成模板，分别适用于不同的前端架构：

## 📁 目录结构

```
vm/
├── antdv/                    # Ant Design Vue 标准模板
│   ├── index.vue.vm         # 主页面模板（Vue3 + Composition API）
│   ├── api.js.vm            # API接口模板
│   └── modules/
│       └── CreateFormVue3.vue.vm  # 新增/编辑弹窗模板（Vue3版本）
├── bearjia/                  # BearJia Vue3 架构模板
│   ├── index.vue.vm         # 主页面模板（基于ProTable组件）
│   ├── addUpdateModal.vue.vm # 新增/编辑弹窗模板
│   ├── detailModal.vue.vm   # 详情弹窗模板
│   └── api.js.vm            # API接口模板
└── README.md                # 本说明文件
```

## 🎯 模板对比

### antdv 模板特点

**适用场景：**
- 标准的 Ant Design Vue 项目
- 需要与现有 Vue3 项目集成
- 希望使用原生 Ant Design Vue 组件

**技术特色：**
- ✅ Vue3 + Composition API
- ✅ 标准的 Ant Design Vue 组件
- ✅ 传统的表格 + 搜索 + 分页布局
- ✅ 支持树形表格
- ✅ 支持富文本编辑器
- ✅ 完整的CRUD操作

**生成文件：**
- `index.vue` - 主页面（包含搜索、表格、操作按钮）
- `modules/CreateForm.vue` - 新增/编辑弹窗
- `api.js` - API接口文件

### bearjia 模板特点

**适用场景：**
- BearJia Vue3 管理系统
- 需要统一的表格组件
- 希望使用封装好的业务组件

**技术特色：**
- ✅ 基于 ProTable 统一表格组件
- ✅ 集成 TableActionBar 操作按钮组件
- ✅ 使用 SearchForm 搜索组件
- ✅ 支持字典标签组件
- ✅ 完整的弹窗组件体系
- ✅ 更简洁的代码结构

**生成文件：**
- `index.vue` - 主页面（基于ProTable组件）
- `addUpdateModal.vue` - 新增/编辑弹窗
- `detailModal.vue` - 详情查看弹窗
- `api.js` - API接口文件

## 🔧 使用方法

### 1. 选择模板类型

在代码生成器中选择对应的模板类型：

- **antdv** - 适用于标准 Ant Design Vue 项目
- **bearjia** - 适用于 BearJia Vue3 管理系统

### 2. 配置生成参数

根据数据表结构配置以下参数：

- **模块名称**：如 `system`
- **业务名称**：如 `user`
- **功能名称**：如 `用户管理`
- **作者信息**：开发者姓名
- **包路径**：Java包路径

### 3. 字段配置

为每个字段配置：

- **是否列表显示**：在表格中显示
- **是否查询字段**：作为搜索条件
- **是否插入字段**：新增时填写
- **是否编辑字段**：编辑时修改
- **显示类型**：输入框、下拉框、日期选择等
- **字典类型**：关联的数据字典

### 4. 特殊配置

#### 树形表格配置
如果需要生成树形表格，需要配置：
- **树编码字段**：如 `id`
- **树父编码字段**：如 `parentId`
- **树名称字段**：如 `name`

#### 富文本编辑器
如果字段需要富文本编辑，设置显示类型为 `editor`

#### 文件上传
如果字段需要文件上传，设置显示类型为：
- `imageUpload` - 图片上传
- `fileUpload` - 文件上传

## 📋 生成的代码特点

### antdv 模板生成的代码

```vue
<!-- 主页面结构 -->
<template>
  <page-header-wrapper>
    <a-card :bordered="false">
      <!-- 搜索区域 -->
      <div class="table-page-search-wrapper">
        <!-- 搜索表单 -->
      </div>
      
      <!-- 操作按钮 -->
      <div class="table-operations">
        <!-- 新增、编辑、删除、导出按钮 -->
      </div>
      
      <!-- 数据表格 -->
      <a-table>
        <!-- 表格列定义 -->
      </a-table>
      
      <!-- 分页组件 -->
      <a-pagination />
    </a-card>
  </page-header-wrapper>
</template>

<script setup>
// Vue3 Composition API
// 标准的增删改查逻辑
</script>
```

### bearjia 模板生成的代码

```vue
<!-- 主页面结构 -->
<template>
  <div class="app-container">
    <!-- 统一的ProTable组件 -->
    <ProTable
      :api="tableApi"
      :columns="columns"
      :searchFields="searchFields"
      :exportConfig="exportConfig"
    >
      <!-- 自定义操作按钮 -->
      <template #tableActionBar>
        <!-- TableActionBar组件 -->
      </template>
      
      <!-- 自定义列渲染 -->
      <template #bodyCell>
        <!-- 字典标签、时间格式化等 -->
      </template>
    </ProTable>
    
    <!-- 弹窗组件 -->
    <AddUpdateModal />
    <DetailModal />
  </div>
</template>

<script setup>
// 基于组合式函数的简洁逻辑
// 统一的表格配置
// 封装的业务组件
</script>
```

## 🎨 代码风格对比

| 特性 | antdv 模板 | bearjia 模板 |
|------|------------|--------------|
| **代码量** | 较多 | 较少 |
| **组件封装** | 原生组件 | 高度封装 |
| **配置复杂度** | 中等 | 简单 |
| **扩展性** | 灵活 | 统一 |
| **学习成本** | 低 | 中等 |
| **维护成本** | 中等 | 低 |

## 🚀 推荐使用

- **新项目**：推荐使用 `bearjia` 模板，代码更简洁，组件更统一
- **现有项目**：根据项目架构选择对应模板
- **快速开发**：`bearjia` 模板提供更高的开发效率
- **定制需求**：`antdv` 模板提供更大的灵活性

## 📝 注意事项

1. **依赖组件**：使用 `bearjia` 模板需要确保项目中已集成相关组件
2. **样式兼容**：不同模板生成的样式可能需要适配
3. **API接口**：两套模板的API接口是兼容的
4. **字典数据**：`bearjia` 模板更好地支持字典数据展示

## 🔄 模板更新

模板会根据框架升级和最佳实践持续更新，建议：

1. 定期检查模板更新
2. 根据项目需求调整模板
3. 反馈使用中的问题和建议
4. 参与模板优化和改进

---

**📞 技术支持**

如有问题或建议，请联系：
- 📧 邮箱：javaxiaobear@163.com
- 🌐 网站：https://javaxiaobear.cn
