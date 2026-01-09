package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.domain.entity.GcBloodAppointment;
import com.javaxiaobear.module.gc.domain.entity.GcSamplingSite;
import com.javaxiaobear.module.gc.mapper.GcBloodAppointmentMapper;
import com.javaxiaobear.module.gc.mapper.GcSamplingSiteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 采血预约Service单元测试
 * 
 * 测试范围：
 * 1. 容量检测功能
 * 2. 可用时间段查询
 * 3. 预约创建验证
 * 4. 时间冲突检测
 * 
 * @author javaxiaobear
 * @date 2025-12-16
 */
@ExtendWith(MockitoExtension.class)
class GcBloodAppointmentServiceImplTest {

    @Mock
    private GcBloodAppointmentMapper appointmentMapper;

    @Mock
    private GcSamplingSiteMapper samplingSiteMapper;

    @InjectMocks
    private GcBloodAppointmentServiceImpl appointmentService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        // 每个测试方法执行前的准备工作
    }

    /**
     * 测试：检查容量 - 未满额情况
     */
    @Test
    void testCheckCapacity_NotFull() throws ParseException {
        // 准备测试数据
        Long siteId = 1L;
        Date appointmentDate = sdf.parse("2025-12-20");
        
        // Mock采血点信息（容量100）
        GcSamplingSite site = new GcSamplingSite();
        site.setSiteId(siteId);
        site.setSiteName("测试采血点");
        site.setDailyCapacity(100);
        
        // Mock当前预约数量（50）
        when(samplingSiteMapper.selectById(siteId)).thenReturn(site);
        when(appointmentMapper.countAppointmentByDate(siteId, appointmentDate)).thenReturn(50);
        
        // 执行测试
        Map<String, Object> result = appointmentService.checkCapacity(siteId, appointmentDate);
        
        // 验证结果
        assertNotNull(result);
        assertFalse((Boolean) result.get("isFull"), "应该未满额");
        assertEquals(50, result.get("currentCount"), "当前数量应为50");
        assertEquals(100, result.get("maxCapacity"), "最大容量应为100");
        assertEquals(50, result.get("remainingCapacity"), "剩余容量应为50");
        
        // 验证方法调用
        verify(samplingSiteMapper, times(1)).selectById(siteId);
        verify(appointmentMapper, times(1)).countAppointmentByDate(siteId, appointmentDate);
    }

    /**
     * 测试：检查容量 - 已满额情况
     */
    @Test
    void testCheckCapacity_Full() throws ParseException {
        // 准备测试数据
        Long siteId = 1L;
        Date appointmentDate = sdf.parse("2025-12-20");
        
        // Mock采血点信息（容量100）
        GcSamplingSite site = new GcSamplingSite();
        site.setSiteId(siteId);
        site.setSiteName("测试采血点");
        site.setDailyCapacity(100);
        
        // Mock当前预约数量（100，已满额）
        when(samplingSiteMapper.selectById(siteId)).thenReturn(site);
        when(appointmentMapper.countAppointmentByDate(siteId, appointmentDate)).thenReturn(100);
        
        // 执行测试
        Map<String, Object> result = appointmentService.checkCapacity(siteId, appointmentDate);
        
        // 验证结果
        assertNotNull(result);
        assertTrue((Boolean) result.get("isFull"), "应该已满额");
        assertEquals(100, result.get("currentCount"), "当前数量应为100");
        assertEquals(100, result.get("maxCapacity"), "最大容量应为100");
        assertEquals(0, result.get("remainingCapacity"), "剩余容量应为0");
    }

    /**
     * 测试：检查容量 - 超额情况
     */
    @Test
    void testCheckCapacity_Overflow() throws ParseException {
        // 准备测试数据
        Long siteId = 1L;
        Date appointmentDate = sdf.parse("2025-12-20");
        
        // Mock采血点信息（容量100）
        GcSamplingSite site = new GcSamplingSite();
        site.setSiteId(siteId);
        site.setSiteName("测试采血点");
        site.setDailyCapacity(100);
        
        // Mock当前预约数量（120，超额）
        when(samplingSiteMapper.selectById(siteId)).thenReturn(site);
        when(appointmentMapper.countAppointmentByDate(siteId, appointmentDate)).thenReturn(120);
        
        // 执行测试
        Map<String, Object> result = appointmentService.checkCapacity(siteId, appointmentDate);
        
        // 验证结果
        assertNotNull(result);
        assertTrue((Boolean) result.get("isFull"), "应该已满额");
        assertEquals(120, result.get("currentCount"), "当前数量应为120");
        assertEquals(100, result.get("maxCapacity"), "最大容量应为100");
        assertEquals(0, result.get("remainingCapacity"), "剩余容量应为0（不为负数）");
    }

    /**
     * 测试：检查容量 - 采血点不存在
     */
    @Test
    void testCheckCapacity_SiteNotFound() throws ParseException {
        // 准备测试数据
        Long siteId = 999L;
        Date appointmentDate = sdf.parse("2025-12-20");
        
        // Mock采血点不存在
        when(samplingSiteMapper.selectById(siteId)).thenReturn(null);
        
        // 执行测试并验证异常
        Exception exception = assertThrows(Exception.class, () -> {
            appointmentService.checkCapacity(siteId, appointmentDate);
        });
        
        assertTrue(exception.getMessage().contains("采血点不存在"));
    }

    /**
     * 测试：检查容量 - 使用默认容量
     */
    @Test
    void testCheckCapacity_DefaultCapacity() throws ParseException {
        // 准备测试数据
        Long siteId = 1L;
        Date appointmentDate = sdf.parse("2025-12-20");
        
        // Mock采血点信息（未设置容量，应使用默认值100）
        GcSamplingSite site = new GcSamplingSite();
        site.setSiteId(siteId);
        site.setSiteName("测试采血点");
        site.setDailyCapacity(null); // 未设置容量
        
        // Mock当前预约数量
        when(samplingSiteMapper.selectById(siteId)).thenReturn(site);
        when(appointmentMapper.countAppointmentByDate(siteId, appointmentDate)).thenReturn(30);
        
        // 执行测试
        Map<String, Object> result = appointmentService.checkCapacity(siteId, appointmentDate);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.get("maxCapacity"), "应使用默认容量100");
        assertEquals(70, result.get("remainingCapacity"), "剩余容量应为70");
    }

    /**
     * 测试：获取可用时间段 - 有剩余容量
     */
    @Test
    void testGetAvailableTimeSlots_HasCapacity() throws ParseException {
        // 准备测试数据
        Long siteId = 1L;
        Date appointmentDate = sdf.parse("2025-12-20");
        
        // Mock采血点信息
        GcSamplingSite site = new GcSamplingSite();
        site.setSiteId(siteId);
        site.setDailyCapacity(100);
        
        // Mock容量检测（未满额）
        when(samplingSiteMapper.selectById(siteId)).thenReturn(site);
        when(appointmentMapper.countAppointmentByDate(siteId, appointmentDate)).thenReturn(50);
        
        // 执行测试
        List<Map<String, Object>> slots = appointmentService.getAvailableTimeSlots(siteId, appointmentDate);
        
        // 验证结果
        assertNotNull(slots);
        assertEquals(2, slots.size(), "应返回2个时间段（上午/下午）");
        
        // 验证上午时间段
        Map<String, Object> morningSlot = slots.get(0);
        assertEquals("1", morningSlot.get("period"));
        assertEquals("上午 08:00-12:00", morningSlot.get("label"));
        assertTrue((Boolean) morningSlot.get("available"));
        
        // 验证下午时间段
        Map<String, Object> afternoonSlot = slots.get(1);
        assertEquals("2", afternoonSlot.get("period"));
        assertEquals("下午 14:00-18:00", afternoonSlot.get("label"));
        assertTrue((Boolean) afternoonSlot.get("available"));
    }

    /**
     * 测试：获取可用时间段 - 已满额
     */
    @Test
    void testGetAvailableTimeSlots_Full() throws ParseException {
        // 准备测试数据
        Long siteId = 1L;
        Date appointmentDate = sdf.parse("2025-12-20");
        
        // Mock采血点信息
        GcSamplingSite site = new GcSamplingSite();
        site.setSiteId(siteId);
        site.setDailyCapacity(100);
        
        // Mock容量检测（已满额）
        when(samplingSiteMapper.selectById(siteId)).thenReturn(site);
        when(appointmentMapper.countAppointmentByDate(siteId, appointmentDate)).thenReturn(100);
        
        // 执行测试
        List<Map<String, Object>> slots = appointmentService.getAvailableTimeSlots(siteId, appointmentDate);
        
        // 验证结果
        assertNotNull(slots);
        assertEquals(0, slots.size(), "已满额应返回空列表");
    }

    /**
     * 测试：新增预约 - 正常情况
     */
    @Test
    void testInsertAppointment_Success() {
        // 准备测试数据
        GcBloodAppointment appointment = new GcBloodAppointment();
        appointment.setResidentId(1L);
        appointment.setAppointmentSiteId(1L);
        appointment.setAppointmentDate(new Date());
        appointment.setAppointmentPeriod("1");
        
        // Mock时间冲突检测（无冲突）
        when(appointmentMapper.checkTimeConflict(anyLong(), any(), anyString(), any())).thenReturn(0);
        when(appointmentMapper.insertAppointment(appointment)).thenReturn(1);
        
        // 执行测试
        int result = appointmentService.insertAppointment(appointment);
        
        // 验证结果
        assertEquals(1, result);
        assertEquals("0", appointment.getAppointmentStatus(), "状态应为待确认");
        assertEquals("0", appointment.getSamplingStatus(), "采样状态应为未采样");
        
        // 验证方法调用
        verify(appointmentMapper, times(1)).checkTimeConflict(anyLong(), any(), anyString(), any());
        verify(appointmentMapper, times(1)).insertAppointment(appointment);
    }

    /**
     * 测试：新增预约 - 时间冲突
     */
    @Test
    void testInsertAppointment_TimeConflict() {
        // 准备测试数据
        GcBloodAppointment appointment = new GcBloodAppointment();
        appointment.setResidentId(1L);
        appointment.setAppointmentSiteId(1L);
        appointment.setAppointmentDate(new Date());
        appointment.setAppointmentPeriod("1");
        
        // Mock时间冲突检测（有冲突）
        when(appointmentMapper.checkTimeConflict(anyLong(), any(), anyString(), any())).thenReturn(1);
        
        // 执行测试并验证异常
        Exception exception = assertThrows(Exception.class, () -> {
            appointmentService.insertAppointment(appointment);
        });
        
        assertTrue(exception.getMessage().contains("该居民在此时间段已有预约"));
        
        // 验证未插入数据
        verify(appointmentMapper, never()).insertAppointment(any());
    }

    /**
     * 测试：新增预约 - 缺少必填字段
     */
    @Test
    void testInsertAppointment_MissingFields() {
        // 准备测试数据（缺少居民ID）
        GcBloodAppointment appointment = new GcBloodAppointment();
        appointment.setAppointmentSiteId(1L);
        appointment.setAppointmentDate(new Date());
        appointment.setAppointmentPeriod("1");
        
        // 执行测试并验证异常
        Exception exception = assertThrows(Exception.class, () -> {
            appointmentService.insertAppointment(appointment);
        });
        
        assertTrue(exception.getMessage().contains("居民ID不能为空"));
    }

    /**
     * 测试：新增预约 - 无效的时间段
     */
    @Test
    void testInsertAppointment_InvalidPeriod() {
        // 准备测试数据（无效的时间段）
        GcBloodAppointment appointment = new GcBloodAppointment();
        appointment.setResidentId(1L);
        appointment.setAppointmentSiteId(1L);
        appointment.setAppointmentDate(new Date());
        appointment.setAppointmentPeriod("3"); // 无效值（仅支持1或2）
        
        // 执行测试并验证异常
        Exception exception = assertThrows(Exception.class, () -> {
            appointmentService.insertAppointment(appointment);
        });
        
        assertTrue(exception.getMessage().contains("预约时间段必须为1(上午)或2(下午)"));
    }

    /**
     * 测试：批量预约 - 部分成功
     */
    @Test
    void testBatchAppointment_PartialSuccess() throws ParseException {
        // 准备测试数据
        List<Long> residentIds = Arrays.asList(1L, 2L, 3L);
        Long siteId = 1L;
        Date appointmentDate = sdf.parse("2025-12-20");
        String appointmentPeriod = "1";
        
        // Mock时间冲突检测（第2个居民有冲突）
        when(appointmentMapper.checkTimeConflict(eq(1L), any(), anyString(), any())).thenReturn(0);
        when(appointmentMapper.checkTimeConflict(eq(2L), any(), anyString(), any())).thenReturn(1);
        when(appointmentMapper.checkTimeConflict(eq(3L), any(), anyString(), any())).thenReturn(0);
        
        // Mock插入操作
        when(appointmentMapper.insertAppointment(any())).thenReturn(1);
        
        // 执行测试
        int successCount = appointmentService.batchAppointment(residentIds, siteId, appointmentDate, appointmentPeriod);
        
        // 验证结果（应成功2个，跳过1个有冲突的）
        assertEquals(2, successCount);
        
        // 验证方法调用次数
        verify(appointmentMapper, times(3)).checkTimeConflict(anyLong(), any(), anyString(), any());
        verify(appointmentMapper, times(2)).insertAppointment(any());
    }
}
