# Uniapp H5 项目

这是一个基于 uni-app 框架的跨平台应用项目，支持 H5 浏览器和微信小程序平台。

## 技术栈

- **框架**: uni-app (Vue 3)
- **构建工具**: Vite 5.2.8
- **运行时**: Vue 3.4.21
- **国际化**: vue-i18n 9.1.9

## 项目结构

```
h5/
├── src/                # 源代码目录
│   ├── pages/         # 页面文件
│   ├── static/        # 静态资源
│   ├── manifest.json  # 应用配置文件
│   └── pages.json     # 页面路由配置
├── index.html         # H5入口文件
├── vite.config.js     # Vite配置
└── package.json       # 项目依赖
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发调试

#### H5 浏览器端

```bash
npm run dev:h5
```

启动后访问: http://localhost:5173/

#### 微信小程序端

```bash
npm run dev:mp-weixin
```

编译后会在 `dist/dev/mp-weixin` 目录生成小程序代码，使用微信开发者工具打开该目录即可预览。

## 构建发布

### H5 浏览器端

```bash
npm run build:h5
```

构建产物在 `dist/build/h5` 目录。

### 微信小程序端

```bash
npm run build:mp-weixin
```

构建产物在 `dist/build/mp-weixin` 目录，上传至微信公众平台即可发布。

## 其他平台支持

本项目同时支持以下平台的开发和构建：

- 支付宝小程序: `dev:mp-alipay` / `build:mp-alipay`
- 百度小程序: `dev:mp-baidu` / `build:mp-baidu`
- 字节跳动小程序: `dev:mp-toutiao` / `build:mp-toutiao`
- QQ小程序: `dev:mp-qq` / `build:mp-qq`
- 快手小程序: `dev:mp-kuaishou` / `build:mp-kuaishou`

## 配置说明

### 微信小程序配置

在 `src/manifest.json` 中配置微信小程序 appid：

```json
"mp-weixin": {
  "appid": "你的微信小程序appid",
  "setting": {
    "urlCheck": false
  }
}
```

### 页面配置

在 `src/pages.json` 中配置页面路由和全局样式。

## 注意事项

1. 开发模式下性能和包体积不如发布模式，这是正常现象
2. H5 端开发时，首次点击未编译页面会先编译后加载，可能较慢
3. 微信小程序需要在微信开发者工具中预览和调试
4. 不同平台的 API 存在差异，使用条件编译处理平台特定代码

## 相关文档

- [uni-app 官方文档](https://uniapp.dcloud.net.cn/)
- [Vue 3 文档](https://cn.vuejs.org/)
- [Vite 文档](https://cn.vitejs.dev/)
