# 身份验证API集成说明

## 概述

本项目支持真实的姓名和身份证号码验证，通过调用第三方实名认证服务实现。

## 方案选择

### 方案1：阿里云实人认证（推荐）⭐⭐⭐

**优势：**
- 准确率高（99.9%）
- 接入简单
- 稳定可靠
- 支持微信小程序

**价格：** 约0.3-1元/次

**接入步骤：**

1. 注册阿里云账号并开通实人认证服务
   - 访问：https://www.aliyun.com/product/cloudauth
   - 完成企业认证
   - 开通实人认证服务

2. 在后端集成阿里云SDK
   ```java
   // Java后端示例
   <dependency>
       <groupId>com.aliyun</groupId>
       <artifactId>cloudauth20190307</artifactId>
       <version>2.0.10</version>
   </dependency>
   ```

3. 创建后端API接口
   ```java
   @PostMapping("/api/identity/verify")
   public Result verifyIdentity(@RequestBody IdentityRequest request) {
       // 调用阿里云实人认证API
       VerifyMaterialResponse response = client.verifyMaterial(
           request.getName(), 
           request.getIdCard()
       );
       
       return Result.success(response);
   }
   ```

4. 修改前端API配置
   - 打开 `h5/src/api/identity.js`
   - 修改 `BASE_URL` 为你的后端地址

### 方案2：腾讯云人脸核身

**优势：**
- 与微信小程序深度集成
- 支持活体检测
- 支持人脸识别

**价格：** 约0.5元/次

**接入步骤：**

1. 注册腾讯云账号并开通人脸核身服务
   - 访问：https://cloud.tencent.com/product/faceid

2. 后端集成示例
   ```java
   <dependency>
       <groupId>com.tencentcloudapi</groupId>
       <artifactId>tencentcloud-sdk-java</artifactId>
       <version>3.1.800</version>
   </dependency>
   ```

### 方案3：聚合数据API

**优势：**
- 价格便宜
- 接口简单

**价格：** 约0.2-0.5元/次

**接入步骤：**

1. 注册聚合数据账号
   - 访问：https://www.juhe.cn/
   - 申请身份证二要素验证API

2. 后端调用示例
   ```java
   String url = "http://op.juhe.cn/idcard/query";
   HttpUtil.post(url, params);
   ```

## 使用说明

### 启用真实验证

默认情况下，代码中已集成真实API验证。如果后端API已就绪，直接使用即可。

### 仅使用格式验证

如果暂时不需要真实验证，可以注释掉API调用部分：

```javascript
// 在 basic.vue 的 validateNameIdCardBinding 方法中
// 注释掉第3步：调用真实的身份验证API
```

### API返回格式

**成功响应：**
```json
{
  "code": 200,
  "message": "验证成功",
  "data": {
    "match": true,
    "name": "张三",
    "idCard": "110101199001011234"
  }
}
```

**失败响应：**
```json
{
  "code": 400,
  "message": "姓名与身份证号不匹配",
  "data": {
    "match": false
  }
}
```

## 后端API接口规范

### 1. 身份证二要素验证

**接口地址：** `POST /api/identity/verify`

**请求参数：**
```json
{
  "name": "张三",
  "idCard": "110101199001011234"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "验证成功",
  "data": {
    "match": true
  }
}
```

### 2. 身份证三要素验证（可选）

**接口地址：** `POST /api/identity/verify-three`

**请求参数：**
```json
{
  "name": "张三",
  "idCard": "110101199001011234",
  "phone": "13800138000"
}
```

## 费用预估

假设日均验证100次：
- 阿里云：100 × 0.5元 = 50元/天
- 腾讯云：100 × 0.5元 = 50元/天
- 聚合数据：100 × 0.3元 = 30元/天

月费用约：900-1500元

## 注意事项

1. **数据安全**：身份证信息属于敏感数据，需要加密传输和存储
2. **合规性**：需遵守《个人信息保护法》等相关法律法规
3. **用户授权**：需获得用户明确授权才能收集和使用身份信息
4. **降级策略**：API调用失败时，自动降级为前端格式验证
5. **缓存机制**：可以缓存验证结果，避免重复验证

## 测试环境配置

开发测试时，可以使用mock数据：

```javascript
// 在 identity.js 中添加测试模式
const TEST_MODE = true; // 开发环境设为true

export function verifyIdentity(name, idCard) {
  if (TEST_MODE) {
    // 返回模拟数据
    return Promise.resolve({
      code: 200,
      data: { match: true }
    });
  }
  // 真实API调用
  ...
}
```

## 技术支持

如有问题，请联系：
- 阿里云：https://help.aliyun.com/
- 腾讯云：https://cloud.tencent.com/document/
- 聚合数据：https://www.juhe.cn/docs
