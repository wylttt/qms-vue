package com.javaxiaobear.module.gc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.module.gc.domain.GcTask;
import com.javaxiaobear.module.gc.domain.vo.TaskProgressVO;
import com.javaxiaobear.module.gc.mapper.GcTaskMapper;
import com.javaxiaobear.module.gc.mapper.GcRegionMapper;
import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.service.IGcTaskService;

/**
 * 任务Service业务层处理
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Service
public class GcTaskServiceImpl implements IGcTaskService {
    
    @Autowired
    private GcTaskMapper taskMapper;
    
    @Autowired
    private GcRegionMapper regionMapper;

    @Override
    public List<GcTask> selectTaskList(GcTask task) {
        return taskMapper.selectTaskList(task);
    }

    @Override
    public GcTask selectTaskById(Long taskId) {
        return taskMapper.selectTaskById(taskId);
    }

    @Override
    public int insertTask(GcTask task) {
        // 验证任务编号唯一性
        if (StringUtils.isNotEmpty(task.getTaskCode())) {
            GcTask existTask = taskMapper.selectTaskByCode(task.getTaskCode());
            if (existTask != null) {
                throw new RuntimeException("任务编号已存在");
            }
        }
        
        // 验证区域是否存在
        if (task.getRegionId() != null) {
            GcRegion region = regionMapper.selectRegionById(task.getRegionId());
            if (region == null) {
                throw new RuntimeException("分配区域不存在");
            }
        }
        
        // 默认状态为草稿
        if (StringUtils.isEmpty(task.getStatus())) {
            task.setStatus("draft");
        }
        
        // 默认目标数量为0
        if (task.getTargetCount() == null) {
            task.setTargetCount(0);
        }
        
        return taskMapper.insertTask(task);
    }

    @Override
    public int updateTask(GcTask task) {
        // 不允许修改任务编号和任务类型
        task.setTaskCode(null);
        task.setTaskType(null);
        
        return taskMapper.updateTask(task);
    }

    @Override
    public int deleteTaskByIds(Long[] taskIds) {
        return taskMapper.deleteTaskByIds(taskIds);
    }

    @Override
    public int deleteTaskById(Long taskId) {
        return taskMapper.deleteTaskById(taskId);
    }

    @Override
    public boolean checkTaskCodeUnique(GcTask task) {
        Long taskId = task.getTaskId() == null ? -1L : task.getTaskId();
        GcTask existTask = taskMapper.selectTaskByCode(task.getTaskCode());
        if (existTask != null && !existTask.getTaskId().equals(taskId)) {
            return false;
        }
        return true;
    }

    @Override
    public List<TaskProgressVO> selectTaskProgress(GcTask task) {
        return taskMapper.selectTaskProgress(task);
    }

    @Override
    public List<TaskProgressVO> selectTaskProgressByRegion(Long regionId, String taskType) {
        return taskMapper.selectTaskProgressByRegion(regionId, taskType);
    }
}
