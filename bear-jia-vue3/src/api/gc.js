import request from '@/utils/request';
import { parseStrEmpty } from '@/utils/bearjia';

// ==================== 居民管理 ====================

/**
 * 查询居民列表
 * @param {Object} query - 查询参数
 */
export function listResident(query) {
  return request({
    url: '/api/gc/resident/list',
    method: 'get',
    params: query,
  });
}

/**
 * 查询居民详细信息
 * @param {Number} residentId - 居民ID
 */
export function getResident(residentId) {
  return request({
    url: '/api/gc/resident/' + parseStrEmpty(residentId),
    method: 'get',
  });
}

/**
 * 新增居民
 * @param {Object} data - 居民信息
 */
export function addResident(data) {
  return request({
    url: '/api/gc/resident',
    method: 'post',
    data: data,
  });
}

/**
 * 修改居民
 * @param {Object} data - 居民信息
 */
export function updateResident(data) {
  return request({
    url: '/api/gc/resident',
    method: 'put',
    data: data,
  });
}

/**
 * 删除居民
 * @param {Number|Array} residentId - 居民ID或ID数组
 */
export function delResident(residentId) {
  return request({
    url: '/api/gc/resident/' + residentId,
    method: 'delete',
  });
}

/**
 * 批量导入居民
 * @param {FormData} data - 文件数据
 */
export function importResident(data) {
  return request({
    url: '/api/gc/resident/import',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 导出居民数据
 * @param {Object} query - 查询参数
 */
export function exportResident(query) {
  return request({
    url: '/api/gc/resident/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  });
}

/**
 * 下载导入模板
 */
export function downloadTemplate() {
  return request({
    url: '/api/gc/resident/template',
    method: 'get',
    responseType: 'blob'
  });
}

// ==================== 问卷管理 ====================

/**
 * 查询问卷模板列表
 * @param {Object} query - 查询参数
 */
export function listQuestionnaire(query) {
  return request({
    url: '/api/gc/questionnaire/list',
    method: 'get',
    params: query,
  });
}

/**
 * 查询问卷模板详细信息
 * @param {Number} questionnaireId - 问卷ID
 */
export function getQuestionnaire(questionnaireId) {
  return request({
    url: '/api/gc/questionnaire/' + parseStrEmpty(questionnaireId),
    method: 'get',
  });
}

/**
 * 新增问卷模板
 * @param {Object} data - 问卷信息
 */
export function addQuestionnaire(data) {
  return request({
    url: '/api/gc/questionnaire',
    method: 'post',
    data: data,
  });
}

/**
 * 修改问卷模板
 * @param {Object} data - 问卷信息
 */
export function updateQuestionnaire(data) {
  return request({
    url: '/api/gc/questionnaire',
    method: 'put',
    data: data,
  });
}

/**
 * 删除问卷模板
 * @param {Number|Array} questionnaireId - 问卷ID或ID数组
 */
export function delQuestionnaire(questionnaireId) {
  return request({
    url: '/api/gc/questionnaire/' + questionnaireId,
    method: 'delete',
  });
}

/**
 * 发布问卷模板
 * @param {Number} questionnaireId - 问卷ID
 */
export function publishQuestionnaire(questionnaireId) {
  return request({
    url: '/api/gc/questionnaire/publish/' + questionnaireId,
    method: 'put',
  });
}

/**
 * 停用问卷模板
 * @param {Number} questionnaireId - 问卷ID
 */
export function disableQuestionnaire(questionnaireId) {
  return request({
    url: '/api/gc/questionnaire/disable/' + questionnaireId,
    method: 'put',
  });
}

// ==================== 问卷记录 ====================

/**
 * 查询问卷记录列表
 * @param {Object} query - 查询参数
 */
export function listRecord(query) {
  return request({
    url: '/api/gc/record/list',
    method: 'get',
    params: query,
  });
}

/**
 * 查询问卷记录详细信息
 * @param {Number} recordId - 记录ID
 */
export function getRecord(recordId) {
  return request({
    url: '/api/gc/record/' + parseStrEmpty(recordId),
    method: 'get',
  });
}

/**
 * 提交问卷记录
 * @param {Object} data - 记录信息
 */
export function submitRecord(data) {
  return request({
    url: '/api/gc/record/submit',
    method: 'post',
    data: data,
  });
}

/**
 * 删除问卷记录
 * @param {Number|Array} recordId - 记录ID或ID数组
 */
export function delRecord(recordId) {
  return request({
    url: '/api/gc/record/' + recordId,
    method: 'delete',
  });
}

/**
 * 按居民ID查询问卷记录
 * @param {Number} residentId - 居民ID
 */
export function getRecordByResident(residentId) {
  return request({
    url: '/api/gc/record/resident/' + residentId,
    method: 'get',
  });
}

// ==================== 任务管理 ====================

/**
 * 查询任务列表
 * @param {Object} query - 查询参数
 */
export function listTask(query) {
  return request({
    url: '/api/gc/task/list',
    method: 'get',
    params: query,
  });
}

/**
 * 查询任务详细信息
 * @param {Number} taskId - 任务ID
 */
export function getTask(taskId) {
  return request({
    url: '/api/gc/task/' + parseStrEmpty(taskId),
    method: 'get',
  });
}

/**
 * 新增任务
 * @param {Object} data - 任务信息
 */
export function addTask(data) {
  return request({
    url: '/api/gc/task',
    method: 'post',
    data: data,
  });
}

/**
 * 修改任务
 * @param {Object} data - 任务信息
 */
export function updateTask(data) {
  return request({
    url: '/api/gc/task',
    method: 'put',
    data: data,
  });
}

/**
 * 删除任务
 * @param {Number|Array} taskId - 任务ID或ID数组
 */
export function delTask(taskId) {
  return request({
    url: '/api/gc/task/' + taskId,
    method: 'delete',
  });
}

/**
 * 查询任务进度统计
 * @param {Number} taskId - 任务ID
 */
export function getTaskProgress(taskId) {
  return request({
    url: '/api/gc/task/progress/' + taskId,
    method: 'get',
  });
}

/**
 * 查询任务汇总统计
 * @param {Object} query - 查询参数
 */
export function getTaskSummary(query) {
  return request({
    url: '/api/gc/task/summary',
    method: 'get',
    params: query,
  });
}

// ==================== 筛查结果管理 ====================

/**
 * 查询筛查结果列表
 * @param {Object} query - 查询参数
 */
export function listScreeningResult(query) {
  return request({
    url: '/api/gc/screening/result/list',
    method: 'get',
    params: query,
  });
}

/**
 * 查询筛查结果详细信息
 * @param {Number} resultId - 结果ID
 */
export function getScreeningResult(resultId) {
  return request({
    url: '/api/gc/screening/result/' + parseStrEmpty(resultId),
    method: 'get',
  });
}

/**
 * 新增筛查结果
 * @param {Object} data - 筛查结果信息
 */
export function addScreeningResult(data) {
  return request({
    url: '/api/gc/screening/result',
    method: 'post',
    data: data,
  });
}

/**
 * 修改筛查结果
 * @param {Object} data - 筛查结果信息
 */
export function updateScreeningResult(data) {
  return request({
    url: '/api/gc/screening/result',
    method: 'put',
    data: data,
  });
}

/**
 * 删除筛查结果
 * @param {Number|Array} resultId - 结果ID或ID数组
 */
export function delScreeningResult(resultId) {
  return request({
    url: '/api/gc/screening/result/' + resultId,
    method: 'delete',
  });
}

/**
 * 审核筛查结果
 * @param {Number} resultId - 结果ID
 */
export function reviewScreeningResult(resultId) {
  return request({
    url: '/api/gc/screening/result/review/' + resultId,
    method: 'put',
  });
}

/**
 * 按居民ID查询筛查结果
 * @param {Number} residentId - 居民ID
 */
export function getScreeningResultByResident(residentId) {
  return request({
    url: '/api/gc/screening/result/resident/' + residentId,
    method: 'get',
  });
}

// ==================== 随访管理 ====================

/**
 * 查询随访对象列表
 * @param {Object} query - 查询参数
 */
export function listFollowUp(query) {
  return request({
    url: '/api/gc/follow-up/list',
    method: 'get',
    params: query,
  });
}

/**
 * 查询随访对象详细信息
 * @param {Number} followUpId - 随访ID
 */
export function getFollowUp(followUpId) {
  return request({
    url: '/api/gc/follow-up/' + parseStrEmpty(followUpId),
    method: 'get',
  });
}

/**
 * 新增随访对象
 * @param {Object} data - 随访对象信息
 */
export function addFollowUp(data) {
  return request({
    url: '/api/gc/follow-up',
    method: 'post',
    data: data,
  });
}

/**
 * 修改随访对象
 * @param {Object} data - 随访对象信息
 */
export function updateFollowUp(data) {
  return request({
    url: '/api/gc/follow-up',
    method: 'put',
    data: data,
  });
}

/**
 * 删除随访对象
 * @param {Number|Array} followUpId - 随访ID或ID数组
 */
export function delFollowUp(followUpId) {
  return request({
    url: '/api/gc/follow-up/' + followUpId,
    method: 'delete',
  });
}

/**
 * 完成随访
 * @param {Number} followUpId - 随访ID
 * @param {String} conclusion - 随访结论
 */
export function completeFollowUp(followUpId, conclusion) {
  return request({
    url: '/api/gc/follow-up/complete/' + followUpId,
    method: 'put',
    data: { followUpConclusion: conclusion },
  });
}

/**
 * 按居民ID查询随访对象
 * @param {Number} residentId - 居民ID
 */
export function getFollowUpByResident(residentId) {
  return request({
    url: '/api/gc/follow-up/resident/' + residentId,
    method: 'get',
  });
}

// ==================== 随访跟踪 ====================

/**
 * 查询随访跟踪记录列表
 * @param {Object} query - 查询参数
 */
export function listFollowUpTrack(query) {
  return request({
    url: '/api/gc/follow-up/track/list',
    method: 'get',
    params: query,
  });
}

/**
 * 查询随访跟踪记录详细信息
 * @param {Number} trackId - 跟踪记录ID
 */
export function getFollowUpTrack(trackId) {
  return request({
    url: '/api/gc/follow-up/track/' + parseStrEmpty(trackId),
    method: 'get',
  });
}

/**
 * 新增随访跟踪记录
 * @param {Object} data - 跟踪记录信息
 */
export function addFollowUpTrack(data) {
  return request({
    url: '/api/gc/follow-up/track',
    method: 'post',
    data: data,
  });
}

/**
 * 修改随访跟踪记录
 * @param {Object} data - 跟踪记录信息
 */
export function updateFollowUpTrack(data) {
  return request({
    url: '/api/gc/follow-up/track',
    method: 'put',
    data: data,
  });
}

/**
 * 删除随访跟踪记录
 * @param {Number|Array} trackId - 跟踪记录ID或ID数组
 */
export function delFollowUpTrack(trackId) {
  return request({
    url: '/api/gc/follow-up/track/' + trackId,
    method: 'delete',
  });
}

/**
 * 按随访ID查询跟踪记录
 * @param {Number} followUpId - 随访ID
 */
export function getTrackByFollowUp(followUpId) {
  return request({
    url: '/api/gc/follow-up/track/follow-up/' + followUpId,
    method: 'get',
  });
}

// ==================== 采血预约 ====================

/**
 * 查询采血预约列表
 * @param {Object} query - 查询参数
 */
export function listBloodAppointment(query) {
  return request({
    url: '/api/gc/blood/appointment/list',
    method: 'get',
    params: query,
  });
}

/**
 * 查询采血预约详细信息
 * @param {Number} appointmentId - 预约ID
 */
export function getBloodAppointment(appointmentId) {
  return request({
    url: '/api/gc/blood/appointment/' + parseStrEmpty(appointmentId),
    method: 'get',
  });
}

/**
 * 新增采血预约
 * @param {Object} data - 预约信息
 */
export function addBloodAppointment(data) {
  return request({
    url: '/api/gc/blood/appointment',
    method: 'post',
    data: data,
  });
}

/**
 * 修改采血预约
 * @param {Object} data - 预约信息
 */
export function updateBloodAppointment(data) {
  return request({
    url: '/api/gc/blood/appointment',
    method: 'put',
    data: data,
  });
}

/**
 * 删除采血预约
 * @param {Number|Array} appointmentId - 预约ID或ID数组
 */
export function delBloodAppointment(appointmentId) {
  return request({
    url: '/api/gc/blood/appointment/' + appointmentId,
    method: 'delete',
  });
}

/**
 * 确认采血预约
 * @param {Number} appointmentId - 预约ID
 */
export function confirmBloodAppointment(appointmentId) {
  return request({
    url: '/api/gc/blood/appointment/confirm/' + appointmentId,
    method: 'put',
  });
}

/**
 * 取消采血预约
 * @param {Number} appointmentId - 预约ID
 * @param {String} reason - 取消原因
 */
export function cancelBloodAppointment(appointmentId, reason) {
  return request({
    url: '/api/gc/blood/appointment/cancel/' + appointmentId,
    method: 'put',
    data: { cancelReason: reason },
  });
}

// ==================== 统计分析 ====================

/**
 * 获取风险等级分布统计
 * @param {Object} query - 查询参数
 */
export function getRiskDistribution(query) {
  return request({
    url: '/api/gc/statistics/risk-distribution',
    method: 'get',
    params: query,
  });
}

/**
 * 获取随访进度统计
 * @param {Object} query - 查询参数
 */
export function getFollowUpProgress(query) {
  return request({
    url: '/api/gc/statistics/follow-up-progress',
    method: 'get',
    params: query,
  });
}

/**
 * 获取随访趋势分析
 * @param {Object} query - 查询参数
 */
export function getVisitTrend(query) {
  return request({
    url: '/api/gc/statistics/visit-trend',
    method: 'get',
    params: query,
  });
}

/**
 * 获取区域汇总统计
 * @param {Object} query - 查询参数
 */
export function getRegionSummary(query) {
  return request({
    url: '/api/gc/statistics/region-summary',
    method: 'get',
    params: query,
  });
}

/**
 * 获取筛查进度统计
 * @param {Object} query - 查询参数
 */
export function getScreeningProgress(query) {
  return request({
    url: '/api/gc/statistics/screening-progress',
    method: 'get',
    params: query,
  });
}

/**
 * 获取采血预约统计
 * @param {Object} query - 查询参数
 */
export function getAppointmentStatistics(query) {
  return request({
    url: '/api/gc/statistics/appointment-statistics',
    method: 'get',
    params: query,
  });
}

/**
 * 获取综合看板数据
 * @param {Object} query - 查询参数
 */
export function getDashboardData(query) {
  return request({
    url: '/api/gc/statistics/dashboard',
    method: 'get',
    params: query,
  });
}
