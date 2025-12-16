package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUp;
import com.javaxiaobear.module.gc.mapper.GcFollowUpMapper;
import com.javaxiaobear.module.gc.mapper.GcFollowUpTrackMapper;
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
 * 随访对象Service单元测试
 * 
 * @author javaxiaobear
 * @date 2024-12-16
 */
@ExtendWith(MockitoExtension.class)
class GcFollowUpServiceImplTest {

    @Mock
    private GcFollowUpMapper followUpMapper;

    @Mock
    private GcFollowUpTrackMapper followUpTrackMapper;

    @InjectMocks
    private GcFollowUpServiceImpl followUpService;

    private GcFollowUp followUp;
    private Map<String, Object> followUpDetail;

    @BeforeEach
    void setUp() {
        // 创建测试随访对象
        followUp = new GcFollowUp();
        followUp.setFollowUpId(1L);
        followUp.setResidentId(100L);
        followUp.setResultId(200L);
        followUp.setFollowUpReason("1"); // 高风险人群
        followUp.setFollowUpStatus("0"); // 待随访
        followUp.setPlannedVisitCount(3);
        followUp.setActualVisitCount(0);
        followUp.setStartDate(new Date());
        
        // 创建测试详情Map
        followUpDetail = new HashMap<>();
        followUpDetail.put("follow_up_id", 1L);
        followUpDetail.put("resident_id", 100L);
        followUpDetail.put("follow_up_status", "0");
        followUpDetail.put("planned_visit_count", 3);
        followUpDetail.put("actual_visit_count", 0);
    }

    // ==================== 随访对象创建测试 ====================

    /**
     * 测试创建随访对象 - 成功
     */
    @Test
    void testInsertFollowUp_Success() {
        when(followUpMapper.selectByResultId(200L)).thenReturn(null);
        when(followUpMapper.insertFollowUp(any(GcFollowUp.class))).thenReturn(1);
        
        int rows = followUpService.insertFollowUp(followUp);
        
        assertEquals(1, rows);
        assertEquals("0", followUp.getFollowUpStatus());
        assertEquals(3, followUp.getPlannedVisitCount());
        assertEquals(0, followUp.getActualVisitCount());
        verify(followUpMapper).insertFollowUp(followUp);
    }

    /**
     * 测试创建随访对象 - 居民ID为空
     */
    @Test
    void testInsertFollowUp_EmptyResidentId() {
        followUp.setResidentId(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.insertFollowUp(followUp);
        });
        
        assertTrue(exception.getMessage().contains("居民ID不能为空"));
    }

    /**
     * 测试创建随访对象 - 随访原因为空
     */
    @Test
    void testInsertFollowUp_EmptyFollowUpReason() {
        followUp.setFollowUpReason(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.insertFollowUp(followUp);
        });
        
        assertTrue(exception.getMessage().contains("随访原因不能为空"));
    }

    /**
     * 测试创建随访对象 - 筛查结果已存在随访对象
     */
    @Test
    void testInsertFollowUp_ResultExists() {
        GcFollowUp existFollowUp = new GcFollowUp();
        existFollowUp.setResultId(200L);
        when(followUpMapper.selectByResultId(200L)).thenReturn(existFollowUp);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.insertFollowUp(followUp);
        });
        
        assertTrue(exception.getMessage().contains("该筛查结果已存在随访对象"));
    }

    /**
     * 测试创建随访对象 - 设置默认值
     */
    @Test
    void testInsertFollowUp_DefaultValues() {
        followUp.setFollowUpStatus(null);
        followUp.setPlannedVisitCount(null);
        followUp.setActualVisitCount(null);
        followUp.setStartDate(null);
        
        when(followUpMapper.selectByResultId(200L)).thenReturn(null);
        when(followUpMapper.insertFollowUp(any(GcFollowUp.class))).thenReturn(1);
        
        followUpService.insertFollowUp(followUp);
        
        assertEquals("0", followUp.getFollowUpStatus());
        assertEquals(3, followUp.getPlannedVisitCount());
        assertEquals(0, followUp.getActualVisitCount());
        assertNotNull(followUp.getStartDate());
    }

    // ==================== 随访对象修改测试 ====================

    /**
     * 测试修改随访对象 - 成功
     */
    @Test
    void testUpdateFollowUp_Success() {
        followUpDetail.put("follow_up_status", "0"); // 待随访
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        when(followUpMapper.updateFollowUp(any(GcFollowUp.class))).thenReturn(1);
        
        followUp.setPlannedVisitCount(5); // 修改计划次数
        int rows = followUpService.updateFollowUp(followUp);
        
        assertEquals(1, rows);
        verify(followUpMapper).updateFollowUp(followUp);
    }

    /**
     * 测试修改随访对象 - 随访ID为空
     */
    @Test
    void testUpdateFollowUp_EmptyId() {
        followUp.setFollowUpId(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.updateFollowUp(followUp);
        });
        
        assertTrue(exception.getMessage().contains("随访ID不能为空"));
    }

    /**
     * 测试修改随访对象 - 随访对象不存在
     */
    @Test
    void testUpdateFollowUp_NotExists() {
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.updateFollowUp(followUp);
        });
        
        assertTrue(exception.getMessage().contains("随访对象不存在"));
    }

    /**
     * 测试修改随访对象 - 已完成不可修改
     */
    @Test
    void testUpdateFollowUp_AlreadyCompleted() {
        followUpDetail.put("follow_up_status", "2"); // 已完成
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.updateFollowUp(followUp);
        });
        
        assertTrue(exception.getMessage().contains("已完成的随访对象不允许修改"));
    }

    /**
     * 测试修改随访对象 - 随访中状态可修改
     */
    @Test
    void testUpdateFollowUp_InProgress() {
        followUpDetail.put("follow_up_status", "1"); // 随访中
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        when(followUpMapper.updateFollowUp(any(GcFollowUp.class))).thenReturn(1);
        
        int rows = followUpService.updateFollowUp(followUp);
        
        assertEquals(1, rows);
    }

    // ==================== 随访对象删除测试 ====================

    /**
     * 测试删除随访对象 - 成功
     */
    @Test
    void testDeleteFollowUp_Success() {
        followUpDetail.put("follow_up_status", "0");
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        when(followUpTrackMapper.countByFollowUpId(1L)).thenReturn(0);
        when(followUpMapper.deleteFollowUpById(1L)).thenReturn(1);
        
        int rows = followUpService.deleteFollowUpById(1L);
        
        assertEquals(1, rows);
        verify(followUpMapper).deleteFollowUpById(1L);
    }

    /**
     * 测试删除随访对象 - 随访ID为空
     */
    @Test
    void testDeleteFollowUp_EmptyId() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.deleteFollowUpById(null);
        });
        
        assertTrue(exception.getMessage().contains("随访ID不能为空"));
    }

    /**
     * 测试删除随访对象 - 随访对象不存在
     */
    @Test
    void testDeleteFollowUp_NotExists() {
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.deleteFollowUpById(1L);
        });
        
        assertTrue(exception.getMessage().contains("随访对象不存在"));
    }

    /**
     * 测试删除随访对象 - 非待随访状态不可删除
     */
    @Test
    void testDeleteFollowUp_NotPending() {
        followUpDetail.put("follow_up_status", "1"); // 随访中
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.deleteFollowUpById(1L);
        });
        
        assertTrue(exception.getMessage().contains("仅允许删除待随访状态的对象"));
    }

    /**
     * 测试删除随访对象 - 存在跟踪记录不可删除
     */
    @Test
    void testDeleteFollowUp_HasTrackRecords() {
        followUpDetail.put("follow_up_status", "0");
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        when(followUpTrackMapper.countByFollowUpId(1L)).thenReturn(2); // 有2条跟踪记录
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.deleteFollowUpById(1L);
        });
        
        assertTrue(exception.getMessage().contains("存在随访跟踪记录"));
    }

    // ==================== 批量删除测试 ====================

    /**
     * 测试批量删除随访对象 - 成功
     */
    @Test
    void testDeleteFollowUpByIds_Success() {
        Long[] followUpIds = {1L, 2L, 3L};
        
        Map<String, Object> detail1 = new HashMap<>();
        detail1.put("follow_up_status", "0");
        Map<String, Object> detail2 = new HashMap<>();
        detail2.put("follow_up_status", "0");
        Map<String, Object> detail3 = new HashMap<>();
        detail3.put("follow_up_status", "0");
        
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(detail1);
        when(followUpMapper.selectFollowUpDetail(2L)).thenReturn(detail2);
        when(followUpMapper.selectFollowUpDetail(3L)).thenReturn(detail3);
        when(followUpTrackMapper.countByFollowUpId(anyLong())).thenReturn(0);
        when(followUpMapper.deleteFollowUpByIds(followUpIds)).thenReturn(3);
        
        int rows = followUpService.deleteFollowUpByIds(followUpIds);
        
        assertEquals(3, rows);
    }

    /**
     * 测试批量删除随访对象 - ID数组为空
     */
    @Test
    void testDeleteFollowUpByIds_EmptyArray() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.deleteFollowUpByIds(new Long[]{});
        });
        
        assertTrue(exception.getMessage().contains("随访ID不能为空"));
    }

    /**
     * 测试批量删除随访对象 - 存在非待随访状态
     */
    @Test
    void testDeleteFollowUpByIds_NotAllPending() {
        Long[] followUpIds = {1L, 2L};
        
        Map<String, Object> detail1 = new HashMap<>();
        detail1.put("follow_up_status", "0");
        Map<String, Object> detail2 = new HashMap<>();
        detail2.put("follow_up_status", "1"); // 随访中
        
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(detail1);
        when(followUpMapper.selectFollowUpDetail(2L)).thenReturn(detail2);
        when(followUpTrackMapper.countByFollowUpId(1L)).thenReturn(0);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.deleteFollowUpByIds(followUpIds);
        });
        
        assertTrue(exception.getMessage().contains("存在非待随访状态的对象"));
    }

    /**
     * 测试批量删除随访对象 - 存在跟踪记录
     */
    @Test
    void testDeleteFollowUpByIds_HasTrackRecords() {
        Long[] followUpIds = {1L, 2L};
        
        Map<String, Object> detail1 = new HashMap<>();
        detail1.put("follow_up_status", "0");
        Map<String, Object> detail2 = new HashMap<>();
        detail2.put("follow_up_status", "0");
        
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(detail1);
        when(followUpMapper.selectFollowUpDetail(2L)).thenReturn(detail2);
        when(followUpTrackMapper.countByFollowUpId(1L)).thenReturn(0);
        when(followUpTrackMapper.countByFollowUpId(2L)).thenReturn(1); // 第2个有跟踪记录
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.deleteFollowUpByIds(followUpIds);
        });
        
        assertTrue(exception.getMessage().contains("存在随访跟踪记录"));
    }

    // ==================== 完成随访测试 ====================

    /**
     * 测试完成随访 - 成功
     */
    @Test
    void testCompleteFollowUp_Success() {
        followUpDetail.put("follow_up_status", "1"); // 随访中
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        when(followUpMapper.updateFollowUpStatus(eq(1L), eq("2"), any(Date.class), eq("随访完成")))
            .thenReturn(1);
        
        int rows = followUpService.completeFollowUp(1L, "随访完成");
        
        assertEquals(1, rows);
        verify(followUpMapper).updateFollowUpStatus(eq(1L), eq("2"), any(Date.class), eq("随访完成"));
    }

    /**
     * 测试完成随访 - 随访ID为空
     */
    @Test
    void testCompleteFollowUp_EmptyId() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.completeFollowUp(null, "随访完成");
        });
        
        assertTrue(exception.getMessage().contains("随访ID不能为空"));
    }

    /**
     * 测试完成随访 - 随访对象不存在
     */
    @Test
    void testCompleteFollowUp_NotExists() {
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.completeFollowUp(1L, "随访完成");
        });
        
        assertTrue(exception.getMessage().contains("随访对象不存在"));
    }

    /**
     * 测试完成随访 - 已完成不可重复操作
     */
    @Test
    void testCompleteFollowUp_AlreadyCompleted() {
        followUpDetail.put("follow_up_status", "2"); // 已完成
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.completeFollowUp(1L, "随访完成");
        });
        
        assertTrue(exception.getMessage().contains("该随访对象已完成"));
    }

    /**
     * 测试完成随访 - 从待随访直接完成
     */
    @Test
    void testCompleteFollowUp_FromPending() {
        followUpDetail.put("follow_up_status", "0"); // 待随访
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        when(followUpMapper.updateFollowUpStatus(eq(1L), eq("2"), any(Date.class), anyString()))
            .thenReturn(1);
        
        int rows = followUpService.completeFollowUp(1L, "随访完成");
        
        assertEquals(1, rows);
    }

    // ==================== 查询测试 ====================

    /**
     * 测试根据居民ID查询随访对象
     */
    @Test
    void testSelectByResidentId_Success() {
        Long residentId = 100L;
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(followUpDetail);
        
        when(followUpMapper.selectByResidentId(residentId)).thenReturn(list);
        
        List<Map<String, Object>> results = followUpService.selectByResidentId(residentId);
        
        assertEquals(1, results.size());
        verify(followUpMapper).selectByResidentId(residentId);
    }

    /**
     * 测试根据居民ID查询 - 居民ID为空
     */
    @Test
    void testSelectByResidentId_EmptyId() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.selectByResidentId(null);
        });
        
        assertTrue(exception.getMessage().contains("居民ID不能为空"));
    }

    /**
     * 测试查询待随访列表
     */
    @Test
    void testSelectPendingFollowUps_Success() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(followUpDetail);
        
        when(followUpMapper.selectPendingFollowUps()).thenReturn(list);
        
        List<Map<String, Object>> results = followUpService.selectPendingFollowUps();
        
        assertEquals(1, results.size());
        verify(followUpMapper).selectPendingFollowUps();
    }

    /**
     * 测试查询超期随访列表 - 成功
     */
    @Test
    void testSelectOverdueFollowUps_Success() {
        int days = 7;
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(followUpDetail);
        
        when(followUpMapper.selectOverdueFollowUps(any(Date.class))).thenReturn(list);
        
        List<Map<String, Object>> results = followUpService.selectOverdueFollowUps(days);
        
        assertEquals(1, results.size());
        verify(followUpMapper).selectOverdueFollowUps(any(Date.class));
    }

    /**
     * 测试查询超期随访列表 - 天数无效
     */
    @Test
    void testSelectOverdueFollowUps_InvalidDays() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.selectOverdueFollowUps(0);
        });
        
        assertTrue(exception.getMessage().contains("超期天数必须大于0"));
        
        exception = assertThrows(ServiceException.class, () -> {
            followUpService.selectOverdueFollowUps(-7);
        });
        
        assertTrue(exception.getMessage().contains("超期天数必须大于0"));
    }

    // ==================== 状态流转测试 ====================

    /**
     * 测试随访完整生命周期 - 待随访→随访中→已完成
     */
    @Test
    void testFollowUpLifecycle_Full() {
        // 1. 创建随访对象（待随访）
        when(followUpMapper.selectByResultId(200L)).thenReturn(null);
        when(followUpMapper.insertFollowUp(any(GcFollowUp.class))).thenReturn(1);
        followUpService.insertFollowUp(followUp);
        assertEquals("0", followUp.getFollowUpStatus());
        
        // 2. 修改为随访中
        followUp.setFollowUpStatus("1");
        followUpDetail.put("follow_up_status", "0");
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        when(followUpMapper.updateFollowUp(any(GcFollowUp.class))).thenReturn(1);
        followUpService.updateFollowUp(followUp);
        
        // 3. 完成随访
        followUpDetail.put("follow_up_status", "1");
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        when(followUpMapper.updateFollowUpStatus(eq(1L), eq("2"), any(Date.class), anyString()))
            .thenReturn(1);
        followUpService.completeFollowUp(1L, "随访完成");
    }

    /**
     * 测试修改状态后删除 - 不允许
     */
    @Test
    void testDeleteAfterStatusChange_NotAllowed() {
        // 随访中状态不允许删除
        followUpDetail.put("follow_up_status", "1");
        when(followUpMapper.selectFollowUpDetail(1L)).thenReturn(followUpDetail);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            followUpService.deleteFollowUpById(1L);
        });
        
        assertTrue(exception.getMessage().contains("仅允许删除待随访状态的对象"));
    }

    // ==================== 边界测试 ====================

    /**
     * 测试计划随访次数边界
     */
    @Test
    void testPlannedVisitCount_Boundary() {
        followUp.setPlannedVisitCount(1); // 最小值
        when(followUpMapper.selectByResultId(200L)).thenReturn(null);
        when(followUpMapper.insertFollowUp(any(GcFollowUp.class))).thenReturn(1);
        
        int rows = followUpService.insertFollowUp(followUp);
        assertEquals(1, rows);
        
        // 测试大值
        followUp.setResultId(201L);
        followUp.setPlannedVisitCount(100);
        when(followUpMapper.selectByResultId(201L)).thenReturn(null);
        
        rows = followUpService.insertFollowUp(followUp);
        assertEquals(1, rows);
    }

    /**
     * 测试超期天数边界
     */
    @Test
    void testOverdueDays_Boundary() {
        when(followUpMapper.selectOverdueFollowUps(any(Date.class)))
            .thenReturn(new ArrayList<>());
        
        // 测试最小有效值
        List<Map<String, Object>> results = followUpService.selectOverdueFollowUps(1);
        assertNotNull(results);
        
        // 测试大值
        results = followUpService.selectOverdueFollowUps(365);
        assertNotNull(results);
    }
}
