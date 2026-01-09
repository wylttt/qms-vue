package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.base.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcTask;
import com.javaxiaobear.module.gc.mapper.GcTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 任务管理Service单元测试
 * 
 * @author javaxiaobear
 * @date 2024-12-16
 */
@ExtendWith(MockitoExtension.class)
class GcTaskServiceImplTest {

    @Mock
    private GcTaskMapper taskMapper;

    @InjectMocks
    private GcTaskServiceImpl taskService;

    private SimpleDateFormat sdf;
    private GcTask task;

    @BeforeEach
    void setUp() throws ParseException {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        // 创建测试任务
        task = new GcTask();
        task.setTaskId(1L);
        task.setTaskCode("TASK202512160001");
        task.setTaskName("2025年胃癌筛查任务");
        task.setTaskType("1"); // 筛查任务
        task.setRegionId(1L);
        task.setRegionLevel(3);
        task.setTargetCount(1000);
        task.setCompletedCount(0);
        task.setTaskStatus("0"); // 草稿
        task.setStartDate(sdf.parse("2025-01-01"));
        task.setEndDate(sdf.parse("2025-12-31"));
    }

    // ==================== 任务创建测试 ====================

    /**
     * 测试创建任务 - 成功
     */
    @Test
    void testInsertTask_Success() {
        when(taskMapper.selectTaskByCode(anyString())).thenReturn(null);
        when(taskMapper.insertTask(any(GcTask.class))).thenReturn(1);
        
        int result = taskService.insertTask(task);
        
        assertEquals(1, result);
        verify(taskMapper).insertTask(task);
        assertEquals("0", task.getTaskStatus()); // 默认草稿状态
        assertEquals(0, task.getCompletedCount()); // 默认完成数为0
    }

    /**
     * 测试创建任务 - 任务编号已存在
     */
    @Test
    void testInsertTask_CodeExists() {
        GcTask existTask = new GcTask();
        existTask.setTaskCode("TASK202512160001");
        when(taskMapper.selectTaskByCode("TASK202512160001")).thenReturn(existTask);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.insertTask(task);
        });
        
        assertTrue(exception.getMessage().contains("任务编号已存在"));
    }

    /**
     * 测试创建任务 - 结束日期早于开始日期
     */
    @Test
    void testInsertTask_InvalidDateRange() throws ParseException {
        task.setStartDate(sdf.parse("2025-12-31"));
        task.setEndDate(sdf.parse("2025-01-01"));
        
        when(taskMapper.selectTaskByCode(anyString())).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.insertTask(task);
        });
        
        assertTrue(exception.getMessage().contains("结束日期不能早于开始日期"));
    }

    /**
     * 测试创建任务 - 目标人数为0
     */
    @Test
    void testInsertTask_ZeroTarget() {
        task.setTargetCount(0);
        when(taskMapper.selectTaskByCode(anyString())).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.insertTask(task);
        });
        
        assertTrue(exception.getMessage().contains("目标人数必须大于0"));
    }

    /**
     * 测试创建任务 - 目标人数为负数
     */
    @Test
    void testInsertTask_NegativeTarget() {
        task.setTargetCount(-100);
        when(taskMapper.selectTaskByCode(anyString())).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.insertTask(task);
        });
        
        assertTrue(exception.getMessage().contains("目标人数必须大于0"));
    }

    /**
     * 测试创建任务 - 自动生成任务编号
     */
    @Test
    void testInsertTask_AutoGenerateCode() {
        task.setTaskCode(null);
        when(taskMapper.selectTaskByCode(anyString())).thenReturn(null);
        when(taskMapper.insertTask(any(GcTask.class))).thenReturn(1);
        
        int result = taskService.insertTask(task);
        
        assertEquals(1, result);
        assertNotNull(task.getTaskCode());
        assertTrue(task.getTaskCode().startsWith("TASK"));
    }

    // ==================== 任务修改测试 ====================

    /**
     * 测试修改任务 - 成功
     */
    @Test
    void testUpdateTask_Success() {
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        when(taskMapper.updateTask(any(GcTask.class))).thenReturn(1);
        
        task.setTaskName("修改后的任务名称");
        int result = taskService.updateTask(task);
        
        assertEquals(1, result);
        verify(taskMapper).updateTask(task);
    }

    /**
     * 测试修改任务 - 任务不存在
     */
    @Test
    void testUpdateTask_NotExists() {
        when(taskMapper.selectTaskById(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.updateTask(task);
        });
        
        assertTrue(exception.getMessage().contains("任务不存在"));
    }

    /**
     * 测试修改任务 - 非草稿状态不可编辑
     */
    @Test
    void testUpdateTask_NotDraft() {
        task.setTaskStatus("1"); // 进行中
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.updateTask(task);
        });
        
        assertTrue(exception.getMessage().contains("仅草稿状态的任务可编辑"));
    }

    // ==================== 任务删除测试 ====================

    /**
     * 测试删除任务 - 成功
     */
    @Test
    void testDeleteTask_Success() {
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        when(taskMapper.deleteTaskById(1L)).thenReturn(1);
        
        int result = taskService.deleteTaskById(1L);
        
        assertEquals(1, result);
        verify(taskMapper).deleteTaskById(1L);
    }

    /**
     * 测试删除任务 - 任务不存在
     */
    @Test
    void testDeleteTask_NotExists() {
        when(taskMapper.selectTaskById(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.deleteTaskById(1L);
        });
        
        assertTrue(exception.getMessage().contains("任务不存在"));
    }

    /**
     * 测试删除任务 - 非草稿状态不可删除
     */
    @Test
    void testDeleteTask_NotDraft() {
        task.setTaskStatus("1"); // 进行中
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.deleteTaskById(1L);
        });
        
        assertTrue(exception.getMessage().contains("仅草稿状态的任务可删除"));
    }

    /**
     * 测试批量删除任务 - 成功
     */
    @Test
    void testDeleteTaskByIds_Success() {
        Long[] taskIds = {1L, 2L, 3L};
        
        GcTask task1 = new GcTask();
        task1.setTaskStatus("0");
        GcTask task2 = new GcTask();
        task2.setTaskStatus("0");
        GcTask task3 = new GcTask();
        task3.setTaskStatus("0");
        
        when(taskMapper.selectTaskById(1L)).thenReturn(task1);
        when(taskMapper.selectTaskById(2L)).thenReturn(task2);
        when(taskMapper.selectTaskById(3L)).thenReturn(task3);
        when(taskMapper.deleteTaskByIds(taskIds)).thenReturn(3);
        
        int result = taskService.deleteTaskByIds(taskIds);
        
        assertEquals(3, result);
        verify(taskMapper).deleteTaskByIds(taskIds);
    }

    /**
     * 测试批量删除任务 - 存在非草稿状态
     */
    @Test
    void testDeleteTaskByIds_NotAllDraft() {
        Long[] taskIds = {1L, 2L};
        
        GcTask task1 = new GcTask();
        task1.setTaskId(1L);
        task1.setTaskStatus("0"); // 草稿
        
        GcTask task2 = new GcTask();
        task2.setTaskId(2L);
        task2.setTaskStatus("1"); // 进行中
        
        when(taskMapper.selectTaskById(1L)).thenReturn(task1);
        when(taskMapper.selectTaskById(2L)).thenReturn(task2);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.deleteTaskByIds(taskIds);
        });
        
        assertTrue(exception.getMessage().contains("不是草稿状态"));
        assertTrue(exception.getMessage().contains("2"));
    }

    // ==================== 任务发布测试 ====================

    /**
     * 测试发布任务 - 成功
     */
    @Test
    void testPublishTask_Success() {
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        when(taskMapper.updateTask(any(GcTask.class))).thenReturn(1);
        
        int result = taskService.publishTask(1L);
        
        assertEquals(1, result);
        assertEquals("1", task.getTaskStatus());
        verify(taskMapper).updateTask(task);
    }

    /**
     * 测试发布任务 - 任务不存在
     */
    @Test
    void testPublishTask_NotExists() {
        when(taskMapper.selectTaskById(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.publishTask(1L);
        });
        
        assertTrue(exception.getMessage().contains("任务不存在"));
    }

    /**
     * 测试发布任务 - 非草稿状态不可发布
     */
    @Test
    void testPublishTask_NotDraft() {
        task.setTaskStatus("1"); // 已经是进行中
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.publishTask(1L);
        });
        
        assertTrue(exception.getMessage().contains("仅草稿状态的任务可发布"));
    }

    // ==================== 任务结束测试 ====================

    /**
     * 测试结束任务 - 成功
     */
    @Test
    void testFinishTask_Success() {
        task.setTaskStatus("1"); // 进行中
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        when(taskMapper.updateTask(any(GcTask.class))).thenReturn(1);
        
        int result = taskService.finishTask(1L);
        
        assertEquals(1, result);
        assertEquals("2", task.getTaskStatus());
        verify(taskMapper).updateTask(task);
    }

    /**
     * 测试结束任务 - 任务不存在
     */
    @Test
    void testFinishTask_NotExists() {
        when(taskMapper.selectTaskById(1L)).thenReturn(null);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.finishTask(1L);
        });
        
        assertTrue(exception.getMessage().contains("任务不存在"));
    }

    /**
     * 测试结束任务 - 非进行中状态不可结束
     */
    @Test
    void testFinishTask_NotInProgress() {
        task.setTaskStatus("0"); // 草稿状态
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.finishTask(1L);
        });
        
        assertTrue(exception.getMessage().contains("仅进行中的任务可结束"));
    }

    // ==================== 任务编号唯一性测试 ====================

    /**
     * 测试任务编号唯一性检查 - 唯一
     */
    @Test
    void testCheckTaskCodeUnique_Unique() {
        when(taskMapper.selectTaskByCode("TASK202512160001")).thenReturn(null);
        
        boolean result = taskService.checkTaskCodeUnique("TASK202512160001");
        
        assertTrue(result);
    }

    /**
     * 测试任务编号唯一性检查 - 不唯一
     */
    @Test
    void testCheckTaskCodeUnique_NotUnique() {
        GcTask existTask = new GcTask();
        existTask.setTaskCode("TASK202512160001");
        when(taskMapper.selectTaskByCode("TASK202512160001")).thenReturn(existTask);
        
        boolean result = taskService.checkTaskCodeUnique("TASK202512160001");
        
        assertFalse(result);
    }

    // ==================== 状态流转测试 ====================

    /**
     * 测试任务完整生命周期 - 草稿→进行中→已结束
     */
    @Test
    void testTaskLifecycle_Full() {
        // 1. 创建草稿
        when(taskMapper.selectTaskByCode(anyString())).thenReturn(null);
        when(taskMapper.insertTask(any(GcTask.class))).thenReturn(1);
        taskService.insertTask(task);
        assertEquals("0", task.getTaskStatus());
        
        // 2. 发布（草稿→进行中）
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        when(taskMapper.updateTask(any(GcTask.class))).thenReturn(1);
        taskService.publishTask(1L);
        assertEquals("1", task.getTaskStatus());
        
        // 3. 结束（进行中→已结束）
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        taskService.finishTask(1L);
        assertEquals("2", task.getTaskStatus());
    }

    /**
     * 测试非法状态流转 - 草稿→已结束（跳过进行中）
     */
    @Test
    void testTaskLifecycle_InvalidTransition() {
        task.setTaskStatus("0"); // 草稿
        when(taskMapper.selectTaskById(1L)).thenReturn(task);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            taskService.finishTask(1L);
        });
        
        assertTrue(exception.getMessage().contains("仅进行中的任务可结束"));
    }

    // ==================== 边界测试 ====================

    /**
     * 测试目标人数边界值
     */
    @Test
    void testInsertTask_BoundaryTarget() {
        // 测试最小有效值
        task.setTargetCount(1);
        when(taskMapper.selectTaskByCode(anyString())).thenReturn(null);
        when(taskMapper.insertTask(any(GcTask.class))).thenReturn(1);
        
        int result = taskService.insertTask(task);
        assertEquals(1, result);
        
        // 测试大数值
        task.setTaskCode("TASK202512160002");
        task.setTargetCount(1000000);
        when(taskMapper.selectTaskByCode("TASK202512160002")).thenReturn(null);
        
        result = taskService.insertTask(task);
        assertEquals(1, result);
    }

    /**
     * 测试日期边界 - 同一天
     */
    @Test
    void testInsertTask_SameDayRange() throws ParseException {
        Date sameDay = sdf.parse("2025-06-01");
        task.setStartDate(sameDay);
        task.setEndDate(sameDay);
        
        when(taskMapper.selectTaskByCode(anyString())).thenReturn(null);
        when(taskMapper.insertTask(any(GcTask.class))).thenReturn(1);
        
        int result = taskService.insertTask(task);
        assertEquals(1, result);
    }
}
