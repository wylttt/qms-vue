package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.mapper.GcBloodAppointmentMapper;
import com.javaxiaobear.module.gc.mapper.GcFollowUpMapper;
import com.javaxiaobear.module.gc.mapper.GcFollowUpTrackMapper;
import com.javaxiaobear.module.gc.mapper.GcScreeningResultMapper;
import com.javaxiaobear.module.gc.service.IGcStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 统计分析Service实现
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcStatisticsServiceImpl implements IGcStatisticsService {

    @Autowired
    private GcScreeningResultMapper screeningResultMapper;

    @Autowired
    private GcFollowUpMapper followUpMapper;

    @Autowired
    private GcFollowUpTrackMapper followUpTrackMapper;

    @Autowired
    private GcBloodAppointmentMapper bloodAppointmentMapper;

    @Override
    public Map<String, Object> getRiskDistribution(Long regionId, Integer regionLevel, Date startDate, Date endDate) {
        // 1. 查询各风险等级人数
        List<Map<String, Object>> riskData = screeningResultMapper.countByRiskLevel(
            regionId, regionLevel, startDate, endDate
        );

        // 2. 计算总人数和各等级统计
        int total = 0;
        int lowRisk = 0;
        int mediumRisk = 0;
        int highRisk = 0;

        for (Map<String, Object> data : riskData) {
            String riskLevel = (String) data.get("risk_level");
            Integer count = ((Number) data.get("count")).intValue();
            total += count;

            if ("1".equals(riskLevel)) {
                lowRisk = count;
            } else if ("2".equals(riskLevel)) {
                mediumRisk = count;
            } else if ("3".equals(riskLevel)) {
                highRisk = count;
            }
        }

        // 3. 计算占比
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("lowRisk", lowRisk);
        result.put("lowRiskRate", calculateRate(lowRisk, total));
        result.put("mediumRisk", mediumRisk);
        result.put("mediumRiskRate", calculateRate(mediumRisk, total));
        result.put("highRisk", highRisk);
        result.put("highRiskRate", calculateRate(highRisk, total));

        // 4. 构建分布数据
        List<Map<String, Object>> distribution = new ArrayList<>();
        for (Map<String, Object> data : riskData) {
            Map<String, Object> item = new HashMap<>();
            String riskLevel = (String) data.get("risk_level");
            Integer count = ((Number) data.get("count")).intValue();
            item.put("riskLevel", riskLevel);
            item.put("count", count);
            item.put("rate", calculateRate(count, total));
            distribution.add(item);
        }
        result.put("distribution", distribution);

        return result;
    }

    @Override
    public Map<String, Object> getFollowUpProgress(Long regionId, Integer regionLevel, Date startDate, Date endDate) {
        // 1. 统计各状态的随访对象数量
        int pending = followUpMapper.countFollowUp(regionId, regionLevel, "0");    // 待随访
        int inProgress = followUpMapper.countFollowUp(regionId, regionLevel, "1"); // 随访中
        int completed = followUpMapper.countFollowUp(regionId, regionLevel, "2");  // 已完成
        int total = pending + inProgress + completed;

        // 2. 计算占比
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pending", pending);
        result.put("pendingRate", calculateRate(pending, total));
        result.put("inProgress", inProgress);
        result.put("inProgressRate", calculateRate(inProgress, total));
        result.put("completed", completed);
        result.put("completedRate", calculateRate(completed, total));
        result.put("completionRate", calculateRate(completed, total));

        // 3. 构建状态分布数据
        List<Map<String, Object>> statusDistribution = new ArrayList<>();
        statusDistribution.add(createStatusItem("0", pending, total));
        statusDistribution.add(createStatusItem("1", inProgress, total));
        statusDistribution.add(createStatusItem("2", completed, total));
        result.put("statusDistribution", statusDistribution);

        // TODO: 计算平均随访次数（需要在Mapper中添加相应方法）
        result.put("averageVisitCount", 0.0);

        return result;
    }

    @Override
    public List<Map<String, Object>> getVisitTrend(Long regionId, Integer regionLevel, 
                                                    Date startDate, Date endDate, String timeUnit) {
        // TODO: 实现趋势分析（需要在Mapper中添加按时间维度统计的方法）
        List<Map<String, Object>> trendList = new ArrayList<>();
        
        // 示例数据结构
        Map<String, Object> trendItem = new HashMap<>();
        trendItem.put("date", "2024-12");
        trendItem.put("newFollowUps", 0);
        trendItem.put("completedFollowUps", 0);
        trendItem.put("visitCount", 0);
        trendItem.put("avgVisitCount", 0.0);
        trendList.add(trendItem);

        return trendList;
    }

    @Override
    public List<Map<String, Object>> getRegionSummary(Long regionId, Integer regionLevel, 
                                                       Date startDate, Date endDate) {
        // TODO: 实现区域汇总统计（需要在Mapper中添加按区域汇总的方法）
        List<Map<String, Object>> summaryList = new ArrayList<>();
        
        // 示例数据结构
        Map<String, Object> summary = new HashMap<>();
        summary.put("regionId", regionId);
        summary.put("regionName", "示例区域");
        summary.put("residentCount", 0);
        summary.put("screenedCount", 0);
        summary.put("screeningRate", 0.0);
        summary.put("highRiskCount", 0);
        summary.put("highRiskRate", 0.0);
        summary.put("followUpCount", 0);
        summary.put("completedFollowUpCount", 0);
        summary.put("followUpCompletionRate", 0.0);
        summaryList.add(summary);

        return summaryList;
    }

    @Override
    public Map<String, Object> getScreeningProgress(Long regionId, Integer regionLevel, 
                                                     Date startDate, Date endDate) {
        // TODO: 实现筛查进度统计（需要在Mapper中添加相应统计方法）
        Map<String, Object> result = new HashMap<>();
        result.put("residentCount", 0);
        result.put("screenedCount", 0);
        result.put("screeningRate", 0.0);
        result.put("reviewedCount", 0);
        result.put("reviewRate", 0.0);
        result.put("bloodScreenedCount", 0);
        result.put("questionnaireScreenedCount", 0);

        return result;
    }

    @Override
    public Map<String, Object> getAppointmentStatistics(Long regionId, Integer regionLevel, 
                                                         Date startDate, Date endDate) {
        // TODO: 实现采血预约统计（需要在GcBloodAppointmentMapper中添加统计方法）
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0);
        result.put("pending", 0);
        result.put("pendingRate", 0.0);
        result.put("confirmed", 0);
        result.put("confirmedRate", 0.0);
        result.put("completed", 0);
        result.put("completedRate", 0.0);
        result.put("cancelled", 0);
        result.put("cancelledRate", 0.0);
        result.put("completionRate", 0.0);

        return result;
    }

    @Override
    public Map<String, Object> getDashboardData(Long regionId, Integer regionLevel) {
        Map<String, Object> dashboard = new HashMap<>();

        // 1. 基础统计数据
        Date now = new Date();
        Date startOfMonth = getStartOfMonth(now);
        
        // 筛查数据
        int highRiskCount = screeningResultMapper.countHighRisk(regionId, regionLevel, null, null);
        dashboard.put("highRiskCount", highRiskCount);

        // 随访数据
        int followUpCount = followUpMapper.countFollowUp(regionId, regionLevel, null);
        int completedFollowUpCount = followUpMapper.countFollowUp(regionId, regionLevel, "2");
        dashboard.put("followUpCount", followUpCount);
        dashboard.put("completedFollowUpCount", completedFollowUpCount);

        // 2. 今日数据（TODO: 需要在Mapper中添加按日期统计的方法）
        dashboard.put("todayAppointment", 0);
        dashboard.put("todayVisit", 0);

        // 3. 预警数据
        Date overdueDate = getDateBefore(now, 7); // 7天未随访视为超期
        List<Map<String, Object>> overdueList = followUpMapper.selectOverdueFollowUps(overdueDate);
        dashboard.put("overdueFollowUp", overdueList.size());

        // 4. 风险分布
        Map<String, Object> riskDistribution = getRiskDistribution(regionId, regionLevel, null, null);
        dashboard.put("riskDistribution", riskDistribution);

        // 5. 近期趋势（最近30天）
        Date startDate = getDateBefore(now, 30);
        List<Map<String, Object>> recentTrend = getVisitTrend(regionId, regionLevel, startDate, now, "day");
        dashboard.put("recentTrend", recentTrend);

        // 6. 总体进度
        dashboard.put("residentCount", 0); // TODO: 需要统计居民总数
        dashboard.put("screenedCount", 0); // TODO: 需要统计筛查人数
        dashboard.put("screeningRate", 0.0);

        return dashboard;
    }

    /**
     * 计算比率（保留4位小数）
     */
    private double calculateRate(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        BigDecimal partDecimal = new BigDecimal(part);
        BigDecimal totalDecimal = new BigDecimal(total);
        return partDecimal.divide(totalDecimal, 4, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 创建状态项
     */
    private Map<String, Object> createStatusItem(String status, int count, int total) {
        Map<String, Object> item = new HashMap<>();
        item.put("status", status);
        item.put("count", count);
        item.put("rate", calculateRate(count, total));
        return item;
    }

    /**
     * 获取月初日期
     */
    private Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取N天前的日期
     */
    private Date getDateBefore(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }
}
