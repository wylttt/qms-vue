package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.AjaxResult;
import com.javaxiaobear.module.gc.service.IGcStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计分析Controller
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/api/gc/statistics")
public class GcStatisticsController extends BaseController {

    @Autowired
    private IGcStatisticsService statisticsService;

    /**
     * 风险等级分布统计
     */
    @PreAuthorize("@ss.hasPermi('gc:statistics:risk')")
    @GetMapping("/riskDistribution")
    public AjaxResult getRiskDistribution(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Integer regionLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Map<String, Object> data = statisticsService.getRiskDistribution(regionId, regionLevel, startDate, endDate);
        return success(data);
    }

    /**
     * 随访进度统计
     */
    @PreAuthorize("@ss.hasPermi('gc:statistics:followup')")
    @GetMapping("/followUpProgress")
    public AjaxResult getFollowUpProgress(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Integer regionLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Map<String, Object> data = statisticsService.getFollowUpProgress(regionId, regionLevel, startDate, endDate);
        return success(data);
    }

    /**
     * 随访趋势分析
     */
    @PreAuthorize("@ss.hasPermi('gc:statistics:trend')")
    @GetMapping("/visitTrend")
    public AjaxResult getVisitTrend(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Integer regionLevel,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "month") String timeUnit) {
        List<Map<String, Object>> data = statisticsService.getVisitTrend(
            regionId, regionLevel, startDate, endDate, timeUnit
        );
        return success(data);
    }

    /**
     * 区域汇总统计
     */
    @PreAuthorize("@ss.hasPermi('gc:statistics:region')")
    @GetMapping("/regionSummary")
    public AjaxResult getRegionSummary(
            @RequestParam Long regionId,
            @RequestParam Integer regionLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Map<String, Object>> data = statisticsService.getRegionSummary(
            regionId, regionLevel, startDate, endDate
        );
        return success(data);
    }

    /**
     * 筛查进度统计
     */
    @PreAuthorize("@ss.hasPermi('gc:statistics:screening')")
    @GetMapping("/screeningProgress")
    public AjaxResult getScreeningProgress(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Integer regionLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Map<String, Object> data = statisticsService.getScreeningProgress(
            regionId, regionLevel, startDate, endDate
        );
        return success(data);
    }

    /**
     * 采血预约统计
     */
    @PreAuthorize("@ss.hasPermi('gc:statistics:appointment')")
    @GetMapping("/appointmentStatistics")
    public AjaxResult getAppointmentStatistics(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Integer regionLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Map<String, Object> data = statisticsService.getAppointmentStatistics(
            regionId, regionLevel, startDate, endDate
        );
        return success(data);
    }

    /**
     * 综合看板数据
     */
    @PreAuthorize("@ss.hasPermi('gc:statistics:dashboard')")
    @GetMapping("/dashboard")
    public AjaxResult getDashboard(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Integer regionLevel) {
        Map<String, Object> data = statisticsService.getDashboardData(regionId, regionLevel);
        return success(data);
    }
}
