/**
 * OCR身份证识别工具
 * 提供三种实现方案：
 * 1. 微信小程序原生OCR（推荐）
 * 2. 后端OCR服务（通用）
 * 3. 手动输入（兜底方案）
 */

import { request } from '@/api/gc.js';

/**
 * OCR识别配置
 */
const OCR_CONFIG = {
  // 使用哪种OCR方案：'wechat' | 'backend' | 'manual'
  mode: 'wechat', // 默认使用微信OCR
  
  // 后端OCR接口地址
  backendOCRUrl: '/api/gc/ocr/idcard',
  
  // 是否允许降级到手动输入
  allowManual: true
};

/**
 * 方案1：微信小程序原生OCR识别
 * 优点：免费、准确率高、速度快
 * 缺点：仅限微信小程序环境
 */
export async function ocrIdCardByWechat() {
  return new Promise((resolve, reject) => {
    // #ifdef MP-WEIXIN
    // 1. 选择图片（拍照或相册）
    uni.chooseImage({
      count: 1,
      sourceType: ['camera', 'album'],
      success: (chooseRes) => {
        const tempFilePath = chooseRes.tempFilePaths[0];
        
        // 2. 调用微信OCR识别
        uni.ocrIdCard({
          filePath: tempFilePath,
          success: (ocrRes) => {
            // 3. 解析识别结果
            const result = parseWechatOCRResult(ocrRes);
            resolve({
              success: true,
              data: result,
              source: 'wechat-ocr'
            });
          },
          fail: (err) => {
            console.error('微信OCR识别失败:', err);
            reject({
              success: false,
              message: '身份证识别失败，请重试或手动输入',
              error: err
            });
          }
        });
      },
      fail: (err) => {
        console.error('选择图片失败:', err);
        reject({
          success: false,
          message: '选择图片失败',
          error: err
        });
      }
    });
    // #endif
    
    // #ifndef MP-WEIXIN
    // 非微信环境，降级到后端OCR
    reject({
      success: false,
      message: '当前环境不支持微信OCR，请使用后端OCR或手动输入'
    });
    // #endif
  });
}

/**
 * 方案2：后端OCR服务识别
 * 优点：多端通用、可自定义、可切换OCR提供商
 * 缺点：需要后端支持、可能需要付费
 */
export async function ocrIdCardByBackend(imagePath) {
  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count: 1,
      sourceType: ['camera', 'album'],
      success: (chooseRes) => {
        const tempFilePath = chooseRes.tempFilePaths[0];
        
        // 上传图片到后端进行OCR识别
        uni.uploadFile({
          url: request.getFullUrl(OCR_CONFIG.backendOCRUrl),
          filePath: tempFilePath,
          name: 'file',
          header: {
            'Authorization': uni.getStorageSync('token') || ''
          },
          success: (uploadRes) => {
            try {
              const data = JSON.parse(uploadRes.data);
              if (data.code === 200) {
                // 解析后端返回的OCR结果
                const result = parseBackendOCRResult(data.data);
                resolve({
                  success: true,
                  data: result,
                  source: 'backend-ocr'
                });
              } else {
                reject({
                  success: false,
                  message: data.msg || '识别失败'
                });
              }
            } catch (e) {
              reject({
                success: false,
                message: '识别结果解析失败',
                error: e
              });
            }
          },
          fail: (err) => {
            console.error('上传图片失败:', err);
            reject({
              success: false,
              message: '上传图片失败',
              error: err
            });
          }
        });
      },
      fail: (err) => {
        console.error('选择图片失败:', err);
        reject({
          success: false,
          message: '选择图片失败',
          error: err
        });
      }
    });
  });
}

/**
 * 方案3：手动输入（兜底方案）
 * 当OCR识别失败或不可用时，引导用户手动输入
 */
export function fallbackToManualInput() {
  return {
    success: false,
    fallbackToManual: true,
    message: 'OCR识别暂不可用，请手动输入身份证信息'
  };
}

/**
 * 智能OCR识别（自动选择最优方案）
 * 优先级：微信OCR > 后端OCR > 手动输入
 */
export async function smartOCRIdCard() {
  try {
    // 优先尝试微信OCR
    if (OCR_CONFIG.mode === 'wechat') {
      try {
        return await ocrIdCardByWechat();
      } catch (wechatErr) {
        console.warn('微信OCR失败，尝试降级到后端OCR:', wechatErr);
        
        // 降级到后端OCR
        if (OCR_CONFIG.mode === 'backend' || OCR_CONFIG.allowManual) {
          try {
            return await ocrIdCardByBackend();
          } catch (backendErr) {
            console.warn('后端OCR也失败:', backendErr);
            
            // 最终降级到手动输入
            if (OCR_CONFIG.allowManual) {
              return fallbackToManualInput();
            }
            throw backendErr;
          }
        }
        throw wechatErr;
      }
    }
    
    // 直接使用后端OCR
    if (OCR_CONFIG.mode === 'backend') {
      return await ocrIdCardByBackend();
    }
    
    // 手动输入模式
    return fallbackToManualInput();
    
  } catch (error) {
    console.error('OCR识别失败:', error);
    
    // 允许降级到手动输入
    if (OCR_CONFIG.allowManual) {
      return fallbackToManualInput();
    }
    
    throw error;
  }
}

/**
 * 解析微信OCR识别结果
 * 微信返回格式：
 * {
 *   name: "张三",
 *   id: "360123199001011234",
 *   gender: "男",
 *   nation: "汉",
 *   birth: "1990/01/01",
 *   address: "江西省九江市濂溪区XX街道XX号"
 * }
 */
function parseWechatOCRResult(ocrRes) {
  const idCardInfo = ocrRes.idCardInfo || {};
  
  // 解析性别
  const gender = idCardInfo.gender === '男' ? '0' : '1';
  
  // 解析出生日期（格式转换：1990/01/01 -> 1990-01-01）
  const birthDate = idCardInfo.birth ? idCardInfo.birth.replace(/\//g, '-') : '';
  
  // 尝试从地址中提取行政区划信息
  const regionInfo = parseRegionFromAddress(idCardInfo.address || '');
  
  return {
    realName: idCardInfo.name || '',
    idCardNo: idCardInfo.id || '',
    gender: gender,
    birthDate: birthDate,
    detailAddress: idCardInfo.address || '',
    ...regionInfo
  };
}

/**
 * 解析后端OCR识别结果
 * 后端统一返回格式：
 * {
 *   name: "张三",
 *   idCard: "360123199001011234",
 *   gender: "男",
 *   birthDate: "1990-01-01",
 *   address: "江西省九江市濂溪区XX街道XX号",
 *   province: "江西省",
 *   city: "九江市",
 *   district: "濂溪区"
 * }
 */
function parseBackendOCRResult(data) {
  // 解析性别
  const gender = data.gender === '男' ? '0' : '1';
  
  return {
    realName: data.name || '',
    idCardNo: data.idCard || '',
    gender: gender,
    birthDate: data.birthDate || '',
    detailAddress: data.address || '',
    province: data.province || '',
    city: data.city || '',
    district: data.district || ''
  };
}

/**
 * 从地址中提取行政区划信息（简单实现）
 * 示例："江西省九江市濂溪区XX街道XX号"
 * 返回：{ province: "江西省", city: "九江市", district: "濂溪区" }
 */
function parseRegionFromAddress(address) {
  const result = {
    province: '',
    city: '',
    district: ''
  };
  
  if (!address) return result;
  
  // 匹配省份
  const provinceMatch = address.match(/(.*?[省|自治区|特别行政区])/);
  if (provinceMatch) {
    result.province = provinceMatch[1];
  }
  
  // 匹配城市
  const cityMatch = address.match(/[省|自治区](.*?[市|地区|自治州])/);
  if (cityMatch) {
    result.city = cityMatch[1];
  }
  
  // 匹配区县
  const districtMatch = address.match(/[市|地区|自治州](.*?[区|县|市])/);
  if (districtMatch) {
    result.district = districtMatch[1];
  }
  
  return result;
}

/**
 * 身份证号验证
 */
export function validateIdCard(idCardNo) {
  if (!idCardNo || idCardNo.length !== 18) {
    return {
      valid: false,
      message: '身份证号必须为18位'
    };
  }
  
  // 验证前17位是否为数字
  const first17 = idCardNo.substring(0, 17);
  if (!/^\d{17}$/.test(first17)) {
    return {
      valid: false,
      message: '身份证号前17位必须为数字'
    };
  }
  
  // 验证校验码
  const checkCode = idCardNo.charAt(17).toUpperCase();
  const calculatedCheckCode = calculateIdCardCheckCode(first17);
  
  if (checkCode !== calculatedCheckCode) {
    return {
      valid: false,
      message: '身份证号校验码错误'
    };
  }
  
  return {
    valid: true,
    message: '身份证号格式正确'
  };
}

/**
 * 计算身份证校验码
 */
function calculateIdCardCheckCode(first17) {
  const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
  const checkCodes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
  
  let sum = 0;
  for (let i = 0; i < 17; i++) {
    sum += parseInt(first17.charAt(i)) * weights[i];
  }
  
  const mod = sum % 11;
  return checkCodes[mod];
}

/**
 * 从身份证号解析信息
 */
export function parseInfoFromIdCard(idCardNo) {
  if (!idCardNo || idCardNo.length !== 18) {
    return {
      gender: null,
      birthDate: null,
      age: null
    };
  }
  
  // 解析出生日期
  const year = idCardNo.substring(6, 10);
  const month = idCardNo.substring(10, 12);
  const day = idCardNo.substring(12, 14);
  const birthDate = `${year}-${month}-${day}`;
  
  // 解析性别（第17位，奇数为男，偶数为女）
  const genderCode = parseInt(idCardNo.charAt(16));
  const gender = (genderCode % 2 === 0) ? '1' : '0'; // 0:男 1:女
  
  // 计算年龄
  const birthYear = parseInt(year);
  const currentYear = new Date().getFullYear();
  const age = currentYear - birthYear;
  
  return {
    gender: gender,
    birthDate: birthDate,
    age: age
  };
}

/**
 * 设置OCR配置
 */
export function setOCRConfig(config) {
  Object.assign(OCR_CONFIG, config);
}

/**
 * 获取OCR配置
 */
export function getOCRConfig() {
  return { ...OCR_CONFIG };
}

export default {
  smartOCRIdCard,
  ocrIdCardByWechat,
  ocrIdCardByBackend,
  fallbackToManualInput,
  validateIdCard,
  parseInfoFromIdCard,
  setOCRConfig,
  getOCRConfig
};
