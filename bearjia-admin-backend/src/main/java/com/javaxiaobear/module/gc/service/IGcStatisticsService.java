package com.javaxiaobear.module.gc.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计分析Service接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface IGcStatisticsService {

    /**
     * 风险等级分布统计
     * 统计各风险等级的人数和占比
     * 
     * @param regionId 区域ID（可选）
     * @param regionLevel 区域层级（可选，3-区/4-街道/5-社区）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 统计结果
     * {
     *   "total": 1000,
     *   "lowRisk": 600,
     *   "lowRiskRate": 0.6,
     *   "mediumRisk": 300,
     *   "mediumRiskRate": 0.3,
     *   "highRisk": 100,
     *   "highRiskRate": 0.1,
     *   "distribution": [
     *     {"riskLevel": "1", "count": 600, "rate": 0.6},
     *     {"riskLevel": "2", "count": 300, "rate": 0.3},
     *     {"riskLevel": "3", "count": 100, "rate": 0.1}
     *   ]
     * }
     */
    Map<String, Object> getRiskDistribution(Long regionId, Integer regionLevel, Date startDate, Date endDate);

    /**
     * 随访进度统计
     * 统计随访对象的状态分布和完成情况
     * 
     * @param regionId 区域ID（可选）
     * @param regionLevel 区域层级（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 统计结果
     * {
     *   "total": 100,
     *   "pending": 30,
     *   "pendingRate": 0.3,
     *   "inProgress": 50,
     *   "inProgressRate": 0.5,
     *   "completed": 20,
     *   "completedRate": 0.2,
     *   "completionRate": 0.2,
     *   "averageVisitCount": 2.5,
     *   "statusDistribution": [
     *     {"status": "0", "count": 30, "rate": 0.3},
     *     {"status": "1", "count": 50, "rate": 0.5},
     *     {"status": "2", "count": 20, "rate": 0.2}
     *   ]
     * }
     */
    Map<String, Object> getFollowUpProgress(Long regionId, Integer regionLevel, Date startDate, Date endDate);

    /**
     * 随访趋势分析
     * 按时间维度统计随访数据趋势
     * 
     * @param regionId 区域ID（可选）
     * @param regionLevel 区域层级（可选）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param timeUnit 时间单位（day-按天/week-按周/month-按月）
     * @return 趋势数据列表
     * [
     *   {
     *     "date": "2024-12",
     *     "newFollowUps": 10,
     *     "completedFollowUps": 5,
     *     "visitCount": 30,
     *     "avgVisitCount": 3.0
     *   }
     * ]
     */
    List<Map<String, Object>> getVisitTrend(Long regionId, Integer regionLevel, Date startDate, Date endDate, String timeUnit);

    /**
     * 区域汇总统计
     * 按区域层级汇总各项指标
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级（3-按街道汇总/4-按社区汇总）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 区域统计列表
     * [
     *   {
     *     "regionId": 1001,
     *     "regionName": "XX街道",
     *     "residentCount": 1000,
     *     "screenedCount": 800,
     *     "screeningRate": 0.8,
     *     "highRiskCount": 100,
     *     "highRiskRate": 0.125,
     *     "followUpCount": 100,
     *     "completedFollowUpCount": 50,
     *     "followUpCompletionRate": 0.5
     *   }
     * ]
     */
    List<Map<String, Object>> getRegionSummary(Long regionId, Integer regionLevel, Date startDate, Date endDate);

    /**
     * 筛查进度统计
     * 统计筛查任务的完成情况
     * 
     * @param regionId 区域ID（可选）
     * @param regionLevel 区域层级（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 统计结果
     * {
     *   "residentCount": 10000,
     *   "screenedCount": 8000,
     *   "screeningRate": 0.8,
     *   "reviewedCount": 7500,
     *   "reviewRate": 0.9375,
     *   "bloodScreenedCount": 6000,
     *   "questionnaireScreenedCount": 2000
     * }
     */
    Map<String, Object> getScreeningProgress(Long regionId, Integer regionLevel, Date startDate, Date endDate);

    /**
     * 采血预约统计
     * 统计采血预约的状态分布和完成情况
     * 
     * @param regionId 区域ID（可选）
     * @param regionLevel 区域层级（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 统计结果
     * {
     *   "total": 1000,
     *   "pending": 100,
     *   "pendingRate": 0.1,
     *   "confirmed": 500,
     *   "confirmedRate": 0.5,
     *   "completed": 300,
     *   "completedRate": 0.3,
     *   "cancelled": 100,
     *   "cancelledRate": 0.1,
     *   "completionRate": 0.3
     * }
     */
    Map<String, Object> getAppointmentStatistics(Long regionId, Integer regionLevel, Date startDate, Date endDate);

    /**
     * 综合看板数据
     * 获取首页看板需要的各项综合数据
     * 
     * @param regionId 区域ID（可选）
     * @param regionLevel 区域层级（可选）
     * @return 看板数据
     * {
     *   "residentCount": 10000,
     *   "screenedCount": 8000,
     *   "screeningRate": 0.8,
     *   "highRiskCount": 100,
     *   "followUpCount": 100,
     *   "completedFollowUpCount": 50,
     *   "todayAppointment": 50,
     *   "todayVisit": 30,
     *   "overdueFollowUp": 10,
     *   "riskDistribution": {...},
     *   "recentTrend": [...]
     * }
     */
    Map<String, Object> getDashboardData(Long regionId, Integer regionLevel);
}
