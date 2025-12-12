/**
 * 身份验证相关API
 * 
 * 使用说明：
 * 1. 默认情况下身份验证功能未启用
 * 2. 需要启用时，请在 basic.vue 中将 enableRealVerify 设置为 true
 * 3. 同时修改下面的 BASE_URL 为你的真实后端API地址
 * 
 * 推荐方案：
 * - 阿里云实人认证：https://www.aliyun.com/product/cloudauth
 * - 腾讯云人脸核身：https://cloud.tencent.com/product/faceid
 * - 聚合数据API：https://www.juhe.cn/
 */

const BASE_URL = 'https://your-backend-api.com'; // TODO: 替换为你的后端API地址

/**
 * 身份证二要素验证（姓名+身份证号）
 * @param {string} name - 姓名
 * @param {string} idCard - 身份证号码
 * @returns {Promise} 验证结果
 */
export function verifyIdentity(name, idCard) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}/api/identity/verify`,
      method: 'POST',
      data: {
        name: name,
        idCard: idCard
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else {
          reject(new Error(res.data.message || '验证失败'));
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 身份证三要素验证（姓名+身份证号+手机号）
 * @param {string} name - 姓名
 * @param {string} idCard - 身份证号码
 * @param {string} phone - 手机号码
 * @returns {Promise} 验证结果
 */
export function verifyIdentityThree(name, idCard, phone) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}/api/identity/verify-three`,
      method: 'POST',
      data: {
        name: name,
        idCard: idCard,
        phone: phone
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else {
          reject(new Error(res.data.message || '验证失败'));
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 银行卡四要素验证（姓名+身份证号+手机号+银行卡号）
 * @param {object} data - 验证数据
 * @returns {Promise} 验证结果
 */
export function verifyBankCard(data) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}/api/identity/verify-bank`,
      method: 'POST',
      data: data,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else {
          reject(new Error(res.data.message || '验证失败'));
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}
