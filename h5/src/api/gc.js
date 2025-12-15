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
 * 获取筛查结果列表
 */
export function getScreeningResults(params) {
  return request({
    url: '/api/gc/screening-result/list',
    method: 'GET',
    data: params
  });
}

/**
 * 获取筛查结果详情
 */
export function getScreeningResultDetail(resultId) {
  return request({
    url: `/api/gc/screening-result/${resultId}`,
    method: 'GET'
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
 * 获取调查员任务列表
 */
export function getSurveyorTasks(params) {
  return request({
    url: '/api/gc/task/surveyor/list',
    method: 'GET',
    data: params
  });
}

/**
 * 获取调查员统计数据
 */
export function getSurveyorStats(surveyorId) {
  return request({
    url: `/api/gc/surveyor/${surveyorId}/stats`,
    method: 'GET'
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

// ==================== 离线填写功能 ====================

/**
 * 保存草稿到本地
 * @param {String} key - 草稿唯一键（如：questionnaire_draft_居民ID_模板ID）
 * @param {Object} data - 草稿数据
 */
export function saveDraft(key, data) {
  try {
    const draft = {
      key: key,
      data: data,
      timestamp: Date.now(),
      synced: false // 是否已同步到服务器
    };
    uni.setStorageSync(key, JSON.stringify(draft));
    
    // 维护草稿列表
    const draftList = getDraftList();
    if (!draftList.includes(key)) {
      draftList.push(key);
      uni.setStorageSync('draft_list', JSON.stringify(draftList));
    }
    
    return true;
  } catch (error) {
    console.error('保存草稿失败:', error);
    return false;
  }
}

/**
 * 获取草稿
 * @param {String} key - 草稿唯一键
 */
export function getDraft(key) {
  try {
    const draftStr = uni.getStorageSync(key);
    if (draftStr) {
      return JSON.parse(draftStr);
    }
    return null;
  } catch (error) {
    console.error('获取草稿失败:', error);
    return null;
  }
}

/**
 * 删除草稿
 * @param {String} key - 草稿唯一键
 */
export function deleteDraft(key) {
  try {
    uni.removeStorageSync(key);
    
    // 从草稿列表中移除
    const draftList = getDraftList();
    const index = draftList.indexOf(key);
    if (index > -1) {
      draftList.splice(index, 1);
      uni.setStorageSync('draft_list', JSON.stringify(draftList));
    }
    
    return true;
  } catch (error) {
    console.error('删除草稿失败:', error);
    return false;
  }
}

/**
 * 获取所有草稿列表
 */
export function getDraftList() {
  try {
    const listStr = uni.getStorageSync('draft_list');
    return listStr ? JSON.parse(listStr) : [];
  } catch (error) {
    console.error('获取草稿列表失败:', error);
    return [];
  }
}

/**
 * 获取所有未同步的草稿
 */
export function getUnsyncedDrafts() {
  const draftList = getDraftList();
  const unsyncedDrafts = [];
  
  draftList.forEach(key => {
    const draft = getDraft(key);
    if (draft && !draft.synced) {
      unsyncedDrafts.push(draft);
    }
  });
  
  return unsyncedDrafts;
}

/**
 * 标记草稿为已同步
 * @param {String} key - 草稿唯一键
 */
export function markDraftAsSynced(key) {
  const draft = getDraft(key);
  if (draft) {
    draft.synced = true;
    draft.syncedAt = Date.now();
    uni.setStorageSync(key, JSON.stringify(draft));
  }
}

/**
 * 检查网络状态
 */
export function checkNetworkStatus() {
  return new Promise((resolve) => {
    uni.getNetworkType({
      success: (res) => {
        const isOnline = res.networkType !== 'none';
        resolve({
          isOnline: isOnline,
          networkType: res.networkType
        });
      },
      fail: () => {
        resolve({
          isOnline: false,
          networkType: 'unknown'
        });
      }
    });
  });
}

/**
 * 监听网络状态变化
 * @param {Function} callback - 回调函数，参数为 { isOnline, networkType }
 */
export function onNetworkStatusChange(callback) {
  uni.onNetworkStatusChange((res) => {
    const isOnline = res.isConnected;
    callback({
      isOnline: isOnline,
      networkType: res.networkType
    });
  });
}

/**
 * 同步所有未同步的草稿到服务器
 */
export async function syncAllDrafts() {
  const networkStatus = await checkNetworkStatus();
  
  if (!networkStatus.isOnline) {
    console.log('网络未连接，无法同步草稿');
    return {
      success: false,
      message: '网络未连接'
    };
  }
  
  const unsyncedDrafts = getUnsyncedDrafts();
  
  if (unsyncedDrafts.length === 0) {
    console.log('没有需要同步的草稿');
    return {
      success: true,
      message: '没有需要同步的草稿',
      count: 0
    };
  }
  
  console.log(`开始同步 ${unsyncedDrafts.length} 个草稿...`);
  
  let successCount = 0;
  let failCount = 0;
  
  for (const draft of unsyncedDrafts) {
    try {
      // 根据草稿key判断类型并调用相应的API
      if (draft.key.startsWith('questionnaire_draft_')) {
        // 同步问卷草稿
        await submitQuestionnaire(draft.data);
        markDraftAsSynced(draft.key);
        successCount++;
      } else if (draft.key.startsWith('resident_draft_')) {
        // 同步居民信息草稿
        await addResident(draft.data);
        markDraftAsSynced(draft.key);
        successCount++;
      }
      // 可以根据需要添加其他类型的草稿同步逻辑
    } catch (error) {
      console.error(`同步草稿 ${draft.key} 失败:`, error);
      failCount++;
    }
  }
  
  return {
    success: successCount > 0,
    message: `成功同步 ${successCount} 个，失败 ${failCount} 个`,
    successCount: successCount,
    failCount: failCount
  };
}
