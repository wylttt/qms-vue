package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUp;
import com.javaxiaobear.module.gc.domain.entity.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningResultVO;
import com.javaxiaobear.module.gc.mapper.GcFollowUpMapper;
import com.javaxiaobear.module.gc.mapper.GcScreeningResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 筛查结果Service单元测试
 * 
 * @author javaxiaobear
 * @date 2024-12-16
 */
@ExtendWith(MockitoExtension.class)
class GcScreeningResultServiceImplTest {

    @Mock
    private GcScreeningResultMapper screeningResultMapper;

    @Mock
    private GcFollowUpMapper followUpMapper;

    @InjectMocks
    private GcScreeningResultServiceImpl screeningResultService;

    private GcScreeningResult result;
    private ScreeningResultVO resultVO;

    @BeforeEach
    void setUp() {
        // 创建测试筛查结果
        result = new GcScreeningResult();
        result.setResultId(1L);
        result.setResidentId(100L);
        result.setAppointmentId(200L);
        result.setScreeningType("1"); // 血液筛查
        result.setBloodResult("0"); // 未检测
        result.setReviewStatus("0"); // 待审核
        result.setNeedFollowUp(0);
        
        // 创建测试VO
        resultVO = new ScreeningResultVO();
        resultVO.setResultId(1L);
        resultVO.setResidentId(100L);
        resultVO.setScreeningType("1");
        resultVO.setReviewStatus("0");
        resultVO.setNeedFollowUp(0);
    }

    // ==================== 筛查结果创建测试 ====================

    /**
     * 测试创建筛查结果 - 成功
     */
    @Test
    void testInsertScreeningResult_Success() {
        when(screeningResultMapper.selectByAppointmentId(200L)).thenReturn(null);
        when(screeningResultMapper.insertScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        
        int rows = screeningResultService.insertScreeningResult(result);
        
        assertEquals(1, rows);
        assertEquals("0", result.getReviewStatus());
        assertEquals("1", result.getRiskLevel()); // 未检测=低风险
        assertEquals(0, result.getNeedFollowUp());
        verify(screeningResultMapper).insertScreeningResult(result);
    }

    /**
     * 测试创建筛查结果 - 居民ID为空
     */
    @Test
    void testInsertScreeningResult_EmptyResidentId() {
        result.setResidentId(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.insertScreeningResult(result);
        });
        
        assertTrue(exception.getMessage().contains("居民ID不能为空"));
    }

    /**
     * 测试创建筛查结果 - 筛查类型为空
     */
    @Test
    void testInsertScreeningResult_EmptyScreeningType() {
        result.setScreeningType(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.insertScreeningResult(result);
        });
        
        assertTrue(exception.getMessage().contains("筛查类型不能为空"));
    }

    /**
     * 测试创建筛查结果 - 预约已存在结果
     */
    @Test
    void testInsertScreeningResult_AppointmentExists() {
        GcScreeningResult existResult = new GcScreeningResult();
        existResult.setAppointmentId(200L);
        when(screeningResultMapper.selectByAppointmentId(200L)).thenReturn(existResult);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.insertScreeningResult(result);
        });
        
        assertTrue(exception.getMessage().contains("该预约已存在筛查结果"));
    }

    // ==================== 风险等级计算测试 ====================

    /**
     * 测试风险等级计算 - 血液筛查低风险
     */
    @Test
    void testCalculateRiskLevel_BloodLowRisk() {
        result.setScreeningType("1");
        result.setBloodResult("0"); // 未检测
        
        when(screeningResultMapper.selectByAppointmentId(200L)).thenReturn(null);
        when(screeningResultMapper.insertScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        
        screeningResultService.insertScreeningResult(result);
        
        assertEquals("1", result.getRiskLevel());
        assertEquals(0, result.getNeedFollowUp());
    }

    /**
     * 测试风险等级计算 - 血液筛查中风险
     */
    @Test
    void testCalculateRiskLevel_BloodMediumRisk() {
        result.setScreeningType("1");
        result.setBloodResult("1"); // 中低风险
        
        when(screeningResultMapper.selectByAppointmentId(200L)).thenReturn(null);
        when(screeningResultMapper.insertScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        
        screeningResultService.insertScreeningResult(result);
        
        assertEquals("2", result.getRiskLevel());
        assertEquals(0, result.getNeedFollowUp());
    }

    /**
     * 测试风险等级计算 - 血液筛查高风险
     */
    @Test
    void testCalculateRiskLevel_BloodHighRisk() {
        result.setScreeningType("1");
        result.setBloodResult("2"); // 高风险
        
        when(screeningResultMapper.selectByAppointmentId(200L)).thenReturn(null);
        when(screeningResultMapper.insertScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        
        screeningResultService.insertScreeningResult(result);
        
        assertEquals("3", result.getRiskLevel());
        assertEquals(1, result.getNeedFollowUp());
    }

    // ==================== 自动创建随访对象测试 ====================

    /**
     * 测试自动创建随访对象 - 高风险
     */
    @Test
    void testAutoCreateFollowUp_HighRisk() {
        result.setScreeningType("1");
        result.setBloodResult("2"); // 高风险
        
        when(screeningResultMapper.selectByAppointmentId(200L)).thenReturn(null);
        when(screeningResultMapper.insertScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        when(followUpMapper.insertFollowUp(any(GcFollowUp.class))).thenReturn(1);
        
        screeningResultService.insertScreeningResult(result);
        
        verify(followUpMapper).insertFollowUp(any(GcFollowUp.class));
    }

    /**
     * 测试不创建随访对象 - 低风险
     */
    @Test
    void testNoFollowUp_LowRisk() {
        result.setScreeningType("1");
        result.setBloodResult("0"); // 低风险
        
        when(screeningResultMapper.selectByAppointmentId(200L)).thenReturn(null);
        when(screeningResultMapper.insertScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        
        screeningResultService.insertScreeningResult(result);
        
        verify(followUpMapper, never()).insertFollowUp(any(GcFollowUp.class));
    }

    // ==================== 筛查结果修改测试 ====================

    /**
     * 测试修改筛查结果 - 成功
     */
    @Test
    void testUpdateScreeningResult_Success() {
        resultVO.setReviewStatus("0"); // 待审核
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        when(screeningResultMapper.updateScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        
        result.setBloodResult("1"); // 修改为中风险
        int rows = screeningResultService.updateScreeningResult(result);
        
        assertEquals(1, rows);
        verify(screeningResultMapper).updateScreeningResult(result);
    }

    /**
     * 测试修改筛查结果 - 结果ID为空
     */
    @Test
    void testUpdateScreeningResult_EmptyResultId() {
        result.setResultId(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.updateScreeningResult(result);
        });
        
        assertTrue(exception.getMessage().contains("结果ID不能为空"));
    }

    /**
     * 测试修改筛查结果 - 结果不存在
     */
    @Test
    void testUpdateScreeningResult_NotExists() {
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.updateScreeningResult(result);
        });
        
        assertTrue(exception.getMessage().contains("筛查结果不存在"));
    }

    /**
     * 测试修改筛查结果 - 已审核不可修改
     */
    @Test
    void testUpdateScreeningResult_AlreadyReviewed() {
        resultVO.setReviewStatus("1"); // 已审核
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.updateScreeningResult(result);
        });
        
        assertTrue(exception.getMessage().contains("已审核的结果不允许修改"));
    }

    /**
     * 测试修改随访标记 - 从不需要变为需要
     */
    @Test
    void testUpdateFollowUpFlag_FromNoToYes() {
        resultVO.setNeedFollowUp(0); // 原本不需要
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        when(screeningResultMapper.updateScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        when(followUpMapper.insertFollowUp(any(GcFollowUp.class))).thenReturn(1);
        
        result.setNeedFollowUp(1); // 改为需要
        screeningResultService.updateScreeningResult(result);
        
        verify(followUpMapper).insertFollowUp(any(GcFollowUp.class));
    }

    /**
     * 测试修改随访标记 - 从需要变为不需要
     */
    @Test
    void testUpdateFollowUpFlag_FromYesToNo() {
        resultVO.setNeedFollowUp(1); // 原本需要
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        when(screeningResultMapper.updateScreeningResult(any(GcScreeningResult.class))).thenReturn(1);
        
        GcFollowUp followUp = new GcFollowUp();
        followUp.setFollowUpId(10L);
        followUp.setFollowUpStatus("0"); // 待随访
        when(followUpMapper.selectByResultId(1L)).thenReturn(followUp);
        when(followUpMapper.deleteFollowUpById(10L)).thenReturn(1);
        
        result.setNeedFollowUp(0); // 改为不需要
        screeningResultService.updateScreeningResult(result);
        
        verify(followUpMapper).deleteFollowUpById(10L);
    }

    // ==================== 筛查结果删除测试 ====================

    /**
     * 测试删除筛查结果 - 成功
     */
    @Test
    void testDeleteScreeningResult_Success() {
        resultVO.setReviewStatus("0");
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        when(followUpMapper.selectByResultId(1L)).thenReturn(null);
        when(screeningResultMapper.deleteScreeningResultById(1L)).thenReturn(1);
        
        int rows = screeningResultService.deleteScreeningResultById(1L);
        
        assertEquals(1, rows);
        verify(screeningResultMapper).deleteScreeningResultById(1L);
    }

    /**
     * 测试删除筛查结果 - 结果ID为空
     */
    @Test
    void testDeleteScreeningResult_EmptyId() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.deleteScreeningResultById(null);
        });
        
        assertTrue(exception.getMessage().contains("结果ID不能为空"));
    }

    /**
     * 测试删除筛查结果 - 结果不存在
     */
    @Test
    void testDeleteScreeningResult_NotExists() {
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.deleteScreeningResultById(1L);
        });
        
        assertTrue(exception.getMessage().contains("筛查结果不存在"));
    }

    /**
     * 测试删除筛查结果 - 已审核不可删除
     */
    @Test
    void testDeleteScreeningResult_AlreadyReviewed() {
        resultVO.setReviewStatus("1");
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.deleteScreeningResultById(1L);
        });
        
        assertTrue(exception.getMessage().contains("仅允许删除待审核状态的结果"));
    }

    /**
     * 测试删除筛查结果 - 同时删除关联的待随访对象
     */
    @Test
    void testDeleteScreeningResult_WithFollowUp() {
        resultVO.setReviewStatus("0");
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        
        GcFollowUp followUp = new GcFollowUp();
        followUp.setFollowUpId(10L);
        followUp.setFollowUpStatus("0"); // 待随访
        when(followUpMapper.selectByResultId(1L)).thenReturn(followUp);
        when(followUpMapper.deleteFollowUpById(10L)).thenReturn(1);
        when(screeningResultMapper.deleteScreeningResultById(1L)).thenReturn(1);
        
        screeningResultService.deleteScreeningResultById(1L);
        
        verify(followUpMapper).deleteFollowUpById(10L);
        verify(screeningResultMapper).deleteScreeningResultById(1L);
    }

    // ==================== 筛查结果审核测试 ====================

    /**
     * 测试审核筛查结果 - 成功
     */
    @Test
    void testReviewScreeningResult_Success() {
        resultVO.setReviewStatus("0");
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        when(screeningResultMapper.updateReviewStatus(eq(1L), eq("1"), eq(999L), eq("张医生"), any(Date.class)))
            .thenReturn(1);
        
        int rows = screeningResultService.reviewScreeningResult(1L, 999L, "张医生");
        
        assertEquals(1, rows);
        verify(screeningResultMapper).updateReviewStatus(eq(1L), eq("1"), eq(999L), eq("张医生"), any(Date.class));
    }

    /**
     * 测试审核筛查结果 - 结果ID为空
     */
    @Test
    void testReviewScreeningResult_EmptyResultId() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.reviewScreeningResult(null, 999L, "张医生");
        });
        
        assertTrue(exception.getMessage().contains("结果ID不能为空"));
    }

    /**
     * 测试审核筛查结果 - 审核人ID为空
     */
    @Test
    void testReviewScreeningResult_EmptyReviewerId() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.reviewScreeningResult(1L, null, "张医生");
        });
        
        assertTrue(exception.getMessage().contains("审核人信息不能为空"));
    }

    /**
     * 测试审核筛查结果 - 审核人姓名为空
     */
    @Test
    void testReviewScreeningResult_EmptyReviewerName() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.reviewScreeningResult(1L, 999L, null);
        });
        
        assertTrue(exception.getMessage().contains("审核人信息不能为空"));
    }

    /**
     * 测试审核筛查结果 - 结果不存在
     */
    @Test
    void testReviewScreeningResult_NotExists() {
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.reviewScreeningResult(1L, 999L, "张医生");
        });
        
        assertTrue(exception.getMessage().contains("筛查结果不存在"));
    }

    /**
     * 测试审核筛查结果 - 已审核不可重复
     */
    @Test
    void testReviewScreeningResult_AlreadyReviewed() {
        resultVO.setReviewStatus("1");
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(resultVO);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.reviewScreeningResult(1L, 999L, "张医生");
        });
        
        assertTrue(exception.getMessage().contains("该结果已审核"));
    }

    // ==================== 批量删除测试 ====================

    /**
     * 测试批量删除筛查结果 - 成功
     */
    @Test
    void testDeleteScreeningResultByIds_Success() {
        Long[] resultIds = {1L, 2L, 3L};
        
        ScreeningResultVO result1 = new ScreeningResultVO();
        result1.setReviewStatus("0");
        ScreeningResultVO result2 = new ScreeningResultVO();
        result2.setReviewStatus("0");
        ScreeningResultVO result3 = new ScreeningResultVO();
        result3.setReviewStatus("0");
        
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(result1);
        when(screeningResultMapper.selectScreeningResultDetail(2L)).thenReturn(result2);
        when(screeningResultMapper.selectScreeningResultDetail(3L)).thenReturn(result3);
        when(screeningResultMapper.deleteScreeningResultByIds(resultIds)).thenReturn(3);
        
        int rows = screeningResultService.deleteScreeningResultByIds(resultIds);
        
        assertEquals(3, rows);
    }

    /**
     * 测试批量删除筛查结果 - ID数组为空
     */
    @Test
    void testDeleteScreeningResultByIds_EmptyArray() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.deleteScreeningResultByIds(new Long[]{});
        });
        
        assertTrue(exception.getMessage().contains("结果ID不能为空"));
    }

    /**
     * 测试批量删除筛查结果 - 存在已审核结果
     */
    @Test
    void testDeleteScreeningResultByIds_HasReviewed() {
        Long[] resultIds = {1L, 2L};
        
        ScreeningResultVO result1 = new ScreeningResultVO();
        result1.setReviewStatus("0");
        ScreeningResultVO result2 = new ScreeningResultVO();
        result2.setReviewStatus("1"); // 已审核
        
        when(screeningResultMapper.selectScreeningResultDetail(1L)).thenReturn(result1);
        when(screeningResultMapper.selectScreeningResultDetail(2L)).thenReturn(result2);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.deleteScreeningResultByIds(resultIds);
        });
        
        assertTrue(exception.getMessage().contains("存在已审核的结果"));
    }

    // ==================== 查询测试 ====================

    /**
     * 测试根据居民ID查询筛查结果
     */
    @Test
    void testSelectByResidentId_Success() {
        Long residentId = 100L;
        List<ScreeningResultVO> list = new ArrayList<>();
        list.add(resultVO);
        
        when(screeningResultMapper.selectByResidentId(residentId)).thenReturn(list);
        
        List<ScreeningResultVO> results = screeningResultService.selectByResidentId(residentId);
        
        assertEquals(1, results.size());
        verify(screeningResultMapper).selectByResidentId(residentId);
    }

    /**
     * 测试根据居民ID查询 - 居民ID为空
     */
    @Test
    void testSelectByResidentId_EmptyId() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.selectByResidentId(null);
        });
        
        assertTrue(exception.getMessage().contains("居民ID不能为空"));
    }

    /**
     * 测试查询筛查结果详情 - 结果ID为空
     */
    @Test
    void testSelectScreeningResultDetail_EmptyId() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            screeningResultService.selectScreeningResultDetail(null);
        });
        
        assertTrue(exception.getMessage().contains("结果ID不能为空"));
    }
}
