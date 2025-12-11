# 🐻 BearJia Admin - Vue3 前端框架

<div align="center">

![Vue](https://img.shields.io/badge/Vue-3.4.21-brightgreen.svg)
![Ant Design Vue](https://img.shields.io/badge/Ant%20Design%20Vue-4.1.2-blue.svg)
![Vite](https://img.shields.io/badge/Vite-5.1.4-646CFF.svg)
![Pinia](https://img.shields.io/badge/Pinia-2.1.7-yellow.svg)
![Version](https://img.shields.io/badge/Version-1.3.0-orange.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

**基于 Vue3 + Composition API + Vite + Ant Design Vue 的现代化管理后台前端框架**

[后端仓库](https://gitee.com/javaxiaobear_admin/bearjia-admin-backend) | [技术文档](https://javaxiaobear.cn) | [在线预览](http://bearjia.javaxiaobear.cn) | [更新日志](./CHANGELOG.md)

</div>

## 📸 项目预览

![image-20250930144247810](https://javaxiaobear-1301481032.cos.ap-guangzhou.myqcloud.com/picture-bed/image-20250930144247810.png)![image-20250930144320917](https://javaxiaobear-1301481032.cos.ap-guangzhou.myqcloud.com/picture-bed/image-20250930144320917.png)

![image-20251103095755285](https://javaxiaobear-1301481032.cos.ap-guangzhou.myqcloud.com/picture-bed/image-20251103095755285.png)

![image-20250930144347695](https://javaxiaobear-1301481032.cos.ap-guangzhou.myqcloud.com/picture-bed/image-20250930144347695.png)

![image-20250930144403331](https://javaxiaobear-1301481032.cos.ap-guangzhou.myqcloud.com/picture-bed/image-20250930144403331.png)

![](https://javaxiaobear-1301481032.cos.ap-guangzhou.myqcloud.com/picture-bed/image-20250930144421524.png)

![](https://javaxiaobear-1301481032.cos.ap-guangzhou.myqcloud.com/picture-bed/image-20250930144437567.png)

![image-20250930144506964](https://javaxiaobear-1301481032.cos.ap-guangzhou.myqcloud.com/picture-bed/image-20250930144506964.png)



## 📖 项目简介

BearJia Admin 前端框架是一个基于 Vue3 + Composition API + Vite + Ant Design Vue 实现的现代化管理后台系统。项目采用最新的前端技术栈和工程化实践，通过模块化架构设计和组件化开发模式，为开发者提供了一个功能完整、易于扩展、开箱即用的企业级管理系统解决方案。

框架内置了完善的权限管理体系、系统监控功能、代码生成工具等核心功能模块，同时支持多主题切换、响应式布局等特性。无论是快速原型开发还是大型企业应用构建，都能满足不同场景的开发需求。

### ✨ 技术特色

#### 🚀 前沿技术栈
- **Vue3.4 生态**：基于最新的 Vue3.4 + Composition API，享受最新特性
- **Vite5 构建**：极速的开发体验，HMR 热更新，生产环境高效打包
- **Pinia 状态管理**：简洁的状态管理方案，完美的 TypeScript 支持
- **Ant Design Vue 4.x**：企业级 UI 组件库，丰富的组件生态

#### 🎯 开发体验
- **语法糖优化**：全面使用 `<script setup>` 语法，代码量减少 30%+
- **组合式函数**：逻辑复用更简洁，useTable、useDict 等实用 hooks
- **智能导入**：自动导入 Vue API 和组件，减少重复代码
- **热重载支持**：开发时修改即时生效，提升开发效率

#### 🎨 界面设计
- **多主题系统**：亮色/暗色主题无缝切换，支持自定义主题色
- **多布局模式**：侧边栏、顶部、混合、分栏、抽屉等 5 种布局
- **响应式设计**：完美适配桌面端、平板、手机等各种设备
- **动画效果**：流畅的页面切换和交互动画，提升用户体验

#### 🛠️ 工程化实践
- **代码规范**：ESLint + Prettier 统一代码风格，EditorConfig 编辑器配置
- **构建优化**：分环境构建配置，资源压缩，按需加载
- **模块分离**：配置文件模块化，插件系统化，便于维护扩展
- **脚本工具**：自动化脚本处理重复任务，提升开发效率

#### 🔐 安全与性能
- **权限控制**：基于 RBAC 的精细化权限管理，支持按钮级权限
- **安全防护**：XSS 防护，CSRF 防护，敏感信息加密
- **性能优化**：组件懒加载，图片压缩，CDN 加速
- **监控体系**：错误边界，性能监控，用户行为追踪

## 🎯 核心功能


| 🔐 权限管理 | 📊 系统监控 | 🛠️ 系统工具 | 🎨 界面特色 |
|:---:|:---:|:---:|:---:|
| 用户/角色管理<br/>菜单权限控制<br/>部门岗位管理 | 在线用户监控<br/>服务性能监控<br/>操作日志管理 | 代码生成器<br/>系统配置<br/>字典管理 | 多主题切换<br/>多布局模式<br/>响应式设计 |

### ✨ 特色亮点
- 🚀 **开箱即用**：完整的管理系统功能，无需从零开始
- 🔧 **高度可定制**：支持主题、布局、功能模块的灵活配置
- 📱 **全端适配**：一套代码适配桌面、平板、移动端
- ⚡ **性能优越**：基于 Vite5 构建，开发和生产环境都极速响应

## 🚀 快速开始

### 环境要求
- Node.js >= 16.0.0
- 现代浏览器（Chrome 88+、Firefox 78+、Safari 14+、Edge 88+）

### 一键启动
```bash
# 克隆并启动项目
git clone https://github.com/javaxiaobear/BearJia.git
cd BearJia/bear-jia-vue3
npm install && npm run dev

# 访问 http://localhost:5173
```

### 常用命令
```bash
npm run dev          # 开发环境
npm run build        # 生产构建
npm run preview      # 预览构建
npm run lint:fix     # 代码检查并修复
npm run format       # 代码格式化
```

### 📋 更多命令


```bash
# 构建相关
npm run build:prod   # 生产环境构建
npm run build:test   # 测试环境构建

# 代码质量
npm run lint         # ESLint 检查
npm run format:check # 格式化检查

# 工具脚本
npm run cleanup      # 清理项目
npm run fix-exports  # 修复重复导出
npm run replace-logs # 替换console.log
```
## 🔧 配置说明

### 环境变量

项目支持多环境配置，通过 `.env` 文件管理：

- `.env.development` - 开发环境配置
- `.env.production` - 生产环境配置
- `.env.staging` - 预发布环境配置

```bash
# 开发环境示例 (.env.development)
VITE_APP_TITLE = BearJia Admin
VITE_APP_BASE_API = http://localhost:8080
VITE_APP_UPLOAD_URL = http://localhost:8080/common/upload
```

### 后端对接

本项目可以对接多种后端框架：

1. **推荐：BearJia SpringBoot**
   - 仓库地址：[BearJia-SpringBoot](https://gitee.com/javaxiaobear_admin/bearjia-admin-backend)
   - 完美适配，功能齐全

2. **兼容：RuoYi-Vue**
   - 仓库地址：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)
   - 需要少量配置调整（详见文档：[若依如何使用](./docs/ruoyi-usage.md)）

## 📁 项目结构

<details>
<summary>🗂️ 点击展开详细结构</summary>

```
bear-jia-vue3/
├── 📁 src/                    # 源代码目录
│   ├── 🔌 api/                # API接口层
│   ├── 🎨 assets/             # 静态资源文件
│   ├── 🧩 components/         # 公共组件库
│   ├── 🎯 composables/        # Vue3组合式函数
│   ├── ⚙️ config/             # 配置文件
│   ├── 📋 directive/          # 自定义指令
│   ├── 🏗️ layout/            # 布局组件
│   ├── 🔧 plugins/           # 插件配置
│   ├── 🚏 router/            # 路由配置
│   ├── 📦 stores/            # Pinia状态管理
│   ├── 🔤 types/             # TypeScript类型
│   ├── 🛠️ utils/             # 工具函数
│   └── 📄 views/             # 页面组件
├── 📁 config/                 # 构建配置
├── 📁 scripts/                # 构建脚本
├── 📄 vite.config.js         # Vite配置
└── 📄 package.json           # 项目配置
```

</details>

**核心目录说明：**
- `src/api/` - 接口层，统一管理所有API调用
- `src/components/` - 公共组件，可复用的业务组件
- `src/composables/` - 组合式函数，逻辑复用的核心
- `src/layout/` - 布局组件，多种布局模式支持
- `src/views/` - 页面组件，具体的业务页面

## 🔧 核心组件


| 🏷️ 组件 | 📋 描述 | ⭐ 特色功能 |
|:---:|:---|:---:|
| **ProTable** | 统一表格组件 | 搜索/分页/导出/树形表格 |
| **WangEditor** | 富文本编辑器 | 图片上传/视频支持/工具栏定制 |
| **HistoryNav** | 历史导航 | 智能标签页/右键菜单/路由同步 |
| **SearchForm** | 搜索表单 | 动态表单/条件筛选/重置功能 |
| **PageContainer** | 页面容器 | 面包屑/标题/操作区布局 |

### 💡 组件特点
- 🎯 **高度封装**：开箱即用，减少重复开发
- 🔧 **灵活配置**：支持丰富的配置选项和插槽扩展
- 📱 **响应式**：完美适配各种设备屏幕
- 🎨 **主题一致**：统一的视觉风格和交互体验

## 🏗️ 技术栈

<details>
<summary>📋 完整技术栈列表</summary>

| 类别 | 技术 | 版本 | 描述 |
|:---:|:---|:---:|:---|
| **核心** | Vue | 3.4.21 | 渐进式JavaScript框架 |
| **构建** | Vite | 5.1.4 | 下一代前端构建工具 |
| **UI库** | Ant Design Vue | 4.1.2 | 企业级UI组件库 |
| **路由** | Vue Router | 4.3.0 | 官方路由管理器 |
| **状态** | Pinia | 2.1.7 | 现代状态管理 |
| **HTTP** | Axios | 1.6.7 | HTTP客户端 |
| **编辑器** | WangEditor | 5.1.23 | 富文本编辑器 |
| **图表** | ECharts | 5.6.0 | 数据可视化 |
| **工具集** | @vueuse/core | 10.9.0 | Vue组合式API工具 |
| **日期** | Day.js | 1.11.10 | 轻量日期处理 |

</details>

## 📈 更新日志

### v2.0.0 (2024-11-13) - 重磅更新 🎉

本次更新共包含 **15 项重要改进**,新增代码 **6,000+ 行**,性能提升显著!

#### 🎨 界面优化
- **登录页面优化**: 重新设计品牌展示区域,提升视觉效果和品牌形象
- **消息中心升级**: 全新的消息列表展示样式,优化消息提醒交互体验
- **图标系统增强**: 新增 677 行代码,支持自定义 SVG 图标和 Ant Design 图标分类展示

#### ✨ 核心功能
- **文件管理系统**: 新增 3,595 行代码,支持文件夹树形结构、文件上传下载、预览、统计分析
- **表单设计器**: 新增 305 行代码,可视化拖拽式表单设计,支持 20+ 种表单组件
- **角色数据权限**: 新增 245 行代码,支持自定义数据权限规则,可视化权限配置界面
- **虚拟滚动**: 新增 682 行代码,支持万级数据流畅渲染,性能提升 90%

#### ⚡ 性能优化
- **渲染性能**: 10,000 条数据渲染时间从 3s 降至 0.3s,提升 **90%**
- **内存优化**: 内存占用从 150MB 降至 45MB,降低 **70%**
- **滚动流畅度**: 滚动帧率从 30fps 提升至 60fps,提升 **100%**
- **首屏加载**: 加载时间从 2.5s 降至 0.5s,提升 **80%**

#### 🔧 系统优化
- **开发环境**: 优化 Vite 构建配置,开发服务器启动速度提升约 40%
- **主题服务**: 集成主题管理服务,支持动态主题切换和自定义主题色
- **配置重构**: 重新组织配置文件结构,统一配置管理方式
- **自动导入**: 优化组件自动导入配置,减少手动导入代码量 80%

#### 🐛 问题修复
- 修复数据监控页面无法打开的问题
- 修复刷新页面后左侧菜单树未联动激活的问题
- 静默应用主题设置,避免初始化提示
- 优化菜单显示逻辑,支持 alwaysShow 配置

#### 📊 更新数据
- 📝 提交次数: 50+ 次
- 📦 新增代码: 6,000+ 行
- 🔧 优化模块: 15 个
- 🐛 修复问题: 10+ 个

### v1.3.0 (2024-09-30) - 架构优化与功能增强
- ✨ 重构项目结构,优化代码组织
- ✨ 增强主题系统,支持动态主题切换
- ✨ 优化布局组件,提升用户体验
- ✨ 完善配置系统,支持灵活配置
- 🐛 修复多个已知问题,提升系统稳定性
- 🎨 优化界面样式,提升视觉效果

### v1.2.0 (2024-07-15) - 重大功能更新
- ✨ 新增ProTable组件,统一表格布局
- ✨ 完善工作台页面,增加统计功能
- ✨ 优化HistoryNav组件,支持动态显示
- ✨ 集成WangEditor v5富文本编辑器
- 🐛 修复部门管理树形结构显示问题
- 🐛 修复字典管理可展开行功能

### v1.1.5 (2024-06-20) - 功能优化
- ✨ 增强TableActionBar组件扩展性
- ✨ 改进SearchForm组件用户体验
- 🐛 修复导航模式选择样式问题
- 🎨 优化表格操作按钮样式

### v1.1.0 (2024-06-05) - 界面美化
- 🎨 重新设计登录页面
- 🎨 优化主题色彩搭配
- ✨ 增加暗色主题支持
- 📱 提升移动端适配效果

### v1.0.0 (2024-06-01) - 正式发布
- ✨ 完成基础框架搭建
- ✨ 实现用户权限管理
- ✨ 集成代码生成功能
- ✨ 建立完整的监控体系

## 🤝 参与贡献

我们欢迎所有形式的贡献！

### 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

### 开发规范

- 🔧 **代码规范**：遵循 ESLint 配置
- 📝 **提交规范**：使用 Conventional Commits
- 🧪 **测试覆盖**：新功能需要添加测试
- 📚 **文档更新**：重要变更需要更新文档

## 🐛 问题反馈

如果您在使用过程中遇到问题,请通过以下方式反馈:

1. **GitHub Issues**: [提交问题](https://github.com/javaxiaobear/BearJia/issues)
2. **Gitee Issues**: [提交问题](https://gitee.com/javaxiaobear/BearJia/issues)
3. **邮箱**: javaxiaobear@qq.com

## 📊 项目统计

- **Star数**: 持续增长中
- **Fork数**: 欢迎Fork和贡献
- **贡献者**: 感谢所有贡献者
- **更新频率**: 持续维护更新

## 📄 开源协议

本项目基于 [MIT License](../LICENSE) 开源协议。

## 🙏 致谢

感谢以下优秀的开源项目：

- [Vue.js](https://vuejs.org/) - 渐进式JavaScript框架
- [Ant Design Vue](https://antdv.com/) - 企业级UI组件库
- [Vite](https://vitejs.dev/) - 下一代前端构建工具
- [WangEditor](https://www.wangeditor.com/) - 轻量级富文本编辑器

## 📞 联系方式

- 🌐 **个人网站**：[https://javaxiaobear.cn](https://javaxiaobear.cn)
- 📧 **邮箱**：javaxiaobear@qq.com
- 🐙 **GitHub**：[JavaXiaoBear](https://github.com/javaxiaobear)
- 🦄 **Gitee**：[JavaXiaoBear](https://gitee.com/javaxiaobear)
- 📱 **公众号**：小熊学Java
- 💬 **技术交流群**：欢迎加入讨论

---

<div align="center">

**🐻 Made with ❤️ by JavaXiaoBear**

如果这个项目对您有帮助，请给我们一个 ⭐ Star 支持一下！

[⬆ 回到顶部](#-bearjia-admin---vue3-前端框架)

</div>
