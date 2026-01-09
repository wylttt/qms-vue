package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.mapper.GcBloodAppointmentMapper;
import com.javaxiaobear.module.gc.mapper.GcFollowUpMapper;
import com.javaxiaobear.module.gc.mapper.GcFollowUpTrackMapper;
import com.javaxiaobear.module.gc.mapper.GcScreeningResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 统计分析Service单元测试
 * 
 * @author javaxiaobear
 * @date 2024-12-16
 */
@ExtendWith(MockitoExtension.class)
class GcStatisticsServiceImplTest {

    @Mock
    private GcScreeningResultMapper screeningResultMapper;

    @Mock
    private GcFollowUpMapper followUpMapper;

    @Mock
    private GcFollowUpTrackMapper followUpTrackMapper;

    @Mock
    private GcBloodAppointmentMapper bloodAppointmentMapper;

    @InjectMocks
    private GcStatisticsServiceImpl statisticsService;

    private Long regionId;
    private Integer regionLevel;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        regionId = 1L;
        regionLevel = 3; // 区级
        
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
        startDate = cal.getTime();
        
        cal.set(2025, Calendar.DECEMBER, 31, 23, 59, 59);
        endDate = cal.getTime();
    }

    // ==================== 风险分布统计测试 ====================

    /**
     * 测试风险分布统计 - 正常场景
     */
    @Test
    void testGetRiskDistribution_Normal() {
        // 准备Mock数据
        List<Map<String, Object>> riskData = new ArrayList<>();
        
        Map<String, Object> lowRisk = new HashMap<>();
        lowRisk.put("risk_level", "1");
        lowRisk.put("count", 100);
        riskData.add(lowRisk);
        
        Map<String, Object> mediumRisk = new HashMap<>();
        mediumRisk.put("risk_level", "2");
        mediumRisk.put("count", 50);
        riskData.add(mediumRisk);
        
        Map<String, Object> highRisk = new HashMap<>();
        highRisk.put("risk_level", "3");
        highRisk.put("count", 30);
        riskData.add(highRisk);
        
        when(screeningResultMapper.countByRiskLevel(regionId, regionLevel, startDate, endDate))
            .thenReturn(riskData);
        
        // 执行测试
        Map<String, Object> result = statisticsService.getRiskDistribution(regionId, regionLevel, startDate, endDate);
        
        // 验证结果
        assertEquals(180, result.get("total"));
        assertEquals(100, result.get("lowRisk"));
        assertEquals(50, result.get("mediumRisk"));
        assertEquals(30, result.get("highRisk"));
        
        // 验证占比（保留4位小数）
        assertEquals(0.5556, (Double) result.get("lowRiskRate"), 0.0001);
        assertEquals(0.2778, (Double) result.get("mediumRiskRate"), 0.0001);
        assertEquals(0.1667, (Double) result.get("highRiskRate"), 0.0001);
        
        // 验证分布数据
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> distribution = (List<Map<String, Object>>) result.get("distribution");
        assertEquals(3, distribution.size());
    }

    /**
     * 测试风险分布统计 - 无数据
     */
    @Test
    void testGetRiskDistribution_EmptyData() {
        when(screeningResultMapper.countByRiskLevel(regionId, regionLevel, startDate, endDate))
            .thenReturn(new ArrayList<>());
        
        Map<String, Object> result = statisticsService.getRiskDistribution(regionId, regionLevel, startDate, endDate);
        
        assertEquals(0, result.get("total"));
        assertEquals(0, result.get("lowRisk"));
        assertEquals(0, result.get("mediumRisk"));
        assertEquals(0, result.get("highRisk"));
        assertEquals(0.0, result.get("lowRiskRate"));
        assertEquals(0.0, result.get("mediumRiskRate"));
        assertEquals(0.0, result.get("highRiskRate"));
    }

    /**
     * 测试风险分布统计 - 仅高风险
     */
    @Test
    void testGetRiskDistribution_OnlyHighRisk() {
        List<Map<String, Object>> riskData = new ArrayList<>();
        Map<String, Object> highRisk = new HashMap<>();
        highRisk.put("risk_level", "3");
        highRisk.put("count", 100);
        riskData.add(highRisk);
        
        when(screeningResultMapper.countByRiskLevel(regionId, regionLevel, startDate, endDate))
            .thenReturn(riskData);
        
        Map<String, Object> result = statisticsService.getRiskDistribution(regionId, regionLevel, startDate, endDate);
        
        assertEquals(100, result.get("total"));
        assertEquals(0, result.get("lowRisk"));
        assertEquals(0, result.get("mediumRisk"));
        assertEquals(100, result.get("highRisk"));
        assertEquals(1.0, result.get("highRiskRate"));
    }

    // ==================== 随访进度统计测试 ====================

    /**
     * 测试随访进度统计 - 正常场景
     */
    @Test
    void testGetFollowUpProgress_Normal() {
        // Mock各状态人数
        when(followUpMapper.countFollowUp(regionId, regionLevel, "0")).thenReturn(50);  // 待随访
        when(followUpMapper.countFollowUp(regionId, regionLevel, "1")).thenReturn(30);  // 随访中
        when(followUpMapper.countFollowUp(regionId, regionLevel, "2")).thenReturn(120); // 已完成
        
        Map<String, Object> result = statisticsService.getFollowUpProgress(regionId, regionLevel, startDate, endDate);
        
        assertEquals(200, result.get("total"));
        assertEquals(50, result.get("pending"));
        assertEquals(30, result.get("inProgress"));
        assertEquals(120, result.get("completed"));
        
        // 验证完成率
        assertEquals(0.6000, (Double) result.get("completionRate"), 0.0001);
        
        // 验证状态分布
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> distribution = (List<Map<String, Object>>) result.get("statusDistribution");
        assertEquals(3, distribution.size());
    }

    /**
     * 测试随访进度统计 - 无随访对象
     */
    @Test
    void testGetFollowUpProgress_EmptyData() {
        when(followUpMapper.countFollowUp(regionId, regionLevel, "0")).thenReturn(0);
        when(followUpMapper.countFollowUp(regionId, regionLevel, "1")).thenReturn(0);
        when(followUpMapper.countFollowUp(regionId, regionLevel, "2")).thenReturn(0);
        
        Map<String, Object> result = statisticsService.getFollowUpProgress(regionId, regionLevel, startDate, endDate);
        
        assertEquals(0, result.get("total"));
        assertEquals(0.0, result.get("completionRate"));
    }

    /**
     * 测试随访进度统计 - 100%完成
     */
    @Test
    void testGetFollowUpProgress_FullyCompleted() {
        when(followUpMapper.countFollowUp(regionId, regionLevel, "0")).thenReturn(0);
        when(followUpMapper.countFollowUp(regionId, regionLevel, "1")).thenReturn(0);
        when(followUpMapper.countFollowUp(regionId, regionLevel, "2")).thenReturn(100);
        
        Map<String, Object> result = statisticsService.getFollowUpProgress(regionId, regionLevel, startDate, endDate);
        
        assertEquals(100, result.get("total"));
        assertEquals(100, result.get("completed"));
        assertEquals(1.0, result.get("completionRate"));
    }

    // ==================== 数据看板统计测试 ====================

    /**
     * 测试数据看板 - 正常场景
     */
    @Test
    void testGetDashboardData_Normal() {
        // Mock高风险人数
        when(screeningResultMapper.countHighRisk(eq(regionId), eq(regionLevel), any(), any()))
            .thenReturn(50);
        
        // Mock随访数据
        when(followUpMapper.countFollowUp(regionId, regionLevel, null)).thenReturn(100);
        when(followUpMapper.countFollowUp(regionId, regionLevel, "2")).thenReturn(60);
        
        // Mock超期随访
        when(followUpMapper.selectOverdueFollowUps(any(Date.class))).thenReturn(new ArrayList<>());
        
        // Mock风险分布
        List<Map<String, Object>> riskData = new ArrayList<>();
        Map<String, Object> highRisk = new HashMap<>();
        highRisk.put("risk_level", "3");
        highRisk.put("count", 50);
        riskData.add(highRisk);
        when(screeningResultMapper.countByRiskLevel(eq(regionId), eq(regionLevel), any(), any()))
            .thenReturn(riskData);
        
        // 执行测试
        Map<String, Object> dashboard = statisticsService.getDashboardData(regionId, regionLevel);
        
        // 验证基础数据
        assertEquals(50, dashboard.get("highRiskCount"));
        assertEquals(100, dashboard.get("followUpCount"));
        assertEquals(60, dashboard.get("completedFollowUpCount"));
        
        // 验证风险分布存在
        assertNotNull(dashboard.get("riskDistribution"));
        
        // 验证超期随访
        assertEquals(0, dashboard.get("overdueFollowUp"));
    }

    /**
     * 测试数据看板 - 有超期随访
     */
    @Test
    void testGetDashboardData_WithOverdue() {
        when(screeningResultMapper.countHighRisk(eq(regionId), eq(regionLevel), any(), any()))
            .thenReturn(50);
        when(followUpMapper.countFollowUp(regionId, regionLevel, null)).thenReturn(100);
        when(followUpMapper.countFollowUp(regionId, regionLevel, "2")).thenReturn(60);
        
        // Mock 5个超期随访
        List<Map<String, Object>> overdueList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            overdueList.add(new HashMap<>());
        }
        when(followUpMapper.selectOverdueFollowUps(any(Date.class))).thenReturn(overdueList);
        
        // Mock风险分布
        when(screeningResultMapper.countByRiskLevel(eq(regionId), eq(regionLevel), any(), any()))
            .thenReturn(new ArrayList<>());
        
        Map<String, Object> dashboard = statisticsService.getDashboardData(regionId, regionLevel);
        
        assertEquals(5, dashboard.get("overdueFollowUp"));
    }

    // ==================== 比率计算测试 ====================

    /**
     * 测试比率计算 - 正常场景
     */
    @Test
    void testCalculateRate_Normal() {
        List<Map<String, Object>> riskData = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("risk_level", "1");
        data.put("count", 50);
        riskData.add(data);
        
        when(screeningResultMapper.countByRiskLevel(regionId, regionLevel, startDate, endDate))
            .thenReturn(riskData);
        
        Map<String, Object> result = statisticsService.getRiskDistribution(regionId, regionLevel, startDate, endDate);
        
        // 50/50 = 1.0
        assertEquals(1.0, result.get("lowRiskRate"));
    }

    /**
     * 测试比率计算 - 除数为0
     */
    @Test
    void testCalculateRate_DivideByZero() {
        when(screeningResultMapper.countByRiskLevel(regionId, regionLevel, startDate, endDate))
            .thenReturn(new ArrayList<>());
        
        Map<String, Object> result = statisticsService.getRiskDistribution(regionId, regionLevel, startDate, endDate);
        
        // 总数为0时，所有比率都应该为0.0
        assertEquals(0.0, result.get("lowRiskRate"));
        assertEquals(0.0, result.get("mediumRiskRate"));
        assertEquals(0.0, result.get("highRiskRate"));
    }

    /**
     * 测试比率计算 - 小数精度
     */
    @Test
    void testCalculateRate_Precision() {
        List<Map<String, Object>> riskData = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("risk_level", "1");
        data.put("count", 1);
        riskData.add(data);
        
        Map<String, Object> data2 = new HashMap<>();
        data2.put("risk_level", "2");
        data2.put("count", 2);
        riskData.add(data2);
        
        when(screeningResultMapper.countByRiskLevel(regionId, regionLevel, startDate, endDate))
            .thenReturn(riskData);
        
        Map<String, Object> result = statisticsService.getRiskDistribution(regionId, regionLevel, startDate, endDate);
        
        // 1/3 = 0.3333, 2/3 = 0.6667 (保留4位小数)
        assertEquals(0.3333, (Double) result.get("lowRiskRate"), 0.0001);
        assertEquals(0.6667, (Double) result.get("mediumRiskRate"), 0.0001);
    }

    // ==================== 边界测试 ====================

    /**
     * 测试大数据量统计
     */
    @Test
    void testGetRiskDistribution_LargeData() {
        List<Map<String, Object>> riskData = new ArrayList<>();
        
        Map<String, Object> lowRisk = new HashMap<>();
        lowRisk.put("risk_level", "1");
        lowRisk.put("count", 10000);
        riskData.add(lowRisk);
        
        Map<String, Object> mediumRisk = new HashMap<>();
        mediumRisk.put("risk_level", "2");
        mediumRisk.put("count", 5000);
        riskData.add(mediumRisk);
        
        Map<String, Object> highRisk = new HashMap<>();
        highRisk.put("risk_level", "3");
        highRisk.put("count", 3000);
        riskData.add(highRisk);
        
        when(screeningResultMapper.countByRiskLevel(regionId, regionLevel, startDate, endDate))
            .thenReturn(riskData);
        
        Map<String, Object> result = statisticsService.getRiskDistribution(regionId, regionLevel, startDate, endDate);
        
        assertEquals(18000, result.get("total"));
        assertEquals(10000, result.get("lowRisk"));
        assertEquals(5000, result.get("mediumRisk"));
        assertEquals(3000, result.get("highRisk"));
    }

    /**
     * 测试无日期范围
     */
    @Test
    void testGetRiskDistribution_NoDateRange() {
        List<Map<String, Object>> riskData = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("risk_level", "1");
        data.put("count", 100);
        riskData.add(data);
        
        when(screeningResultMapper.countByRiskLevel(regionId, regionLevel, null, null))
            .thenReturn(riskData);
        
        Map<String, Object> result = statisticsService.getRiskDistribution(regionId, regionLevel, null, null);
        
        assertEquals(100, result.get("total"));
        verify(screeningResultMapper).countByRiskLevel(regionId, regionLevel, null, null);
    }

    /**
     * 测试不同区域层级
     */
    @Test
    void testGetFollowUpProgress_DifferentLevels() {
        // 街道级（4级）
        Integer streetLevel = 4;
        when(followUpMapper.countFollowUp(regionId, streetLevel, "0")).thenReturn(10);
        when(followUpMapper.countFollowUp(regionId, streetLevel, "1")).thenReturn(20);
        when(followUpMapper.countFollowUp(regionId, streetLevel, "2")).thenReturn(30);
        
        Map<String, Object> result = statisticsService.getFollowUpProgress(regionId, streetLevel, startDate, endDate);
        
        assertEquals(60, result.get("total"));
        assertEquals(0.5000, (Double) result.get("completionRate"), 0.0001);
    }
}
