package com.javaxiaobear.module.gc.service;

import java.util.List;
import com.javaxiaobear.module.gc.domain.entity.GcTask;
import com.javaxiaobear.module.gc.domain.vo.TaskVO;
import com.javaxiaobear.module.gc.domain.vo.TaskProgressVO;

/**
 * 任务Service接口
 * 
 * @author javaxiaobear
 */
public interface IGcTaskService {
    
    /**
     * 查询任务列表
     */
    List<TaskVO> selectTaskList(GcTask task);
    
    /**
     * 查询任务详情
     */
    GcTask selectTaskById(Long taskId);
    
    /**
     * 新增任务
     */
    int insertTask(GcTask task);
    
    /**
     * 修改任务
     */
    int updateTask(GcTask task);
    
    /**
     * 删除任务
     */
    int deleteTaskById(Long taskId);
    
    /**
     * 批量删除任务
     */
    int deleteTaskByIds(Long[] taskIds);
    
    /**
     * 发布任务
     */
    int publishTask(Long taskId);
    
    /**
     * 结束任务
     */
    int finishTask(Long taskId);
    
    /**
     * 查询任务进度
     */
    TaskProgressVO selectTaskProgress(Long taskId);
    
    /**
     * 校验任务编号唯一性
     */
    boolean checkTaskCodeUnique(String taskCode);
}
