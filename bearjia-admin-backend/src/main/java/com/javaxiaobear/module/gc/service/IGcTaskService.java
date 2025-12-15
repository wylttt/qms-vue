package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcTask;
import com.javaxiaobear.module.gc.domain.vo.TaskProgressVO;

import java.util.List;

/**
 * 任务Service接口
 * 
 * @author Bear
 * @date 2025-01-09
 */
public interface IGcTaskService {
    
    /**
     * 查询任务列表
     * 
     * @param task 任务
     * @return 任务集合
     */
    List<GcTask> selectTaskList(GcTask task);

    /**
     * 查询任务详情
     * 
     * @param taskId 任务ID
     * @return 任务
     */
    GcTask selectTaskById(Long taskId);

    /**
     * 新增任务
     * 
     * @param task 任务
     * @return 结果
     */
    int insertTask(GcTask task);

    /**
     * 修改任务
     * 
     * @param task 任务
     * @return 结果
     */
    int updateTask(GcTask task);

    /**
     * 批量删除任务
     * 
     * @param taskIds 需要删除的任务ID数组
     * @return 结果
     */
    int deleteTaskByIds(Long[] taskIds);

    /**
     * 删除任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    int deleteTaskById(Long taskId);

    /**
     * 检查任务编号唯一性
     * 
     * @param task 任务
     * @return 结果
     */
    boolean checkTaskCodeUnique(GcTask task);

    /**
     * 查询任务进度统计
     * 
     * @param task 查询条件
     * @return 任务进度列表
     */
    List<TaskProgressVO> selectTaskProgress(GcTask task);

    /**
     * 根据区域ID查询任务进度统计(含下级区域汇总)
     * 
     * @param regionId 区域ID
     * @param taskType 任务类型
     * @return 任务进度列表
     */
    List<TaskProgressVO> selectTaskProgressByRegion(Long regionId, String taskType);
}
