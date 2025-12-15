package com.javaxiaobear.module.gc.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcTask;
import com.javaxiaobear.module.gc.domain.vo.TaskVO;
import com.javaxiaobear.module.gc.domain.vo.TaskProgressVO;
import com.javaxiaobear.module.gc.domain.vo.SubRegionProgressVO;
import com.javaxiaobear.module.gc.mapper.GcTaskMapper;
import com.javaxiaobear.module.gc.service.IGcTaskService;

/**
 * 任务Service业务层处理
 * 
 * @author javaxiaobear
 */
@Service
public class GcTaskServiceImpl implements IGcTaskService {
    
    @Autowired
    private GcTaskMapper taskMapper;
    
    @Override
    public List<TaskVO> selectTaskList(GcTask task) {
        return taskMapper.selectTaskList(task);
    }
    
    @Override
    public GcTask selectTaskById(Long taskId) {
        return taskMapper.selectTaskById(taskId);
    }
    
    @Override
    public int insertTask(GcTask task) {
        // 1. 生成任务编号
        if (task.getTaskCode() == null || task.getTaskCode().isEmpty()) {
            task.setTaskCode(generateTaskCode());
        }
        
        // 2. 验证任务编号唯一性
        if (!checkTaskCodeUnique(task.getTaskCode())) {
            throw new ServiceException("任务编号已存在");
        }
        
        // 3. 验证结束日期 > 开始日期
        if (task.getEndDate() != null && task.getStartDate() != null) {
            if (task.getEndDate().before(task.getStartDate())) {
                throw new ServiceException("结束日期不能早于开始日期");
            }
        }
        
        // 4. 验证目标人数 > 0
        if (task.getTargetCount() == null || task.getTargetCount() <= 0) {
            throw new ServiceException("目标人数必须大于0");
        }
        
        // 5. 设置默认值
        if (task.getTaskStatus() == null) {
            task.setTaskStatus("0"); // 默认草稿状态
        }
        if (task.getCompletedCount() == null) {
            task.setCompletedCount(0);
        }
        
        return taskMapper.insertTask(task);
    }
    
    @Override
    public int updateTask(GcTask task) {
        // 验证任务是否存在
        GcTask existTask = taskMapper.selectTaskById(task.getTaskId());
        if (existTask == null) {
            throw new ServiceException("任务不存在");
        }
        
        // 仅草稿状态可编辑
        if (!"0".equals(existTask.getTaskStatus())) {
            throw new ServiceException("仅草稿状态的任务可编辑");
        }
        
        return taskMapper.updateTask(task);
    }
    
    @Override
    public int deleteTaskById(Long taskId) {
        // 验证任务是否存在
        GcTask task = taskMapper.selectTaskById(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        
        // 仅草稿状态可删除
        if (!"0".equals(task.getTaskStatus())) {
            throw new ServiceException("仅草稿状态的任务可删除");
        }
        
        return taskMapper.deleteTaskById(taskId);
    }
    
    @Override
    public int deleteTaskByIds(Long[] taskIds) {
        for (Long taskId : taskIds) {
            GcTask task = taskMapper.selectTaskById(taskId);
            if (task != null && !"0".equals(task.getTaskStatus())) {
                throw new ServiceException("任务ID:" + taskId + " 不是草稿状态，不允许删除");
            }
        }
        return taskMapper.deleteTaskByIds(taskIds);
    }
    
    @Override
    public int publishTask(Long taskId) {
        GcTask task = taskMapper.selectTaskById(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        
        if (!"0".equals(task.getTaskStatus())) {
            throw new ServiceException("仅草稿状态的任务可发布");
        }
        
        task.setTaskStatus("1"); // 设置为进行中
        return taskMapper.updateTask(task);
    }
    
    @Override
    public int finishTask(Long taskId) {
        GcTask task = taskMapper.selectTaskById(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        
        if (!"1".equals(task.getTaskStatus())) {
            throw new ServiceException("仅进行中的任务可结束");
        }
        
        task.setTaskStatus("2"); // 设置为已结束
        return taskMapper.updateTask(task);
    }
    
    @Override
    public TaskProgressVO selectTaskProgress(Long taskId) {
        // TODO: 实现进度查询逻辑
        // 1. 查询任务信息
        // 2. 根据任务类型统计完成数量
        // 3. 计算完成率
        // 4. 如果有下级区域，查询下级区域进度
        return new TaskProgressVO();
    }
    
    @Override
    public boolean checkTaskCodeUnique(String taskCode) {
        GcTask task = taskMapper.selectTaskByCode(taskCode);
        return task == null;
    }
    
    /**
     * 生成任务编号
     * 格式：TASK+年月日+5位序号
     */
    private String generateTaskCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        // TODO: 查询今天已生成的最大序号，+1
        String序号 = String.format("%05d", 1);
        return "TASK" + dateStr + 序号;
    }
}
