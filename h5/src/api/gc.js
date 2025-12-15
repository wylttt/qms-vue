/**
 * 胃癌筛查相关API
 * 基于uni-app封装，支持Promise
 */

// 后端API基础地址（根据环境自动切换）
const BASE_URL = process.env.NODE_ENV === 'development' 
  ? 'http://localhost:8080' 
  : 'https://your-production-api.com'; // TODO: 替换为生产环境地址

/**
 * 统一请求封装
 */
function request(options) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': uni.getStorageSync('token') || ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data);
          } else {
            uni.showToast({
              title: res.data.msg || '请求失败',
              icon: 'none'
            });
            reject(res.data);
          }
        } else {
          uni.showToast({
            title: '网络请求失败',
            icon: 'none'
          });
          reject(res);
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络异常，请检查网络',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
}

// ==================== 行政区划相关 ====================

/**
 * 获取省列表
 */
export function getProvinces() {
  return request({
    url: '/api/gc/region/provinces',
    method: 'GET'
  });
}

/**
 * 获取市列表
 */
export function getCities(provinceId) {
  return request({
    url: '/api/gc/region/cities',
    method: 'GET',
    data: { provinceId }
  });
}

/**
 * 获取区列表
 */
export function getDistricts(cityId) {
  return request({
    url: '/api/gc/region/districts',
    method: 'GET',
    data: { cityId }
  });
}

/**
 * 获取街道列表
 */
export function getStreets(districtId) {
  return request({
    url: '/api/gc/region/streets',
    method: 'GET',
    data: { districtId }
  });
}

/**
 * 获取社区列表
 */
export function getCommunities(streetId) {
  return request({
    url: '/api/gc/region/communities',
    method: 'GET',
    data: { streetId }
  });
}

// ==================== 居民相关 ====================

/**
 * 新增居民信息
 */
export function addResident(data) {
  return request({
    url: '/api/gc/resident',
    method: 'POST',
    data
  });
}

/**
 * 验证身份证号唯一性
 */
export function checkIdCard(idCardNo) {
  return request({
    url: '/api/gc/resident/checkIdCard',
    method: 'GET',
    data: { idCardNo }
  });
}

/**
 * 获取居民详情
 */
export function getResidentInfo(residentId) {
  return request({
    url: `/api/gc/resident/${residentId}`,
    method: 'GET'
  });
}

// ==================== 问卷相关 ====================

/**
 * 获取有效问卷模板
 */
export function getActiveTemplate() {
  return request({
    url: '/api/gc/questionnaire/template/active',
    method: 'GET'
  });
}

/**
 * 提交问卷
 */
export function submitQuestionnaire(data) {
  return request({
    url: '/api/gc/questionnaire/record',
    method: 'POST',
    data
  });
}

/**
 * 获取问卷记录
 */
export function getQuestionnaireRecord(residentId) {
  return request({
    url: '/api/gc/questionnaire/record/list',
    method: 'GET',
    data: { residentId }
  });
}

// ==================== 采血点相关 ====================

/**
 * 获取采血点列表（下拉选项）
 */
export function getSamplingSiteOptions(regionId) {
  return request({
    url: '/api/gc/sampling-site/options',
    method: 'GET',
    data: { regionId }
  });
}

/**
 * 获取采血点详情
 */
export function getSamplingSiteInfo(siteId) {
  return request({
    url: `/api/gc/sampling-site/${siteId}`,
    method: 'GET'
  });
}

// ==================== 采血预约相关 ====================

/**
 * 创建采血预约
 */
export function createAppointment(data) {
  return request({
    url: '/api/gc/blood/appointment',
    method: 'POST',
    data
  });
}

/**
 * 获取预约列表
 */
export function getAppointmentList(residentId) {
  return request({
    url: '/api/gc/blood/appointment/list',
    method: 'GET',
    data: { residentId }
  });
}

/**
 * 取消预约
 */
export function cancelAppointment(appointmentId) {
  return request({
    url: `/api/gc/blood/appointment/${appointmentId}`,
    method: 'DELETE'
  });
}

// ==================== 筛查结果相关 ====================

/**
 * 获取筛查结果
 */
export function getScreeningResult(residentId) {
  return request({
    url: '/api/gc/screening-result/list',
    method: 'GET',
    data: { residentId }
  });
}

// ==================== 调查员相关 ====================

/**
 * 调查员登录
 */
export function surveyorLogin(data) {
  return request({
    url: '/api/login',
    method: 'POST',
    data
  });
}

/**
 * 获取调查员任务进度
 */
export function getSurveyorProgress(surveyorId) {
  return request({
    url: '/api/gc/surveyor/performance',
    method: 'GET',
    data: { surveyorId }
  });
}

/**
 * 获取调查员下拉列表
 */
export function getSurveyorOptions(params) {
  return request({
    url: '/api/gc/surveyor/options',
    method: 'GET',
    data: params
  });
}

// ==================== 工具方法 ====================

/**
 * 保存token
 */
export function saveToken(token) {
  uni.setStorageSync('token', token);
}

/**
 * 清除token
 */
export function clearToken() {
  uni.removeStorageSync('token');
}

/**
 * 获取token
 */
export function getToken() {
  return uni.getStorageSync('token');
}

/**
 * 保存用户信息
 */
export function saveUserInfo(userInfo) {
  uni.setStorageSync('userInfo', JSON.stringify(userInfo));
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  const userInfo = uni.getStorageSync('userInfo');
  return userInfo ? JSON.parse(userInfo) : null;
}

/**
 * 清除用户信息
 */
export function clearUserInfo() {
  uni.removeStorageSync('userInfo');
}
