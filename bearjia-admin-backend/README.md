# 🐻 BearJia 管理系统

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)
![Vue](https://img.shields.io/badge/Vue-3.4.21-brightgreen.svg)
![Ant Design Vue](https://img.shields.io/badge/Ant%20Design%20Vue-4.1.2-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)
![Redis](https://img.shields.io/badge/Redis-6.0+-red.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

**基于 Spring Boot + Vue3 前后端分离的现代化企业级管理系统**

[在线演示](http://bearjia.javaxiaobear.cn) | [GitHub仓库](https://github.com/javaxiaobear/BearJia) | [Gitee仓库](https://gitee.com/javaxiaobear/BearJia) | [技术文档](https://javaxiaobear.cn) | [Docker部署](./Docker部署文档.md)

</div>

## 📖 项目简介

**BearJia 管理系统** 是一个基于 Spring Boot 2.7.18 + Vue 3.4.21 + Ant Design Vue 4.1.2 构建的现代化企业级管理系统。采用前后端分离架构，集成了用户管理、权限控制、系统监控、代码生成等核心功能，为企业提供完整的后台管理解决方案。

### 🎯 项目特色

- **🏗️ 现代化架构**：Spring Boot 2.7.18 + Vue 3 + Composition API + Vite 5
- **🔐 完善的权限体系**：基于 RBAC 的权限模型，支持菜单权限、按钮权限、数据权限
- **📱 响应式设计**：Ant Design Vue 4.x，完美适配各种设备
- **⚡ 高性能**：Redis 缓存、Druid 连接池、分页查询优化
- **🛠️ 开发友好**：代码生成器、Swagger 文档、热部署支持
- **📊 系统监控**：实时监控系统状态、在线用户、操作日志
- **🎨 主题系统**：支持多种布局模式和主题切换

## 🏗️ 系统架构

### 技术架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Vue 3)                        │
├─────────────────────────────────────────────────────────────┤
│  Vue 3.4.21  │  Ant Design Vue 4.1.2  │  Vite 5.1.4      │
│  Pinia 2.1.7 │  Vue Router 4.3.0       │  Axios 1.6.7     │
└─────────────────────────────────────────────────────────────┘
                                │
                         HTTP/HTTPS (RESTful API)
                                │
┌─────────────────────────────────────────────────────────────┐
│                      后端层 (Spring Boot)                    │
├─────────────────────────────────────────────────────────────┤
│  Spring Boot 2.7.18  │  Spring Security  │  JWT Token      │
│  MyBatis 3.5.x       │  PageHelper 2.0.1 │  Swagger 3.0.0  │
│  Quartz 2.3.x         │  Druid 1.2.23     │  FastJSON 2.0.47│
└─────────────────────────────────────────────────────────────┘
                                │
                         JDBC / Redis Protocol
                                │
┌─────────────────────────────────────────────────────────────┐
│                        数据层                                │
├─────────────────────────────────────────────────────────────┤
│           MySQL 8.0+                │      Redis 6.0+      │
│        (主数据存储)                   │     (缓存/会话)       │
└─────────────────────────────────────────────────────────────┘
```

### 项目结构

```
BearJia/
├── bearjia-admin/                 # 后端项目 (Spring Boot)
│   ├── src/main/java/com/javaxiaobear/
│   │   ├── JavaXiaoBearApplication.java    # 启动类
│   │   ├── base/                           # 基础框架
│   │   │   ├── common/                     # 通用工具类
│   │   │   │   ├── constant/               # 常量定义
│   │   │   │   ├── core/                   # 核心组件
│   │   │   │   ├── enums/                  # 枚举类
│   │   │   │   ├── exception/              # 异常处理
│   │   │   │   ├── utils/                  # 工具类
│   │   │   │   └── xss/                    # XSS防护
│   │   │   └── framework/                  # 框架配置
│   │   │       ├── aspectj/                # 切面编程
│   │   │       ├── config/                 # 配置类
│   │   │       ├── security/               # 安全配置
│   │   │       ├── redis/                  # Redis配置
│   │   │       └── web/                    # Web配置
│   │   └── module/                         # 业务模块
│   │       ├── common/                     # 通用控制器
│   │       ├── system/                     # 系统管理
│   │       ├── monitor/                    # 系统监控
│   │       └── tool/                       # 系统工具
│   ├── src/main/resources/
│   │   ├── application.yml                 # 主配置文件
│   │   ├── mybatis/                        # MyBatis配置
│   │   └── vm/                             # 代码生成模板
│   └── pom.xml                             # Maven配置
│
├── bearjia-ui/                    # 前端项目 (Vue 3)
│   ├── src/
│   │   ├── api/                            # API接口
│   │   │   ├── system/                     # 系统管理接口
│   │   │   ├── monitor/                    # 监控接口
│   │   │   └── tool/                       # 工具接口
│   │   ├── components/                     # 公共组件
│   │   │   ├── BearJiaProTable/            # 表格组件
│   │   │   ├── editor/                     # 富文本编辑器
│   │   │   ├── layout/                     # 布局组件
│   │   │   └── common/                     # 通用组件
│   │   ├── composables/                    # 组合式函数
│   │   │   ├── useTable.js                 # 表格逻辑
│   │   │   ├── useDict.js                  # 字典逻辑
│   │   │   └── usePermission.js            # 权限逻辑
│   │   ├── layout/                         # 布局文件
│   │   ├── router/                         # 路由配置
│   │   ├── stores/                         # 状态管理 (Pinia)
│   │   ├── utils/                          # 工具函数
│   │   ├── views/                          # 页面组件
│   │   │   ├── system/                     # 系统管理页面
│   │   │   ├── monitor/                    # 监控页面
│   │   │   ├── tool/                       # 工具页面
│   │   │   └── workbench/                  # 工作台
│   │   └── style/                          # 样式文件
│   ├── public/                             # 静态资源
│   ├── vite.config.js                      # Vite配置
│   └── package.json                        # 项目配置
│
└── logs/                          # 日志文件
    ├── sys-error.log                       # 错误日志
    ├── sys-info.log                        # 信息日志
    └── sys-user.log                        # 用户操作日志
```

## 🚀 核心功能

### 👥 系统管理
- **用户管理**：用户信息维护、角色分配、状态管理、批量操作
- **角色管理**：角色权限配置、数据权限设置、角色分配
- **菜单管理**：动态菜单配置、权限控制、图标管理
- **部门管理**：组织架构管理、树形结构展示、层级管理
- **岗位管理**：岗位信息维护、人员分配、岗位层级
- **字典管理**：数据字典维护、下拉选项配置、字典缓存
- **参数管理**：系统参数配置、动态参数管理、配置热更新

### 📊 系统监控
- **在线用户**：实时在线用户监控、强制下线、会话管理
- **服务监控**：服务器性能监控、JVM监控、系统信息展示
- **缓存监控**：Redis缓存监控、缓存管理、性能统计
- **操作日志**：用户操作记录、系统访问日志、日志分析
- **登录日志**：用户登录记录、异常登录监控、IP地址追踪

### 🛠️ 系统工具
- **代码生成**：一键生成前后端代码、支持自定义模板、预览功能
- **系统接口**：Swagger3 集成、API文档自动生成、在线测试
- **定时任务**：Quartz集成、任务调度管理、执行结果日志
- **通知公告**：系统通知发布、富文本编辑、消息推送

### 🎨 前端特色
- **多布局模式**：侧边栏、顶部菜单、混合布局、分栏布局
- **主题切换**：亮色/暗色主题、主题色自定义、布局配置
- **ProTable组件**：统一表格组件、集成搜索分页、支持导出
- **富文本编辑器**：WangEditor v5集成、图片视频上传
- **历史导航**：智能标签页管理、快速页面切换、右键菜单

## 🔧 环境要求

### 后端环境
- **JDK**: 1.8+
- **Maven**: 3.6+
- **MySQL**: 5.7+ / 8.0+
- **Redis**: 3.0+

### 前端环境
- **Node.js**: 16.0+
- **npm**: 8.0+ 或 yarn 1.22+
- **现代浏览器**: Chrome 88+、Firefox 78+、Safari 14+、Edge 88+

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/javaxiaobear/BearJia.git
cd BearJia
```

### 2. 后端启动
```bash
# 进入后端目录
cd bearjia-admin

# 创建数据库并导入SQL脚本
# 执行 sql/ 目录下的数据库脚本

# 修改配置文件
# 编辑 src/main/resources/application.yml
# 配置数据库连接信息和Redis连接信息

# 启动后端服务
mvn spring-boot:run
```

### 3. 前端启动
```bash
# 进入前端目录
cd bearjia-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 4. 访问系统
- **前端地址**: http://localhost:5173
- **后端接口**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui/
- **Druid监控**: http://localhost:8080/druid/
- **默认账号**: admin / admin123

## 🐳 Docker部署

项目支持Docker容器化部署,详细步骤请参考:[Docker部署文档](./Docker部署文档.md)

### 快速部署
```bash
# 使用docker-compose一键部署
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f bearjia-admin
```

## 📊 系统截图

详细的系统截图请查看前端项目README:[bear-jia-vue3/README.md](../bear-jia-vue3/README.md)

## 🔄 更新日志

### v1.3.0 (2025-11-24) - 企业级优化版本
- ✨ **新增功能**:文件管理模块,支持文件上传、预览、统计
- ✨ **性能优化**:实现组件懒加载、路由懒加载、图片懒加载
- ✨ **安全增强**:添加XSS防护、CSRF防护、敏感信息加密
- ✨ **监控体系**:集成性能监控、错误追踪、用户行为分析
- 🐳 **Docker支持**:提供完整的Docker部署方案
- 📚 **文档完善**:更新技术文档,添加部署指南
- 🐛 **问题修复**:修复多个已知问题,提升系统稳定性

### v1.2.0 (2025-07-15) - 功能增强
- ✨ 优化代码生成器,支持更多模板
- ✨ 完善系统监控功能
- 🐛 修复若干Bug

### v1.0.0 (2025-06-01) - 正式发布
- ✨ 完成基础框架搭建
- ✨ 实现核心功能模块
- ✨ 发布正式版本

## 🤝 参与贡献

我们欢迎所有形式的贡献!

### 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

### 开发规范

- 🔧 **代码规范**:遵循阿里巴巴Java开发手册
- 📝 **提交规范**:使用 Conventional Commits
- 🧪 **测试覆盖**:新功能需要添加单元测试
- 📚 **文档更新**:重要变更需要更新文档

## 🐛 问题反馈

如果您在使用过程中遇到问题,请通过以下方式反馈:

1. **GitHub Issues**: [提交问题](https://github.com/javaxiaobear/BearJia/issues)
2. **Gitee Issues**: [提交问题](https://gitee.com/javaxiaobear/BearJia/issues)
3. **邮箱**: javaxiaobear@qq.com

## ⚠️ 注意事项

1. **数据库版本**:建议使用MySQL 8.0+,5.7版本需要调整部分SQL语法
2. **Redis配置**:生产环境建议配置Redis密码
3. **文件上传**:注意配置文件上传路径和大小限制
4. **端口占用**:确保8080端口未被占用
5. **JVM参数**:生产环境建议配置合适的JVM参数


## 📄 开源协议

本项目基于 [MIT License](LICENSE) 开源协议。

## 🙏 致谢

感谢以下优秀的开源项目：
- [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) - 基础框架参考
- [Spring Boot](https://spring.io/projects/spring-boot) - 后端框架
- [Vue.js](https://vuejs.org/) - 前端框架
- [Ant Design Vue](https://antdv.com/) - UI组件库

## 📞 联系方式

- 🌐 **官网**: https://javaxiaobear.cn
- 📧 **邮箱**: javaxiaobear@qq.com
- 🐙 **GitHub**: [JavaXiaoBear](https://github.com/javaxiaobear)
- 🦄 **Gitee**: [JavaXiaoBear](https://gitee.com/javaxiaobear)
- 📱 **公众号**: 小熊学Java
- 💬 **技术交流群**: 欢迎加入讨论

## 📈 项目统计

- **Star数**: 持续增长中
- **Fork数**: 欢迎Fork和贡献
- **贡献者**: 感谢所有贡献者
- **更新频率**: 持续维护更新

---

<div align="center">

**🐻 Made with ❤️ by JavaXiaoBear**

如果这个项目对您有帮助，请给我们一个 ⭐ Star 支持一下！

</div>
